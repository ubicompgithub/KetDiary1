package com.ubicomp.ketdiary.statistic.coping;

import java.util.Random;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.EndOnClickListener;

public class InspireContent extends QuestionnaireContent {

	private static String[] help;
	
	public InspireContent(QuestionnaireDialog msgBox) {
		super(msgBox);
		if (help==null)
			help = msgBox.getContext().getResources().getStringArray(R.array.question_inspire_question);
	}

	@Override
	protected void setContent() {
		msgBox.showCloseButton(false);
		Random rand = new Random();
		int idx = rand.nextInt(help.length);
		
		setHelp(help[idx]);
		msgBox.showQuestionnaireLayout(false);
		msgBox.setNextButton(R.string.done,new EndOnClickListener(msgBox));
	}

}
