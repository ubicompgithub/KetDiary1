package com.ubicomp.ketdiary.test.camera;

import android.os.Handler;
import android.os.Message;

public class CameraRunHandler extends Handler {

	public CameraRecorder cameraRecorder;

	public CameraRunHandler(CameraRecorder cameraRecorder) {
		this.cameraRecorder = cameraRecorder;
	}

	public void handleMessage(Message msg) {
		if (msg.what == 0)
			cameraRecorder.takePicture();
		else{
			cameraRecorder.closeFail(msg.what-1);
		}
	}

}
