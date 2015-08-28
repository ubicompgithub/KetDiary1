package com.ubicomp.ketdiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.noUse.NoteCatagory3;
import com.ubicomp.ketdiary.system.NoteCategory4;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.Typefaces;
import com.ubicomp.ketdiary.ui.spinnergroup.MultiRadioGroup;

/**
 * Activity for Selection Button
 * 
 * @author Andy Chen
 */
public class SelectActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout;
	private MultiRadioGroup noteGroup1;
	private MultiRadioGroup noteGroup2;
	private MultiRadioGroup noteGroup3;
	private MultiRadioGroup noteGroup4;
	private MultiRadioGroup noteGroup5;
	private MultiRadioGroup noteGroup6;
	private MultiRadioGroup noteGroup7;
	private MultiRadioGroup noteGroup8;
	
	private boolean[] result1;
	private boolean[] result2;
	private boolean[] result3;
	private boolean[] result4;
	private boolean[] result5;
	private boolean[] result6;
	private boolean[] result7;
	private boolean[] result8;
	
	private View type1View;	
	private View type2View;
	private View type3View;
	private View type4View;
	private View type5View;
	private View type6View;
	private View type7View;
	private View type8View;
	private String[] Type1Content;
	private String[] Type2Content;
	private String[] Type3Content;
	private String[] Type4Content;
	private String[] Type5Content;
	private String[] Type6Content;
	private String[] Type7Content;
	private String[] Type8Content;
	

	private Activity activity;
	private DatabaseControl db;
	private static final String[] RESULT = {"陰性", "陽性", ""};
	private NoteCategory4 noteCategory;
	private static final String[] TYPE_TITLE = App.getContext().getResources().getStringArray(R.array.trigger_list); 
	
	public static final int NOTE_UPPER_BOUND = 10; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coping);

		this.activity = this;
		titleLayout = (LinearLayout) this
				.findViewById(R.id.coping_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.coping_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		mainLayout.removeAllViews();

		View title = BarButtonGenerator.createTitleView(R.string.help_page);
		titleLayout.addView(title);
		
		db = new DatabaseControl();
		noteCategory = new NoteCategory4();
		

		setViews1();
		setViews2();
		setViews3();
		setViews4();
		setViews5();
		setViews6();
		setViews7();
		setViews8();
		
	}
	
	private void setViews1(){
		
		RelativeLayout titleView = createListView(
				TYPE_TITLE[0], new OnClickListener() {
	
					private boolean visible = false;	
						@Override
					public void onClick(View v) {	
							ImageView list = (ImageView) v
									.findViewById(R.id.question_list);
							if (visible) {
								type1View.setVisibility(View.GONE);
								list.setVisibility(View.INVISIBLE);
							} else {
								type1View.setVisibility(View.VISIBLE);
								list.setVisibility(View.VISIBLE);
							}
							visible = !visible;
						}
					});
		mainLayout.addView(titleView);
			Type1Content = (String[]) (noteCategory.negative).values().toArray(new String[0]);		
			String[] selected = PreferenceControl.getType1();
			boolean[] socialSelected = new boolean[Type1Content.length];
			for (int j = 0; j < socialSelected.length; ++j) {
				socialSelected[j] = false;
				for (int k = 0; k < selected.length; ++k)
				if (Type1Content[j].equals( selected[k]))
						socialSelected[j] = true;
			}					
			noteGroup1 = new MultiRadioGroup(activity, Type1Content,
					socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
					ClickLogId.SETTING_SELECT + 0);
			type1View = noteGroup1.getView();
			mainLayout.addView(type1View);
			type1View.setVisibility(View.GONE);
		
	}
