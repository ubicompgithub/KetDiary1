package com.ubicomp.ketdiary.test.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
	
	private Camera mCamera;
	private SurfaceHolder mSurfHolder;
	private Activity mActivity;
	
	public CameraPreview(Context context) {
		super(context);

		mSurfHolder = getHolder();
		mSurfHolder.addCallback(this);
	}

	public void set(Activity activity, Camera camera){
		mActivity = activity;
		mCamera = camera;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try{
			mCamera.setPreviewDisplay(mSurfHolder);
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
		} catch (Exception e){
			Toast.makeText(getContext(), "Camera can't be open", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}
	
}
