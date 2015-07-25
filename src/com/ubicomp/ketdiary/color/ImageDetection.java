package com.ubicomp.ketdiary.color;

import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE3;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * Created by larry on 15/7/22.
 */
public class ImageDetection {
    private static final String TAG = "ImageDetetion";

    private static final int roiXmin = 80;
    private static final int roiXmax = 240;
    private static final int roiYmin = 60;
    private static final int roiYmax = 160;

    private static final int defaultXmin = 45;
    private static final int defaultXmax = 135;
    private static final int defaultYmin = 20;
    private static final int defaultYmax = 50;


    private int Xmin = roiXmin;
    private int Xmax = roiXmax;
    private int Ymin = roiYmin;
    private int Ymax = roiYmax;

    ColorDetectListener colorDetectListener;
    public ImageDetection(ColorDetectListener colorDetectListener){
    	this.colorDetectListener = colorDetectListener;
    }

    public void roiDetectionOnWhite(Bitmap bitmap){
        Mat matOrigin = new Mat ();
        Utils.bitmapToMat(bitmap, matOrigin);
        Mat matROI = matOrigin.submat(roiYmin, roiYmax, roiXmin, roiXmax);

        //Mat matClone = new Mat(matROI.cols(),matROI.rows(), CvType.CV_8UC1);
        //Imgproc.cvtColor(matROI, matClone, Imgproc.COLOR_RGB2GRAY);

        Bitmap roiBmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(matROI, roiBmp);

        int w = roiBmp.getWidth();
        int h = roiBmp.getHeight();

        int xSum = 0;
        int ySum = 0;
        int count = 0;

        for(int i = 0; i < h; i++){
            for (int j = 0; j < w; j++) {
                int pixel = roiBmp.getPixel(j, i);
                int value = ((pixel >> 16) & 0xff);
                if (value > 250) {
                    xSum += j;
                    ySum += i;
                    count++;
                }
            }
        }

        int xCenter = xSum / count;
        int yCenter = ySum / count;

        Xmin = xCenter - 45;
        Xmax = xCenter + 45;
        Ymin = yCenter - 15;
        Ymax = yCenter + 15;

        Log.i(TAG, "Xmin: "+ Xmin + ", Xmax: " + Xmax + ", Ymin: " + Ymin + ", Ymax: " + Ymax);

        Point p1 = new Point(roiXmin + Xmin, roiYmin + Ymin);
        Point p2 = new Point(roiXmin + Xmin, roiYmin + Ymax);
        Point p3 = new Point(roiXmin + Xmax, roiYmin + Ymin);
        Point p4 = new Point(roiXmin + Xmax, roiYmin + Ymax);

        Imgproc.line(matOrigin, p1, p2, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p2, p4, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p4, p3, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p3, p1, new Scalar(255,0,0), 3);


        Bitmap bmp = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matOrigin, bmp);

