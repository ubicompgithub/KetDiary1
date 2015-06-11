package com.ubicomp.ketdiary.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.db.DBTip;
import com.ubicomp.ketdiary.file.QuestionFile;
import com.ubicomp.ketdiary.system.PreferenceControl;


/**
 * Note after testing
 * @author Andy
 *
 */
public class NoteDialog2{
	
	private Activity activity;
	private NoteDialog2 noteFragment = this;
	private static final String TAG = "ADD_PAGE";
	
	private TestQuestionCaller testQuestionCaller;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private LinearLayout center_layout, title_layout, main_layout, bottom_layout;
	
	private RelativeLayout mainLayout;
	private View view;
	
	private ViewPager vPager;
	private ImageView iv_try, iv_smile, iv_urge,
					  iv_cry, iv_not_good;
	private ImageView iv_conflict, iv_social, iv_playing;
	private Spinner sp_date, sp_timeslot, sp_item;
	private Button bt_confirm, bt_cancel;
	private SeekBar impactSeekBar;
	private TextView text_self, text_other, text_item, text_impact, text_description, tv_knowdlege, tv_title;
	private EditText edtext, typetext;
	
	private String[] coping_msg;
	private int state;
	
	
	//write File
	private File mainDirectory;
	private long timestamp = 0;
	private QuestionFile questionFile; 
	
	//Listener
	private SpinnerXMLSelectedListener selectListener;
	private EndOnClickListener endOnClickListener;
	private GoResultOnClickListener goResultOnClickListener;
	private GoCopingToResultOnClickListener goCopingToResultOnClickListener;
	private MyOnPageChangeListener myOnPageChangeListener;
	
	private int type;
	private int items;
	private int impact;
	private String description;
	
	public static final int STATE_TEST = 0;
	public static final int STATE_NOTE = 1;
	public static final int STATE_COPE = 2;
	public static final int STATE_KNOW = 3;
	
	public NoteDialog2(TestQuestionCaller testQuestionCaller, RelativeLayout mainLayout){
		
		this.testQuestionCaller = testQuestionCaller;
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		
		coping_msg = context.getResources().getStringArray(R.array.coping_list);
		//activity = this.getActivity();
	    
		//view = inflater.inflate(R.layout.fragment_note, container, false);
		selectListener = new SpinnerXMLSelectedListener();
		endOnClickListener = new EndOnClickListener();
		goResultOnClickListener = new GoResultOnClickListener();
		goCopingToResultOnClickListener = new GoCopingToResultOnClickListener();
				
	    setting();
	    mainLayout.addView(boxLayout);
	    
	
	}
	
	protected void setting() {
		
		boxLayout = (RelativeLayout) inflater.inflate(
				R.layout.note, null);
		boxLayout.setVisibility(View.INVISIBLE);
		title_layout = (LinearLayout) boxLayout.findViewById(R.id.note_title_layout);
		main_layout = (LinearLayout) boxLayout.findViewById(R.id.note_main_layout);
		bottom_layout = (LinearLayout) boxLayout.findViewById(R.id.note_bottom_layout);
		
		
		//
		
		//Title View
		View title = BarButtonGenerator.createAddNoteView(selectListener);
		title_layout.addView(title);
		
		
		
		center_layout = (LinearLayout) inflater.inflate(
				R.layout.note_main2, null);
		text_self = (TextView)center_layout.findViewById(R.id.text_self);
	    text_other = (TextView)center_layout.findViewById(R.id.text_other);
	    
		text_self.setTypeface(Typefaces.getWordTypeface());
	    text_other.setTypeface(Typefaces.getWordTypeface());
	    
		text_self.setTextColor(context.getResources().getColor(R.color.blue));    
	    text_self.setOnClickListener(new MyClickListener(0));
	    text_other.setOnClickListener(new MyClickListener(1));
	    
		initTypePager();
			
		//Description
		LinearLayout type_layout = (LinearLayout) inflater.inflate(
				R.layout.bar_description, null);
		
		TextView type_title = (TextView)type_layout.findViewById(R.id.description_title);
		type_title.setText("生活項目：");
		type_title.setTypeface(Typefaces.getWordTypefaceBold());
		typetext = (EditText)type_layout.findViewById(R.id.description_content);
		typetext.setEnabled(false);
		
		//Spinner
		LinearLayout spinner_layout = (LinearLayout) inflater.inflate(
				R.layout.bar_spinner, null);
			
		sp_item = (Spinner)spinner_layout.findViewById(R.id.spinner_content);
		SetItem(sp_item, R.array.item_select);
		TextView spin_title = (TextView)spinner_layout.findViewById(R.id.spinner_title);
		spin_title.setText("詳細事件：");
		spin_title.setTypeface(Typefaces.getWordTypefaceBold());
		
		
		//Impact
		LinearLayout impact_layout = (LinearLayout) inflater.inflate(
				R.layout.bar_impact, null);
		impactSeekBar=(SeekBar)impact_layout.findViewById(R.id.impact_seek_bar);
		TextView impact_title = (TextView)impact_layout.findViewById(R.id.impact_title);
		impact_title.setTypeface(Typefaces.getWordTypefaceBold());
		
		//Description
		LinearLayout discription_layout = (LinearLayout) inflater.inflate(
				R.layout.bar_description, null);
		
		TextView dec_title = (TextView)discription_layout.findViewById(R.id.description_title);
		dec_title.setText("內容簡述：");
		dec_title.setTypeface(Typefaces.getWordTypefaceBold());
		edtext = (EditText)discription_layout.findViewById(R.id.description_content);
		
		//Bottom View
		View bottom = BarButtonGenerator.createTwoButtonView(R.string.cancel, R.string.ok, endOnClickListener, endOnClickListener);
		
		main_layout.addView(center_layout);
		main_layout.addView(type_layout);
		main_layout.addView(spinner_layout);
		main_layout.addView(impact_layout);
		main_layout.addView(discription_layout);
		
		bottom_layout.addView(bottom);
		//main_layout.addView(bottom);
	}
	
