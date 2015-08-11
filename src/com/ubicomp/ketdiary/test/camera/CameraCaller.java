package com.ubicomp.ketdiary.test.camera;

import android.graphics.Point;
import android.widget.FrameLayout;

import com.ubicomp.ketdiary.test.camera.Tester;

/**
 * Interface of calling camera functions
 * 
 * @author Stanley Wang
 * */
public interface CameraCaller extends Tester {
	
	/** Tell caller that the BrAC test stopped because of failing
	 * @param fail fail code*/
	public void stopByFail(int fail);

	/** Generate camera preview frame
	 * @retun FrameLayout contains camera preview*/
	public FrameLayout getPreviewFrameLayout();

	/** Get preview view size
	 * @return Preview view size in Point*/
	public Point getPreviewSize();
}
