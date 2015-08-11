package com.ubicomp.ketdiary.data.file;

import java.io.File;

import android.os.Environment;

import com.ubicomp.ketdiary.App;

/**
 * This class is used for getting mainStorage path
 * 
 * @author Stanley Wang
 */
public class MainStorage {

	/** File to record main storage path */
	private static File mainStorage = null;

	/**
	 * Get main storage directory path. If the path does not exist, make
	 * directory for it.
	 * 
	 * @return File directory path
	 */
	public static final File getMainStorageDirectory() {
		if (mainStorage == null) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
				mainStorage = new File(
						Environment.getExternalStorageDirectory(), "DrugfreeDiary");
			else
				mainStorage = new File(App.getContext().getFilesDir(),
						"DrugfreeDiary");
		}
		if (!mainStorage.exists())
			mainStorage.mkdirs();

		return mainStorage;
	}
}
