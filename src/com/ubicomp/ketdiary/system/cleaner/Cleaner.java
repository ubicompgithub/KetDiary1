package com.ubicomp.ketdiary.system.cleaner;

import java.io.File;

import android.app.AlarmManager;

import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.file.MainStorage;
import com.ubicomp.ketdiary.data.structure.TestResult;

public class Cleaner {

	public static void clean() {

		long cur_ts = System.currentTimeMillis() - AlarmManager.INTERVAL_DAY;

		DatabaseControl db = new DatabaseControl();
		//Detection[] detections = db.getAllNotUploadedDetection();
		TestResult[] testResults = null;

		File mainStorageDir = MainStorage.getMainStorageDirectory();

		for (File file : mainStorageDir.listFiles()) {
			String name = file.getName();
			if (name == null)
				continue;
			if (name.contains("audio_records"))
				continue;
			else if (name.contains("sequence_log"))
				continue;
			else if (name.contains("feedbacks")) {
				continue;
			} else if (testResults != null) {
				boolean uploaded = true;
				for (int j = 0; j < testResults.length; ++j) {
					String ts = String.valueOf(testResults[j].getTv()
							.getTimestamp());
					if (name.contains(ts)) {
						uploaded = false;
						break;
					}
				}
				if (!uploaded)
					continue;
			}
			try {
				long dir_time = Long.valueOf(name);
				if (dir_time > cur_ts) {
					continue;
				}
			} catch (Exception e) {
			}
			if (file.isDirectory()) {
				recursiveDelete(file);
			}
		}
	}

	private static void recursiveDelete(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				recursiveDelete(child);
			}
		}
		file.delete();
	}
}
