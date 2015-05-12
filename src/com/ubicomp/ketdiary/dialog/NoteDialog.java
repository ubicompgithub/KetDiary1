package com.ubicomp.ketdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ubicomp.ketdiary.R;

public class NoteDialog extends Dialog{
	public static int RetValue = 0;
	public static int RETVALUE_CANCEL = 0;
	public static int RETVALUE_ENTER = 1;
	
	private Dialog that;
	
	private Button btn_enter, btn_cancel;
	private void SetItem(Context cxt, Spinner sp, String[] strs){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			cxt, android.R.layout.simple_spinner_item, strs );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
	}
	public NoteDialog(Context context){
        super(context, android.R.style.Theme_Light);
        setContentView(R.layout.test_dialog);
		that = this;
        
        
        RetValue = RETVALUE_CANCEL;
        btn_enter = (Button)findViewById(R.id.btn_enter);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_enter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RetValue = RETVALUE_ENTER;
				that.dismiss();
			}
		});
        
        btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RetValue = RETVALUE_CANCEL;
				that.dismiss();
			}
		});
        
        //WindowManager.LayoutParams lp = getWindow().getAttributes();
		Spinner sp_class = (Spinner)findViewById(R.id.sp_class),
				sp_trunk = (Spinner)findViewById(R.id.sp_trunk);
		
		SetItem(context, sp_class, new String[]{"開心", "難過"});
		SetItem(context, sp_trunk, new String[]{"上午", "中午", "下午"});
		/*sp_class.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView adapterView, View view, int position, long id){
			}
			public void onNothingSelected(AdapterView arg0) {
			}
		});*/
		
		
	}
	
	
}