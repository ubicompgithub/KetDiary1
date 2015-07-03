package com.ubicomp.ketdiary.db;

import java.io.IOException;

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

import com.ubicomp.ketdiary.data.structure.CopingSkill;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.QuestionTest;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.data.structure.TestResult;

/**
 * Used for upload data to the server
 * 
 * @author Andy Chen
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
		
		
		private DatabaseControl db;
		
		/** ENUM UPLOAD ERROR */
		public static final int ERROR = -1;
		/** ENUM UPLOAD SUCCESS */
		public static final int SUCCESS = 1;

		/** Constructor */
		public DataUploadTask() {
			db = new DatabaseControl();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			Log.d(TAG, "upload start");

			
			/*if (connectToServer() == ERROR) {
				Log.d(TAG, "FAIL TO CONNECT TO THE SERVER");
			}*/


			
			// Patient
			// UserInfo
			if(connectToServer() == ERROR)
				Log.d(TAG, "FAIL TO UPLOAD - Patient");
			
			
			// TestResult		
			TestResult testResults[] = db.getAllNotUploadedTestResult();
			if (testResults != null) {
				for (int i = 0; i < testResults.length; ++i) {
					if (connectToServer(testResults[i]) == ERROR)
						Log.d(TAG, "FAIL TO UPLOAD - TESTRESULT");
				}
			}
			
			// NoteAdd
			NoteAdd noteAdds[] = db.getNotUploadedNoteAdd();
			if (noteAdds != null) {
				for (int i = 0; i < noteAdds.length; ++i) {
					if (connectToServer(noteAdds[i]) == ERROR)
						Log.d(TAG, "FAIL TO UPLOAD - NOTEADD");
				}
			}
			// TestDetail
			TestDetail testDetails[] = db.getNotUploadedTestDetail();
			if (testDetails != null) {
				for (int i = 0; i < testDetails.length; ++i) {
					if (connectToServer(testDetails[i]) == ERROR)
						Log.d(TAG, "FAIL TO UPLOAD - TestDetail");
				}
			}			
			
			// QuestionTest
			QuestionTest questionTests[] = db.getNotUploadedQuestionTest();
			if (questionTests != null) {
				for (int i = 0; i < questionTests.length; ++i) {
					if (connectToServer(questionTests[i]) == ERROR)
					Log.d(TAG, "FAIL TO UPLOAD - QuestionTest");
				}
			}
			
			// CopingSkill
			CopingSkill copingSkills[] = db.getNotUploadedCopingSkill();
			if (copingSkills != null) {
				for (int i = 0; i < copingSkills.length; ++i) {
					if (connectToServer(copingSkills[i]) == ERROR)
					Log.d(TAG, "FAIL TO UPLOAD - CopingSkill");
				}
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
				HttpPost httpPost = HttpPostGenerator.genPost();
				if (!upload(httpClient, httpPost))
					return ERROR;
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
		
		
		private int connectToServer(TestResult data) {
			try {
				DefaultHttpClient httpClient = HttpSecureClientGenerator
						.getSecureHttpClient();
				HttpPost httpPost = HttpPostGenerator.genPost(data);
				if (upload(httpClient, httpPost)){
					db.setTestResultUploaded(data.getTv().getTimestamp());
					Log.d(TAG, "Upload TestResult Success.");
				}
				else
					return ERROR;
			} catch (Exception e) {
				Log.d(TAG, "EXCEPTION:" + e.toString());
				return ERROR;
			}
			return SUCCESS;
		}
		
		private int connectToServer(NoteAdd data) {
			try {
				DefaultHttpClient httpClient = HttpSecureClientGenerator
						.getSecureHttpClient();
				HttpPost httpPost = HttpPostGenerator.genPost(data);
				if (upload(httpClient, httpPost)){
					db.setNoteAddUploaded(data.getTv().getTimestamp());
					Log.d(TAG, "Upload NoteAdd Success.");
				}
				else
					return ERROR;
			} catch (Exception e) {
				Log.d(TAG, "EXCEPTION:" + e.toString());
				return ERROR;
			}
			return SUCCESS;
		}
		
		private int connectToServer(TestDetail data) {
			try {
				DefaultHttpClient httpClient = HttpSecureClientGenerator
						.getSecureHttpClient();
				HttpPost httpPost = HttpPostGenerator.genPost(data);
				if (upload(httpClient, httpPost)){
					db.setTestDetailUploaded(data.getTv().getTimestamp());
					Log.d(TAG, "Upload TestDetail Success.");
				}
				else
					return ERROR;
			} catch (Exception e) {
				Log.d(TAG, "EXCEPTION:" + e.toString());
				return ERROR;
			}
			return SUCCESS;
		}
		
		private int connectToServer(QuestionTest data) {
			try {
				DefaultHttpClient httpClient = HttpSecureClientGenerator
						.getSecureHttpClient();
				HttpPost httpPost = HttpPostGenerator.genPost(data);
				if (upload(httpClient, httpPost)){
					db.setQuestionTestUploaded(data.getTv().getTimestamp());
					Log.d(TAG, "Upload QuestionTest Success.");
				}
				else
					return ERROR;
			} catch (Exception e) {
				Log.d(TAG, "EXCEPTION:" + e.toString());
				return ERROR;
			}
			return SUCCESS;
		}
		
		private int connectToServer(CopingSkill data) {
			try {
				DefaultHttpClient httpClient = HttpSecureClientGenerator
						.getSecureHttpClient();
				HttpPost httpPost = HttpPostGenerator.genPost(data);
				if (upload(httpClient, httpPost)){
					db.setCopingSkillUploaded(data.getTv().getTimestamp());
					Log.d(TAG, "Upload CopingSkill Success.");
				}
				else
					return ERROR;
			} catch (Exception e) {
				Log.d(TAG, "EXCEPTION:" + e.toString());
				return ERROR;
			}
			return SUCCESS;
		}
		
		
		// handle upload respond message
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
