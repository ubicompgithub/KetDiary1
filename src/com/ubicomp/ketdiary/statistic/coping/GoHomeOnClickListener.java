package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import android.view.View;

public class GoHomeOnClickListener extends QuestionnaireOnClickListener {

	public GoHomeOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_HOME);
		seq.add(7);
		msgBox.closeDialog();
	}

}
