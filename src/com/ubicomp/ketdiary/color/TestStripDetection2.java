/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubicomp.ketdiary.color;

import java.io.File;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
//import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.ubicomp.ketdiary.App;

/**
 *
 * @author larry
 */
public class TestStripDetection2 {

    private static int xOffset = 120;
    private static int yOffset = 95;
    private static int sampleLen = 80;
    private static int sampleWidth = 25; 
    
    private static final String TAG = "TestStripDetection2";
    private static final String FILE = "Sample";
    

    public TestStripDetection2() {

    }
    
    public void testOpencv() {
        Log.i(TAG, "Test opencv.");
        File mainStorage = null;
        if (mainStorage == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                mainStorage = new File(Environment.getExternalStorageDirectory(), "TempPicDir");
            else
                mainStorage = new File(App.getContext().getFilesDir(), "TempPicDir");
        }
        if (!mainStorage.exists())
            mainStorage.mkdirs();

        File file = new File(mainStorage, FILE+".jpg");

        Mat matOrigin = Imgcodecs.imread(file.getAbsolutePath());
        Log.d(TAG, matOrigin.dump());
        Mat matROI = matOrigin.submat(60, 160, 80, 240);

        matOrigin.release();
        Mat matClone = new Mat(matROI.cols(),matROI.rows(), CvType.CV_8UC1);
        Imgproc.cvtColor(matROI, matClone, Imgproc.COLOR_RGB2GRAY);

        Mat matFilter = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC3);

        Mat kernel = new Mat(8, 8, CvType.CV_32F);
        kernel.setTo(new Scalar((double)1/64));
//        for(int i = 0; i < 8; i++){
//            for(int j = 0; j < 8; j++){
//                kernel.put(i, j, (float) 1/64);
//            }
//        }
        Imgproc.filter2D(matROI, matFilter, -1, kernel);

        kernel.release();
        matROI.release();
        Mat matCanny = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC1);
        Imgproc.Canny(matFilter, matCanny, 20, 120, 3, true);
        matFilter.release();
        Mat matLines = new Mat();

        int threshold = 20;
        int minLineSize = 10;
        int lineGap = 10;
        Imgproc.HoughLinesP(matCanny, matLines, 1, Math.PI/180, threshold, lineGap , minLineSize);

        matCanny.release();
        //Log.i(TAG, "Num of lines: " + matLines.rows());

        int xmin = 160;
        int xmax = 0;
        int ymin = 100;
        int ymax = 0;

        for (int x = 0; x < matLines.rows(); x++)
        {
            double[] vec = matLines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

            if( xmin > (int) Math.min(x1, x2))
                xmin = (int) Math.min(x1, x2);
            if( xmax < (int) Math.max(x1, x2))
                xmax = (int) Math.max(x1, x2);
            if( ymin > (int) Math.min(y1, y2))
                ymin = (int) Math.min(y1, y2);
            if( ymax < (int) Math.max(y1, y2))
                ymax = (int) Math.max(y1, y2);

//            Point start = new Point(x1, y1);
//            Point end = new Point(x2, y2);
//            Imgproc.line(matClone, start, end, new Scalar(255,0,0), 3);
        }

        Log.i(TAG, "Xmin: "+ xmin + ", Xmax: " + xmax + ", Ymin: " + ymin + ", Ymax: " + ymax);
        for(int i = 0; i < 2; i++) {
            if (ymax - ymin > 25 && ymax - ymin < 35) {
                if (xmax - xmin < 100) {
                    if (xmin > 80)
                        xmin = xmax - 100;
                    else if (xmax < 80)
                        xmax = xmin + 100;
                    else {
                    }
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
                    if (Math.abs(ymin) < Math.abs(240 - ymax))
                        ymin = ymax - 30;
                    else {
                        ymax = ymin + 30;
                    }
                } else {
                }
            }
        }
        Log.i(TAG, "Xmin: " + xmin + ", Xmax: " + xmax + ", Ymin: " + ymin + ", Ymax: " + ymax);
        if( ymax-ymin <= 0 || xmax-xmin <= 0){
           /* Handle exceptions*/
            xmin = 36; xmax = 125; ymin = 30; ymax = 60;
        }

//        Imgproc.circle(matClone, new Point(xmin, ymin), 3, new Scalar(0, 255, 0), 3);
//        Imgproc.circle(matClone, new Point(xmin, ymax), 3, new Scalar(0, 255, 0), 3);
//        Imgproc.circle(matClone, new Point(xmax, ymin), 3, new Scalar(0, 255, 0), 3);
//        Imgproc.circle(matClone, new Point(xmax, ymax), 3, new Scalar(0, 255, 0), 3);
        Log.d(TAG, matClone.dump());
        matROI = matClone.submat(ymin+2, ymax-2, xmin+2, xmax-2);
        Bitmap bmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(matROI, bmp);

        boolean result = checkResult(bmp);

        Log.i( TAG, "Result: " + result);     
        File output = new File(mainStorage, FILE+"out.jpg");
        String filename = output.toString();
//        Boolean bool = Highgui.imwrite(filename, matROI);
//
//        if (bool)
//         Log.i(TAG, "SUCCESS writing image to external storage");
//        else
//         Log.i(TAG, "Fail writing image to external storage");

    }
    

    
    public void printPixelGrayScale(int pixel){
        int value = (pixel >> 16) & 0xff;
        System.out.println("gray: " + value);
    }
    
    
    public boolean checkResult(Bitmap image){
        int w = image.getWidth();
        int h = image.getHeight();
        Log.i(TAG, "width: " + w + " , height: " + h);

        final float eps = (float) -0.000001;
        float [] x0 = new float[w];
        float [] diff = new float[w-1];
        float [] pivot = new float[w-2];
        int [] rowNum = new int[]{6, 13, 18};
        float check = 0;

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
            float sel = (maximum-minimum)/4;
            boolean isFoundMax = false;
            int maxIdx = 0;
//            Log.i(TAG, "Sel: " + String.valueOf(sel));
            for(int k= 0; k < vector.size(); k++){
                int idx = (Integer) vector.get(k);
                if(x0[idx] == maximum){
//                    Log.i(TAG, "Maximum in Id:" + idx);
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
                            if(x0[idx] - x0[idx-5] > sel/2 && x0[idx] - x0[idx+5] > sel/2){
                                if( (idx - maxIdx) > 30 && (idx - maxIdx) < 45){
                                    //System.out.println(idx);
                                    check += 4;
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
