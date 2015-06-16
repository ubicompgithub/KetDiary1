package com.ubicomp.ketdiary.dialog;

/**
 * Interface for calling the test questionnaire dialog
 * 
 * @author Andy Chen
 */
public interface TestQuestionCaller {
	/** write the questionnaire results into a file */
	public void writeQuestionFile(int day, int timeslot, int type, int item, int impact, String description);
}