        //((BluetoothListener) activity).setImgPreview(bmp);

    }

    public boolean roiDetection(Bitmap bitmap){

        boolean result = false;
        Mat matOrigin = new Mat ();
        Utils.bitmapToMat(bitmap, matOrigin);

        //Mat matOrigin = Imgcodecs.imread(filePath);
        Mat matROI = matOrigin.submat(roiYmin, roiYmax, roiXmin, roiXmax);

        //matOrigin.release();
        Mat matClone = new Mat(matROI.cols(),matROI.rows(), CvType.CV_8UC1);
        Imgproc.cvtColor(matROI, matClone, Imgproc.COLOR_RGB2GRAY);

        Mat matFilter = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC3);

        int filterSize = 8;

        Mat kernel = new Mat(filterSize, filterSize, CvType.CV_32F);
        kernel.setTo(new Scalar((double)1 /(filterSize * filterSize)));

        Imgproc.filter2D(matClone, matFilter, -1, kernel);

        kernel.release();
        matROI.release();
        Mat matCanny = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC1);
        Imgproc.Canny(matFilter, matCanny, 20, 100, 3, true);
        matFilter.release();
        Mat matLines = new Mat();

        int houghThreshold = 20;
        int minLineSize = 10;
        int lineGap = 10;
        Imgproc.HoughLinesP(matCanny, matLines, 1, Math.PI/180, houghThreshold, lineGap , minLineSize);

        matCanny.release();
        Log.i(TAG, "Num of lines: " + matLines.cols());   // Warning: The number of lines is different from java version.

        Xmin = roiXmax-roiXmin;
        Xmax = 0;
        Ymin = roiYmax-roiYmin;
        Ymax = 0;

        for (int x = 0; x < matLines.cols(); x++)
        {
            double[] vec = matLines.get(0, x);
            double  x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

//            Log.i(TAG, "x1: "+ x1 + ", x2: " + x2 + ", y1: " + y1 + ", y2: " + y2);
            if( Xmin > (int) Math.min(x1, x2))
                Xmin = (int) Math.min(x1, x2);
            if( Xmax < (int) Math.max(x1, x2))
                Xmax = (int) Math.max(x1, x2);
            if( Ymin > (int) Math.min(y1, y2))
                Ymin = (int) Math.min(y1, y2);
            if( Ymax < (int) Math.max(y1, y2))
                Ymax = (int) Math.max(y1, y2);

        }

        Log.i(TAG, "Xmin: "+ Xmin + ", Xmax: " + Xmax + ", Ymin: " + Ymin + ", Ymax: " + Ymax);
        for(int i = 0; i < 2; i++) {
            if (Ymax - Ymin > 25 && Ymax - Ymin < 35) {
                if (Xmax - Xmin < 80) {
                    if (Xmin > 55)
                        Xmin = Xmax - 90;
                    else if (Xmax < 110)
                        Xmax = Xmin + 90;
                    else {
                    }
                }
                else if(Xmax - Xmin > 100){
                    if(Math.abs(Xmin) < Math.abs(160-Xmax))
                        Xmin = Xmax - 90;
                    else{
                        Xmax = Xmin + 90;
                    }
                }
                else{
                }
            }

            if (Xmax - Xmin > 70) {
                if (Ymax - Ymin < 25) {
                    if (Ymin > 50)
                        Ymin = Ymax - 30;
                    else if (Ymax < 50)
                        Ymax = Ymin + 30;
                    else {
                    }
                } else if (Ymax - Ymin > 35) {
                    if (Math.abs(Ymin) < Math.abs(100 - Ymax))
                        Ymin = Ymax - 30;
                    else {
                        Ymax = Ymin + 30;
                    }
                }
                else{
                }
            }
        }
        Log.i(TAG, "Xmin: " + Xmin + ", Xmax: " + Xmax + ", Ymin: " + Ymin + ", Ymax: " + Ymax);
        if( Ymax-Ymin < 25 || Xmax-Xmin < 50){
           /* Handle exceptions*/
            Xmin = defaultXmin; Xmax = defaultXmax; Ymin = defaultYmin; Ymax = defaultYmax;
        }
        else{
            result = true;
        }

        Point p1 = new Point(roiXmin + Xmin, roiYmin + Ymin);
        Point p2 = new Point(roiXmin + Xmin, roiYmin + Ymax);
        Point p3 = new Point(roiXmin + Xmax, roiYmin + Ymin);
        Point p4 = new Point(roiXmin + Xmax, roiYmin + Ymax);

        Imgproc.line(matOrigin, p1, p2, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p2, p4, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p4, p3, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p3, p1, new Scalar(255,0,0), 3);

//        matROI = matClone.submat(Ymin + 3, Ymax - 3, Xmin+3, Xmax-3);
//        Bitmap bmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
//        Utils.matToBitmap(matROI, bmp);

        Bitmap bmp = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matOrigin, bmp);

        //((BluetoothListener) activity).setImgPreview(bmp);
        return result;
    }

    public int testStripDetection(Bitmap bitmap){
        Bitmap roiBmp = Bitmap.createBitmap(bitmap, roiXmin + Xmin + 3, roiYmin + Ymin + 3, Xmax - Xmin - 6, Ymax -Ymin-6);
        int w = roiBmp.getWidth();
        int h = roiBmp.getHeight();
        Log.i(TAG, "width: " + w + " , height: " + h);

        final float eps = (float) -0.000001;
        float [] x0 = new float[w];
        float [] diff = new float[w-1];
        float [] pivot = new float[w-2];
        float check = 0;

        for(int i = 0; i < h; i++){
            float maximum = 0;
            float minimum = 255;
            float sum = 0;
            Vector vector = new Vector();
            for (int j = 0; j < w; j++) {
                //int pixel = image.getRGB(j, rowNum[i]);
                int pixel = roiBmp.getPixel(j, i);
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
            float sel = (maximum-minimum)/4;
            boolean isFoundRef = false;
            int refIdx = 0;
            Log.i(TAG, "Sel: " + String.valueOf(sel));

            for(int k= 0; k < vector.size(); k++){
                int idx = (Integer) vector.get(k);

//                if(x0[idx] == maximum){
//                    Log.i(TAG, "Maximum in Id:" + idx);
//                    isFoundRef = true;
//                    refIdx = idx;
//                    if(k == vector.size()-1){
//                        check -= 1;
//                    }
//                }

                if( idx > 25 && idx < 40){
                    if( x0[idx] - average > sel){
                        Log.i(TAG, "Reference in Id:" + idx);
                        isFoundRef = true;
                        refIdx = idx;
                        if(k == vector.size()-1){
                            check -= 1;
                        }
                    }
                }


                else if(isFoundRef == true) {
                    if(x0[idx] - average > sel){
//                        Log.i(TAG, String.valueOf((int) vector.get(k)));
                        if(idx > 5 && idx < w-6){
                            if( (idx - refIdx) > 35 && (idx - refIdx) < 45){
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
                else if( k == vector.size()-1){
                    Log.i(TAG, "No maximum found!");
                }
            }
        }
        Log.i(TAG, "Check: " + String.valueOf(check));
        
        int result2 = check > 0 ? 0:1;
        PreferenceControl.setTestResult(result2);
        
        
        
        return (int)check;
    }
}
