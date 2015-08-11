package com.ubicomp.ketdiary.data.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * Handler of recording the Bitmap
 * 
 * @author AndyChen
 */
public class PicFileHandler{
	private File file;
	private FileOutputStream out;
	private File directory;
	private String filename;
	private int count;
	private File mainStorage = null;
	
	private long ts;
	
	private Bitmap bitmap;
	private static final String TAG = "PIC_HANDLER";
	

	public PicFileHandler(int count, Bitmap bitmap) {

		this.count = count;
		this.bitmap = bitmap;
		
		ts = PreferenceControl.getUpdateDetectionTimestamp();
        File dir = MainStorage.getMainStorageDirectory();
        mainStorage = new File(dir, String.valueOf(ts));
 
		file = new File(mainStorage, "PIC_" + ts + "_" + count + ".sob");
		try {
			out = new FileOutputStream(file, true);
		} catch (IOException e) {
			Log.d(TAG, "FAIL TO OPEN");
			out = null;
		}
	}



	public void save(){
		if(out != null){
			try {
	            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); 
	            // bmp is your Bitmap instance
	            // PNG is a lossless format, the compression factor (100) is ignored
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (out != null) {
	                    out.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		}
		else{
			Log.e(TAG, "NULL TO WRITE");
		}
	}

	/** Close the writer */
	public void close() {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				Log.e(TAG, "FAIL TO CLOSE");
			}
		}
	}
}
