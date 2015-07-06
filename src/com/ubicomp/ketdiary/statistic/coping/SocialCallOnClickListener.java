package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.ConnectContent;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import android.view.View;

public class SocialCallOnClickListener extends QuestionnaireOnClickListener {

	public SocialCallOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_SOCIAL);
		contentSeq.add(new ConnectContent(msgBox,ConnectContent.TYPE_SOCIAL));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
