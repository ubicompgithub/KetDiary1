package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class CallDialOnClickListener extends QuestionnaireOnClickListener {

	private String phone;
	public CallDialOnClickListener(QuestionnaireDialog msgBox,String phone) {
		super(msgBox);
		this.phone = phone;
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_CALL_OK);
		msgBox.closeDialogAndCall();
		Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+phone));
		msgBox.getContext().startActivity(intentDial);
	}

}