	public void copingSetting(){
		//boxLayout = (RelativeLayout) inflater.inflate(R.layout.activity_qtip, null);
		//mainLayout.addView(boxLayout);
		state = STATE_COPE;
		PreferenceControl.setAfterTestState(STATE_COPE);
		
		title_layout.removeAllViews();
		main_layout.removeAllViews();
		bottom_layout.removeAllViews();
		
		center_layout = (LinearLayout) inflater.inflate(R.layout.knowledge, null);
		tv_knowdlege = (TextView)center_layout.findViewById(R.id.qtip_tv_tips);
		tv_title = (TextView)center_layout.findViewById(R.id.text_knowing_title);
		
		tv_title.setText(R.string.coping_page);
		
		Random rand = new Random();
		int idx = rand.nextInt(coping_msg.length);
		tv_knowdlege.setText(coping_msg[idx]);
		main_layout.addView(center_layout);
		
		View bottom = BarButtonGenerator.createOneButtonView( R.string.Iknow, endOnClickListener );
		bottom_layout.addView(bottom);
		
	}
	
	public void copingSettingToResult(){
		//boxLayout = (RelativeLayout) inflater.inflate(R.layout.activity_qtip, null);
		//mainLayout.addView(boxLayout);
		state = STATE_COPE;
		PreferenceControl.setAfterTestState(STATE_COPE);
		
		title_layout.removeAllViews();
		main_layout.removeAllViews();
		bottom_layout.removeAllViews();
		
		center_layout = (LinearLayout) inflater.inflate(R.layout.knowledge, null);
		tv_knowdlege = (TextView)center_layout.findViewById(R.id.qtip_tv_tips);
		tv_title = (TextView)center_layout.findViewById(R.id.text_knowing_title);
		
		tv_title.setText(R.string.coping_page);
		
		Random rand = new Random();
		int idx = rand.nextInt(coping_msg.length);
		tv_knowdlege.setText(coping_msg[idx]);
		main_layout.addView(center_layout);
		
		View bottom = BarButtonGenerator.createOneButtonView( R.string.go_result, goResultOnClickListener );
		bottom_layout.addView(bottom);
		
	}
	
