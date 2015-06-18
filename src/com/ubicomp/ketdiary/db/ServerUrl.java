package com.ubicomp.ketdiary.db;



/** 
 * Return Server's Url
 * @author Andy Chen
 */
public class ServerUrl {
	private static final String SERVER_URL = "https://140.112.30.171/rehabdiary/";
	
	/** create instance*/
	//public static ServerUrl inst = new ServerUrl();
	//private ServerUrl(){}
	
	/**
	 * 
	 * @return upload TestDetail server url
	 */
	public static String getTestDetailUrl(){
		return SERVER_URL + "test/add_test_detail.php";
	}
	
	public static String getPatientUrl(){
		return SERVER_URL + "test/Patient2.php";
	}
	
	public static String getTestDetail2Url(){
		return SERVER_URL + "test/TestDetail.php";
	}
	
	public static String getTestResultUrl(){
		return SERVER_URL + "test/TestResult.php";
	}
	
	public static String getNoteAddUrl(){
		return SERVER_URL + "test/NoteAdd.php";
	}
	
	/**
	 * URL for querying the long-term ranking
	 * 
	 * @return url
	 */
	public static String SERVER_URL_RANK_ALL() {
		final String URL = "rank/rankAll.php";
		return SERVER_URL + URL;
	}
	/**
	 * URL for querying the short-term ranking
	 * 
	 * @return url
	 */
	public static String SERVER_URL_RANK_WEEK() {
		final String URL = "rank/rankWeek.php";
		return SERVER_URL + URL;
	}
	
	
}
