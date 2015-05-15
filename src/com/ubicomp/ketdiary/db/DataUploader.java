package com.ubicomp.ketdiary.db;

import java.io.IOException;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

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

		/** Constructor */
		public DataUploadTask() {
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			Log.d(TAG, "upload start");

			// UserInfo
			/*if (connectToServer() == ERROR) {
				Log.d(TAG, "FAIL TO CONNECT TO THE SERVER");
			}*/

			// EmotionDIY
			/*if (e_data != null) {
				for (int i = 0; i < e_data.length; ++i) {
					if (connectToServer(e_data[i]) == ERROR)
						Log.d(TAG, "FAIL TO UPLOAD - EMOTION DIY");
				}
			}*/
			
			// TestDetail
			Vector<Datatype.TestDetail> ttds = DBControl.inst.getNotUploadedTestDetail();
			for(int lx = 0;lx < ttds.size();lx++){
				if(connectToServer(ttds.get(lx)) == ERROR)
					Log.d(TAG, "FAIL TO UPLOAD - TestDetail");
			}
			
			return null;
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
		
		private int connectToServer(Datatype.TestDetail ttd){
			try {
				Log.d("a", "1");
				DefaultHttpClient httpClient = HttpSecureClientGenerator.getSecureHttpClient();
				Log.d("a", "2");
				HttpPost httpPost = HttpPostGenerator.genPost(ttd);
				if (!upload(httpClient, httpPost))
					return ERROR;
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
