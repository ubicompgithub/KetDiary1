package com.ubicomp.ketdiary.db;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;

/**
 * Used for generating Http POST
 * 
 * @author Stanley Wang
 */
public class HttpPostGenerator {
	
	/** Instancelize */
	public HttpPostGenerator inst = new HttpPostGenerator();
	private HttpPostGenerator(){}
	
	/**
	 * Generate POST of TestDetail
	 * @param ttd
	 * @return
	 */
	public static HttpPost genPost(Datatype.TestDetail ttd){
		HttpPost httpPost = new HttpPost(ServerUrl.inst.getTestDetailUrl());
		String uid = DBControl.inst.getUserID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("USERNAME", uid));
		nvps.add(new BasicNameValuePair("RESULT", String.valueOf(ttd.result)));
		@SuppressWarnings("deprecation")
		String str_date = String.valueOf(ttd.date.getYear()) + "-"
						+ String.valueOf(ttd.date.getMonth()) + "-"
						+ String.valueOf(ttd.date.getDay());
		nvps.add(new BasicNameValuePair("DATE", str_date));
		nvps.add(new BasicNameValuePair("TIMESLOT", String.valueOf(ttd.time_trunk)));
		nvps.add(new BasicNameValuePair("ISFILLED", String.valueOf(ttd.is_filled)));
		nvps.add(new BasicNameValuePair("CATAID", String.valueOf(ttd.catagory_id)));
		nvps.add(new BasicNameValuePair("TYPEID", String.valueOf(ttd.type_id)));
		nvps.add(new BasicNameValuePair("REASONID", String.valueOf(ttd.reason_id)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}
	
	
}
