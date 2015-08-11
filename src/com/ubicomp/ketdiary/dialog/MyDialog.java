package com.ubicomp.ketdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.noUse.NoteCategory2;

public class MyDialog extends Dialog{
	
	private NoteCategory2 dict;
	private static final String[] timeslot = {"上午", "下午", "晚上"};

    public MyDialog(Context context) {
        super(context, android.R.style.Theme_Light);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_detail_activity);
        dict = new NoteCategory2();
    }
    
    public void initial(int date, int dayOfweek, int slot, int type, int items, int impact, String descripton){
    	ImageView type_icon = (ImageView) findViewById(R.id.type_icon);
    	//type_icon.setImageResource(R.drawable.ic_launcher);
    	TextView detail_time = (TextView) findViewById(R.id.detail_time);
		detail_time.setText(date+"號\n"+"星期"+dayOfweek+"\n"+timeslot[slot]);
		TextView detail_type = (TextView) findViewById(R.id.detail_type_content);
		detail_type.setText("負面情緒");
		TextView detail_item = (TextView) findViewById(R.id.detail_item_content);
		detail_item.setText(dict.getItems(items));
		TextView detail_impact = (TextView) findViewById(R.id.detail_impact_content);
		detail_impact.setText(""+impact);
		TextView detail_description = (TextView) findViewById(R.id.detail_description_content);
		detail_description.setText(descripton); 	
    	
    }

}