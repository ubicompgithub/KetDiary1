package com.ubicomp.ketdiary.dialog;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.structure.QuestionTest;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.Typefaces;


/**
 * Note after testing
 * @author Andy
 *
 */
public class QuestionDialog{
	
	private Activity activity;
	private QuestionDialog noteFragment = this;
	private static final String TAG = "ADD_PAGE";
	
	private TestQuestionCaller testQuestionCaller;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private LinearLayout questionLayout;
	
	private String question = "";
	private String answer = "";
	private String selectedAnswer = "";
	
	private RelativeLayout mainLayout;
	
	private TextView tv_question, tv_answer1, tv_answer2, tv_answer3, tv_answer4, tv_confirm, tv_cancel;
	private ImageView radio1, radio2, radio3, radio4;
	private LinearLayout layout1, layout2, layout3, layout4;
	/** @see Typefaces */
	private Typeface wordTypeface, wordTypefaceBold, digitTypeface,
			digitTypefaceBold;
	
	private int select = -1;
	private String[] selection;
	private DatabaseControl db;
	
	public QuestionDialog(RelativeLayout mainLayout){
		
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		
		db = new DatabaseControl();
		
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		digitTypeface = Typefaces.getDigitTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		
	    setting();
	    mainLayout.addView(boxLayout);

	}
	
	protected void setting() {
		
		boxLayout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_answer_question, null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		tv_question = (TextView) boxLayout.findViewById(R.id.question_question);
		tv_answer1 = (TextView) boxLayout.findViewById(R.id.question_answer1);
		tv_answer2 = (TextView) boxLayout.findViewById(R.id.question_answer2);
		tv_answer3 = (TextView) boxLayout.findViewById(R.id.question_answer3);
		tv_answer4 = (TextView) boxLayout.findViewById(R.id.question_answer4);
		tv_cancel = (TextView)  boxLayout.findViewById(R.id.question_cancel);
		tv_confirm = (TextView) boxLayout.findViewById(R.id.question_confirm);
		
		tv_confirm.setOnClickListener(new ConfirmOnClickListener());
		tv_cancel.setOnClickListener(new CancelOnClickListener());
		
		radio1 = (ImageView) boxLayout.findViewById(R.id.question_radio1);
		radio2 = (ImageView) boxLayout.findViewById(R.id.question_radio2);
		radio3 = (ImageView) boxLayout.findViewById(R.id.question_radio3);
		radio4 = (ImageView) boxLayout.findViewById(R.id.question_radio4);
		
		//radio1.setOnClickListener(new RadioOnClickListener());
		//radio2.setOnClickListener(new RadioOnClickListener());
		//radio3.setOnClickListener(new RadioOnClickListener());
		//radio4.setOnClickListener(new RadioOnClickListener());
		
		layout1 = (LinearLayout) boxLayout.findViewById(R.id.question_answer1_layout);
		layout2 = (LinearLayout) boxLayout.findViewById(R.id.question_answer2_layout);
		layout3 = (LinearLayout) boxLayout.findViewById(R.id.question_answer3_layout);
		layout4 = (LinearLayout) boxLayout.findViewById(R.id.question_answer4_layout);
		
		layout1.setOnClickListener(new RadioOnClickListener());
		layout2.setOnClickListener(new RadioOnClickListener());
		layout3.setOnClickListener(new RadioOnClickListener());
		layout4.setOnClickListener(new RadioOnClickListener());
		
		
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
		
		selection = settingQuestion();
		tv_answer1.setText(selection[0]);
		tv_answer2.setText(selection[1]);
		tv_answer3.setText(selection[2]);
		tv_answer4.setText(selection[3]);
		
		tv_question.setText(question);
		
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
		resetAllImage();
		if (boxLayout != null)
			boxLayout.setVisibility(View.INVISIBLE);
	}
	
	private String[] settingQuestion() {
		String[] questions = null;
		String[] answers = null;
		Resources r = App.getContext().getResources();
		questions = r.getStringArray(R.array.question_1);
		answers = r.getStringArray(R.array.question_answer_1);
		
		Random rand = new Random();
		int qid = rand.nextInt(3);
		question = questions[qid];
		answer = new String(answers[qid * 4]);

		String[] tempSelection = new String[4];
		for (int i = 0; i < tempSelection.length; ++i)
			tempSelection[i] = answers[qid * 4 + i];
		shuffleArray(tempSelection);
		String[] selectAns = new String[4];
		for (int i = 0; i < selectAns.length; ++i)
			selectAns[i] = tempSelection[i];

		//int ans_id = rand.nextInt(selectAns.length); //把隨機一個選項換成答案
		//selectAns[ans_id] = answer;

		return selectAns;
	}

	private static void shuffleArray(String[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; --i) {
			int index = rnd.nextInt(i + 1);
			String a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	private class ConfirmOnClickListener implements View.OnClickListener {

		@Override
		/**Cancel and dismiss the check check dialog*/
		public void onClick(View v) {
			long ts = System.currentTimeMillis();
			int questionType = 0;
			int isCorrect = 0;
			String selection = selectedAnswer;
			int choose = select;
			if(selectedAnswer.equals(answer)){
				isCorrect = 1;
			}
			else{
				
			}

			QuestionTest questionTest = new QuestionTest(ts, questionType, isCorrect, selection, choose, 0);
			int addScore = db.insertQuestionTest(questionTest);
			
			if(isCorrect == 0){
				CustomToast.generateToast(R.string.question_wrong, -1);
			}
			else{
				CustomToast.generateToast(R.string.question_correct, addScore);
			}
			
			PreferenceControl.setPoint(addScore);			
			
			
			MainActivity.getMainActivity().enableTabAndClick(true);
			close();
			//clear();	
		}

	}


	private class CancelOnClickListener implements View.OnClickListener {
		@Override
		/**Calling out*/
		public void onClick(View v) {
			MainActivity.getMainActivity().enableTabAndClick(true);
        	close();
			//clear();
		}
	}
	
	/** OnClickListener for checking out */
	private class RadioOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			resetAllImage();
			switch (v.getId()){
			case R.id.question_answer1_layout:
				radio1.setImageResource(R.drawable.radio_node_select);
				selectedAnswer = selection[0];
				select = 0;
				break;
			case R.id.question_answer2_layout:
				radio2.setImageResource(R.drawable.radio_node_select);
				selectedAnswer = selection[1];
				select = 1;
				break;
			case R.id.question_answer3_layout:
				radio3.setImageResource(R.drawable.radio_node_select);
				selectedAnswer = selection[2];
				select = 2;
				break;
			case R.id.question_answer4_layout:
				radio4.setImageResource(R.drawable.radio_node_select);
				selectedAnswer = selection[3];
				select = 3;
				break;
			/*
			default:				
				((ImageView)v).setImageResource(R.drawable.radio_node_select);
				break;
				*/				
			}
	
		}
	}
	
	private void resetAllImage(){
		radio1.setImageResource(R.drawable.radio_node);
		radio2.setImageResource(R.drawable.radio_node);
		radio3.setImageResource(R.drawable.radio_node);
		radio4.setImageResource(R.drawable.radio_node);
	}
	
}
