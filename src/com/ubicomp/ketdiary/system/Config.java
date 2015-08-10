package com.ubicomp.ketdiary.system;

/**
 * Class defines constants of RehabDiary
 * 
 * @author Stanley Wang
 */
public class Config {

	// TestFragment

	/** Time gap between each completed BrAC test */
	public static final long TEST_GAP_DURATION_LONG = 120 * 1000L;

	/** Time gap after an incomplete BrAC test */
	public static final long TEST_GAP_DURATION_SHORT = 60 * 1000L;

	/** Count seconds before a BrAC test */
	public static final int COUNT_DOWN_SECOND = 10;

	/** Count seconds before a BrAC test under debug mode */
	public static final int COUNT_DOWN_SECOND_DEBUG = 2;

	// StorytellingFragment

	/**
	 * Time to add counters for getting credit if the user read the page >
	 * READING_PAGE_TIME
	 */
	public static final long READING_PAGE_TIME = 2500;

	/** Getting credit if counters > STORYTELLING_READ_LIMIT */
	public static final int STORYTELLING_READ_LIMIT = 8;

	// Coupon
	/** COUPON_CREDITS credits = 1 coupons */
	public static final int COUPON_CREDITS = 20;

	// PWD
	/** Password to enter the developer setting mode */
	public static final String PASSWORD = "rehab_2015";

	// GCM
	/** GCM sender ID */
	public static final String SENDER_ID = "1075576910063";

	// Alarm actions
	/** Action of regular notification */
	public static final String ACTION_REGULAR_NOTIFICATION = "Regular_notification";
	/** Action of regular Internet connection check */
	public static final String ACTION_REGULAR_CHECK = "Regular_check";
	/** Action of regular Set Daily event */
	public static final String ACTION_DAILY_EVENT = "Daily_event";
}
