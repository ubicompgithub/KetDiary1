package com.ubicomp.ketdiary.data.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Handler of recording the BrAC detection
 * 
 * @author Stanley Wang
 */
public class VoltageFileHandler extends Handler {
	private File file;
	private BufferedWriter writer;
	private File directory;
	private String timestamp;

	private static final String TAG = "VOLTAGE_VALUE_HANDLER";

	/**
	 * Constructor
	 * 
	 * @param directory
	 *            directory of the detection data
	 * @param timestamp
	 *            string of the detection timestamp
	 */
	public VoltageFileHandler(File directory, String timestamp) {
		this.directory = directory;
		this.timestamp = timestamp;
		//file = new File(directory, timestamp + ".txt");
		file = new File(directory, "voltage.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d(TAG, "FAIL TO OPEN");
			writer = null;
		}
	}

	/**
	 * get string of the detection timestamp
	 * 
	 * @return string of the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * get the directory of the detection data
	 * 
	 * @return directory of the detection data
	 */
	public File getDirectory() {
		return directory;
	}

	@Override
	public void handleMessage(Message msg) {
		String str = msg.getData().getString("VOLTAGE");
		if (writer != null) {
			try {
				writer.write(str);
			} catch (IOException e) {
				Log.d(TAG, "FAIL TO WRITE");
			}
		} else {
			Log.d(TAG, "NULL TO WRITE");
		}
	}

	/** Close the writer */
	public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				Log.d(TAG, "FAIL TO CLOSE");
			}
		}
	}
}
