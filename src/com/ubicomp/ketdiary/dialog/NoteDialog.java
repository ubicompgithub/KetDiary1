package com.ubicomp.ketdiary.dialog;

import android.R.string;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ubicomp.ketdiary.R;

public class NoteDialog extends Dialog{
	public NoteDialog(Context context){
        super(context, android.R.style.Theme_Light);
		setContentView(R.layout.test_dialog);
		Spinner spinner = (Spinner)findViewById(R.id.sp_class);
		//建立一個ArrayAdapter物件，並放置下拉選單的內容
		/*ArrayAdapter<string> adapter = new ArrayAdapter<string>(
			this,android.R.layout.simple_spinner_item
			,new String[]{"紅茶","奶茶","綠茶"}
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView adapterView, View view, int position, long id){
			}
			public void onNothingSelected(AdapterView arg0) {
			}
		});*/
	}
}