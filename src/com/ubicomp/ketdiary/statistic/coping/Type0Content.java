package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.BreathOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.EmotionDIYOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.InspireOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.SelectedListener;
import com.ubicomp.ketdiary.statistic.coping.TryAgainDoneOnClickListener;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class Type0Content extends QuestionnaireContent {

	public Type0Content(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		msgBox.showDialog();
		setHelp(R.string.question_type0_help);
		setSelectItem(R.string.breath_help, new SelectedListener(msgBox, new BreathOnClickListener(msgBox),
				R.string.next));
		setSelectItem(R.string.inspire_help, new SelectedListener(msgBox, new InspireOnClickListener(msgBox),
				R.string.next));
		setSelectItem(R.string.start_emotion_diy_help, new SelectedListener(msgBox, new EmotionDIYOnClickListener(
				msgBox), R.string.next));
		if (PreferenceControl.isDeveloper())
			setSelectItem(R.string.try_again, new SelectedListener(msgBox, new TryAgainDoneOnClickListener(msgBox),
					R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

}
