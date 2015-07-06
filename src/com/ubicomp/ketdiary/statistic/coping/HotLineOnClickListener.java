package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.CallCheckContent;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import android.view.View;

public class HotLineOnClickListener extends QuestionnaireOnClickListener {

	public HotLineOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_HOTLINE);
		seq.add(5);
		String emotion_hot_line = msgBox.getContext().getString(R.string.call_check_help_emotion_hot_line);
		contentSeq.add(new CallCheckContent(msgBox,emotion_hot_line,"0800788995",true));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
