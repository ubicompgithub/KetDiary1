package com.ubicomp.ketdiary.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.check.TimeBlock;
import com.ubicomp.ketdiary.file.QuestionFile;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.Typefaces;


/**
 * Note after testing
 * @author Andy
 *
 */
public class AddNoteDialog2 implements ChooseItemCaller{
	
	private Activity activity;
	private AddNoteDialog2 addNoteDialog = this;
	private static final String TAG = "ADD_PAGE";
	
	private TestQuestionCaller2 testQuestionCaller;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private LinearLayout center_layout, title_layout, main_layout, bottom_layout, title, date_layout, timeslot_layout;
	
	private RelativeLayout mainLayout;
	private View view;
	
	private ViewPager vPager;
	private ImageView iv_try, iv_smile, iv_urge,
					  iv_cry, iv_not_good;
	private ImageView iv_conflict, iv_social, iv_playing;
	private Spinner sp_date, sp_timeslot;// sp_item;
	private Button bt_confirm, bt_cancel;
	private SeekBar impactSeekBar;
	private TextView text_self, text_other, text_item, text_impact, text_description,
	     tv_knowdlege, tv_title, note_title, sp_content, date_txt, timeslot_txt, title_txt;
	
	private EditText edtext, typetext;
	private ListView listView;
	
	private String[] coping_msg;
	private String[] knowing_msg;
	private static int knowing_index=-1;
	
	private int state;
	private ChooseItemDialog chooseBox;
	
	
	//write File
	private File mainDirectory;
	private long timestamp = 0;
	private QuestionFile questionFile; 
	
	//Listener

	private EndOnClickListener endOnClickListener;
	private GoResultOnClickListener goResultOnClickListener;
	private GoCopingToResultOnClickListener goCopingToResultOnClickListener;
	private MyOnPageChangeListener myOnPageChangeListener;
	
	private int day=1;
	private int timeslot=1; //TODO: default
	private int type;
	private int items;
	private int impact;
	private String description;
	private boolean viewshow = false;
	
	private ScrollView sv;
	
	public static final int STATE_TEST = 0;
	public static final int STATE_NOTE = 1;
	public static final int STATE_COPE = 2;
	public static final int STATE_KNOW = 3;
	
	private static final String[] Timeslot_str = {"上午", "下午", "晚上"};
	private static final String[] Date_str = {"今天", "昨天", "前天"};
	
	private static Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
	private static Typeface wordTypeface = Typefaces.getWordTypeface();
	
	public AddNoteDialog2(TestQuestionCaller2 testQuestionCaller, RelativeLayout mainLayout){
		
		this.testQuestionCaller = testQuestionCaller;
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		
		coping_msg = context.getResources().getStringArray(R.array.coping_list);
		
	    
		//view = inflater.inflate(R.layout.fragment_note, container, false);
		endOnClickListener = new EndOnClickListener();
		goResultOnClickListener = new GoResultOnClickListener();
		goCopingToResultOnClickListener = new GoCopingToResultOnClickListener();
				
	    
	    
	
	}
	
