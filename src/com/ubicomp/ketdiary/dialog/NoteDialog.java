package com.ubicomp.ketdiary.dialog;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.ubicomp.ketdiary.R;
import android.view.View.OnClickListener;

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
	    SetItem(getContext(), sp_date, new String[]{
	    	String.valueOf(dt.getMonth()) + "/" + 
	        String.valueOf(dt.getDay()) });
	    
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
		
		iv_smile = (ImageView) findViewById(R.id.vts_iv_smile);
		iv_not_good = (ImageView) findViewById(R.id.vts_iv_not_good);
		iv_urge = (ImageView) findViewById(R.id.vts_iv_urge);
		iv_cry = (ImageView) findViewById(R.id.vts_iv_cry);
		iv_try = (ImageView) findViewById(R.id.vts_iv_try);

		iv_social = (ImageView) findViewById(R.id.vts_iv_social);
		iv_playing = (ImageView) findViewById(R.id.vts_iv_playing);
		iv_conflict = (ImageView) findViewById(R.id.vts_iv_conflict);
		
		iv_smile.setOnClickListener(SelectItem);
	}
	
	View.OnClickListener SelectItem = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
	        switch(v.getId()){
	        case R.id.vts_iv_cry:
	        	break;
	        case R.id.vts_iv_not_good:
		        break;
	        case R.id.vts_iv_smile:
	        	break;
	        case R.id.vts_iv_try:
	        	break;
	        case R.id.vts_iv_urge:
	        	break;
	        case R.id.vts_iv_playing:
	        	break;
	        case R.id.vts_iv_social:
	        	break;
	        case R.id.vts_iv_conflict:
	        	break;
	        }
		}
	};
	
	
	
	
}
