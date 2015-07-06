package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.SelfHelpContent;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import android.view.View;

public class SelfOnClickListener extends QuestionnaireOnClickListener {

	public SelfOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_SELFHELP);
		seq.add(6);
		contentSeq.add(new SelfHelpContent(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
