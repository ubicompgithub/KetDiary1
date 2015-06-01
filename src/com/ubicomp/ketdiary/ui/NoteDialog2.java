package com.ubicomp.ketdiary.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.db.DBTip;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.file.QuestionFile;


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
	private TextView text_self, text_other, text_item, text_impact, text_description, tv_knowdlege;
	
	
	private int state;
	
	
	//write File
	private File mainDirectory;
	private long timestamp = 0;
	private QuestionFile questionFile; 
	
	//Listener
	private SpinnerXMLSelectedListener selectListener;
	private EndOnClickListener buttonOnClickListener;
	
	
	private int type;
	private int items;
	private int impact;
	
	private static final int STATE_NOTE = 1;
	private static final int STATE_KNOW = 2;
	
	public NoteDialog2(TestQuestionCaller testQuestionCaller, RelativeLayout mainLayout){
		
		this.testQuestionCaller = testQuestionCaller;
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		
		//activity = this.getActivity();
	    
		//view = inflater.inflate(R.layout.fragment_note, container, false);
		selectListener = new SpinnerXMLSelectedListener();
		buttonOnClickListener = new EndOnClickListener();
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
		
		View bottom = BarButtonGenerator.createTwoButtonView(R.string.cancel, R.string.ok, buttonOnClickListener, buttonOnClickListener);
		bottom_layout.addView(bottom);
		
		View title = BarButtonGenerator.createAddNoteView(selectListener);
		title_layout.addView(title);
		
		
		
		center_layout = (LinearLayout) inflater.inflate(
				R.layout.note_main, null);
			
	    sp_item = (Spinner)center_layout.findViewById(R.id.note_sp_items);
	    impactSeekBar=(SeekBar)center_layout.findViewById(R.id.seekBar1);
	    
	    SetItem(sp_item, R.array.item_select);
	    
	    text_self = (TextView)center_layout.findViewById(R.id.text_self);
	    text_other = (TextView)center_layout.findViewById(R.id.text_other);
	    text_item = (TextView)center_layout.findViewById(R.id.text_item);
	    text_impact = (TextView)center_layout.findViewById(R.id.text_impact);
	    text_description = (TextView)center_layout.findViewById(R.id.text_description);
	    
	    text_self.setTypeface(Typefaces.getWordTypeface());
	    text_other.setTypeface(Typefaces.getWordTypeface());
	    text_item.setTypeface(Typefaces.getWordTypeface());
	    text_impact.setTypeface(Typefaces.getWordTypeface());
	    text_description.setTypeface(Typefaces.getWordTypeface());
	    
	    
	    
		initTypePager();
		//setStorage();
		
		main_layout.addView(center_layout);
	}
	
	public void copingSetting(){
		//boxLayout = (RelativeLayout) inflater.inflate(R.layout.activity_qtip, null);
		//mainLayout.addView(boxLayout);
		state = STATE_KNOW;
		
		title_layout.removeAllViews();
		main_layout.removeAllViews();
		//main_layout.removeView(center_layout);
		center_layout = (LinearLayout) inflater.inflate(
				R.layout.knowledge, null);
		tv_knowdlege = (TextView)center_layout.findViewById(R.id.qtip_tv_tips);
		tv_knowdlege.setText(DBTip.inst.getTip());
		main_layout.addView(center_layout);
	}
	
	
	/** Initialize the dialog */
	public void initialize() {
	}
	
	/** show the dialog */
	public void show() {
		
		//questionLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		state = STATE_NOTE;
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
	}
	
	
	/** 設定Spinner的Item */
	private void SetItem(Spinner sp, int array){
		ArrayAdapter adapter = ArrayAdapter.createFromResource(context, array, android.R.layout.simple_spinner_item);
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
			//questionFile.write(type, items, impact);
			
				Log.d(TAG, items+"\t"+impact);
			//questionLayout.setVisibility(View.GONE);
			//clear();
				copingSetting();
			//questionFile.write(0, 0, 0);
			//startActivity(new Intent(that, EventCopeSkillActivity.class));
			}
			else if(state == STATE_KNOW){
				tv_knowdlege.setText(DBTip.inst.getTip());
			}
			
			/*
			Datatype.TestDetail ttd = Datatype.inst.newTestDetail();
			ttd.is_filled = true;
			ttd.date = new Date();
			ttd.time_trunk = 1;
			ttd.result = 1;
			ttd.catagory_id = 1;
			ttd.type_id = 1;
			ttd.reason_id = 1;
			ttd.description = "abc";
			DBControl.inst.addTestResult(ttd);*/
			//that.dismiss();
	    
	    }
	}
	
	public void onBackPressed() {
	    //super.onBackPressed(); 
	    //startActivity(new Intent(that, EventCopeSkillActivity.class));
	    // Do extra stuff here
	}
	
	private void initTypePager(){
	    vPager = (ViewPager) center_layout.findViewById(R.id.viewpager);
	    
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
	        		SetItem(sp_item,R.array.note_negative);
	        		sp_item.performClick();
	        		type = 1;
	        		break;
		        case R.id.vts_iv_not_good:
		        	SetItem(sp_item,R.array.note_notgood);
		        	sp_item.performClick();
		        	type = 2;
			        break;
		        case R.id.vts_iv_smile:
		        	SetItem(sp_item, R.array.note_positive);
		        	sp_item.performClick();
		        	type = 3;
		        	break;
		        case R.id.vts_iv_try:
		        	SetItem(sp_item,R.array.note_selftest);
		        	sp_item.performClick();
		        	type = 4; 
		        	break;
		        case R.id.vts_iv_urge:
		        	SetItem(sp_item,R.array.note_temptation);
		        	sp_item.performClick();
		        	type = 5;
		        	break;
		        case R.id.vts_iv_playing:
		        	SetItem(sp_item,R.array.note_play);
		        	sp_item.performClick();
		        	type = 6;
		        	break;
		        case R.id.vts_iv_social:
		        	SetItem(sp_item,R.array.note_social);
		        	sp_item.performClick();
		        	type = 7;
		        	break;
		        case R.id.vts_iv_conflict:
		        	SetItem(sp_item,R.array.note_conflict);
		        	sp_item.performClick();
		        	type = 8;
		        	break;
		        	
		        }
			}
		};
	}

	
}
