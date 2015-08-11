/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubicomp.ketdiary.noUse;

import java.io.File;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ubicomp.ketdiary.data.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;
//import org.opencv.highgui.Highgui;
import com.ubicomp.ketdiary.test.color.ColorDetectListener;

/**
 *
 * @author larry
 */
public class TestStripDetection4 extends Handler{

    
    private static final String TAG = "TestStripDetection2";
    private static final String FILE = "Sample";
    private String file_name = null;
    private File file;
    public float check = 0 ;
    ColorDetectListener colorDetectListener;
    public TestStripDetection4(ColorDetectListener colorDetectListener) {
    	this.colorDetectListener = colorDetectListener;
    }
    
    
    @Override
	public void handleMessage(Message msg) {
    	File mainStorage = null;  
        long ts = PreferenceControl.getUpdateDetectionTimestamp();
        File dir = MainStorage.getMainStorageDirectory();
        mainStorage = new File(dir, String.valueOf(ts));
        file_name = "PIC_" + ts + "_" + 0 + ".sob";
        file = new File(mainStorage, file_name);
        imgDetect(null);
    }
    
    public void imgDetect(Bitmap bitmap) {
//        Mat matOrigin = new Mat ();
//        Utils.bitmapToMat(bitmap, matOrigin);
            
        Mat matOrigin = Imgcodecs.imread(file.getAbsolutePath());
        Mat matROI = matOrigin.submat(60, 160, 80, 240);

        //matOrigin.release();
        Mat matClone = new Mat(matROI.cols(),matROI.rows(), CvType.CV_8UC1);
        Imgproc.cvtColor(matROI, matClone, Imgproc.COLOR_RGB2GRAY);

        Mat matFilter = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC3);

        Mat kernel = new Mat(10, 10, CvType.CV_32F);
        kernel.setTo(new Scalar((double)1/100));

        Imgproc.filter2D(matClone, matFilter, -1, kernel);

        kernel.release();
        matROI.release();
        Mat matCanny = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC1);
        Imgproc.Canny(matFilter, matCanny, 20, 100, 3, true);
        matFilter.release();
        Mat matLines = new Mat();

        int threshold = 20;
        int minLineSize = 10;
        int lineGap = 10;
        Imgproc.HoughLinesP(matCanny, matLines, 1, Math.PI/180, threshold, lineGap , minLineSize);

        matCanny.release();
        Log.i(TAG, "Num of lines: " + matLines.cols());   // Warning: The number of lines is different from java version.

        int xmin = 160;
        int xmax = 0;
        int ymin = 100;
        int ymax = 0;

        for (int x = 0; x < matLines.cols(); x++)
        {
            double[] vec = matLines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

//            Log.i(TAG, "x1: "+ x1 + ", x2: " + x2 + ", y1: " + y1 + ", y2: " + y2);
            if( xmin > (int) Math.min(x1, x2))
                xmin = (int) Math.min(x1, x2);
            if( xmax < (int) Math.max(x1, x2))
                xmax = (int) Math.max(x1, x2);
            if( ymin > (int) Math.min(y1, y2))
                ymin = (int) Math.min(y1, y2);
            if( ymax < (int) Math.max(y1, y2))
                ymax = (int) Math.max(y1, y2);

        }

        Log.i(TAG, "Xmin: "+ xmin + ", Xmax: " + xmax + ", Ymin: " + ymin + ", Ymax: " + ymax);
        for(int i = 0; i < 2; i++) {
            if (ymax - ymin > 25 && ymax - ymin < 35) {
                if (xmax - xmin < 80) {
                    if (xmin > 55)
                        xmin = xmax - 90;
                    else if (xmax < 110)
                        xmax = xmin + 90;
                    else {
                    }
                }
                else if(xmax - xmin > 100){
                    if(Math.abs(xmin) < Math.abs(160-xmax))
                        xmin = xmax - 90;
                    else{
                        xmax = xmin + 90;
                    }
                }
                else{
                }
            }

            if (xmax - xmin > 70) {
                if (ymax - ymin < 25) {
                    if (ymin > 50)
                        ymin = ymax - 30;
                    else if (ymax < 50)
                        ymax = ymin + 30;
                    else {
                    }
                } else if (ymax - ymin > 35) {
                    if (Math.abs(ymin) < Math.abs(100 - ymax))
                        ymin = ymax - 30;
                    else {
                        ymax = ymin + 30;
                    }
                }
                else{
                }
            }
        }
        Log.i(TAG, "Xmin: " + xmin + ", Xmax: " + xmax + ", Ymin: " + ymin + ", Ymax: " + ymax);
        if( ymax-ymin < 25 || xmax-xmin < 50){
           /* Handle exceptions*/
            xmin = 36; xmax = 125; ymin = 30; ymax = 60;
        }

