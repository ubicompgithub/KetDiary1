package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.MusicContent;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import android.view.View;

public class MusicOnClickListener extends QuestionnaireOnClickListener {

	private int aid;
	public MusicOnClickListener(QuestionnaireDialog msgBox,int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_SITUATION);
		seq.add(aid);
		contentSeq.add(new MusicContent(msgBox,aid));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
