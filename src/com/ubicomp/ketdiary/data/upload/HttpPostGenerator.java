package com.ubicomp.ketdiary.data.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.data.file.MainStorage;
import com.ubicomp.ketdiary.data.structure.CopingSkill;
import com.ubicomp.ketdiary.data.structure.ExchangeHistory;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.QuestionTest;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.check.WeekNumCheck;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

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
		
	private static final int PicNum = 8;
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
		nvps.add(new BasicNameValuePair("uid", uid));
		//@SuppressWarnings("deprecation")
		
		Calendar c = PreferenceControl.getStartDate();
		String joinDate = c.get(Calendar.YEAR) + "-"
				+ (c.get(Calendar.MONTH) + 1) + "-"
				+ c.get(Calendar.DAY_OF_MONTH);

		nvps.add(new BasicNameValuePair("userData[]", joinDate));
		nvps.add(new BasicNameValuePair("userData[]", PreferenceControl.getDeviceId()));
		nvps.add(new BasicNameValuePair("userData[]", String
				.valueOf(PreferenceControl.getUsedCounter())));
		PackageInfo pinfo;
		try {
			pinfo = App.getContext().getPackageManager()
					.getPackageInfo(App.getContext().getPackageName(), 0);
			String versionName = pinfo.versionName;
			nvps.add(new BasicNameValuePair("userData[]", versionName));
		} catch (NameNotFoundException e) {
		}
		nvps.add(new BasicNameValuePair("userData[]", String.valueOf( WeekNumCheck.getWeek(System.currentTimeMillis()))));		
		nvps.add(new BasicNameValuePair("userData[]", String.valueOf( PreferenceControl.getPosition()-10)));
		nvps.add(new BasicNameValuePair("userData[]", String.valueOf( PreferenceControl.getPoint())));
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
		builder.addTextBody("data[]", String.valueOf(data.getScore()));
		builder.addTextBody("data[]", String.valueOf(data.getTv().getWeek()));
		

		String _ts = String.valueOf(data.tv.getTimestamp());
		File[] imageFiles;
		File[] picFiles;
		File testFile, detectionFile;
		int fileNum = new File(mainStorageDir.getPath() + File.separator + _ts
				+ File.separator).listFiles().length;
		
		Log.d("FileNum: ", _ts + " Num: "+ fileNum);
		
		builder.addTextBody("data[]", String.valueOf(fileNum));
		
		imageFiles = new File[fileNum - PicNum];
		picFiles = new File[PicNum];
		
		testFile = new File(mainStorageDir.getPath() + File.separator + _ts
				+ File.separator + "voltage.txt");

		detectionFile = new File(mainStorageDir.getPath() + File.separator
				+ _ts + File.separator + "color_raw.txt");

		for (int i = 0; i < imageFiles.length; ++i)
			imageFiles[i] = new File(mainStorageDir.getPath() + File.separator
					+ _ts + File.separator + "IMG_" + _ts + "_" + (i + 1)
					+ ".sob");
		
		for (int i = 0; i < picFiles.length; ++i)
			picFiles[i] = new File(mainStorageDir.getPath() + File.separator
					+ _ts + File.separator + "PIC_" + _ts + "_" + (i)
					+ ".sob");

		if (testFile.exists())
			builder.addPart("file[]", new FileBody(testFile));
		if (detectionFile.exists())
			builder.addPart("file[]", new FileBody(detectionFile));
		for (int i = 0; i < imageFiles.length; ++i)
			if (imageFiles[i].exists()){
				builder.addPart("file[]", new FileBody(imageFiles[i]));
				Log.d("image", imageFiles[i].getName());
			}
		
		for (int i = 0; i < picFiles.length; ++i)
			if (picFiles[i].exists()){
				builder.addPart("file[]", new FileBody(picFiles[i]));
				Log.d("Pic", picFiles[i].getName());
			}
		
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
		String deviceId = PreferenceControl.getDeviceId();
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getIsAfterTest())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getRecordTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTimeSlot())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getCategory())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getType())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getItems())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getImpact())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getDescription())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}
	
	public static HttpPost genPost(TestDetail data){
		HttpPost httpPost = new HttpPost(ServerUrl.getTestDetail2Url());
		String uid = PreferenceControl.getUID();
		String deviceId=PreferenceControl.getDeviceId();
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		nvps.add(new BasicNameValuePair("data[]", deviceId));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getCassetteId())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getFailedState())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getFirstVoltage())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getSecondVoltage())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getDevicePower())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getColorReading())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getConnectionFailRate())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getFailedReason())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getHardwareVersion())));
		PackageInfo pinfo;
		try {
			pinfo = App.getContext().getPackageManager()
					.getPackageInfo(App.getContext().getPackageName(), 0);
			String versionName = pinfo.versionName;
			nvps.add(new BasicNameValuePair("data[]", versionName));
		} catch (NameNotFoundException e) {
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}
	
	/**
	 * Generate POST of test results of QuestionTest
	 * 
	 * @param data
	 *            QuestionTest
	 * @return HttpPost contains QuestionTest
	 * @see ubicomp.soberdiary.data.structure.QuestionTest
	 */
	public static HttpPost genPost(QuestionTest data) {
		HttpPost httpPost = new HttpPost(ServerUrl.getQuestionTestUrl());
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getQuestionType())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getisCorrect())));
		nvps.add(new BasicNameValuePair("data[]", data.getSelection()));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getChoose())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}
	
	/**
	 * Generate POST of test results of CopingSkill
	 * 
	 * @param data
	 *            CopingSkill
	 * @return HttpPost contains QuestionTest
	 * @see ubicomp.soberdiary.data.structure.QuestionTest
	 */
	public static HttpPost genPost(CopingSkill data) {
		HttpPost httpPost = new HttpPost(ServerUrl.getCopingSkillUrl());
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getSkillType())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getSkillSelect())));
		nvps.add(new BasicNameValuePair("data[]", data.getRecreation()));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}
	
	/**
	 * Generate POST of ClickLog
	 * 
	 * @param logFile
	 *            file of the click log
	 * @return HttpPost contains click log file
	 */
	public static HttpPost genPost(File logFile) {
		HttpPost httpPost = new HttpPost(ServerUrl.SERVER_URL_CLICKLOG());
		String uid = PreferenceControl.getUID();

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("uid", uid);
		if (logFile.exists()) {
			builder.addPart("file[]", new FileBody(logFile));
		}
		httpPost.setEntity(builder.build());
		return httpPost;
	}
	
	/**
	 * Generate POST of credits exchange history
	 * 
	 * @param data
	 *            ExchangeHistory
	 * @return HttpPost contains ExchangeHistory
	 * @see ubicomp.soberdiary.data.structure.ExchangeHistory
	 */
	public static HttpPost genPost(ExchangeHistory data) {
		HttpPost httpPost = new HttpPost(ServerUrl.SERVER_URL_EXCHANGE_HISTORY());
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv()
				.getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data
				.getExchangeNum())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}
	
	/**
	 * Generate POST of ClickLog
	 * 
	 * @param logFile
	 *            file of the click log
	 * @return HttpPost contains click log file
	 */
	/*
	public static HttpPost genPost(File logFile) {
		SERVER_URL_CLICKLOG = ServerUrl.SERVER_URL_CLICKLOG();
		HttpPost httpPost = new HttpPost(SERVER_URL_CLICKLOG);
		String uid = PreferenceControl.getUID();

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("uid", uid);
		if (logFile.exists()) {
			builder.addPart("file[]", new FileBody(logFile));
		}
		httpPost.setEntity(builder.build());

		return httpPost;
	}*/
	
	
}
