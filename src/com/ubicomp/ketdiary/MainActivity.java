package com.ubicomp.ketdiary;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import com.ubicomp.ketdiary.color.TestStripDetection;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.dialog.CheckResultDialog;
import com.ubicomp.ketdiary.dialog.NoteDialog2;
import com.ubicomp.ketdiary.fragment.DaybookFragment;
import com.ubicomp.ketdiary.fragment.NoteFragment;
import com.ubicomp.ketdiary.fragment.StatisticFragment;
import com.ubicomp.ketdiary.fragment.TestFragment;
import com.ubicomp.ketdiary.system.Config;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomMenu;
import com.ubicomp.ketdiary.ui.CustomTab;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.LoadingDialogControl;
import com.ubicomp.ketdiary.ui.ScreenSize;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Main activity of KetDiary. This activity contains the three functions -
 * test, statistic, and storytelling.
 * 
 * @author Andy Chen
 */
public class MainActivity extends FragmentActivity {
	
	
	private static MainActivity mainActivity = null;

	private TabHost tabHost;

	private TabSpec[] tabs;
	private CustomTab[] customTabs;

	private static final String[] tabName = { "Test", "Statistic",
			"Storytelling" };
	private static final int[] iconId = { R.drawable.tab_test_selector,
			R.drawable.tab_statistic_selector,
			R.drawable.tab_storytelling_selector };
	
	private static final int[] iconOnId = { R.drawable.bar_test_button_pressed,
			R.drawable.bar_find_button_pressed,
			R.drawable.bar_data_button_pressed };

	private Fragment[] fragments;
	private android.support.v4.app.FragmentTransaction ft;
	private android.support.v4.app.FragmentManager fm;

	private RelativeLayout mainLayout;
	
	private ImageView loading_page, animationImg;
	private LoadingPageTimer loadingPageTimer;
	private Handler loadingHandler = new LoadingHandler();
	private AnimationDrawable animation;

	private CustomMenu menu;

	private RelativeLayout count_down_layout;
	private TextView count_down_text, count_down_label;

	private static final String TAG = "MAIN_ACTIVITY";

	private boolean canUpdate;
	private CountDownTimer updateTestTimer = null;

	private static final long TEST_GAP_DURATION_LONG = Config.TEST_GAP_DURATION_LONG;
	private static final long TEST_GAP_DURATION_SHORT = Config.TEST_GAP_DURATION_SHORT;
	
	private CountDownTimer sensorCountDownTimer = null;
	private boolean isRecovery = false;
	private boolean restart = false;

	private SoundPool soundpool;
	private int timer_sound_id;

	private int notify_action = 0;
	private CheckResultDialog msgBox;

	private boolean clickable = false;   // back 
	private boolean doubleClickState = false;
	private long latestClickTime = 0;
	
	private int changeClock=0;
	
	public static long WAIT_RESULT_TIME = PreferenceControl.getAfterCountDown() * 1000;
	public static final int ACTION_RECORD = 1;
	public static final int ACTION_QUESTIONNAIRE = 2;
	
	private TestStripDetection testStripDetection;
	
	private DatabaseControl db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainActivity = this;
		setContentView(R.layout.activity_main);
		enableTabAndClick(false);

		loading_page = (ImageView) findViewById(R.id.loading_page);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		animationImg = (ImageView) findViewById(R.id.main_count_down_image);
		animation = (AnimationDrawable) animationImg.getDrawable();
		
		count_down_layout = (RelativeLayout) findViewById(R.id.main_count_down_layout);
		count_down_text = (TextView) findViewById(R.id.main_count_down_text);
		count_down_label= (TextView) findViewById(R.id.main_count_down_text2);
		
		mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
		
		if (soundpool == null) {
			soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
			timer_sound_id = soundpool.load(getApplicationContext(),
					R.raw.end_count_down, 0);
		}
		