private void setViews2(){
		
		RelativeLayout titleView = createListView(
				TYPE_TITLE[1], new OnClickListener() {
	
					private boolean visible = false;	
						@Override
					public void onClick(View v) {	
							ImageView list = (ImageView) v
									.findViewById(R.id.question_list);
							if (visible) {
								type2View.setVisibility(View.GONE);
								list.setVisibility(View.INVISIBLE);
							} else {
								type2View.setVisibility(View.VISIBLE);
								list.setVisibility(View.VISIBLE);
							}
							visible = !visible;
						}
					});
		mainLayout.addView(titleView);
			Type2Content = (String[]) (noteCategory.notgood).values().toArray(new String[0]);		
			String[] selected = PreferenceControl.getType2();
			boolean[] socialSelected = new boolean[Type2Content.length];
			for (int j = 0; j < socialSelected.length; ++j) {
				socialSelected[j] = false;
				for (int k = 0; k < selected.length; ++k)
					if (Type2Content[j].equals( selected[k]))
							socialSelected[j] = true;
			}					
			noteGroup2 = new MultiRadioGroup(activity, Type2Content,
					socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
					ClickLogId.SETTING_SELECT + 0);
			type2View = noteGroup2.getView();
			mainLayout.addView(type2View);
			type2View.setVisibility(View.GONE);
		
	}
