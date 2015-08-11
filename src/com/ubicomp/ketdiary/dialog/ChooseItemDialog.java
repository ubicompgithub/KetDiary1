package com.ubicomp.ketdiary.dialog;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
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
import com.ubicomp.ketdiary.system.check.TimeBlock;
import com.ubicomp.ketdiary.ui.Typefaces;


/**
 * Note after testing
 * @author Andy
 *
 */
public class ChooseItemDialog implements OnClickListener{
	
	private Activity activity;
	private ChooseItemDialog noteFragment = this;
	private static final String TAG = "CHOOSE_ITEM";
	
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
	private int select = -1;
	private int day;
	private int time_slot=4;
	
	private static final int[] TimeslotId = {R.array.note_time_slot, R.array.note_time_slot2, R.array.note_time_slot3};
	private static final int DAY_TYPE = 1;
	private static final int SLOT_TYPE = 2;
	
	private static final int TODAY = 0;
	private static final int OTHERDAY = 1;
	
	public ChooseItemDialog(ChooseItemCaller caller, RelativeLayout mainLayout, int type , int day){
		
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		this.caller = caller;
		this.type = type;
		this.day = day;
		
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		digitTypeface = Typefaces.getDigitTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		
	    setting();
	    mainLayout.addView(boxLayout);

	}
	
	protected void setting() {
		
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
		
		if(type == SLOT_TYPE){
			time_slot = 2;
			if(day == TODAY){ //是今天才去判斷
				Calendar cal = Calendar.getInstance();
				int hours = cal.get(Calendar.HOUR_OF_DAY);
				time_slot = TimeBlock.getTimeBlock(hours);
			}
			
			adapter = ArrayAdapter.createFromResource(context, TimeslotId[time_slot] , R.layout.choose_listitem);
			title.setText("時段");
			
		}
		
	    listView.setAdapter(adapter);  
	    listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				if(type == DAY_TYPE){
					select = position;
					close();
					clear();
				}
				else if(type == SLOT_TYPE){
					if(position > time_slot)
						return;
					
					select = position;			
					close();
					clear();				
				}
				
			}
			   
		});
	}
	
	public View getViewByPosition(int pos, ListView listView) {
	    final int firstListItemPosition = listView.getFirstVisiblePosition();
	    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - listView.getHeaderViewsCount();

	    if (pos < firstListItemPosition || pos > lastListItemPosition ) {
	        return listView.getAdapter().getView(pos, null, listView);
	    } else {
	        final int childIndex = pos - firstListItemPosition;
	        return listView.getChildAt(childIndex);
	    }
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
		MainActivity.getMainActivity().enableTabAndClick(true);
		
		caller.resetView(type, select);
		Log.d(TAG, "Select:"+select);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.choose_full_layout:
			close();
			clear();
			
			break;
		}
		//clear();
		//close();
	}
	
	

	
}
