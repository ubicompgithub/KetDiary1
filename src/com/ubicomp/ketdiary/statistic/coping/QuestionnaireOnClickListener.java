package com.ubicomp.ketdiary.statistic.coping;

import java.util.ArrayList;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireContent;
import android.view.View.OnClickListener;

public abstract class QuestionnaireOnClickListener implements OnClickListener {

	protected QuestionnaireDialog msgBox;
	protected ArrayList<Integer>seq;
	protected ArrayList <QuestionnaireContent> contentSeq;
	
	public QuestionnaireOnClickListener (QuestionnaireDialog msgBox){
		this.msgBox = msgBox;
		this.seq = msgBox.getClickSequence();
		this.contentSeq = msgBox.getQuestionSequence();
	}
	
}
