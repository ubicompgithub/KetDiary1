package com.ubicomp.ketdiary.ui;

/**
 * Interface for calling the test questionnaire dialog
 * 
 * @author Stanley Wang
 */
public interface TestQuestionCaller {
	/** write the questionnaire results into a file */
	public void writeQuestionFile(int emotion, int craving);
}
