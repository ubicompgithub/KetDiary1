package com.ubicomp.ketdiary.db;


/** 
 * Return Server's Url
 * @author Andy
 */
public class ServerUrl {
	private static final String SERVER_URL = "https://140.112.30.171/drugfreediary/";
	
	/** create instance*/
	public static ServerUrl inst = new ServerUrl();
	private ServerUrl(){}
	
	/**
	 * 
	 * @return upload TestDetail server url
	 */
	public String getTestDetailUrl(){
		return SERVER_URL + "test/add_test_detail.php";
	}
	
	public String getPatientUrl(){
		return SERVER_URL + "test/Patient2.php";
	}
	
	public String getTestDetail2Url(){
		return SERVER_URL + "test/test_detail.php";
	}
	
	public String getTestResultUrl(){
		return SERVER_URL + "test/test_result.php";
	}
	
	public String getNoteAddUrl(){
		return SERVER_URL + "test/test_note_add.php";
	}
}
