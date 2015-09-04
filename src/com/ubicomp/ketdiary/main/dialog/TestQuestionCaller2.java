package com.ubicomp.ketdiary.main.dialog;

/**
 * Interface for calling the test questionnaire dialog
 * 
 * @author Andy Chen
 */
public interface TestQuestionCaller2 {
	/** write the questionnaire results into a file */
	public void writeQuestionFile(int day, int timeslot, int type, int item, int impact, String description);
	
	public void resetView();
}
