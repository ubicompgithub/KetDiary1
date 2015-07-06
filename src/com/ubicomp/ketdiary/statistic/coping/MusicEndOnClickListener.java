package com.ubicomp.ketdiary.statistic.coping;

import android.util.Log;
import android.view.View;

import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;

public class MusicEndOnClickListener extends QuestionnaireOnClickListener {

	
	public MusicEndOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_END);
		Log.d("CONTENT","MUSIC END");
		msgBox.closeDialog();
	}

}