private void setViews3(){
	
	RelativeLayout titleView = createListView(
			TYPE_TITLE[2], new OnClickListener() {

				private boolean visible = false;	
					@Override
				public void onClick(View v) {	
						ImageView list = (ImageView) v
								.findViewById(R.id.question_list);
						if (visible) {
							type3View.setVisibility(View.GONE);
							list.setVisibility(View.INVISIBLE);
						} else {
							type3View.setVisibility(View.VISIBLE);
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
	mainLayout.addView(titleView);
	Type3Content= (String[]) (noteCategory.positive).values().toArray(new String[0]);		
	String[] selected = PreferenceControl.getType3();
		boolean[] socialSelected = new boolean[Type3Content.length];
		for (int j = 0; j < socialSelected.length; ++j) {
			socialSelected[j] = false;
			for (int k = 0; k < selected.length; ++k)
				if (Type3Content[j].equals( selected[k]))
						socialSelected[j] = true;
		}					
		noteGroup3 = new MultiRadioGroup(activity, Type3Content,
				socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
				ClickLogId.SETTING_SELECT + 0);
		type3View = noteGroup3.getView();
		mainLayout.addView(type3View);
		type3View.setVisibility(View.GONE);
	
}
private void setViews4(){
	
	RelativeLayout titleView = createListView(
			TYPE_TITLE[3], new OnClickListener() {

				private boolean visible = false;	
					@Override
				public void onClick(View v) {	
						ImageView list = (ImageView) v
								.findViewById(R.id.question_list);
						if (visible) {
							type4View.setVisibility(View.GONE);
							list.setVisibility(View.INVISIBLE);
						} else {
							type4View.setVisibility(View.VISIBLE);
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
	mainLayout.addView(titleView);
	Type4Content = (String[]) (noteCategory.selftest).values().toArray(new String[0]);		
	String[] selected = PreferenceControl.getType4();
		boolean[] socialSelected = new boolean[Type4Content.length];
		for (int j = 0; j < socialSelected.length; ++j) {
			socialSelected[j] = false;
			for (int k = 0; k < selected.length; ++k)
				if (Type4Content[j].equals( selected[k]))
						socialSelected[j] = true;
		}					
		noteGroup4 = new MultiRadioGroup(activity, Type4Content,
				socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
				ClickLogId.SETTING_SELECT + 0);
		type4View = noteGroup4.getView();
		mainLayout.addView(type4View);
		type4View.setVisibility(View.GONE);
	
}
private void setViews5(){
	
	RelativeLayout titleView = createListView(
			TYPE_TITLE[4], new OnClickListener() {

				private boolean visible = false;	
					@Override
				public void onClick(View v) {	
						ImageView list = (ImageView) v
								.findViewById(R.id.question_list);
						if (visible) {
							type5View.setVisibility(View.GONE);
							list.setVisibility(View.INVISIBLE);
						} else {
							type5View.setVisibility(View.VISIBLE);
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
	mainLayout.addView(titleView);
	Type5Content = (String[]) (noteCategory.temptation).values().toArray(new String[0]);		
	String[] selected = PreferenceControl.getType5();
		boolean[] socialSelected = new boolean[Type5Content.length];
		for (int j = 0; j < socialSelected.length; ++j) {
			socialSelected[j] = false;
			for (int k = 0; k < selected.length; ++k)
				if (Type5Content[j].equals( selected[k]))
						socialSelected[j] = true;
		}					
		noteGroup5 = new MultiRadioGroup(activity, Type5Content,
				socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
				ClickLogId.SETTING_SELECT + 0);
		type5View = noteGroup5.getView();
		mainLayout.addView(type5View);
		type5View.setVisibility(View.GONE);
	
}
private void setViews6(){
	
	RelativeLayout titleView = createListView(
			TYPE_TITLE[5], new OnClickListener() {

				private boolean visible = false;	
					@Override
				public void onClick(View v) {	
						ImageView list = (ImageView) v
								.findViewById(R.id.question_list);
						if (visible) {
							type6View.setVisibility(View.GONE);
							list.setVisibility(View.INVISIBLE);
						} else {
							type6View.setVisibility(View.VISIBLE);
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
	mainLayout.addView(titleView);
	Type6Content = (String[]) (noteCategory.conflict).values().toArray(new String[0]);		
	String[] selected = PreferenceControl.getType6();
		boolean[] socialSelected = new boolean[Type6Content.length];
		for (int j = 0; j < socialSelected.length; ++j) {
			socialSelected[j] = false;
			for (int k = 0; k < selected.length; ++k)
				if (Type6Content[j].equals( selected[k]))
						socialSelected[j] = true;
		}					
		noteGroup6 = new MultiRadioGroup(activity, Type6Content,
				socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
				ClickLogId.SETTING_SELECT + 0);
		type6View = noteGroup6.getView();
		mainLayout.addView(type6View);
		type6View.setVisibility(View.GONE);
	
}
private void setViews7(){
	
	RelativeLayout titleView = createListView(
			TYPE_TITLE[6], new OnClickListener() {

				private boolean visible = false;	
					@Override
				public void onClick(View v) {	
						ImageView list = (ImageView) v
								.findViewById(R.id.question_list);
						if (visible) {
							type7View.setVisibility(View.GONE);
							list.setVisibility(View.INVISIBLE);
						} else {
							type7View.setVisibility(View.VISIBLE);
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
	mainLayout.addView(titleView);
	Type7Content = (String[]) (noteCategory.social).values().toArray(new String[0]);		
	String[] selected = PreferenceControl.getType7();
		boolean[] socialSelected = new boolean[Type7Content.length];
		for (int j = 0; j < socialSelected.length; ++j) {
			socialSelected[j] = false;
			for (int k = 0; k < selected.length; ++k)
				if (Type7Content[j].equals( selected[k]))
						socialSelected[j] = true;
		}					
		noteGroup7 = new MultiRadioGroup(activity, Type7Content,
				socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
				ClickLogId.SETTING_SELECT + 0);
		type7View = noteGroup7.getView();
		mainLayout.addView(type7View);
		type7View.setVisibility(View.GONE);
	
}
private void setViews8(){
	
	RelativeLayout titleView = createListView(
			TYPE_TITLE[7], new OnClickListener() {

				private boolean visible = false;	
					@Override
				public void onClick(View v) {	
						ImageView list = (ImageView) v
								.findViewById(R.id.question_list);
						if (visible) {
							type8View.setVisibility(View.GONE);
							list.setVisibility(View.INVISIBLE);
						} else {
							type8View.setVisibility(View.VISIBLE);
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
	mainLayout.addView(titleView);
	Type8Content = (String[]) (noteCategory.play).values().toArray(new String[0]);		
	String[] selected = PreferenceControl.getType8();
		boolean[] socialSelected = new boolean[Type8Content.length];
		for (int j = 0; j < socialSelected.length; ++j) {
			socialSelected[j] = false;
			for (int k = 0; k < selected.length; ++k)
				if (Type8Content[j].equals( selected[k]))
						socialSelected[j] = true;
		}					
		noteGroup8 = new MultiRadioGroup(activity, Type8Content,
				socialSelected, NOTE_UPPER_BOUND, R.string.setting_limit,
				ClickLogId.SETTING_SELECT + 0);
		type8View = noteGroup8.getView();
		mainLayout.addView(type8View);
		type8View.setVisibility(View.GONE);
	
}

	
	

	private void setViews9(){
		
		TestResult[] testResult = db.getAllPrimeTestResult();
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("確定修改結果?");
//		//builder.setPositiveButton("確定", new ModifyListener());
//		builder.setNegativeButton("取消", null);
//		AlertDialog cleanAlertDialog = builder.create();
		
		for(int i=0; i<testResult.length; i++){
			
			int year = testResult[i].getTv().getYear();
			int month = testResult[i].getTv().getMonth();
			int day = testResult[i].getTv().getDay();
			final long ts = testResult[i].getTv().getTimestamp();
			final int result = testResult[i].getResult();
			if(result < 0 || result > 1 )
				continue;
			
			String text = year+"年"+(month+1)+"月"+day+"日"+" 結果: "+RESULT[result];
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle( year+"年"+(month+1)+"月"+day+"日\n"+"確定修改成 "+ RESULT[result^1]+"?");
//			builder.setPositiveButton("確定", new ModifyListener(ts, result)); 
//			builder.setNegativeButton("取消", null);
			builder.setNegativeButton("確定", new ModifyListener(ts, result)); 
			builder.setPositiveButton("取消", null);
			AlertDialog cleanAlertDialog = builder.create();	
			RelativeLayout aboutView = createListView(text,
					new AlertOnClickListener(cleanAlertDialog));
			mainLayout.addView(aboutView);		
		}
	}
	private class AlertOnClickListener implements View.OnClickListener {

		private AlertDialog alertDialog;

		public AlertOnClickListener(AlertDialog ad) {
			this.alertDialog = ad;
		}

		@Override
		public void onClick(View v) {
			alertDialog.show();
		}
	}
	
	private void updateView(){
		mainLayout.removeAllViews();
		//setViews();
	}
	
	private class ModifyListener implements
	DialogInterface.OnClickListener {
		long ts;
		int result;
		public ModifyListener(long ts, int result){
			this.ts = ts;
			this.result = result;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			db.modifyResultByTs(ts, (result ^ 1) );
			updateView();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void storeType1(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup1.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type1Content[i];
		}
		PreferenceControl.setType1(socialSelections);
	}
	
	private void storeType2(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup2.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type2Content[i];
		}
		PreferenceControl.setType2(socialSelections);
	}
	private void storeType3(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup3.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type3Content[i];
		}
		PreferenceControl.setType3(socialSelections);
	}
	private void storeType4(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup4.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type4Content[i];
		}
		PreferenceControl.setType4(socialSelections);
	}
	private void storeType5(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup5.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type5Content[i];
		}
		PreferenceControl.setType5(socialSelections);
	}
	private void storeType6(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup6.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type6Content[i];
		}
		PreferenceControl.setType6(socialSelections);
	}
	private void storeType7(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup7.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type7Content[i];
		}
		PreferenceControl.setType7(socialSelections);
	}
	private void storeType8(){
		//String[] socialSelections = new String[NOTE_UPPER_BOUND];
		String[] socialSelections = {"","","","","","","","","",""};
		int social_idx = 0;
		boolean[] socialSelected = noteGroup8.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = Type8Content[i];
		}
		PreferenceControl.setType8(socialSelections);
	}
	


	@Override
	protected void onPause() {
		storeType1();
		storeType2();
		storeType3();
		storeType4();
		storeType5();
		storeType6();
		storeType7();
		storeType8();
		
		super.onPause();
	}

	private RelativeLayout createListView(String titleStr, OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_list_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);
		layout.setOnClickListener(listener);
		return layout;
	}
	private RelativeLayout createListView(int titleStr, OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_list_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);
		layout.setOnClickListener(listener);
		return layout;
	}


}
