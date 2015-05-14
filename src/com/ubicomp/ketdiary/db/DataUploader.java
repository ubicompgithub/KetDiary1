package com.ubicomp.ketdiary.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Used for upload data to the server
 * 
 * @author Stanley Wang
 */
public class DataUploader {

	private static DataUploadTask uploader = null;

	private static final String TAG = "UPLOAD";

	/** Upload the data & remove uploaded data */
	public static void upload() {

		if (SynchronizedLock.sharedLock.tryLock()) {
			SynchronizedLock.sharedLock.lock();
			uploader = new DataUploadTask();
			uploader.execute();
		}
	}

	/** AsyncTask handles the data uploading task */
	public static class DataUploadTask extends AsyncTask<Void, Void, Void> {

		
		/** ENUM UPLOAD ERROR */
		public static final int ERROR = -1;
		/** ENUM UPLOAD SUCCESS */
		public static final int SUCCESS = 1;
		private File logDir;

		/** Constructor */
		public DataUploadTask() {
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			Log.d(TAG, "upload start");

			// UserInfo
			if (connectToServer() == ERROR) {
				Log.d(TAG, "FAIL TO CONNECT TO THE SERVER");
			}

			// EmotionDIY
			/*if (e_data != null) {
				for (int i = 0; i < e_data.length; ++i) {
					if (connectToServer(e_data[i]) == ERROR)
						Log.d(TAG, "FAIL TO UPLOAD - EMOTION DIY");
				}
			}*/
			return null;
		}

		private String[] getNotUploadedClickLog() {
			if (!logDir.exists()) {
				Log.d(TAG, "Cannot find clicklog dir");
				return null;
			}

			String[] all_logs = null;
			String latestUpload = null;
			File latestUploadFile = new File(logDir, "latest_uploaded");
			if (latestUploadFile.exists()) {
				try {
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new FileReader(latestUploadFile));
					latestUpload = br.readLine();
				} catch (IOException e) {
				}
			}
			all_logs = logDir.list(new logFilter(latestUpload));
			return all_logs;
		}

		private class logFilter implements FilenameFilter {
			String _latestUpload;
			String today;

			@SuppressLint("SimpleDateFormat")
			public logFilter(String latestUpload) {
				_latestUpload = latestUpload;
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
				today = sdf.format(cal.getTime()) + ".txt";
			}

			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg1.equals("latest_uploaded"))
					return false;
				else {
					if (today.compareTo(arg1) > 0)
						if (_latestUpload == null || (_latestUpload != null && (arg1.compareTo(_latestUpload)) > 0))
							return true;
					return false;
				}
			}
		}

		private void set_uploaded_logfile(String name) {
			File latestUploadFile = new File(logDir, "latest_uploaded");
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(latestUploadFile));
				writer.write(name);
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				writer = null;
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			uploader = null;
			SynchronizedLock.sharedLock.unlock();
		}

		@Override
		protected void onCancelled() {
			uploader = null;
			SynchronizedLock.sharedLock.unlock();
		}

		private int connectToServer() {
			try {
				DefaultHttpClient httpClient = HttpSecureClientGenerator.getSecureHttpClient();
				HttpPost httpPost;// = HttpPostGenerator.genPost();
				//if (!upload(httpClient, httpPost))
				//	return ERROR;
			} catch (Exception e) {
				Log.d(TAG, "EXCEPTION:" + e.toString());
				return ERROR;
			}
			return SUCCESS;
		}

		private boolean upload(HttpClient httpClient, HttpPost httpPost) {
			HttpResponse httpResponse;
			ResponseHandler<String> res = new BasicResponseHandler();
			boolean result = false;
			try {
				httpResponse = httpClient.execute(httpPost);
				int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				result = (httpStatusCode == HttpStatus.SC_OK);
				if (result) {
					String response = res.handleResponse(httpResponse).toString();
					Log.d(TAG, "response=" + response);
					result &= (response.contains("upload success"));
					Log.d(TAG, "result=" + result);
				} else {
					Log.d(TAG, "fail result=" + result);
				}
			} catch (ClientProtocolException e) {
				Log.d(TAG, "ClientProtocolException " + e.toString());
			} catch (IOException e) {
				Log.d(TAG, "IOException " + e.toString());
			} finally {
				if (httpClient != null) {
					ClientConnectionManager ccm = httpClient.getConnectionManager();
					if (ccm != null)
						ccm.shutdown();
				}
			}
			return result;
		}
	}

}
