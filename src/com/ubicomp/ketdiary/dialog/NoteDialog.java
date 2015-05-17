package com.ubicomp.ketdiary.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.db.NoteCatagory;

public class NoteDialog extends Dialog{
	
	private ViewPager vPager;
	private ImageView iv_try, iv_smile, iv_urge,
					  iv_cry, iv_not_good;
	private ImageView iv_conflict, iv_social, iv_playing;
	private Spinner sp_date, sp_timeslot, sp_item;
	
	public NoteDialog(Context context) {
		super(context);
		
	}
	
	/** 設定Spinner的Item */
	private void SetItem(Context cxt, Spinner sp, String[] strs){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			cxt, android.R.layout.simple_spinner_item, strs );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
	}
	
	
	@SuppressLint("InflateParams")
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE); //before     
	    setContentView(R.layout.test_dialog);
	    
	    sp_date = (Spinner)findViewById(R.id.note_sp_date);
	    sp_timeslot = (Spinner)findViewById(R.id.note_sp_timeslot);
	    sp_item = (Spinner)findViewById(R.id.note_sp_items);
	    
	    Date dt = new Date();
	    
	    SetItem(getContext(), sp_timeslot, new String[]{"上午", "中午", "下午"});
	    SetItem(getContext(), sp_item, new String[]{"請選擇分類"});
	    SetItem(
	    	getContext(), sp_date, new String[]{
	    	(new SimpleDateFormat("MM-dd")).format(new Date())
	    });
	    
		initTypePager();
	}
	
	
	private void initTypePager(){
	    vPager = (ViewPager) findViewById(R.id.viewpager);
		LayoutInflater li = getLayoutInflater();
		ArrayList<View> aList = new ArrayList<View>();
		aList.add(li.inflate(R.layout.view_typepager_self, null));
		aList.add(li.inflate(R.layout.view_typepager_other, null));
		TypePageAdapter mAdapter = new TypePageAdapter(aList);		
		vPager.setAdapter(mAdapter);
	}
	
	private void Click(){
		/*Datatype.TestDetail ttd = Datatype.inst.newTestDetail();
		ttd.is_filled = true;
		ttd.date = new Date();
		ttd.time_trunk = 1;
		ttd.result = 1;
		ttd.catagory_id = 1;
		ttd.type_id = 1;
		ttd.reason_id = 1;
		ttd.description = "abc";
		DBControl.inst.addTestResult(ttd);
		*/
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
				iv_smile = (ImageView) findViewById(R.id.vts_iv_smile);
				iv_not_good = (ImageView) findViewById(R.id.vts_iv_not_good);
				iv_urge = (ImageView) findViewById(R.id.vts_iv_urge);
				iv_cry = (ImageView) findViewById(R.id.vts_iv_cry);
				iv_try = (ImageView) findViewById(R.id.vts_iv_try);
				
				iv_smile.setOnClickListener(SelectItem);
				iv_not_good.setOnClickListener(SelectItem);
				iv_urge.setOnClickListener(SelectItem);
				iv_cry.setOnClickListener(SelectItem);
				iv_try.setOnClickListener(SelectItem);
			}else{
				iv_social = (ImageView) findViewById(R.id.vts_iv_social);
				iv_playing = (ImageView) findViewById(R.id.vts_iv_playing);
				iv_conflict = (ImageView) findViewById(R.id.vts_iv_conflict);
			
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
	        		SetItem(
	        			getContext(), sp_item,
	    	    		NoteCatagory.inst.VectorDataToStringArr(
	    	    			NoteCatagory.inst.note.negative
	    	    		)
	    	    	);
	        		break;
		        case R.id.vts_iv_not_good:
		        	SetItem(
		        		getContext(), sp_item,
		    	    	NoteCatagory.inst.VectorDataToStringArr(
		    	    		NoteCatagory.inst.note.notgood
		    	    	)
		    	    );
			        break;
		        case R.id.vts_iv_smile:
		        	SetItem(
		        		getContext(), sp_item,
		    	    	NoteCatagory.inst.VectorDataToStringArr(
		    	    		NoteCatagory.inst.note.positive
		    	    	)
		    	    );
		        	break;
		        case R.id.vts_iv_try:
		        	SetItem(
		        		getContext(), sp_item,
		    	    	NoteCatagory.inst.VectorDataToStringArr(
		    	    		NoteCatagory.inst.note.selftest
		    	    	)
		    	    );
		        	break;
		        case R.id.vts_iv_urge:
		        	SetItem(
		        		getContext(), sp_item,
		    	    	NoteCatagory.inst.VectorDataToStringArr(
		    	    		NoteCatagory.inst.note.temptation
		    	    	)
		    	    );
		        	break;
		        case R.id.vts_iv_playing:
		        	SetItem(
		        		getContext(), sp_item,
		    	    	NoteCatagory.inst.VectorDataToStringArr(
		    	    		NoteCatagory.inst.note.play
		    	    	)
		    	    );
		        	break;
		        case R.id.vts_iv_social:
		        	SetItem(
		        		getContext(), sp_item,
		    	    	NoteCatagory.inst.VectorDataToStringArr(
		    	    		NoteCatagory.inst.note.social
		    	    	)
		    	    );
		        	break;
		        case R.id.vts_iv_conflict:
		        	SetItem(
		        		getContext(), sp_item,
		    	    	NoteCatagory.inst.VectorDataToStringArr(
		    	    		NoteCatagory.inst.note.conflict
		    	    	)
		    	    );
		        	break;
		        }
			}
		};
	}

	
}
