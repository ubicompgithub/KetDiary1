package com.ubicomp.ketdiary.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.ui.Typefaces;


/**
 * Note after testing
 * @author Andy
 *
 */
public class ChooseItemDialog implements OnClickListener{
	
	private Activity activity;
	private ChooseItemDialog noteFragment = this;
	private static final String TAG = "ADD_PAGE";
	
	private TestQuestionCaller testQuestionCaller;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private RelativeLayout fullLayout;
	 
	private LinearLayout questionLayout;
	
	private RelativeLayout mainLayout;
	private ChooseItemCaller caller;
	private TextView title;
	/** @see Typefaces */
	private Typeface wordTypeface, wordTypefaceBold, digitTypeface,
			digitTypefaceBold;
	
	private ListView listView;
	private int type;
	private int select;
	

	
	public ChooseItemDialog(ChooseItemCaller caller, RelativeLayout mainLayout, int type){
		
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		this.caller = caller;
		this.type = type;
		
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		digitTypeface = Typefaces.getDigitTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		
	    setting(type);
	    mainLayout.addView(boxLayout);

	}
	
	protected void setting(int type) {
		
		boxLayout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_choose_item2, null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		fullLayout=(RelativeLayout)boxLayout.findViewById(R.id.choose_full_layout);
		fullLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				close();
				clear();
			}
			
		});
		
		title = (TextView) boxLayout.findViewById(R.id.choose_title);
		
		title.setTypeface(Typefaces.getWordTypefaceBold());
		
		listView = (ListView)boxLayout.findViewById(R.id.choose_listview);
		/*
		String[] arr = new String[]{
	         "今天","昨天","前天"
	    };
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_listitem,arr);*/
		ArrayAdapter adapter;
		title.setText("日期");
		adapter = ArrayAdapter.createFromResource(context, R.array.note_date , R.layout.choose_listitem);
		if(type == 2){
			adapter = ArrayAdapter.createFromResource(context, R.array.note_time_slot , R.layout.choose_listitem);
			title.setText("時段");
		}
	    listView.setAdapter(adapter);
	    listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				select = position;
				clear();
				close();
			}
			   
		});
	}
	

	
	/** Initialize the dialog */
	public void initialize() {
		//RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) boxLayout.getLayoutParams();
		
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout
				.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		boxParam.width = LayoutParams.MATCH_PARENT;
		boxParam.height = LayoutParams.MATCH_PARENT;
	}
	
	/** show the dialog */
	public void show() {
		MainActivity.getMainActivity().enableTabAndClick(false);
		boxLayout.setVisibility(View.VISIBLE);

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
		
		caller.resetView(type, select);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.choose_full_layout:
			clear();
			close();
			
			break;
		}
		//clear();
		//close();
	}
	
	

	
}
