package com.ubicomp.ketdiary.clicklog;

public class ClickLogId {
	/*
	 * Format 0 00 0 0000 [page] [item] [cond] [appendix]
	 */

	public final static String LOG_MSG_ID = "CLICK_MSG";
	
	//About Activity
	public final static long ABOUT_ENTER = 0; // 00000000
	public final static long ABOUT_LEAVE = 10000; // 00010000
	public final static long ABOUT_EMAIL = 100000; // 00100000
	public final static long ABOUT_CALL = 200000; // 00200000;
	public final static long ABOUT_CALL_OK = 300000; // 00300000;
	public final static long ABOUT_CALL_CANCEL = 400000; // 00400000;
	public final static long ABOUT_WEBSITE = 500000; // 00500000;

	
	//Setting Activity
	public final static long SETTING_ENTER = 1000000; // 01000000;
	public final static long SETTING_LEAVE = 1010000; // 01010000;
	public final static long SETTING_TITLE_LIST = 1100000; // 01100000;
	public final static long SETTING_CHECK = 1200000;
	public final static long SETTING_EDIT = 1300000;
	public final static long SETTING_SPINNER = 1400000;
	public final static long SETTING_SELECT = 1500000;
	
	//Test Fragment
	public final static long TEST_ENTER = 10000000;
	public final static long TEST_LEAVE = 10010000;
	public final static long TEST_TUTORIAL_BUTTON = 10200000;
	public final static long TEST_START_BUTTON = 10300000;
	public final static long TEST_END_BUTTON = 10400000;
	
	public final static long TEST_QUESTION_CANCEL = 10500000;
	public final static long TEST_QUESTION_SEND = 10600000;
	public final static long TEST_QUESTION_SEND_EMPTY = 10600001;
	public final static long TEST_COPING_CONFIRM = 10700000;
	
	public final static long TEST_NOTE_ENTER = 12000000;
	public final static long TEST_COPE_ENTER = 12100000;
	public final static long TEST_KOWING_ENTER = 12200000;
	
	public final static long TEST_KOWING_NEXT = 12300000;
	public final static long TEST_KOWING_LAST = 12400000;
	public final static long TEST_CHECKING_RESULT = 12500000;
	
//	public final static long TEST_ADDITIONAL_QUESTION_CANCEL = 12000000;
//	public final static long TEST_ADDITIONAL_QUESTION_SEND = 12100000;
//	public final static long TEST_ADDITIONAL_QUESTION_SEND_EMPTY = 12100001;

//	public final static long TEST_FEEDBACK_REC_BUTTON = 13000000;
//	public final static long TEST_FEEDBACK_STOP_REC_BUTTON = 13100000;
//	public final static long TEST_FEEDBACK_DONE = 13200000;

	public final static long TEST_NOTIFICATION_CANCEL = 14000000;
	public final static long TEST_NOTIFICATION_GOTO = 14100000;

	
	//Statistic Fragment
	public final static long STATISTIC_ENTER = 20000000;
	public final static long STATISTIC_LEAVE = 20010000;
	public final static long STATISTIC_TODAY = 20100000;
	public final static long STATISTIC_WEEK = 20110000;
	public final static long STATISTIC_ANALYSIS = 20200000;
	public final static long STATISTIC_COPING_BUTTON = 20300000;
	public final static long STATISTIC_QUESTIONTEST_BUTTON = 20400000;
	public final static long STATISTIC_QUESTIONTEST_CONFIRM = 20410000;
	public final static long STATISTIC_QUESTIONTEST_CANCEL = 20420000;
	
	
	public final static long STATISTIC_RADAR_CHART_OPEN = 22000000;
	public final static long STATISTIC_RADAR_CHART_CLOSE = 22100000;
	public final static long STATISTIC_DETAIL_CHART_OPEN = 22200000;
	public final static long STATISTIC_DETAIL_CHART_CLOSE = 22300000;
	
	public final static long STATISTIC_QUESTION_EXIT = 20400000;
	public final static long STATISTIC_QUESTION_BREATH = 20500000;
	public final static long STATISTIC_QUESTION_CALL_CHECK = 20600000;
	public final static long STATISTIC_QUESTION_CALL_OK = 20700000;
	public final static long STATISTIC_QUESTION_CLOSE = 20800000;
	public final static long STATISTIC_QUESTION_EMOTIONDIY = 20900000;
	public final static long STATISTIC_QUESTION_END = 21000000;
	public final static long STATISTIC_QUESTION_FAMILY = 21100000;
	public final static long STATISTIC_QUESTION_HOME = 21200000;
	public final static long STATISTIC_QUESTION_HOTLINE = 21300000;
	public final static long STATISTIC_QUESTION_INSPIRE = 21400000;
	public final static long STATISTIC_QUESTION_READING = 21500000;
	public final static long STATISTIC_QUESTION_SELFHELP = 21600000;
	public final static long STATISTIC_QUESTION_SITUATION = 21700000;
	public final static long STATISTIC_QUESTION_SOCIAL = 21800000;
	public final static long STATISTIC_QUESTION_TRYAGAIN = 21900000;

	

