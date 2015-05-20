package com.ubicomp.ketdiary.system;

import java.util.Calendar;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.graphics.Point;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;

/**
 * Class for controlling Android Preference
 * 
 * @author Stanley Wang
 */
public class PreferenceControl {

	private static final SharedPreferences sp = App.getSp();

	/** Default setting at the first time of launching SoberDiary */
	public static void defaultSetting() {
		setUID("sober_default_test");
		setIsDeveloper(false);
		Calendar cal = Calendar.getInstance();
		setStartDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * get UID
	 * 
	 * @return uid (default: sober_default_test)
	 */
	public static String getUID() {
		return sp.getString("uid", "sober_default_test");
	}

	/**
	 * set UID
	 * 
	 * @param uid
	 *            new UID
	 */
	public static void setUID(String uid) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("uid", uid);
		edit.commit();
	}

	/**
	 * Check if the UID is default UID
	 * 
	 * @return true of the UID is default UID
	 */
	public static boolean defaultCheck() {
		return getUID().equals("sober_default_test");
	}
     
	
	/**
	 * get DeviceId
	 * 
	 * @return DeviceId (default: device_default)
	 */
	public static String getDeviceId() {
		return sp.getString("device_id", "device_default");
	}

	/**
	 * set DeviceId
	 * 
	 * @param device_id
	 *            new DeviceId
	 */
	public static void setDeviceId(String device_id) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("device_id", device_id);
		edit.commit();
	}

	
	
	
	
	
	
	
	
	
	
	// haven't use
	/**
	 * Check if it is the first time launching SoberDiary
	 * 
	 * @return true if UID is ""
	 */
	public static boolean checkFirstUID() {
		return sp.getString("uid", "").equals("");
	}

	/**
	 * Get sensorId
	 * 
	 * @return sensor Id. If there are no sensor used before, return "unknown"
	 */
	public static String getSensorID() {
		return sp.getString("sensor_id", "unknown");
	}

	/**
	 * Set sensor id
	 * 
	 * @param sensorID
	 *            sensor ID
	 */
	public static void setSensorID(String sensorID) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("sensor_id", sensorID);
		edit.commit();
	}

	/** Get the names of the */
	public static String[] getConnectFamilyName() {
		String[] names = new String[3];
		names[0] = sp.getString("family_name0", "");
		names[1] = sp.getString("family_name1", "");
		names[2] = sp.getString("family_name2", "");
		return names;
	}

	public static String[] getConnectFamilyPhone() {
		String[] calls = new String[3];
		calls[0] = sp.getString("family_phone0", "");
		calls[1] = sp.getString("family_phone1", "");
		calls[2] = sp.getString("family_phone2", "");
		return calls;
	}

	public static void setFamilyCallData(String name, String phone, int id) {
		String keyName = "family_name" + id;
		String keyPhone = "family_phone" + id;
		SharedPreferences.Editor edit = sp.edit();
		edit.putString(keyName, name);
		edit.putString(keyPhone, phone);
		edit.commit();
	}



	public static void setTestResult(int result) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("testResult", result);
		edit.commit();
	}

	public static int getTestResult() {
		return sp.getInt("testResult", -1);
	}



	public static long getDetectionTimestamp() {
		return sp.getLong("latestDetectionTimestamp", 0);
	}

	public static void setTestFail() {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestDetectionDoneTimestamp", System.currentTimeMillis());
		edit.putBoolean("latestTestFail", true);
		edit.commit();
	}

	public static void setTestSuccess() {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestDetectionDoneTimestamp", System.currentTimeMillis());
		edit.putBoolean("latestTestFail", false);
		edit.commit();
	}

	public static long getLastTestTime() {
		return sp.getLong("latestDetectionDoneTimestamp", 0);
	}

	public static boolean isTestFail() {
		return sp.getBoolean("latestTestFail", false);
	}

	public static void timeReset() {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestDetectionDoneTimestamp", 0L);
		edit.putLong("latestStartGPSTimestamp", 0);
		edit.putLong("latestDetectionTimestamp", 0);
		edit.commit();
	}

	public static boolean isDebugMode() {
		return sp.getBoolean("debug", false);
	}

	public static void setDebugMode(boolean debug) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("debug", debug);
		edit.commit();
	}

	public static boolean debugType() {
		return sp.getBoolean("debugType", false);
	}

	public static void setDebugType(boolean type) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("debugType", type);
		edit.commit();
	}

	public static boolean isFirstTime() {
		return sp.getBoolean("firstTime", true);
	}

	public static void setAfterFirstTime() {
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("firstTime", false);
		editor.commit();
	}

	public static boolean isDeveloper() {
		return sp.getBoolean("developer", false);
	}

	public static void setIsDeveloper(boolean developer) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("developer", developer);
		edit.commit();
	}

	public static int lastShowedCoupon() {
		return sp.getInt("showedCoupon", 0);
	}

	public static void setShowedCoupon(int num) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("showedCoupon", num);
		edit.commit();
	}

	public static void setGoal(String goal, int money, int drink_cost) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("targetGood", goal);
		edit.putInt("targetMoney", money);
		edit.putInt("perDrinkCost", drink_cost);
		edit.commit();
	}

	public static int getSavingGoalMoney() {
		return sp.getInt("targetMoney", 50000);
	}

	public static int getSavingDrinkCost() {
		return sp.getInt("perDrinkCost", 200);
	}

	public static Calendar getStartDate() {
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0] = sp.getInt("sYear", cal.get(Calendar.YEAR));
		data[1] = sp.getInt("sMonth", cal.get(Calendar.MONTH));
		data[2] = sp.getInt("sDay", cal.get(Calendar.DATE));

		cal.set(data[0], data[1], data[2], 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static int[] getStartDateData() {
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0] = sp.getInt("sYear", cal.get(Calendar.YEAR));
		data[1] = sp.getInt("sMonth", cal.get(Calendar.MONTH));
		data[2] = sp.getInt("sDay", cal.get(Calendar.DATE));
		return data;
	}

	public static void setStartDate(int year, int month, int day) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("sYear", year);
		edit.putInt("sMonth", month);
		edit.putInt("sDay", day);
		edit.commit();
	}

	public static boolean isLocked() {
		return sp.getBoolean("systemLock", false);
	}

	public static void setLocked(boolean lock) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("systemLock", lock);
		edit.commit();
	}

	public static Calendar getLockDate() {
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0] = sp.getInt("lockYear", cal.get(Calendar.YEAR));
		data[1] = sp.getInt("lockMonth", cal.get(Calendar.MONTH));
		data[2] = sp.getInt("lockDay", cal.get(Calendar.DATE));

		cal.set(data[0], data[1], data[2], 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static int[] getLockDateData() {
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0] = sp.getInt("lockYear", cal.get(Calendar.YEAR));
		data[1] = sp.getInt("lockMonth", cal.get(Calendar.MONTH));
		data[2] = sp.getInt("lockDay", cal.get(Calendar.DATE));
		return data;
	}

	public static void setLockDate(int year, int month, int day) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("lockYear", year);
		edit.putInt("lockMonth", month);
		edit.putInt("lockDay", day);
		edit.commit();
	}

	public static void setShowAdditonalQuestionnaire() {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("additionalQuestionTime", System.currentTimeMillis());
		edit.commit();
	}


	public static int getStorytellingReadTimes() {
		return sp.getInt("readTimes", 0);
	}

	public static void addStorytellingReadTimes() {
		int time = sp.getInt("readTimes", 0);
		if (time < Config.STORYTELLING_READ_LIMIT) {
			SharedPreferences.Editor edit = sp.edit();
			edit.putInt("readTimes", (time + 1));
			edit.commit();
		}
	}

	public static void resetStorytellingReadTimes() {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("readTimes", 0);
		edit.commit();
	}




	public static int getUsedCounter() {
		return sp.getInt("usedCounter", 0);
	}

	public static void setUsedCounter(int counter) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("usedCounter", counter);
		edit.commit();
	}

	public static void setLatestTestCompleteTime(long timestamp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("testCompleteTime", timestamp);
		edit.commit();
	}

	public static long getLatestTestCompleteTime() {
		return sp.getLong("testCompleteTime", 0);
	}



	public static void setUpdateDetection(boolean update) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("updateDetection", update);
		edit.commit();
	}

	public static boolean getUpdateDetection() {
		return sp.getBoolean("updateDetection", false);
	}

	public static void setUpdateDetectionTimestamp(long timestamp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("updateDetectionTimestamp", timestamp);
		edit.commit();
	}

	public static long getUpdateDetectionTimestamp() {
		return sp.getLong("updateDetectionTimestamp", 0);
	}

	public static int getNotificationTimeIdx() {
		return sp.getInt("notificationTimeGap", 2);
	}

	public static int getNotificationTime() {
		int[] time = { 30, 60, 120, -1 };
		int idx = sp.getInt("notificationTimeGap", 2);
		return time[idx];
	}

	public static void setNotificationTimeIdx(int idx) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("notificationTimeGap", idx);
		edit.commit();
	}

	public static void setDebugDetectionTimestamp(long timestamp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("debugDetection", timestamp);
		edit.commit();
	}

	public static long getDebugDetectionTimestamp() {
		return sp.getLong("debugDetection", 0);
	}

	public static void setPrevShowWeekState(int week, int state) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("prevShowWeek", week);
		edit.putInt("prevShowWeekState", state);
		edit.commit();
	}

	public static int getPrevShowWeek() {
		return sp.getInt("prevShowWeek", 0);
	}

	public static int getPrevShowWeekState() {
		return sp.getInt("prevShowWeekState", 0);
	}

	public static void setPageChange(boolean change) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("pageChange", change);
		edit.commit();
	}

	public static boolean getPageChange() {
		// TimeValue curTV = TimeValue.generate(System.currentTimeMillis());
		// if (curTV.getWeek() > 11)
		// return false;
		return sp.getBoolean("pageChange", false);
	}

	public static boolean getUseNewSensor() {
		return sp.getBoolean("useNewSensor", false);
	}

	public static void setUseNewSensor(boolean isNew) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("useNewSensor", isNew);
		edit.commit();
	}

	public static boolean showNotificationDialog() {
		long lastTime = sp.getLong("lastShowNotification", 0);
		long curTime = System.currentTimeMillis();
		return (curTime - lastTime > AlarmManager.INTERVAL_DAY * 2);
	}

	public static void setShowedNotificationDialog() {
		long curTime = System.currentTimeMillis();
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("lastShowNotification", curTime);
		edit.commit();
	}

	public static void setStorytellingImageSize(int width, int height) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("storytellingImageWidth", width);
		edit.putInt("storytellingImageHeight", height);
		edit.commit();
	}

	public static Point getStorytellingImageSize() {
		int width = sp.getInt("storytellingImageWidth", 1);
		int height = sp.getInt("storytellingImageHeight", 1);
		return new Point(width, height);
	}

}