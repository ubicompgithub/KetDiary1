package com.ubicomp.ketdiary.ui;

/**
 * Interface for calling the test questionnaire dialog
 * 
 * @author Andy Chen
 */
public interface TestQuestionCaller {
	/** write the questionnaire results into a file */
	public void writeQuestionFile(int type, int item, int impact, String description);
}