	public final static long DAYBOOK_ENTER = 30000000;
	public final static long DAYBOOK_LEAVE = 30010000;
	public final static long DAYBOOK_PAGE_UP = 30100000;
	public final static long DAYBOOK_PAGE_DOWN = 30110000;

	public final static long DAYBOOK_CHART_TYPE0 = 30200000;
	public final static long DAYBOOK_CHART_TYPE1 = 30210000;
	public final static long DAYBOOK_CHART_TYPE2 = 30220000;

	public final static long DAYBOOK_CHART = 30400000;
	public final static long DAYBOOK_CHART_ROTATE = 30410000;
	public final static long DAYBOOK_CHART_TAP = 30420000;
	public final static long DAYBOOK_CHART_SCROLL = 30420000;
	
	public final static long DAYBOOK_CALENDAR = 30500000; 
	public final static long DAYBOOK_CHANGE_MONTH = 30510000; 
	public final static long DAYBOOK_TODAY = 30520000;
	public final static long DAYBOOK_SPECIFIC_DAY = 30530000;
	
	public final static long DAYBOOK_SHOWDETAIL = 30600000;
	
	public final static long DAYBOOK_RANDOMTEST = 30700000;
	public final static long DAYBOOK_RANDOMTEST_CONFIRM = 30710000;
	public final static long DAYBOOK_RANDOMTEST_CANCEL = 30720000;
	
	public final static long DAYBOOK_FILTER_BUTTON = 30800000;
	public final static long DAYBOOK_FILTER = 30900000;
	public final static long DAYBOOK_TOGGLE = 31000000;
	public final static long DAYBOOK_FILTER_LONGCLICK = 31000000;
//	public final static long DAYBOOK_RECORD_REC = 30900000;
//	public final static long DAYBOOK_RECORD_PAUSE_REC = 30910000;
//	public final static long DAYBOOK_RECORD_PLAY = 31000000;
//	public final static long DAYBOOK_RECORD_PAUSE_PLAY = 31010000;
//	public final static long DAYBOOK_RECORD_ADD_EM = 31100000;
//	public final static long DAYBOOK_RECORD_EM_HISTORY = 31200000;
//	public final static long DAYBOOK_RECORD_BACK = 31300000;

	public final static long DAYBOOK_ADDNOTE = 31400000;
	public final static long DAYBOOK_ADDNOTE_CONFIRM = 31410000;
	public final static long DAYBOOK_ADDNOTE_CANCEL = 31420000;
	public final static long DAYBOOK_ADDNOTE_ENTER = 31430000;
	public final static long DAYBOOK_ADDNOTE_LEAVE = 31440000;
	//public final static long DAYBOOK_READ_OK = 31400000;
	//public final static long DAYBOOK_READ_CANCEL = 31500000;
	
	
	//Coping Skill
	public final static long COPING_ENTER = 40000000;
	public final static long COPING_LEAVE = 40010000;
	public final static long COPING_RETURN = 40200000;
	public final static long COPING_SELECTION = 40300000;
	public final static long COPING_PLAY = 40400000;
	public final static long COPING_PAUSE = 40410000;
	public final static long COPING_CANCEL_PLAY = 40500000;// Not use
	public final static long COPING_CALL_OK = 40600000;
	public final static long COPING_CALL_CANCEL = 40700000;
	public final static long COPING_STOP = 40800000;
	public final static long COPING_END_PLAY_CANCEL = 40900000;
	public final static long COPING_END_PLAY = 41000000;

//	public final static long EMOTION_MANAGE_ENTER = 50000000;
//	public final static long EMOTION_MANAGE_LEAVE = 50010000;
//	public final static long EMOTION_MANAGE_SELECTION = 50100000;
//	public final static long EMOTION_MANAGE_RETURN = 50200000;
//	public final static long EMOTION_MANAGE_LIST_TEXT = 50300000;
//	public final static long EMOTION_MANAGE_SELECT_TEXT = 50400000;
//
//	public final static long EMOTION_MANAGE_HISTORY_ENTER = 55000000;
//	public final static long EMOTION_MANAGE_HISTORY_LEAVE = 55010000;
//	public final static long EMOTION_MANAGE_HISTORY_SELECT = 55100000;
//
//	public final static long TUTORIAL_ENTER = 60000000;
//	public final static long TUTORIAL_LEAVE = 60010000;
//	public final static long TUTORIAL_NEXT = 60100000;
//	public final static long TUTORIAL_REPLAY = 60200000;

	public final static long DAYBOOK_TEST_ENTER = 70000000;
	public final static long DAYBOOK_TEST_LEAVE = 70010000;
	public final static long DAYBOOK_TEST_SELECT = 70100000;
	public final static long DAYBOOK_TEST_SUBMIT = 70200000; // change on
																	// agreement
	public final static long DAYBOOK_TEST_SUBMIT_EMPTY = 70210000; // no
																		// change
																		// on
																		// agreement
	public final static long DAYBOOK_TEST_CANCEL = 70300000;

	public final static long TAB_TEST = 90000000;
	public final static long TAB_STATISTIC = 90100000;
	public final static long TAB_DAYBOOK = 90200000;
}
