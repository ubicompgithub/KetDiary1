package com.ubicomp.ketdiary.statistic.coping;

import android.content.Context;

import com.ubicomp.ketdiary.ui.EnablePage;

public interface QuestionnaireDialogCaller extends EnablePage {
	public void setQuestionAnimation();
	public void updateSelfHelpCounter();
	public Context getContext();
}
