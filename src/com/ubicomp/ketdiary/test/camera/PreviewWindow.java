package com.ubicomp.ketdiary.test.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
/**View shows the camera preview
 * 
 * @author Stanley Wang*/
public class PreviewWindow extends SurfaceView implements SurfaceHolder.Callback {

	private CameraRecorder cameraRecorder;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Activity context
	 * @param cameraRecorder
	 *            cameraRecorder
	 * @see CameraRecorder
	 */
	public PreviewWindow(Context context, CameraRecorder cameraRecorder) {
		super(context);
		this.cameraRecorder = cameraRecorder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			cameraRecorder.getCamera().setPreviewDisplay(holder);
			cameraRecorder.getCamera().startPreview();
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	/** Restart the preview */
	public void restartPreview() {
		if (cameraRecorder.getCamera() != null) {
			try {
				cameraRecorder.getCamera().stopPreview();
				cameraRecorder.getCamera().startPreview();
			} catch (Exception e) {
			}
		}
	}
}
