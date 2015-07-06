package com.ubicomp.ketdiary.statistic.coping;

import android.view.View;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class TryAgainDoneOnClickListener extends QuestionnaireOnClickListener {

	public TryAgainDoneOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_TRYAGAIN);
		seq.add(8);
		msgBox.closeDialog(R.string.try_again_toast);
		PreferenceControl.setUpdateDetection(true);
		MainActivity.getMainActivity().changeTab(0);
	}

}