	public void knowingSetting(){
		state = STATE_KNOW;
		PreferenceControl.setAfterTestState(STATE_KNOW);
		MainActivity.getMainActivity().enableTabAndClick(true);
		
		title_layout.removeAllViews();
		main_layout.removeAllViews();
		bottom_layout.removeAllViews();
		
		View bottom = BarButtonGenerator.createTwoButtonView(R.string.last, R.string.next, endOnClickListener, endOnClickListener);
		bottom_layout.addView(bottom);
		//main_layout.removeView(center_layout);
		center_layout = (LinearLayout) inflater.inflate(R.layout.knowledge, null);
		tv_knowdlege = (TextView)center_layout.findViewById(R.id.qtip_tv_tips);
		tv_knowdlege.setText(DBTip.inst.getTip());
		main_layout.addView(center_layout);
		
		main_layout.getLayoutParams().height = center_layout.getLayoutParams().height;
	}
	
	
	public void setResult(){
		bottom_layout.removeAllViews();
		//Toast.makeText(context, "倒數結束", Toast.LENGTH_SHORT).show();
		
		if(state == STATE_NOTE){
			Toast.makeText(context, "請完成新增記事以查看檢測結果", Toast.LENGTH_SHORT).show();
			View bottom = BarButtonGenerator.createTwoButtonView(R.string.cancel, R.string.ok, goCopingToResultOnClickListener, goCopingToResultOnClickListener);
			bottom_layout.addView(bottom);
		}
		else if(state == STATE_COPE){
			Toast.makeText(context, "請點選以查看檢測結果", Toast.LENGTH_SHORT).show();
			View bottom = BarButtonGenerator.createOneButtonView( R.string.go_result, goResultOnClickListener );
			bottom_layout.addView(bottom);
		}
		else if(state == STATE_KNOW){
			Toast.makeText(context, "請點選以查看檢測結果", Toast.LENGTH_SHORT).show();
			View bottom = BarButtonGenerator.createOneButtonView( R.string.go_result, goResultOnClickListener );
			bottom_layout.addView(bottom);
		}
	
	}
	
	public void testSetting(){
		
		title_layout.removeAllViews();
		main_layout.removeAllViews();
		bottom_layout.removeAllViews();
		

		//main_layout.removeView(center_layout);
		center_layout = (LinearLayout) inflater.inflate(R.layout.bar_impact, null);
		//tv_knowdlege = (TextView)center_layout.findViewById(R.id.qtip_tv_tips);
		//tv_knowdlege.setText(DBTip.inst.getTip());
		main_layout.addView(center_layout);
		
		//main_layout.getLayoutParams().height = center_layout.getLayoutParams().height;
	}
	
	/** Initialize the dialog */
	public void initialize() {
	}
	
	/** show the dialog */
	public void show() {
		state = STATE_NOTE;
		PreferenceControl.setAfterTestState(STATE_NOTE);
		//questionLayout.setVisibility(View.VISIBLE);
		
		MainActivity.getMainActivity().enableTabAndClick(false);
		boxLayout.setVisibility(View.VISIBLE);
		
		/*enableSend(false);
		PreferenceControl.setTestSuccess();
		help.setText("");
		questionLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		send.setOnClickListener(endListener);
		notSend.setOnClickListener(cancelListener);

		gpsRadioGroup.check(R.id.msg_gps_no);
		enableSend(false);
		MainActivity.getMainActivity().enableTabAndClick(false);*/
	}
	
	/** remove the dialog and release the resources */
	public void clear() {
		if (mainLayout != null && boxLayout != null
				&& boxLayout.getParent() != null
				&& boxLayout.getParent().equals(mainLayout))
			mainLayout.removeView(boxLayout);
	}
	
	/** close the dialog */
	public void close() {
		if (boxLayout != null)
			boxLayout.setVisibility(View.INVISIBLE);
	}
	/*
	private void setStorage() {
		File dir = MainStorage.getMainStorageDirectory();

		mainDirectory = new File(dir, String.valueOf(timestamp));
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}
		questionFile = new QuestionFile(mainDirectory);
	}
	
	public void writeQuestionFile(int type, int items, int impact) {
		questionFile.write(type, items, impact);
	}*/
	
	
	/** 設定Spinner的Item */
	private void SetItem(Spinner sp, int array){
		//ArrayAdapter adapter = ArrayAdapter.createFromResource(context, array, android.R.layout.simple_spinner_item);
		
		ArrayAdapter adapter = ArrayAdapter.createFromResource(context, array, R.layout.my_spinner);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strs );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
		//sp.setPrompt("負面情緒");
        
