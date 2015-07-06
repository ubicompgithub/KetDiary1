package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.EmotionDIYOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.HotLineOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.FamilyCallOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.ReadingOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.SelectedListener;
import com.ubicomp.ketdiary.statistic.coping.SocialCallOnClickListener;

public class Type1Content extends QuestionnaireContent {

	public Type1Content(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		msgBox.showDialog();
		setHelp(R.string.question_type1_help);
		setSelectItem(R.string.read_sentence, new SelectedListener(msgBox, new ReadingOnClickListener(msgBox),
				R.string.next));
		setSelectItem(R.string.connect_to_family, new SelectedListener(msgBox, new FamilyCallOnClickListener(msgBox),
				R.string.next));
		setSelectItem(R.string.connect_to_emotion_hot_line, new SelectedListener(msgBox, new HotLineOnClickListener(
				msgBox), R.string.next));
		setSelectItem(R.string.connect_for_social_help, new SelectedListener(msgBox, new SocialCallOnClickListener(
				msgBox), R.string.next));
		setSelectItem(R.string.start_emotion_diy_help, new SelectedListener(msgBox, new EmotionDIYOnClickListener(
				msgBox), R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

}
