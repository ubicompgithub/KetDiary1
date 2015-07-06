package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.EmotionDIYOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.FamilyCallOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.SelectedListener;
import com.ubicomp.ketdiary.statistic.coping.SelfOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.TryAgainDoneOnClickListener;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class Type2Content extends QuestionnaireContent {

	public Type2Content(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		msgBox.showDialog();
		setHelp(R.string.question_type2_help);
		DatabaseControl db = new DatabaseControl();
		if (db.canTryAgain() && PreferenceControl.questionnaireShowUpdateDetection())
			setSelectItem(R.string.try_again, new SelectedListener(msgBox, new TryAgainDoneOnClickListener(msgBox),
					R.string.next));
		setSelectItem(R.string.self_help, new SelectedListener(msgBox, new SelfOnClickListener(msgBox), R.string.next));
		setSelectItem(R.string.connect_to_family, new SelectedListener(msgBox, new FamilyCallOnClickListener(msgBox),
				R.string.next));
		setSelectItem(R.string.start_emotion_diy_help, new SelectedListener(msgBox, new EmotionDIYOnClickListener(
				msgBox), R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

}
