package com.ubicomp.ketdiary.statistic.coping;

import android.view.View;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;

public class CloseClickListener extends QuestionnaireOnClickListener {

	public CloseClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_CLOSE);
		msgBox.closeBoxNull();
	}

}
