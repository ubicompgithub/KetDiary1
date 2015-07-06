package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.BreathContent;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import android.view.View;

public class BreathOnClickListener extends QuestionnaireOnClickListener {

	public BreathOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_BREATH);
		seq.add(0);
		contentSeq.add(new BreathContent(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