		loadingHandler.sendEmptyMessage(0);

		loadingPageTimer = new LoadingPageTimer();
		loadingPageTimer.start();

	}

	private class LoadingHandler extends Handler {
		public void handleMessage(Message msg) {
			if (PreferenceControl.checkFirstUID())
				PreferenceControl.defaultSetting();
			
			//For test Sevice
			//Intent startIntent =  new  Intent( mainActivity , ResultService. class );  
			//startService(startIntent); 
            //
			
			//testStripDetection = new TestStripDetection();
			
			Typefaces.initAll();
			CustomToast.settingSoundPool();

			tabHost.setup();

			if (tabs == null)
				tabs = new TabSpec[3];
			if (customTabs == null)
				customTabs = new CustomTab[3];

			for (int i = 0; i < 3; ++i) {
				customTabs[i] = new CustomTab(iconId[i], iconOnId[i]);
				tabs[i] = tabHost.newTabSpec(tabName[i]).setIndicator(
						customTabs[i].getTab());
				tabs[i].setContent(new DummyTabFactory(mainActivity));
				tabHost.addTab(tabs[i]);
			}
			fm = getSupportFragmentManager();
			fragments = new Fragment[3];
			tabHost.setOnTabChangedListener(new TabChangeListener());

			setDefaultTab();
			//setDefaultTab2();
			//enableTab(false);
			
			TabWidget tabWidget = tabHost.getTabWidget();

			int count = tabWidget.getChildCount();
			for (int i = 0; i < count; ++i)
				tabWidget.getChildTabViewAt(i).setMinimumWidth(
						ScreenSize.getScreenSize().x / count);
			
			
			count_down_text.setTypeface(Typefaces.getDigitTypefaceBold());
			count_down_layout.setOnTouchListener(new CountDownCircleOnTouchListener());
		}
	}
	
	private class CountDownCircleOnTouchListener implements
			View.OnTouchListener {
		private int width = 0, height = 0;

		public boolean onTouch(View v, MotionEvent event) {
			RelativeLayout.LayoutParams param = (LayoutParams) v
					.getLayoutParams();
			Point screen = ScreenSize.getScreenSize();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				width = width > 0 ? width : v.getWidth();
				height = height > 0 ? height : v.getHeight();
				param.width = width * 3 / 2;
				param.height = height * 3 / 2;
				count_down_text.setTextSize(21);
				count_down_label.setTextSize(21);
				//v.setBackgroundResource(R.drawable.count_down_circle_pressed);
				v.setLayoutParams(param);
				v.invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				param.leftMargin = (int) event.getRawX() - width * 3 / 4;
				param.topMargin = (int) event.getRawY() - height * 3 / 4;
				param.leftMargin = Math.max(param.leftMargin, 0);
				param.topMargin = Math.max(param.topMargin, 0);
				param.leftMargin = Math.min(param.leftMargin, screen.x - width
						* 3 / 2);
				param.topMargin = Math.min(param.topMargin, screen.y - height
						* 3 / 2);
				v.setLayoutParams(param);
				v.invalidate();
				break;

			case MotionEvent.ACTION_UP:
				//v.setBackgroundResource(R.drawable.count_down_circle_normal);
				param.width = width;
				param.height = height;
				v.setLayoutParams(param);
				count_down_text.setTextSize(14);
				count_down_label.setTextSize(14);
				v.invalidate();
				break;
			}
			return true;
		}
	}

	@Override
	protected void onStart() {
		UploadService.startUploadService(this);
		super.onStart();
	}

	protected void onResume() {
		super.onResume();
		/*if (LockCheck.check()) {
			Intent lock_intent = new Intent(this, LockedActivity.class);
			startActivity(lock_intent);
			finish();
			return;
		}*/
		//showResult();
		WAIT_RESULT_TIME = PreferenceControl.getAfterCountDown() * 1000;
		PreferenceControl.setInApp(true);		
		boolean inApp = PreferenceControl.getInApp();	
		Log.d("InApp",String.valueOf(inApp)+"WAIT_TIME: "+ WAIT_RESULT_TIME);
		
		long curTime = System.currentTimeMillis();
		long testTime = PreferenceControl.getLatestTestCompleteTime();
		long pastTime = curTime - testTime;
		int state = PreferenceControl.getAfterTestState();
		Log.d("InApp",String.valueOf(state));
		
		if(state == NoteDialog2.STATE_NOTE || state == NoteDialog2.STATE_COPE){
			enableTabAndClick(false);
			//Log.d("InApp","Disable click");
		}
		else{
			clickable = true;
		}
		
		if(PreferenceControl.getCheckResult() && pastTime < WAIT_RESULT_TIME)
			setTimers();
		
		else if(PreferenceControl.getCheckResult() && pastTime >= WAIT_RESULT_TIME){
			setResult();
			//showResult();
			//setTimers();
			//changeTab(1);
		}
		else{
			enableTabAndClick(true);
		}
		
		Log.d("InApp",String.valueOf(clickable));
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0); //回到APP裡要把Notification關掉
		//clickable = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		closeOptionsMenu();
		closeTimers();
		
		PreferenceControl.setInApp(false);		
		boolean inApp = PreferenceControl.getInApp();
		Log.d("InApp",String.valueOf(inApp));
		
		
		super.onPause();
	}
	
	@Override
	protected void onStop() {		
		super.onStop();
	}

	public void setTabState(String tabId) {
		for (int i = 0; i < 3; ++i) {
			if (tabId.equals(tabName[i]))
				customTabs[i].changeState(true);
			else
				customTabs[i].changeState(false);
		}
	}

	public void changeTab(int pos) {                             //change Fragment
		TabWidget tabWidget = tabHost.getTabWidget();
		int count = tabWidget.getChildCount();
		if (pos >= 0 && pos < count) {
			tabHost.setCurrentTab(pos);
		}
	}

	public void changeTab(int pos, int action) {
		TabWidget tabWidget = tabHost.getTabWidget();
		int count = tabWidget.getChildCount();
		if (pos >= 0 && pos < count) {
			notify_action = action;
			tabHost.setCurrentTab(pos);
		}
	}

	public void setCouponChange(boolean change) {
		customTabs[1].showHighlight(change);
	}

	private void setDefaultTab() {
		ft = fm.beginTransaction();
		fragments[0] = new TestFragment();
		ft.add(android.R.id.tabcontent, fragments[0], tabName[0]);
		setTabState(tabName[0]);

		//customTabs[1].showHighlight(PreferenceControl.getCouponChange());
		customTabs[2].showHighlight(PreferenceControl.getPageChange());

		ft.commit();
	}
	
	private void setDefaultTab2() {
		ft = fm.beginTransaction();
		fragments[2] = new DaybookFragment();
		ft.add(android.R.id.tabcontent, fragments[2], tabName[2]);
		setTabState(tabName[2]);

		//customTabs[1].showHighlight(PreferenceControl.getCouponChange());
		//customTabs[2].showHighlight(PreferenceControl.getPageChange());

		ft.commit();
	}
	
	public void setTabHostVisible(int Visibility){
		tabHost.getTabWidget().setVisibility(Visibility);
	}

	public class TabChangeListener implements TabHost.OnTabChangeListener {

		private String lastTabId;

		public TabChangeListener() {
			lastTabId = tabName[0];
		}

		@Override
		public void onTabChanged(String tabId) {
			if (lastTabId.equals(tabId))
				return;
			ft = fm.beginTransaction();
			int lastTabPos = 0, tabPos = 0;
			for (int i = 0; i < fragments.length; ++i) {
				if (lastTabId.equals(tabName[i]))
					lastTabPos = i;
				else if (tabId.equals(tabName[i]))
					tabPos = i;
			}
			if (Build.VERSION.SDK_INT >= 11)
				if (lastTabPos < tabPos)
					ft.setCustomAnimations(R.anim.animation_right_enter,
							R.anim.animation_left_exit);
				else
					ft.setCustomAnimations(R.anim.animation_left_enter,
							R.anim.animation_right_exit);
			
			
			LoadingDialogControl.show(MainActivity.this);
			//setTimers();
			if (tabId.equals(tabName[0])) {
				ClickLog.Log(ClickLogId.TAB_TEST);
				customTabs[1]
						.showHighlight(PreferenceControl.getCouponChange());
				customTabs[2].showHighlight(PreferenceControl.getRandomQustion());
			} else if (tabId.equals(tabName[1])) {
				ClickLog.Log(ClickLogId.TAB_STATISTIC);
				customTabs[1].showHighlight(false);
				customTabs[2].showHighlight(PreferenceControl.getRandomQustion());
			} else if (tabId.equals(tabName[2])) {
				ClickLog.Log(ClickLogId.TAB_DAYBOOK);
				customTabs[1]
						.showHighlight(PreferenceControl.getCouponChange());
				customTabs[2].showHighlight(false);
			}
			
			for (int i = 0; i < fragments.length; ++i) {
				if (fragments[i] != null)
					ft.detach(fragments[i]);
			}
			for (int i = 0; i < tabName.length; ++i) {
				if (tabId.equals(tabName[i])) {
					boolean newFragment = false;
					if (fragments[i] == null) {
						switch (i) {
						case 0:
							fragments[i] = new TestFragment();
							break;
						case 1:
							fragments[i] = new StatisticFragment();
							break;
						case 2:
							fragments[i] = new DaybookFragment();
							break;
						}
						newFragment = true;
					}
					if (notify_action == ACTION_RECORD) {
						Bundle data = new Bundle();
						data.putInt("action", notify_action);
						if (!newFragment) {
							ft.remove(fragments[i]);
							fragments[i] = new DaybookFragment();
							newFragment = true;
						}
						fragments[i].setArguments(data);
						notify_action = 0;
					} else if (notify_action == ACTION_QUESTIONNAIRE) {
						Bundle data = new Bundle();
						data.putInt("action", notify_action);
						if (!newFragment) {
							ft.remove(fragments[i]);
							fragments[i] = new StatisticFragment();
							newFragment = true;
						}
						fragments[i].setArguments(data);
						notify_action = 0;
					}
					if (newFragment)
						ft.add(android.R.id.tabcontent, fragments[i],
								tabName[i]);
					else
						ft.attach(fragments[i]);
					break;
				}
			}
			lastTabId = tabId;
			setTabState(tabId);
			ft.commit();
		}

	}

	private class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context context;

		public DummyTabFactory(Context context) {
			this.context = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(context);
			return v;
		}
	}

	public void enableTabAndClick(boolean enable) {
		enableTab(enable);
		setClickable(enable);
	}

	private void enableTab(boolean enable) {
		if (tabHost == null || tabHost.getTabWidget() == null)
			return;

		int count = tabHost.getTabWidget().getChildCount();
		for (int i = 0; i < count; ++i) {
			tabHost.getTabWidget().getChildAt(i).setClickable(enable);
		}
	}

	public void setClickable(boolean enable) {
		clickable = enable;
	}

	public boolean getClickable() {
		return clickable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!clickable)
			return super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			long cur_time = System.currentTimeMillis();
			if ((cur_time - latestClickTime) < 600 && doubleClickState) {
				doubleClickState = false;
				openOptionsMenu();
				latestClickTime = 0;
				return false;
			} else if ((cur_time - latestClickTime) >= 600 || !doubleClickState) {
				doubleClickState = true;
				latestClickTime = cur_time;
				return false;
			}
		}
		return super.onTouchEvent(event);
	}

	private class LoadingPageTimer extends CountDownTimer {
		public LoadingPageTimer() {
			super(3000, 3000);
		}
		@Override
		public void onFinish() {
			loading_page.setVisibility(View.INVISIBLE);
			enableTabAndClick(true);
		}
		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
	
	public boolean canUpdate() {
		return canUpdate;
	}

	public static MainActivity getMainActivity() {
		return mainActivity;
	}
	
	public void openOptionsMenu() {
		if (Build.VERSION.SDK_INT < 14) {
			super.openOptionsMenu();
			return;
		}
		if (menu == null)
			menu = new CustomMenu(this);
		if (!menu.isShowing() && clickable)
			menu.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0,
					0);
	}

	@Override
	public void closeOptionsMenu() {
		if (Build.VERSION.SDK_INT < 14) {
			super.closeOptionsMenu();
			return;
		}
		if (menu != null && menu.isShowing())
			menu.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	
	// add by 
	public void setNotePage(){
		ft = fm.beginTransaction();
		fragments[0] = new NoteFragment();
		ft.add(android.R.id.tabcontent, fragments[0], tabName[0]);
		setTabState(tabName[0]);
		ft.commit();
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent newIntent;
		switch (id) {
		case R.id.menu_about:
			newIntent = new Intent(this, AboutActivity.class);
			startActivity(newIntent);
			return true;
		case R.id.menu_setting:
			newIntent = new Intent(this, SettingActivity.class);
			startActivity(newIntent);
			//setNotePage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	public int getTabHeight() {
		View v = findViewById(android.R.id.tabs);
		return v.getBottom() - v.getTop();
	}
	
	
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (Build.VERSION.SDK_INT < 14) {
				return super.onKeyUp(keyCode, event);
			} else {
				if (menu != null && menu.isShowing())
					closeOptionsMenu();
				else
					openOptionsMenu();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {	
			if (menu != null && menu.isShowing()) {
				closeOptionsMenu();
				return true;
			} else {
				if (clickable) {
					if (tabHost.getCurrentTab() == 2 && fragments[2] != null
							&& fragments[2].isAdded()) {
						if(((DaybookFragment) fragments[2]).isNotePageShow){
							((DaybookFragment) fragments[2]).notePage.close();
							((DaybookFragment) fragments[2]).notePage.clear();
						}
						else
							super.onKeyUp(keyCode, event);
					}
					else
						super.onKeyUp(keyCode, event);
					return true;
				} else
					return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	

	private void setSensorCountDownTimer() {
		long lastTime = PreferenceControl.getLatestTestCompleteTime();
		long curTime = System.currentTimeMillis();
		boolean debug = PreferenceControl.isDebugMode();
		boolean testFail = PreferenceControl.isTestFail();

		long time = curTime - lastTime;
		long countTime = WAIT_RESULT_TIME - time;
		
		isRecovery = false;
		closeSensorCountDownTimer();
		
		sensorCountDownTimer = new SensorCountDownTimer(countTime);
		//sensorCountDownTimer = new SensorCountDownTimer( Math.min(waitTime, waitTime-time) );
		sensorCountDownTimer.start();
			
			
			/*
			else {
				isRecovery = true;
				long test_gap_time = TEST_GAP_DURATION - time;
				sensorCountDownTimer = new SensorCountDownTimer(Math.min(
						test_gap_time, TEST_GAP_DURATION));
			}*/
			
		
	}

	private void closeSensorCountDownTimer() {
		if (sensorCountDownTimer != null) {
			sensorCountDownTimer.cancel();
			sensorCountDownTimer = null;	
		}
	}


	private class SensorCountDownTimer extends CountDownTimer {

		public SensorCountDownTimer(long millisInFuture) {
			super(millisInFuture, 100);
				animation.start();
		}

		@Override
		public void onFinish() {
			//soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
			isRecovery = false;
			count_down_layout.setVisibility(View.GONE);
			
			//showResult();
			if (tabHost.getCurrentTab() == 0 && fragments[0] != null
					&& fragments[0].isAdded()) {
				soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
				((TestFragment) fragments[0]).msgBox.setResult();
				//((TestFragment) fragments[0]).setState(TestFragment.STATE_INIT);
				//((TestFragment) fragments[0]).enableStartButton(true);
			}
			else if(tabHost.getCurrentTab() == 1 && fragments[1] != null
					&& fragments[1].isAdded()){
				
				checkResultAddPoint();
				/*
				PreferenceControl.setPoint(2);
				CustomToast.generateToast(R.string.after_test_pass, 2);
				PreferenceControl.setCheckResult( false );*/
			}
			else{
				soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
				showResult();
			}
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long time = millisUntilFinished / 1000L;
			
			if(time > 60){
				count_down_text.setText(String.valueOf(time/60));
				count_down_label.setText("'");
				animationImg.setImageResource(R.anim.animation_clock2);
				animation = (AnimationDrawable) animationImg.getDrawable();
				animation.start();
			}
			else{
				count_down_text.setText(String.valueOf(time));//TODO:分跟秒要不一樣
				count_down_label.setText("\"");
				animationImg.setImageResource(R.anim.animation_clock);
				animation = (AnimationDrawable) animationImg.getDrawable();
				animation.start();
			}
			
			
			isRecovery = true;
			
			count_down_layout.setVisibility(View.VISIBLE);

		}
	}
	private void setResult(){
		if (tabHost.getCurrentTab() == 0 && fragments[0] != null
				&& fragments[0].isAdded()) {
			//soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
			((TestFragment) fragments[0]).msgBox.setResult();
			//((TestFragment) fragments[0]).setState(TestFragment.STATE_INIT);
			//((TestFragment) fragments[0]).enableStartButton(true);
		}
		else if(tabHost.getCurrentTab() == 1 && fragments[1] != null
				&& fragments[1].isAdded()){
			
			checkResultAddPoint();
			//CustomToast.generateToast(R.string.after_test_pass, 2);
			//PreferenceControl.setCheckResult( false );
		}
		else{
			//soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
			//showResult();
		}
	}
	
	public void checkResultAddPoint(){
		db = new DatabaseControl();
		int addScore=0;
		//NoteAdd noteAdd = ((TestFragment) fragments[0]).TDP.noteAdd;//TODO: set NoteAdd, get NoteAdd
		
		long timestamp = PreferenceControl.getUpdateDetectionTimestamp();
		int result = PreferenceControl.getTestResult();//TODO: check if no data
		int isFilled = PreferenceControl.getIsFilled();
				
		TestResult testResult = new TestResult(result, timestamp, "tmp_id",	1, isFilled, 0, 0);
		
		addScore = db.insertTestResult(testResult, false);
			
		Log.d(TAG,""+timestamp+" "+addScore);
		//PreferenceControl.setTestAddScore(addScore);
		
		
		
		PreferenceControl.setPoint(addScore);
		Log.d(TAG, "AddScore:"+addScore);		
		if (addScore == 0 && result == 1){ // TestFail & get no credit 
			CustomToast.generateToast(R.string.after_test_fail, -1);
		}
		else if(result == 1){
			CustomToast.generateToast(R.string.after_test_fail, addScore);
		}
		else
			CustomToast.generateToast(R.string.after_test_pass, addScore);
		
		
		
		
		PreferenceControl.setCheckResult( false );
	}

	private void showResult(){
		
		msgBox = new CheckResultDialog(mainLayout);
		msgBox.initialize();
		msgBox.show();
		
	}
	
	

	public void setTimers() {
		closeTimers();
		setSensorCountDownTimer();
		
	}

	public void closeTimers() {
		count_down_layout.setVisibility(View.GONE);
		closeSensorCountDownTimer();
	}


	
}