        //sp.setVisibility(View.VISIBLE);  
       // sp.performClick();
	}
	
	private class SpinnerXMLSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
			items = 100*type + arg2;
			Log.d(TAG, items+"");
			//Toast.makeText(getContext(), "你選的是"+items.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            //view2.setText("你使用什么样的手机："+adapter2.getItem(arg2));  
        }  
  
        public void onNothingSelected(AdapterView<?> arg0) {  
        	type = 0;
        }  
	}
	class MyOnLongClickListener implements OnLongClickListener{
	    public boolean onLongClick(View v){
	    	//dialog.show();
	    	return true;
	    }
	}
	
	
	//把所選取的結果送出 
	class EndOnClickListener implements View.OnClickListener{
		public void onClick(View v){
			
			
			if(state == STATE_NOTE){
				impact = impactSeekBar.getProgress();
				testQuestionCaller.writeQuestionFile(type, items, impact, edtext.getText().toString());
			
				Log.d(TAG, items+"\t"+impact);
			//questionLayout.setVisibility(View.GONE);
			//clear();
				
				//testSetting(); //For Test
				
				copingSetting();
			//questionFile.write(0, 0, 0);
			//startActivity(new Intent(that, EventCopeSkillActivity.class));
			}
			else if(state == STATE_COPE){
				knowingSetting();
			}
			else if(state == STATE_KNOW){
				tv_knowdlege.setText(DBTip.inst.getTip());
			}
	    }
	}
	
	class GoResultOnClickListener implements View.OnClickListener{
		public void onClick(View v){

			MainActivity.getMainActivity().changeTab(1);
			
	    }
	}
	
	class GoCopingToResultOnClickListener implements View.OnClickListener{
		public void onClick(View v){
			
			
			if(state == STATE_NOTE){
				impact = impactSeekBar.getProgress();
				testQuestionCaller.writeQuestionFile(type, items, impact, edtext.getText().toString());
			
				Log.d(TAG, items+"\t"+impact);

				copingSettingToResult();
			}

	    }
	}
	
	public void onBackPressed() {
	    //super.onBackPressed(); 
	    //startActivity(new Intent(that, EventCopeSkillActivity.class));
	    // Do extra stuff here
	}
	
	private void initTypePager(){
	    vPager = (ViewPager) center_layout.findViewById(R.id.viewpager);
	    vPager.setOnPageChangeListener(new MyOnPageChangeListener());
	    
		//LayoutInflater li = LayoutInflater.from(context); //getLayoutInflater();
		ArrayList<View> aList = new ArrayList<View>();
		aList.add(inflater.inflate(R.layout.typepager_self, null));
		aList.add(inflater.inflate(R.layout.typepager_other, null));
		TypePageAdapter mAdapter = new TypePageAdapter(aList);		
		vPager.setAdapter(mAdapter);
	}
	
	
	public class TypePageAdapter extends PagerAdapter{
		
		private ArrayList<View> viewLists;	

		public TypePageAdapter() {}	
		public TypePageAdapter(ArrayList<View> viewLists)
		{
			super();
			this.viewLists = viewLists;
		}
		
		@Override
		public int getCount() {
			return viewLists.size();
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		private void resetView(){
			iv_smile.setImageResource(R.drawable.emoji4);
			iv_not_good.setImageResource(R.drawable.emoji2);
			iv_urge.setImageResource(R.drawable.emoji3);
			iv_cry.setImageResource(R.drawable.emoji5);
			iv_try.setImageResource(R.drawable.emoji1);
			
			iv_social.setImageResource(R.drawable.others_emoji1);
			iv_playing.setImageResource(R.drawable.others_emoji2);
			iv_conflict.setImageResource(R.drawable.others_emoji3);
		}
		
		/** 初始化Type*/
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewLists.get(position));
			if(position == 0){
				iv_smile = (ImageView) center_layout.findViewById(R.id.vts_iv_smile);
				iv_not_good = (ImageView) center_layout.findViewById(R.id.vts_iv_not_good);
				iv_urge = (ImageView) center_layout.findViewById(R.id.vts_iv_urge);
				iv_cry = (ImageView) center_layout.findViewById(R.id.vts_iv_cry);
				iv_try = (ImageView) center_layout.findViewById(R.id.vts_iv_try);
				
				iv_smile.setOnClickListener(SelectItem);
				iv_not_good.setOnClickListener(SelectItem);
				iv_urge.setOnClickListener(SelectItem);
				iv_cry.setOnClickListener(SelectItem);
				iv_try.setOnClickListener(SelectItem);
			}else{
				iv_social = (ImageView) center_layout.findViewById(R.id.vts_iv_social);
				iv_playing = (ImageView) center_layout.findViewById(R.id.vts_iv_playing);
				iv_conflict = (ImageView) center_layout.findViewById(R.id.vts_iv_conflict);
			
				iv_social.setOnClickListener(SelectItem);
				iv_playing.setOnClickListener(SelectItem);
				iv_conflict.setOnClickListener(SelectItem);
			}
			Log.d("FORTEST", "aabb");
			
			return viewLists.get(position);	
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewLists.get(position));
		}
		
		View.OnClickListener SelectItem = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        switch(v.getId()){
		        
		        case R.id.vts_iv_cry:
		        	resetView();
		        	iv_cry.setImageResource(R.drawable.emoji5_pressed);
		        	typetext.setHint(R.string.note_negative);
		        	
	        		SetItem(sp_item,R.array.note_negative);
	        		sp_item.performClick();
	        		type = 1;
	        		break;
		        case R.id.vts_iv_not_good:
		        	resetView();
		        	iv_not_good.setImageResource(R.drawable.emoji2_pressed);
		        	typetext.setHint(R.string.note_notgood);
		        	
		        	SetItem(sp_item,R.array.note_notgood);
		        	sp_item.performClick();
		        	type = 2;
			        break;
		        case R.id.vts_iv_smile:
		        	resetView();
		        	iv_smile.setImageResource(R.drawable.emoji4_pressed);
		        	typetext.setHint(R.string.note_positive);
		        	
		        	SetItem(sp_item, R.array.note_positive);
		        	sp_item.performClick();
		        	type = 3;
		        	break;
		        case R.id.vts_iv_try:
		        	resetView();
		        	iv_try.setImageResource(R.drawable.emoji1_pressed);
		        	typetext.setHint(R.string.note_selftest);
		        	
		        	SetItem(sp_item,R.array.note_selftest);
		        	sp_item.performClick();
		        	type = 4; 
		        	break;
		        case R.id.vts_iv_urge:
		        	resetView();
		        	iv_urge.setImageResource(R.drawable.emoji3_pressed);
		        	typetext.setHint(R.string.note_temptation);
		        	
		        	SetItem(sp_item,R.array.note_temptation);
		        	sp_item.performClick();
		        	type = 5;
		        	break;
		        case R.id.vts_iv_playing:
		        	resetView();
		        	iv_playing.setImageResource(R.drawable.others_emoji2_pressed);
		        	
		        	SetItem(sp_item,R.array.note_play);
		        	sp_item.performClick();
		        	type = 6;
		        	break;
		        case R.id.vts_iv_social:
		        	resetView();
		        	iv_social.setImageResource(R.drawable.others_emoji1_oressed);
		        	
		        	SetItem(sp_item,R.array.note_social);
		        	sp_item.performClick();
		        	type = 7;
		        	break;
		        case R.id.vts_iv_conflict:
		        	resetView();
		        	iv_conflict.setImageResource(R.drawable.others_emoji3_pressed);
		        	
		        	SetItem(sp_item,R.array.note_conflict);
		        	sp_item.performClick();
		        	type = 8;
		        	break;
		        	
		        }
			}
		};
	}
	public class MyClickListener implements OnClickListener
	{
		private int index = 0;
		public MyClickListener(int i){
			index = i;
		}
		
		@Override
		public void onClick(View arg0) {
			vPager.setCurrentItem(index);
			switch(index){
			case 0:
				text_self.setTextColor(context.getResources().getColor(R.color.blue));
				text_other.setTextColor(context.getResources().getColor(R.color.text_gray3));
				break;
			case 1:
				text_self.setTextColor(context.getResources().getColor(R.color.text_gray3));
				text_other.setTextColor(context.getResources().getColor(R.color.blue));
				break;

			}
		}
		
	}
	
	
	//監聽頁面切換時間,主要做的是動畫處理,就是移動條的移動
		public class MyOnPageChangeListener implements OnPageChangeListener {


			@Override
			public void onPageSelected(int index) {

				switch (index) {
				case 0:
					text_self.setTextColor(context.getResources().getColor(R.color.blue));
					text_other.setTextColor(context.getResources().getColor(R.color.text_gray3));
					break;
				case 1:
					text_self.setTextColor(context.getResources().getColor(R.color.text_gray3));
					text_other.setTextColor(context.getResources().getColor(R.color.blue));
					break;
				}

			}
			@Override
			public void onPageScrollStateChanged(int arg0) {}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
		
		}

	
}
