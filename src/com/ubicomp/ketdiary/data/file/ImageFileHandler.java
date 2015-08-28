	package com.ubicomp.ketdiary.data.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.ubicomp.ketdiary.test.camera.CameraRecorder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Handler for saving the DCIMs of the user
 * 
 * @author Stanley Wang
 */
public class ImageFileHandler extends Handler {
	private File file;
	private FileOutputStream writer;
	private File directory;
	private String timestamp;
	private CameraRecorder recorder;

	private static final String TAG = "IMAGE_FILE_HANDLER";

	/**
	 * Constructor
	 * 
	 * @param directory
	 *            directory of the detection data
	 * @param timestamp
	 *            string of the detection timestamp
	 */
	public ImageFileHandler(File directory, String timestamp) {
		this.directory = directory;
		this.timestamp = timestamp;
	}

	/**
	 * Set the camera recorder
	 * 
	 * @param recorder
	 *            camera recorder
	 */
	public void setRecorder(CameraRecorder recorder) {
		this.recorder = recorder;
	}

	@Override
	public void handleMessage(Message msg) {

		int count = msg.what;
		String file_name = "IMG_" + timestamp + "_" + count + ".sob";

		file = new File(directory, file_name);
		byte[] img = msg.getData().getByteArray("Img");
		try {
			 writer = new FileOutputStream(file);
			 BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
             bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
             ByteArrayInputStream inputStream = new ByteArrayInputStream(img);
             Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bmpFactoryOptions);
             bitmap.compress(CompressFormat.JPEG, 30, writer);
           			
			//writer.write(img);
			writer.close();
		} catch (Exception e) {
			Log.d(TAG, "FAIL TO OPEN");
			try {
				writer.close();
			} catch (Exception e1) {
			}
			writer = null;
		} finally {
			try {
				writer.close();
			} catch (Exception e1) {
			}
		}
		
		
		//if(count)
		//recorder.closeSuccess();
		//if (count == 3) {
		//	recorder.closeSuccess();
		//}
	}
}
