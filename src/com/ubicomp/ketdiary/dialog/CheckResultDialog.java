package com.ubicomp.ketdiary.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;


/**
 * Note after testing
 * @author Andy
 *
 */
public class CheckResultDialog{
	
	private Activity activity;
	private CheckResultDialog noteFragment = this;
	private static final String TAG = "ADD_PAGE";
	
	private TestQuestionCaller testQuestionCaller;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private LinearLayout questionLayout;
	
	private RelativeLayout mainLayout;
	private boolean testFail;
	private DatabaseControl db;
	
	private TextView checkOK, checkCancel, checkHelp;
	/** @see Typefaces */
	private Typeface wordTypeface, wordTypefaceBold, digitTypeface,
			digitTypefaceBold;
	

	
	public CheckResultDialog(RelativeLayout mainLayout){
		
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		digitTypeface = Typefaces.getDigitTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		
		db = new DatabaseControl();
		
	    setting();
	    mainLayout.addView(boxLayout);

	}
	
	protected void setting() {
		
		boxLayout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_end_animation2, null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		checkOK = (TextView) boxLayout.findViewById(R.id.anim_ok_button);
		checkCancel = (TextView) boxLayout.findViewById(R.id.anim_cancel_button);
		checkHelp = (TextView) boxLayout.findViewById(R.id.anim_help);
		
		//RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) boxLayout.getLayoutParams();
		//boxParam.width = LayoutParams.MATCH_PARENT;
		//boxParam.height = LayoutParams.MATCH_PARENT;

		checkHelp.setTypeface(wordTypefaceBold);
		checkHelp.setText("前往測試結果?");
		checkOK.setTypeface(wordTypefaceBold);
		checkCancel.setTypeface(wordTypefaceBold);
		
		checkOK.setOnClickListener(new CheckOnClickListener());
		checkCancel.setOnClickListener(new CheckCancelOnClickListener());
	}
	

	
	/** Initialize the dialog */
	public void initialize() {
		//RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) boxLayout.getLayoutParams();
		TestDetail testDetail = db.getLatestTestDetail();
		int failType = testDetail.getFailedState();
		String failedReason = testDetail.getFailedReason();
		testFail = PreferenceControl.isTestFail();
		if(testFail){
			if(failType == 8 || failType == 9){
				checkHelp.setText(R.string.result_fail);			
			}
			else if(failedReason.equals("無法判斷檢測結果")){
				checkHelp.setText("無法判斷檢測結果,回到檢測頁重測?");
			}
			else if(failedReason.equals("試紙匣被拔出")){
				checkHelp.setText("檢測過程中請勿抽出試紙匣,回到檢測頁重測?");
			}
			else{
				checkHelp.setText("測試失敗,回到檢測頁重測?");
			}
		}
		
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
	}
	
	
	/** Initialize the dialog to check if the user check out to the developer */
	private void initializeCallCheckDialog() {
	}


	/** OnClickListener for canceling and dismiss the check check dialog */
	private class CheckCancelOnClickListener implements View.OnClickListener {

		@Override
		/**Cancel and dismiss the check check dialog*/
		public void onClick(View v) {
			close();
			clear();
			MainActivity.getMainActivity().enableTabAndClick(true);
		}

	}

	/** OnClickListener for checking out */
	private class CheckOnClickListener implements View.OnClickListener {
		@Override
		/**Calling out*/
		public void onClick(View v) {
			MainActivity.getMainActivity().enableTabAndClick(true);
			//CustomToast.generateToast(R.string.after_test_pass, 2);
			if(!testFail)
				MainActivity.getMainActivity().changeTab(1);
			else
				MainActivity.getMainActivity().changeTab(0);
        	close();
			clear();
		}
	}
	
}
