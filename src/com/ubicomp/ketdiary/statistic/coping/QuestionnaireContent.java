package com.ubicomp.ketdiary.statistic.coping;

import java.util.ArrayList;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

abstract public class QuestionnaireContent {
	
	protected LinearLayout questionnaireLayout;
	private Drawable choiceDrawable;
	protected ArrayList<Integer>seq;
	protected ArrayList <QuestionnaireContent> contentSeq;
	protected QuestionnaireDialog msgBox;
	private Context context;
	private Typeface wordTypefaceBold;
	
	private static LayoutInflater inflater = null;
	
	public QuestionnaireContent( QuestionnaireDialog msgBox ){
		this.questionnaireLayout = msgBox.getQuestionnaireLayout();
		this.seq = msgBox.getClickSequence();
		this.contentSeq = msgBox.getQuestionSequence();
		this.choiceDrawable = msgBox.getChoiceDrawable();
		this.msgBox = msgBox;
		this.context = App.getContext();
		if (inflater == null)
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wordTypefaceBold = msgBox.getTypeface();
	}
	
	public void onPush(){
		questionnaireLayout.removeAllViews();
		setContent();
	}
	
	abstract protected void setContent();
	
	protected void setHelp(String str){
		msgBox.setHelpMessage(str);
	}
	
	protected void setHelp(int str_id){
		msgBox.setHelpMessage(str_id);
	}
	
	protected void setSelectItem(String str, View.OnClickListener listener){
		RelativeLayout v =(RelativeLayout) inflater.inflate(R.layout.dialog_statistic_item, null);
		TextView text = (TextView) v.findViewById(R.id.questionnaire_text);
		text.setText(str);
		text.setTypeface(wordTypefaceBold);
		
		ImageView button =(ImageView) v.findViewById(R.id.questionnaire_button);
		button.setImageDrawable(choiceDrawable);
		
		v.setOnClickListener(listener);
		
		questionnaireLayout.addView(v);
	}
	
	protected void setSelectItem(int str_id, View.OnClickListener listener){
		RelativeLayout v =(RelativeLayout) inflater.inflate(R.layout.dialog_statistic_item, null);
		TextView text = (TextView) v.findViewById(R.id.questionnaire_text);
		text.setText(str_id);
		text.setTypeface(wordTypefaceBold);
		
		ImageView button =(ImageView) v.findViewById(R.id.questionnaire_button);
		button.setImageDrawable(choiceDrawable);
		
		v.setOnClickListener(listener);
		
		questionnaireLayout.addView(v);
	}
	
	public void cleanSelection(){
		int count = questionnaireLayout.getChildCount();
		for (int i=0;i<count;++i){
			ViewGroup v = (ViewGroup) questionnaireLayout.getChildAt(i);
			ImageView img = (ImageView) v.findViewById(R.id.questionnaire_button);
			img.setImageDrawable(choiceDrawable);
		}
	}
}