	protected void setting() {
		
		day = 0;
		timeslot = 0;
		type = 0;
		items = 0;
		impact = 0 ;
		description = "";
		
		boxLayout = (RelativeLayout) inflater.inflate(
				R.layout.note, null);
		boxLayout.setVisibility(View.INVISIBLE);
		title_layout = (LinearLayout) boxLayout.findViewById(R.id.note_title_layout);
		main_layout = (LinearLayout) boxLayout.findViewById(R.id.note_main_layout);
		bottom_layout = (LinearLayout) boxLayout.findViewById(R.id.note_bottom_layout);
		sv = (ScrollView) boxLayout.findViewById(R.id.note_main_scroll);
		
		//
		
		//Title View
		//View title = BarButtonGenerator.createAddNoteView(new DateSelectedListener(), new TimeslotSelectedListener() );
		
		title = (LinearLayout) inflater.inflate(
				R.layout.bar_addnote2, null);
		title_txt = (TextView)title.findViewById(R.id.note_title);
		date_layout = (LinearLayout) title.findViewById(R.id.note_date_layout);
		timeslot_layout = (LinearLayout) title.findViewById(R.id.note_timeslot_layout);
		date_txt = (TextView)title.findViewById(R.id.note_tx_date);
		timeslot_txt= (TextView)title.findViewById(R.id.note_tx_timeslot);
		
		Calendar cal = Calendar.getInstance();
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int time_slot = TimeBlock.getTimeBlock(hours);
		
		timeslot_txt.setText(Timeslot_str[time_slot]);
		
		title_txt.setTypeface(Typefaces.getWordTypefaceBold());
		date_txt.setTypeface(Typefaces.getWordTypefaceBold());
		timeslot_txt.setTypeface(Typefaces.getWordTypefaceBold());
		
		date_layout.setOnClickListener(new OnClickListener(){
			

			@Override
			public void onClick(View v) {
				//title_layout.setEnabled(false);
				//main_layout.setEnabled(false);
				//bottom_layout.setEnabled(false);
				//boxLayout.setEnabled(false);
				setEnabledAll(boxLayout, false);
				
				chooseBox = new ChooseItemDialog(addNoteDialog,boxLayout, 1);
				chooseBox.initialize();
				chooseBox.show();
			}
			
		});
		
		timeslot_layout.setOnClickListener(new OnClickListener(){
			

			@Override
			public void onClick(View v) {
				setEnabledAll(boxLayout, false);
				chooseBox = new ChooseItemDialog(addNoteDialog,boxLayout, 2);
				chooseBox.initialize();
				chooseBox.show();			
			}
			
		});
		
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
		type_title.setText("事件類型：");
		type_title.setTypeface(Typefaces.getWordTypefaceBold());
		typetext = (EditText)type_layout.findViewById(R.id.description_content);
		typetext.setEnabled(false);
		
		//Spinner
		LinearLayout spinner_layout = (LinearLayout) inflater.inflate(
				R.layout.bar_spinner, null);
			
		//sp_item = (Spinner)spinner_layout.findViewById(R.id.spinner_content);
		//SetItem(sp_item, R.array.item_select);
		
		TextView spin_title = (TextView)spinner_layout.findViewById(R.id.spinner_title);
		spin_title.setText("發生事件：");
		spin_title.setTypeface(Typefaces.getWordTypefaceBold());
		
		sp_content = (TextView)spinner_layout.findViewById(R.id.spinner_content);
		sp_content.setText("");
		sp_content.setTypeface(Typefaces.getWordTypefaceBold());
		sp_content.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//listView.setVisibility(View.VISIBLE);
				listViewShowHide();
			}
								
		});
		
		listView = (ListView)spinner_layout.findViewById(R.id.item_listview);
	    
	    /*
	    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(     
                LinearLayout.LayoutParams.MATCH_PARENT,     
                //LinearLayout.LayoutParams.WRAP_CONTENT
                //listView.getHeight()
                listView.getLayoutParams().height
        );
	    
	    Log.d(TAG, "ListView:" + String.valueOf(listView.getLayoutParams().height));*/
		/*
	    RelativeLayout.LayoutParams boxParam = (LayoutParams) listView.getLayoutParams();
		//boxParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		boxParam.width = LayoutParams.MATCH_PARENT;
		boxParam.height = LayoutParams.MATCH_PARENT;*/
	    
	    // get content height
	    /*
	    int contentHeight = listView.getHeight();
	    		
	    // set listview height
	    ViewGroup.LayoutParams lp = (LayoutParams) listView.getLayoutParams();
	    lp.height = contentHeight*5;
	    listView.setLayoutParams(lp);*/
		
		
		
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
		dec_title.setText("補充說明：");
		dec_title.setTypeface(Typefaces.getWordTypefaceBold());
		edtext = (EditText)discription_layout.findViewById(R.id.description_content);
		
		//Bottom View
		View bottom = BarButtonGenerator.createTwoButtonView(R.string.cancel, R.string.ok, new CancelOnClickListener(), endOnClickListener);
		
		main_layout.addView(center_layout);
		main_layout.addView(type_layout);
		main_layout.addView(spinner_layout);
		main_layout.addView(impact_layout);
		main_layout.addView(discription_layout);
		
		//main_layout.addView(bottom);
		bottom_layout.addView(bottom);
		title_layout.bringToFront();
		//bottom_layout.setVisibility(View.GONE);
		bottom_layout.bringToFront();
		//main_layout.addView(bottom);
	}
	
	public void setEnabledAll(View v, boolean enabled) {
	    v.setEnabled(enabled);
	    v.setFocusable(enabled);

	    if(v instanceof ViewGroup) {
	        ViewGroup vg = (ViewGroup) v;
	        for (int i = 0; i < vg.getChildCount(); i++)
	            setEnabledAll(vg.getChildAt(i), enabled);
	    }
	}
	
	private void listViewShowHide(){
		if(!viewshow)
			listView.setVisibility(View.VISIBLE);
		else
			listView.setVisibility(View.GONE);
		
		viewshow = !viewshow;
	}
	
	public void copingSetting(){
		//boxLayout = (RelativeLayout) inflater.inflate(R.layout.activity_qtip, null);
		//mainLayout.addView(boxLayout);
		state = STATE_COPE;
		PreferenceControl.setAfterTestState(STATE_COPE);
		
		title_layout.removeAllViews();
		main_layout.removeAllViews();
		bottom_layout.removeAllViews();
		bottom_layout.setVisibility(View.VISIBLE);
		
		//Title View
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_addnote, null);
		
		note_title = (TextView) layout
				.findViewById(R.id.note_title);
		//Spinner sp_date = (Spinner)layout.findViewById(R.id.note_tx_date);
	    //Spinner sp_timeslot = (Spinner)layout.findViewById(R.id.note_sp_timeslot);
	    
	    note_title.setTypeface(wordTypefaceBold);
	    note_title.setTextColor(context.getResources().getColor(R.color.text_gray2));
	    note_title.setText(R.string.countdown);
	    
	    sp_date.setVisibility(View.INVISIBLE);
	    sp_timeslot.setVisibility(View.INVISIBLE);
		title_layout.addView(layout);
		
		
		//View title = BarButtonGenerator.createWaitingTitle();
		
		
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
		
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_addnote, null);
		
		note_title = (TextView) layout
				.findViewById(R.id.note_title);
		//Spinner sp_date = (Spinner)layout.findViewById(R.id.note_tx_date);
	    //Spinner sp_timeslot = (Spinner)layout.findViewById(R.id.note_sp_timeslot);
	    
	    note_title.setTypeface(wordTypefaceBold);
	    note_title.setTextColor(context.getResources().getColor(R.color.text_gray2));
	    note_title.setText(R.string.countdown);
	    
	    sp_date.setVisibility(View.INVISIBLE);
	    sp_timeslot.setVisibility(View.INVISIBLE);
		title_layout.addView(layout);
		
		//View title = BarButtonGenerator.createWaitingTitle();
		//title_layout.addView(title);
		
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
			note_title.setText(R.string.test_done);
			View bottom = BarButtonGenerator.createOneButtonView( R.string.go_result, goResultOnClickListener );
			bottom_layout.addView(bottom);
		}

	
	}
	
	
	/** Initialize the dialog */
	public void initialize() {
		
		setting();
	    mainLayout.addView(boxLayout);
		
	}
	
	/** show the dialog */
	public void show() {
		state = STATE_NOTE;
		//PreferenceControl.setAfterTestState(STATE_NOTE);
		//questionLayout.setVisibility(View.VISIBLE);
		
		MainActivity.getMainActivity().enableTabAndClick(false);
		boxLayout.setVisibility(View.VISIBLE);
		
		//chooseBox = new ChooseItemDialog(boxLayout, 1);
		//chooseBox.initialize();
		//chooseBox.show();

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
		MainActivity.getMainActivity().enableTabAndClick(true);
		if (boxLayout != null)
			boxLayout.setVisibility(View.INVISIBLE);
	}
	
	
	/** 設定Spinner的Item */
	private void SetItem(Spinner sp, int array){
		//ArrayAdapter adapter = ArrayAdapter.createFromResource(context, array, android.R.layout.simple_spinner_item);
		
		ArrayAdapter adapter = ArrayAdapter.createFromResource(context, array, R.layout.my_spinner);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strs );
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		
		//sp.setPrompt("負面情緒");
        
        //sp.setVisibility(View.VISIBLE);  
       // sp.performClick();
	}
	
	private void SetListItem(int array){
		//ArrayAdapter adapter = ArrayAdapter.createFromResource(context, array, android.R.layout.simple_list_item_1);
		
		sp_content.setText(""); //TODO: 假如點到同一個不要清掉
		ArrayAdapter adapter = ArrayAdapter.createFromResource(context, array, R.layout.my_listitem);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
		   View view2; //保存點選的View
	       int select_item=-1;
		   @Override
		   public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			   TextView c = (TextView) view.findViewById(android.R.id.text1);
			    String playerChanged = c.getText().toString();
			    
			    items = 100*type + position;
				Log.d(TAG, items+"");
			    //Toast.makeText(Settings.this,playerChanged, Toast.LENGTH_SHORT).show();  
			 sp_content.setText(playerChanged);
			 listView.setVisibility(View.GONE);
			 

		   }
		   
		});
		setListViewHeightBasedOnItems(listView);
		listView.setVisibility(View.VISIBLE);
		sv.smoothScrollTo(0 , 600);
		
		//.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
	}
	
	private class DateSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
			day = arg2;
			//Log.d(TAG, view.toString());
			//items = 100*type + arg2;
			//Log.d(TAG, items+"");
			//Toast.makeText(getContext(), "你選的是"+items.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            //view2.setText("你使用什么样的手机："+adapter2.getItem(arg2));  
        }  
  
        public void onNothingSelected(AdapterView<?> arg0) {  
        	day = 1;
        }  
	}
	
	private class TimeslotSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
			timeslot = arg2;
			//Log.d(TAG, view.toString());
			//items = 100*type + arg2;
			//Log.d(TAG, items+"");
			//Toast.makeText(getContext(), "你選的是"+items.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            //view2.setText("你使用什么样的手机："+adapter2.getItem(arg2));  
        }  
  
        public void onNothingSelected(AdapterView<?> arg0) {  
        	timeslot = 1;
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
			
			Log.d(TAG, items+"\t"+impact);
			if(state == STATE_NOTE){
				if(type <= 0 || items < 100){
					//CustomToastSmall.generateToast(R.string.note_check);
					Toast.makeText(context, R.string.note_check ,Toast.LENGTH_SHORT).show();
				}
				else{
					if(listView.getVisibility() == View.VISIBLE){
						Toast.makeText(context, "請選擇項目再送出", Toast.LENGTH_SHORT).show();
						listView.setVisibility(View.GONE);
					}
					else{
					
						impact = impactSeekBar.getProgress();
						testQuestionCaller.writeQuestionFile(day, timeslot, type, items, impact, edtext.getText().toString());
						close();
						clear();
						//copingSetting();
						testQuestionCaller.resetView();
						
					}
				}
				
			}
			else if(state == STATE_COPE){
				//knowingSetting();
				//
				testQuestionCaller.resetView();
				close();
				clear();
			}

	    }
	}
	
	//把所選取的結果取消
	class CancelOnClickListener implements View.OnClickListener{
		public void onClick(View v){
			
			
			testQuestionCaller.writeQuestionFile(day, timeslot, -1, -1, -1, edtext.getText().toString());
			close();
			clear();
			testQuestionCaller.resetView();
			
				//copingSetting();

		}
	}
	
	class GoResultOnClickListener implements View.OnClickListener{
		public void onClick(View v){
			CustomToast.generateToast(R.string.after_test_pass, 2);
			MainActivity.getMainActivity().changeTab(1);
			
	    }
	}
	
	class GoCopingToResultOnClickListener implements View.OnClickListener{
		public void onClick(View v){
			
			
			if(state == STATE_NOTE){
				impact = impactSeekBar.getProgress();
				testQuestionCaller.writeQuestionFile(day, timeslot, type, items, impact, edtext.getText().toString());
			
				Log.d(TAG, items+" "+impact);

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
			
			iv_social.setImageResource(R.drawable.others_emoji2);
			iv_playing.setImageResource(R.drawable.others_emoji1);
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
		        	
		        	SetListItem(R.array.note_negative);
		        	//listViewShowHide();
	        		//SetItem(sp_item,R.array.note_negative);
	        		//spinner_content.performClick();
	        		type = 1;
	        		break;
		        case R.id.vts_iv_not_good:
		        	resetView();
		        	iv_not_good.setImageResource(R.drawable.emoji2_pressed);
		        	typetext.setHint(R.string.note_notgood);
		        	
		        	SetListItem(R.array.note_notgood);
		        	//SetItem(sp_item,R.array.note_notgood);
		        	//spinner_content.performClick();
		        	type = 2;
			        break;
		        case R.id.vts_iv_smile:
		        	resetView();
		        	iv_smile.setImageResource(R.drawable.emoji4_pressed);
		        	typetext.setHint(R.string.note_positive);
		        	
		        	SetListItem(R.array.note_positive);
		        	//SetItem(sp_item, R.array.note_positive);
		        	//spinner_content.performClick();
		        	type = 3;
		        	break;
		        case R.id.vts_iv_try:
		        	resetView();
		        	iv_try.setImageResource(R.drawable.emoji1_pressed);
		        	typetext.setHint(R.string.note_selftest);
		        	
		        	SetListItem(R.array.note_selftest);
		        	//SetItem(sp_item,R.array.note_selftest);
		        	//sp_item.performClick();
		        	type = 4; 
		        	break;
		        case R.id.vts_iv_urge:
		        	resetView();
		        	iv_urge.setImageResource(R.drawable.emoji3_pressed);
		        	typetext.setHint(R.string.note_temptation);
		        	
		        	SetListItem(R.array.note_temptation);
		        	//SetItem(sp_item,R.array.note_temptation);
		        	//sp_item.performClick();
		        	type = 5;
		        	break;
		        case R.id.vts_iv_playing:
		        	resetView();
		        	iv_playing.setImageResource(R.drawable.others_emoji1_oressed);
		        	typetext.setHint(R.string.note_play);
		        	
		        	SetListItem(R.array.note_play);
		        	//SetItem(sp_item,R.array.note_play);
		        	//sp_item.performClick();
		        	type = 6;
		        	break;
		        case R.id.vts_iv_social:
		        	resetView();
		        	iv_social.setImageResource(R.drawable.others_emoji2_pressed);
		        	typetext.setHint(R.string.note_social);
		        	
		        	SetListItem(R.array.note_social);
		        	//SetItem(sp_item,R.array.note_social);
		        	//sp_item.performClick();
		        	type = 7;
		        	break;
		        case R.id.vts_iv_conflict:
		        	resetView();
		        	iv_conflict.setImageResource(R.drawable.others_emoji3_pressed);
		        	typetext.setHint(R.string.note_conflict);
		        	
		        	SetListItem(R.array.note_conflict);
		        	//SetItem(sp_item,R.array.note_conflict);
		        	//sp_item.performClick();
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
		
		public float getDensity(){
			 DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			 return metrics.density;
			}
		
		public float convertDpToPixel(float dp){
		    float px = dp * getDensity();
		    return px;
		}
		/**
		 * Sets ListView height dynamically based on the height of the items.   
		 *
		 * @param listView to be resized
		 * @return true if the listView is successfully resized, false otherwise
		 */
		public boolean setListViewHeightBasedOnItems(ListView listView) {

		    ListAdapter listAdapter = listView.getAdapter();
		    if (listAdapter != null) {

		        int numberOfItems = listAdapter.getCount();

		        // Get total height of all items.
		        int totalItemsHeight = 0;
		        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
		            View item = listAdapter.getView(itemPos, null, listView);
		            item.measure(0, 0);
		            totalItemsHeight += item.getMeasuredHeight();
		        }

		        // Get total height of all item dividers.
		        int totalDividersHeight = listView.getDividerHeight() * 
		                (numberOfItems - 1);

		        // Set list height.
		        ViewGroup.LayoutParams params = listView.getLayoutParams();
		        //params.height = totalItemsHeight + totalDividersHeight;
		        params.height = (int) (convertDpToPixel((float)40)* numberOfItems);
		        listView.setLayoutParams(params);
		        listView.requestLayout();

		        return true;

		    } else {
		        return false;
		    }

		}

		@Override
		public void resetView(int type, int select) {
			setEnabledAll(boxLayout, true);
			if(type == 1){
				day = select;
				date_txt.setText(Date_str[select]);
			}
			else{
				timeslot = select;
				timeslot_txt.setText(Timeslot_str[select]);
			}
			
		}
	
}
