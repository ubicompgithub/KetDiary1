package com.ubicomp.ketdiary.system;

/**
 * Class defines constants of RehabDiary
 * 
 * @author Stanley Wang
 */
public class Config {

	// TestFragment

	/** Count seconds before a BrAC test */
	public static final int COUNT_DOWN_SECOND = 10;

	// Coupon
	/** COUPON_CREDITS credits = 1 coupons */
	public static final int COUPON_CREDITS = 20;

	// PWD
	/** Password to enter the developer setting mode */
	public static final String PASSWORD = "rehab_2015";


	// Alarm actions
	/** Action of regular notification */
	public static final String ACTION_REGULAR_NOTIFICATION = "Regular_notification";
	/** Action of regular Internet connection check */
	public static final String ACTION_REGULAR_CHECK = "Regular_check";
	/** Action of regular Set Daily event */
	public static final String ACTION_DAILY_EVENT = "Daily_event";
}
