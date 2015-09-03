package com.ubicomp.ketdiary;

import java.util.Random;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.download.CassetteIDCollector;
import com.ubicomp.ketdiary.data.structure.Cassette;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.dialog.CheckResultDialog;
import com.ubicomp.ketdiary.dialog.NoteDialog4;
import com.ubicomp.ketdiary.main.fragment.DaybookFragment;
import com.ubicomp.ketdiary.main.fragment.StatisticFragment;
import com.ubicomp.ketdiary.main.fragment.TestFragment2;
import com.ubicomp.ketdiary.system.Config;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.check.StartDateCheck;
import com.ubicomp.ketdiary.system.clicklog.ClickLog;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;
import com.ubicomp.ketdiary.test.color.ImageDetectionValidate;
import com.ubicomp.ketdiary.ui.CustomMenu;
import com.ubicomp.ketdiary.ui.CustomTab;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.LoadingDialogControl;
import com.ubicomp.ketdiary.ui.ScreenSize;
import com.ubicomp.ketdiary.ui.Typefaces;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

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
	public Handler resultFailHandler = new ResultFailHandler();
	//private UpdateCassetteTask updateTask;
	
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
	private boolean testFail = false;
	private boolean resultServiceRun = false;
	
	private int changeClock=0;
	
	public static long WAIT_RESULT_TIME = PreferenceControl.getAfterCountDown() * 1000;
	public static final int ACTION_RECORD = 1;
	public static final int ACTION_QUESTIONNAIRE = 2;
	
	private ImageDetectionValidate imageDetectionValidate;
	
	static {
       System.loadLibrary("opencv_java");
    }
	
	private DatabaseControl db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//OpenCVLoader.initDebug();
		//OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    
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
		
		db = new DatabaseControl();

		long prev_check = PreferenceControl.getOpenAppTimestamp(); //check didn't do test day since last check
		int noTestDay = db.noTestDayCount(prev_check, System.currentTimeMillis());
				
		
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int mMemoryClass = am.getMemoryClass();
		long mLargeMemoryClass = am.getLargeMemoryClass();
		Log.i(TAG, "Memory: " + mMemoryClass + " Larger Memory: " + mLargeMemoryClass);
		
		loadingHandler.sendEmptyMessage(0);

		loadingPageTimer = new LoadingPageTimer();
		loadingPageTimer.start();

	}
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:{
                    Log.i(TAG, "OpenCV loaded successfully");
                    //testStripDetection = new TestStripDetection3();
                } break;
                default:{
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

	private class LoadingHandler extends Handler {
		public void handleMessage(Message msg) {
			if (PreferenceControl.checkFirstUID())
				PreferenceControl.defaultSetting();
			
			//For test Sevice
			//Intent startIntent =  new  Intent( mainActivity , ResultService2.class );  
			//startService(startIntent); 
            //
			
//			testStripDetection = new TestStripDetection3();
//			testStripDetection.sendEmptyMessage(0);
//			testStripDetection = new TestStripDetection2();
//			testStripDetection.testOpencv();

			//testStripDetection.testOpencv();

//			File mainStorageDir = MainStorage.getMainStorageDirectory();	    	
//	        Mat matOrigin = Imgcodecs.imread(mainStorageDir.getPath() + File.separator + "Avon.jpg");
//	        Log.d(TAG, "TEST");
//	        Mat matROI = matOrigin.submat(60, 160, 80, 240);
//	        Mat matClone = matROI.clone();
//	        Imgproc.cvtColor(matROI, matROI, Imgproc.COLOR_RGB2GRAY, 0);
//	        
//	        Log.d(TAG, "TEST");
//	        
//	        Mat matFilter = new Mat();
//	        Mat matCanny = new Mat();
//	        Mat matLines = new Mat();
			//TODO: Test, 
			
//			imageDetectionValidate = new ImageDetectionValidate();
//			imageDetectionValidate.roiDetectionOnWhite();
//			imageDetectionValidate.testStripDetection();
			//TODO:
			
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
			
			
			
			//Random Question setting
			if(!db.randomQuestionDone() && !PreferenceControl.getRandomQustion()){
			
			//if(!PreferenceControl.getRandomQustion()){
				Random rand = new Random();
				int prob = rand.nextInt(100);
				if( prob >= 50 ){
					PreferenceControl.setRandomQustion(true);
					PreferenceControl.setRandomTs(System.currentTimeMillis());
				}
			}
			else if(PreferenceControl.getRandomDiff(System.currentTimeMillis())){
					PreferenceControl.setRandomQustion(false);
			}

			
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
		
		UpdateCassetteTask updateTask = new UpdateCassetteTask();
		updateTask.execute();
		
		super.onStart();
	}

	protected void onResume() {
		super.onResume();
		if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
			
		/*if (LockCheck.check()) {
			Intent lock_intent = new Intent(this, LockedActivity.class);
			startActivity(lock_intent);
			finish();
			return;
		}*/
		//showResult();
		resultServiceRun = PreferenceControl.getResultServiceRun();
		testFail = PreferenceControl.isTestFail();
		
		WAIT_RESULT_TIME = PreferenceControl.getAfterCountDown() * 1000;
		
		PreferenceControl.setInApp(true);		
		boolean inApp = PreferenceControl.getInApp();	
		Log.d("InApp",String.valueOf(inApp)+"WAIT_TIME: "+ WAIT_RESULT_TIME);
		
		
		boolean serviceRun = PreferenceControl.getResultServiceRun();
		
		long curTime = System.currentTimeMillis();
		long testTime = PreferenceControl.getLatestTestCompleteTime();
		long pastTime = curTime - testTime;
		long countTime = ResultService3.spentTime;
		Log.d(TAG, "RestTime: " + countTime);
		
		int state = PreferenceControl.getAfterTestState();
		Log.d("InApp",String.valueOf(state));
		
		if(state == NoteDialog4.STATE_NOTE || state == NoteDialog4.STATE_COPE){
			enableTabAndClick(false);
			//Log.d("InApp","Disable click");
		}
		else{
			clickable = true;
		}
		
		if(!resultServiceRun){
			enableTabAndClick(true);
		}
		if(PreferenceControl.getCheckResult() && countTime > 0)
			setTimers2();
		else if(PreferenceControl.getCheckResult() && countTime <= 0){
			if(testFail){
				showResultFail();
			}
			else{
				setResult();
			}
		}
		else{
			enableTabAndClick(true);
		}
		
		Log.d("InApp",String.valueOf(clickable));
		
		
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
		fragments[0] = new TestFragment2();
		ft.add(android.R.id.tabcontent, fragments[0], tabName[0]);
		setTabState(tabName[0]);

		customTabs[1].showHighlight(PreferenceControl.getCouponChange());
		customTabs[2].showHighlight(PreferenceControl.getRandomQustion());

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
							fragments[i] = new TestFragment2();
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
			ClickLog.Log(ClickLogId.MAIN_BACK_PRESS);
			if (menu != null && menu.isShowing()) {
				closeOptionsMenu();
				return true;
			} else {
				if (clickable) {
					if (tabHost.getCurrentTab() == 2 && fragments[2] != null  //讓第三頁的新增記事按back可以關掉
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
		//long countTime = ResultService3.spentTime;
		Log.i(TAG, "sensorCountDown: "+countTime);
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
	
	private void setSensorCountDownTimer2() {
		long lastTime = PreferenceControl.getLatestTestCompleteTime();
		long curTime = System.currentTimeMillis();
		boolean debug = PreferenceControl.isDebugMode();
		boolean testFail = PreferenceControl.isTestFail();

		long time = curTime - lastTime;
		//long countTime = WAIT_RESULT_TIME - time;
		long countTime = ResultService3.spentTime;
		Log.i(TAG, "sensorCountDown: "+countTime);
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
			testFail = PreferenceControl.isTestFail();
			isRecovery = false;
			count_down_layout.setVisibility(View.GONE);
			
			
			if(testFail){
				showResultFail();
			}
			else{
				if (tabHost.getCurrentTab() == 0 && fragments[0] != null
						&& fragments[0].isAdded()) {
					soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
					((TestFragment2) fragments[0]).msgBox.setResult();
					//((TestFragment) fragments[0]).setState(TestFragment.STATE_INIT);
					//((TestFragment) fragments[0]).enableStartButton(true);
				}
				else if(tabHost.getCurrentTab() == 1 && fragments[1] != null
						&& fragments[1].isAdded()){
					
					ft = fm.beginTransaction(); //TODO: add by Andy 8/4
					ft.detach(fragments[1]);
					ft.attach(fragments[1]);
					ft.commit();
					
					checkResultAddPoint();
				}
				else{
					soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
					showResult();
				}
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
				count_down_text.setText(String.valueOf(time));
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
			((TestFragment2) fragments[0]).msgBox.setResult();
		}
		else if(tabHost.getCurrentTab() == 1 && fragments[1] != null
				&& fragments[1].isAdded()){			
			checkResultAddPoint();
		}
		else{
			//soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
			//showResult();
		}
	}
	
	public void checkResultAddPoint(){
		
		if( getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setTabHostVisible(View.VISIBLE);
		}
		
		int addScore=0;
		//NoteAdd noteAdd = ((TestFragment) fragments[0]).TDP.noteAdd;//TODO: set NoteAdd, get NoteAdd
		
		PreferenceControl.setCheckResult(false);
		
		long timestamp = PreferenceControl.getUpdateDetectionTimestamp();
		int result = PreferenceControl.getTestResult();//TODO: check if no data
		int isFilled = PreferenceControl.getIsFilled();
		TestDetail testDetail = db.getLatestTestDetail();
		String cassetteId = testDetail.getCassetteId();
		if(cassetteId == null)
			cassetteId = "CT_Test";
		TestResult testResult = new TestResult(result, timestamp, cassetteId,	1, isFilled, 0, 0);
		
		if(db.getTodayTestCount() == 1){
			addScore = db.insertTestResult(testResult, true);
		}
		else{
			addScore = db.insertTestResult(testResult, false);
		}
			
		Log.d(TAG,""+timestamp+" "+addScore);
		//PreferenceControl.setTestAddScore(addScore);

		PreferenceControl.setPoint(addScore);
		Log.d(TAG, "AddScore:"+addScore);
		int addPos = 0;
		if (addScore == 0 && result == 1){ // TestFail & get no credit 
			CustomToast.generateToast(R.string.after_test_fail, -1);
			addPos = -1;
		}
		else if(result == 1){
			CustomToast.generateToast(R.string.after_test_fail, addScore);
			addPos = -1;
		}
		else{
			CustomToast.generateToast(R.string.after_test_pass, addScore);
			addPos = 1;
		}
		
		if(StartDateCheck.afterStartDate())
			PreferenceControl.setPosition(addPos);
		
		if(PreferenceControl.getPowerNotEnough() == 1){
			generateDialog("電量不足，請將檢測器充電");
		}
		
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0); //做完檢測才把Notification關掉
		
		//PreferenceControl.setCheckResult( false );
	}

	private void showResult(){
		
		msgBox = new CheckResultDialog(mainLayout);
		msgBox.initialize();
		msgBox.show();
		
	}
	
	private void showResultFail(){
		
		PreferenceControl.setCheckResult(false);
		msgBox = new CheckResultDialog(mainLayout);
		msgBox.initialize();
		msgBox.show();
		PreferenceControl.setAfterTestState(NoteDialog4.STATE_TEST);
		if (tabHost.getCurrentTab() == 0 && fragments[0] != null
				&& fragments[0].isAdded()) {
			ft = fm.beginTransaction();
			ft.detach(fragments[0]);
			ft.attach(fragments[0]);
			ft.commit();
		}
		else if(tabHost.getCurrentTab() == 1 && fragments[1] != null
				&& fragments[1].isAdded()){						
		}
		else{
		}
	}
	
	public void setResultFail(){
		
		closeTimers();
		showResultFail();
		//animation.stop();
		//PreferenceControl.setAfterTestState(NoteDialog3.STATE_TEST);
		//PreferenceControl.setCheckResult(false);
		
	}
	private class ResultFailHandler extends Handler {
		public void handleMessage(Message msg) {
			setResultFail();
		}
	}
	
	public void setResultSuccess(){
		
		closeTimers();
		setResult();
		//showResultFail();
		//animation.stop();
		//PreferenceControl.setAfterTestState(NoteDialog3.STATE_TEST);
		//PreferenceControl.setCheckResult(false);
		
	}
	
	public void setTimers() {
		closeTimers();
		setSensorCountDownTimer();
		
	}
	
	public void setTimers2() {
		closeTimers();
		setSensorCountDownTimer2();
		
	}

	public void closeTimers() {
		count_down_layout.setVisibility(View.GONE);
		closeSensorCountDownTimer();
	}
	
	private Cassette[] cassettes;
	private CassetteIDCollector cassetteCollector;

	private class UpdateCassetteTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			cassetteCollector = new CassetteIDCollector(mainActivity);
			cassettes = cassetteCollector.update();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (cassettes == null ) {
				return;
			}

			db.clearCassette(); //delete table and Insert table from db
			
			for (int i = 0; i < cassettes.length; ++i)
				db.updateCassette(cassettes[i]);
		}

	}
	private void generateDialog(String textResource){
		// Create custom dialog object
        final Dialog dialog = new Dialog(this);
        
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        dialog.setContentView(R.layout.dialog);
        TextView dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogText.setText(textResource);
        dialogText.setTextColor(getResources().getColor(R.color.dark_gray));
        
        dialog.show();
         
        TextView dialogOKButton = (TextView) dialog.findViewById(R.id.ok_button);
        dialogOKButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                
            }
        });
        //dialogOKButton.setOnClickListener(new EndOnClickListener() );
	}

	
}
