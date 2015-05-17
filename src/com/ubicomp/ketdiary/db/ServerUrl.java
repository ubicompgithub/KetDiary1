package com.ubicomp.ketdiary.db;


/** 
 * Return Server's Url
 * @author mudream
 *
 */
public class ServerUrl {
	private String server_url = "https://140.112.30.171/drugfreediary/";
	
	public static ServerUrl inst = new ServerUrl();
	private ServerUrl(){}
	
	public String getTestDetailUrl(){
		return server_url + "test/add_test_detail.php";
	}
}
