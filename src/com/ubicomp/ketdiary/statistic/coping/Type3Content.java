package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.EmotionDIYOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.GoHomeOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.SelectedListener;
import com.ubicomp.ketdiary.statistic.coping.SelfOnClickListener;

public class Type3Content extends QuestionnaireContent {

	public Type3Content(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		msgBox.showDialog();
		setHelp(R.string.question_type3_help);
		setSelectItem(R.string.go_home, new SelectedListener(msgBox,new GoHomeOnClickListener(msgBox),R.string.ok));
		setSelectItem(R.string.self_help,new SelectedListener(msgBox,new SelfOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.start_emotion_diy_help,new SelectedListener(msgBox,new EmotionDIYOnClickListener(msgBox),R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

}
