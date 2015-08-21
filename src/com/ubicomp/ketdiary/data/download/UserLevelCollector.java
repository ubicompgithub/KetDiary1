package com.ubicomp.ketdiary.data.download;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.util.Log;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.data.upload.ServerUrl;

public class UserLevelCollector {

	private static String SERVER_URL_RANK_ALL;
	private static String SERVER_URL_RANK_WEEK;
	private static String TAG = "UserLevelCollect";

	private Context context;
	private ResponseHandler<String> responseHandler;

	public UserLevelCollector(Context context) {
		this.context = context;
		SERVER_URL_RANK_ALL = ServerUrl.SERVER_URL_RANK_ALL();
		SERVER_URL_RANK_WEEK = ServerUrl.SERVER_URL_RANK_WEEK();
		responseHandler = new BasicResponseHandler();
	}

	public Rank[] update() {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();

			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			InputStream instream = context.getResources().openRawResource(
					ServerUrl.SERVER_CERTIFICATE());
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_RANK_ALL);
			httpClient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
					3000);

			HttpResponse httpResponse;
			httpResponse = httpClient.execute(httpPost);
			String responseString = responseHandler
					.handleResponse(httpResponse);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			Log.d(TAG, responseString);
			
			if (responseString != null && httpStatusCode == HttpStatus.SC_OK) {
				Rank[] ranks = parse(responseString);
				return ranks;
			}

		} catch (Exception e) {
		}

		return null;
	}

	public Rank[] updateShort() {
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			InputStream instream = context.getResources().openRawResource(
					ServerUrl.SERVER_CERTIFICATE());
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_RANK_WEEK);
			httpClient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
					3000);

			HttpResponse httpResponse;
			httpResponse = httpClient.execute(httpPost);
			String responseString = responseHandler
					.handleResponse(httpResponse);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();

			if (responseString != null && httpStatusCode == HttpStatus.SC_OK) {
				Rank[] ranks = parseShort(responseString);
				return ranks;
			}

		} catch (Exception e) {
		}

		return null;
	}

	Rank[] parse(String response) {
		if (response == null)
			return null;
		response = response.substring(2, response.length() - 2);
		String[] tmp = response.split("],");
		if (tmp.length == 0)
			return null;
		Rank[] ranks;
		ranks = new Rank[tmp.length];
		for (int i = 0; i < tmp.length; ++i) {
			if (tmp[i].charAt(0) == '[')
				tmp[i] = tmp[i].substring(1, tmp[i].length());

			String[] items = tmp[i].split(",");
			String uid = items[0].substring(1, items[0].length() - 1);
			int level;
			if (items[1].equals("null"))
				level = 0;
			else
				level = Integer.valueOf(items[1]);
			int test = Integer.valueOf(items[2]);
			int note = Integer.valueOf(items[3]);
			int question = Integer.valueOf(items[4]);
			int coping = Integer.valueOf(items[5]);
			int[] additionals = new int[4];
			for (int j = 0; j < additionals.length; ++j)
				additionals[j] = Integer.valueOf(items[6+j]);
			
			Log.i(TAG, uid+" "+level+" "+test+" "+note+" "+question+" "+coping+" "+additionals[0]
					+" "+additionals[1]+" "+additionals[2]+" "+additionals[3]);
			
			ranks[i] = new Rank(uid, level, test, note, question, coping, additionals);

		}

		return ranks;
	}

	Rank[] parseShort(String response) {
		if (response == null)
			return null;
		response = response.substring(2, response.length() - 2);
		String[] tmp = response.split("],");
		if (tmp.length == 0)
			return null;
		Rank[] ranks;
		ranks = new Rank[tmp.length];
		for (int i = 0; i < tmp.length; ++i) {
			if (tmp[i].charAt(0) == '[')
				tmp[i] = tmp[i].substring(1, tmp[i].length());
			String[] items = tmp[i].split(",");
			String uid = items[0].substring(1, items[0].length() - 1);
			int level;
			if (items[1].equals("null"))
				level = 0;
			else
				level = Integer.valueOf(items[1]);
			ranks[i] = new Rank(uid, level);
		}

		return ranks;
	}


}
