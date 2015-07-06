package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.InspireContent;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import android.view.View;

public class InspireOnClickListener extends QuestionnaireOnClickListener {

	public InspireOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_INSPIRE);
		seq.add(1);
		contentSeq.add(new InspireContent(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