//        Imgproc.circle(matClone, new Point(xmin, ymin), 3, new Scalar(0, 255, 0), 3);
//        Imgproc.circle(matClone, new Point(xmin, ymax), 3, new Scalar(0, 255, 0), 3);
//        Imgproc.circle(matClone, new Point(xmax, ymin), 3, new Scalar(0, 255, 0), 3);
//        Imgproc.circle(matClone, new Point(xmax, ymax), 3, new Scalar(0, 255, 0), 3);
        Point p1 = new Point(80 + xmin, 60 + ymin);
        Point p2 = new Point(80 + xmin, 60 + ymax);
        Point p3 = new Point(80 + xmax, 60 + ymin);
        Point p4 = new Point(80 + xmax, 60 + ymax);
        Imgproc.line(matOrigin, p1, p2, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p2, p4, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p4, p3, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p3, p1, new Scalar(255,0,0), 3);

        matROI = matClone.submat(ymin + 3, ymax - 3, xmin+3, xmax-3);
        Bitmap bmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(matROI, bmp);

        boolean result = checkResult(bmp);
        Log.i( TAG, "Result: " + result);
        
        int result2 = result? 0:1;
        PreferenceControl.setTestResult(result2);
        PreferenceControl.setTestSuccess();
        colorDetectListener.colorDetectSuccess((int)check);

        bmp = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(matOrigin, bmp);
   
    }
    

    
    public void printPixelGrayScale(int pixel){
        int value = (pixel >> 16) & 0xff;
        System.out.println("gray: " + value);
    }
    
    
    public boolean checkResult(Bitmap image){
        int w = image.getWidth();
        int h = image.getHeight();
        Log.i(TAG, "width: " + w + " , height: " + h);
        check = 0;

        final float eps = (float) -0.000001;
        float [] x0 = new float[w];
        float [] diff = new float[w-1];
        float [] pivot = new float[w-2];
        int [] rowNum = new int[]{6, 13, 18};
        //float check = 0;

        for(int i = 0; i < h; i++){
            float maximum = 0;
            float minimum = 255;
            float sum = 0;
            Vector vector = new Vector();
            for (int j = 0; j < w; j++) {
                //int pixel = image.getRGB(j, rowNum[i]);
                int pixel = image.getPixel(j, i);
                int value = 255 - ((pixel >> 16) & 0xff);
                x0[j] = value;
                sum += x0[j];

                if( j > 0 ){
                    diff[j-1] = x0[j] - x0[j-1];
                    if (diff[j-1] == 0)
                        diff[j-1] = eps;
                }

                if( j > 1 ){
                    pivot[j-2] = diff[j-2] * diff[j-1];
                    if( pivot[j-2] < 0 && diff[j-2] > 0 ){
                        vector.add(j-1);
                    }
                }

                if(x0[j] > maximum)
                    maximum = x0[j];

                if(x0[j] < minimum)
                    minimum = x0[j];

            }
            if( (maximum - minimum) < 50 )
                continue;

//            Log.i(TAG, "Vector size: " + vector.size());

            float average = sum/w;
            float sel = (maximum-minimum)/5;
            boolean isFoundMax = false;
            int maxIdx = 0;
            Log.i(TAG, "Sel: " + String.valueOf(sel));

            for(int k= 0; k < vector.size(); k++){
                int idx = (Integer) vector.get(k);
                if(x0[idx] == maximum){
                    Log.i(TAG, "Maximum in Id:" + idx);
                    isFoundMax = true;
                    maxIdx = idx;
                    if(k == vector.size()-1){
                        check -= 1;
                    }
                }
                else if(isFoundMax == true) {
                    if(x0[idx] - average > sel){
//                        Log.i(TAG, String.valueOf((int) vector.get(k)));
                        if(idx > 5 && idx < w-6){
                            if( (idx - maxIdx) > 35 && (idx - maxIdx) < 45){
                                if(x0[idx] - x0[idx-5] > sel/3 && x0[idx] - x0[idx+5] > sel/3){
                                    //System.out.println(idx);
                                    check += 5;
                                }
                                else{
                                    check -= 1;
                                }
                                break;
                            }
                        }
                    }
                    else if(k == vector.size()-1){
                        check -= 1;
                    }
                }
            }
        }
        Log.i(TAG, "Check: " + String.valueOf(check));
        if(check > 0)
            return true;
        else
            return false;
    }
    
}
