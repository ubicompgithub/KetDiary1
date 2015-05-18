package com.ubicomp.ketdiary.db;


/** 
 * Return Server's Url
 * @author mudream
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
}
