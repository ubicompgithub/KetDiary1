package com.ubicomp.ketdiary.db;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * Used for generating Http POST
 * 
 * @author Andy Chen
 */
public class HttpPostGenerator {
	
	//private static final String MainStorage = null;
	/** Instancelize */
	//public HttpPostGenerator inst = new HttpPostGenerator();
	//private HttpPostGenerator(){}
	
	/**
	 * Generate POST of TestDetail
	 * @param ttd
	 * @return
	 */
	public static HttpPost genPost(Datatype.TestDetail ttd){
		HttpPost httpPost = new HttpPost(ServerUrl.getTestDetailUrl());
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
	
	
	
	
	/**
	 * Generate POST of Patient
	 * @param p
	 * @return
	 */
	public static HttpPost genPost(){
		HttpPost httpPost = new HttpPost(ServerUrl.getPatientUrl());
		String uid = PreferenceControl.getUID();
		Log.i("debug", uid);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("USERID", uid));
		//@SuppressWarnings("deprecation")
		//Calendar c = PreferenceControl.getStartDate();
		//String joinDate = c.get(Calendar.YEAR) + "-"
		//		+ (c.get(Calendar.MONTH) + 1) + "-"
		//		+ c.get(Calendar.DAY_OF_MONTH);
		//nvps.add(new BasicNameValuePair("JOIN_DATE", joinDate));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}
	
	/**
	 * Generate POST of TestResult (Contain data and file)
	 * 
	 * @param data
	 *            TestResult
	 * @return HttpPost contains TestResult
	 * @see ubicomp.soberdiary.data.structure.TestResult
	 */
	public static HttpPost genPost( TestResult data) {
		//SERVER_URL_DETECTION = ServerUrl.SERVER_URL_DETECTION();
		File mainStorageDir = MainStorage.getMainStorageDirectory();
		String uid = PreferenceControl.getUID();
		String deviceId=PreferenceControl.getDeviceId();
		
		HttpPost httpPost = new HttpPost(ServerUrl.getTestResultUrl());
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("uid", uid);
		builder.addTextBody("data[]",
				String.valueOf(data.tv.getTimestamp()));
		builder.addTextBody("data[]", String.valueOf(deviceId));
		builder.addTextBody("data[]", String.valueOf(data.result));
		builder.addTextBody("data[]", String.valueOf(data.cassette_id));
		builder.addTextBody("data[]", String.valueOf(data.isPrime));
		builder.addTextBody("data[]", String.valueOf(data.isFilled));
		//builder.addTextBody("data[]", String.valueOf(data.getScore()));

		String _ts = String.valueOf(data.tv.getTimestamp());
		File[] imageFiles;
		File testFile, detectionFile;
		imageFiles = new File[3];

		testFile = new File(mainStorageDir.getPath() + File.separator + _ts
				+ File.separator + "voltage.txt");

		detectionFile = new File(mainStorageDir.getPath() + File.separator
				+ _ts + File.separator + "color_raw.txt");

		for (int i = 0; i < imageFiles.length; ++i)
			imageFiles[i] = new File(mainStorageDir.getPath() + File.separator
					+ _ts + File.separator + "IMG_" + _ts + "_" + (i + 1)
					+ ".sob");

		if (testFile.exists())
			builder.addPart("file[]", new FileBody(testFile));
		if (detectionFile.exists())
			builder.addPart("file[]", new FileBody(detectionFile));
		for (int i = 0; i < imageFiles.length; ++i)
			if (imageFiles[i].exists())
				builder.addPart("file[]", new FileBody(imageFiles[i]));

		httpPost.setEntity(builder.build());
		return httpPost;
	}
	
	/**
	 * Generate POST of NoteAdd
	 * @param data
	 * @return
	 */
	
	public static HttpPost genPost(NoteAdd data){
		HttpPost httpPost = new HttpPost(ServerUrl.getNoteAddUrl());
		String uid = PreferenceControl.getUID();
		String deviceId=PreferenceControl.getDeviceId();
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isAfterTest)));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.recordTv)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.category)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.type)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.items)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.impact)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.description)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}
	
	public static HttpPost genPost(TestDetail data){
		HttpPost httpPost = new HttpPost(ServerUrl.getNoteAddUrl());
		String uid = PreferenceControl.getUID();
		String deviceId=PreferenceControl.getDeviceId();
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isAfterTest)));
		
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.getTimestamp())));
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.recordTv)));
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.category)));
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.type)));
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.items)));
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.impact)));
		//nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.description)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}
	
	
}
