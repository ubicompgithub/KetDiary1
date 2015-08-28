package com.ubicomp.ketdiary.test.color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.util.Log;

import com.ubicomp.ketdiary.data.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * Created by larry on 15/7/22.
 */
public class ImageDetectionValidate {
    private static final String TAG = "ImageDetetion";

    private static final int ROI_X_MIN = 80;
    private static final int ROI_X_MAX = 240;
    private static final int ROI_Y_MIN = 80;
    private static final int ROI_Y_MAX = 160;
    
    //
    private static final int DEFAULT_X_MIN = 45;
    private static final int DEFAULT_X_MAX = 135;
    private static final int DEFAULT_Y_MIN = 20;
    private static final int DEFAULT_Y_MAX = 50;
    
    private static final int LBOUND_BETWEEN_LINE = 30;
    private static final int UBOUND_BETWEEN_LINE = 45;
    
    private static final int WHITE_THRESHOLD = 160;
    private static final int VALID_THRESHOLD = -15;
    private static final int MINIMAL_EFFECTIVE_RANGE = 20;
    
    private static final int LBOUND_EFFECTIVE_GRAYSCALE = 40;
    private static final float NO_LINE_PENALTY = 1;
    
    private static final int LBOUND_FIRST_LINE_RANGE = 20;
    private static final int UBOUND_FIRST_LINE_RANGE = 40;
    private static final int SELECTIVITY_CONST = 3; 
    
    private static final int FIRST_LINE_UNFOUND_PENALTY = 1;
    private static final int SECOND_LINE_UNFOUND_PENALTY = 1;
    private static final int FOUND_REWARD = 4;
    private static final int ALLOWED_TEST_LINE_WIDTH = 2;
    
    //private static final int DETECT_THRESHOLD = 20;
    
    
    private static final float eps = (float) -0.000001;
    
    private int xmin = DEFAULT_X_MIN;
    private int xmax = DEFAULT_X_MAX;
    private int ymin = DEFAULT_Y_MIN;
    private int ymax = DEFAULT_Y_MAX;
    
    private File mainStorage = null;
    private int picNum = 2;
    private long ts;
    private FileOutputStream out = null;
    private FileOutputStream out2 = null;
    private FileOutputStream out3 = null;
    ColorDetectListener colorDetectListener;
    
    public ImageDetectionValidate(){    	
    	ts = PreferenceControl.getUpdateDetectionTimestamp();
        File dir = MainStorage.getMainStorageDirectory();
        mainStorage = dir;       
    }

    public void roiDetectionOnWhite(){
//        Mat matOrigin = new Mat ();
//        Utils.bitmapToMat(bitmap, matOrigin);
        //Mat matROI = matOrigin.submat(ROI_Y_MIN, ROI_Y_MAX, ROI_X_MIN, ROI_X_MAX);
    	String file_name = "PIC_" + "1440632179425" + "_" + 1 + ".jpg";
        File file = new File(mainStorage, file_name);
        
        Mat matOrigin = Imgcodecs.imread(file.getAbsolutePath());
        Bitmap bitmap = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.ARGB_4444);; 
        Utils.matToBitmap(matOrigin, bitmap);
        //Mat matClone = new Mat(matROI.cols(),matROI.rows(), CvType.CV_8UC1);
        //Imgproc.cvtColor(matROI, matClone, Imgproc.COLOR_RGB2GRAY);

//        Bitmap roiBmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
//        Utils.matToBitmap(matROI, roiBmp);
        Bitmap roiBmp = Bitmap.createBitmap(bitmap, ROI_X_MIN, ROI_Y_MIN, ROI_X_MAX - ROI_X_MIN, ROI_Y_MAX - ROI_Y_MIN);

        int width = roiBmp.getWidth();
        int height = roiBmp.getHeight();

        int xSum = 0;
        int ySum = 0;
        int count = 0;
     
        for(int i = 0; i < height; i++){
            for (int j = 0; j < width; j++) {
                int pixel = roiBmp.getPixel(j, i);
                int value = ((pixel >> 16) & 0xff);
                if (value > WHITE_THRESHOLD) {
                    xSum += j;
                    ySum += i;
                    count++;
                }
            }
        }

        int xCenter = xSum / count;
        int yCenter = ySum / count;

        xmin = xCenter - 45;
        xmax = xCenter + 45;
        ymin = yCenter - 15;
        ymax = yCenter + 15;

        Log.i(TAG, "xmin: "+ xmin + ", xmax: " + xmax + ", ymin: " + ymin + ", ymax: " + ymax);

        Point p1 = new Point(ROI_X_MIN + xmin, ROI_Y_MIN + ymin);
        Point p2 = new Point(ROI_X_MIN + xmin, ROI_Y_MIN + ymax);
        Point p3 = new Point(ROI_X_MIN + xmax, ROI_Y_MIN + ymin);
        Point p4 = new Point(ROI_X_MIN + xmax, ROI_Y_MIN + ymax);

        Imgproc.line(matOrigin, p1, p2, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p2, p4, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p4, p3, new Scalar(255,0,0), 3);
        Imgproc.line(matOrigin, p3, p1, new Scalar(255,0,0), 3);


        Bitmap bmp = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matOrigin, bmp);
        
        //String file_name = "PIC_" + ts + "_" + 0 + ".sob";
        String file_name2 = "PIC_" + ts + "_" + 1 + ".sob";
        //File file = new File(mainStorage, file_name);
        File file2 = new File(mainStorage, file_name2);
        try {
            out = new FileOutputStream(file, true);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out2 = new FileOutputStream(file2, true);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out2); 
            // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (out2 != null) {
                    out2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        //Log.i(TAG, matOrigin.dump());
        //((BluetoothListener) activity).setImgPreview(bmp);

    }
    public int testStripDetection(){
    	
    	String fn = "PIC_" + "1440632179425" + "_" + 4 + ".jpg";
        File f = new File(mainStorage, fn);
        
        Mat matOrigin = Imgcodecs.imread(f.getAbsolutePath());
        Bitmap bitmap = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.ARGB_4444);; 
        Utils.matToBitmap(matOrigin, bitmap);

        Bitmap roiBmp = Bitmap.createBitmap(bitmap, ROI_X_MIN + xmin + 2, ROI_Y_MIN + ymin + 1, xmax - xmin - 4, ymax - ymin - 2);
        int width = roiBmp.getWidth();
        int height = roiBmp.getHeight();
        int middle = width/2;
        int halfHeight = height / 2;
        Log.i(TAG, "width: " + width + " , height: " + height);

        Mat matROI = new Mat();
        Utils.bitmapToMat(roiBmp, matROI);


        float [] x0 = new float[width];
        float [] diff = new float[width-1];
        float [] pivot = new float[width-2];
        float validatity = 0;
        float check = 0;
        
        HashMap testLineVoteMap = new HashMap();

        for(int i = 0; i < height; i++){
            float maximum = 0;
            float minimum = 255;
            float sumAll= 0;
            float sumAfterMiddle = 0;
            float maximumAfterMiddle = 0;
            float minimumAfterMiddle = 255;
            Vector vector = new Vector();
            for (int j = 0; j < width; j++) {
                //int pixel = image.getRGB(j, i);
                int pixel = roiBmp.getPixel(j, i);
                int value = 255 - ((pixel >> 16) & 0xff);
                x0[j] = value;
                sumAll += x0[j];
                if(j >= middle){
                    sumAfterMiddle += x0[j];
                }

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

                if(j >= middle){
                    if(x0[j] > maximumAfterMiddle)
                        maximumAfterMiddle = x0[j];

                    if(x0[j] < minimumAfterMiddle)
                        minimumAfterMiddle = x0[j];
                }

            }
            float avgAll = sumAll /width;
            if( (maximum - minimum) < MINIMAL_EFFECTIVE_RANGE ){
                Log.i(TAG, "Useless row.");
                if( avgAll > LBOUND_EFFECTIVE_GRAYSCALE) {
                    validatity -= NO_LINE_PENALTY;
                    //check -= 0.5;                               // Modified by larry on 7/27
                }
                continue;
            }


            float avgAfterMiddle = sumAfterMiddle /middle;
            float sel = (maximum-minimum)/4;
            float selAfterMiddle = (maximumAfterMiddle-minimumAfterMiddle)/SELECTIVITY_CONST;
//            if(i < halfHeight){
//                selAfterMiddle = (maximumAfterMiddle-minimumAfterMiddle)/SELECTIVITY_CONST;
//            }
//            else{
            selAfterMiddle = (maximumAfterMiddle-minimumAfterMiddle)/(SELECTIVITY_CONST + 10);
//          }
            
            float refCandidate = 0;
            boolean isFoundRef = false;
            int refIdx = 0;
            float secondMaximal = 0;
            int secondIdx = 0;

//            Log.i(TAG, "Avg: " + String.valueOf(avgAll));
//            Log.i(TAG, "AvgAfter50: " + String.valueOf(avgAfterMiddle));
//            Log.i(TAG, "Sel: " + String.valueOf(sel));
//            Log.i(TAG, "SelAfter50: " + String.valueOf(selAfterMiddle));


            Vector candidateVector = new Vector();
//            Log.i(TAG, "K = " + vector.size());
            for(int k= 0; k < vector.size(); k++) {
                int idx = (Integer) vector.get(k);

                if (idx > LBOUND_FIRST_LINE_RANGE && idx <= UBOUND_FIRST_LINE_RANGE) {
                    if (x0[idx] - avgAll > sel) {
                        candidateVector.add(idx);
                        Log.i(TAG, "Reference in Id:" + idx);
                    }
                }

                if (idx > UBOUND_FIRST_LINE_RANGE && isFoundRef == false) {
                    for (int m = 0; m < candidateVector.size(); m++) {
                        int tempIdx = (Integer) candidateVector.get(m);
                        if (x0[tempIdx] > refCandidate) {
                            refCandidate = x0[tempIdx];
                            refIdx = tempIdx;
                        }
                    }
                    if (refIdx == 0) {
                        Log.i(TAG, "Can't find refPoint in " + i + "th row.");
                        validatity -= FIRST_LINE_UNFOUND_PENALTY;
                        //check -= 1;
                        break;
                    }
                    else{
                        Point point = new Point(refIdx, i);
                        Imgproc.circle(matROI, point, 1, new Scalar(0, 255, 0), 1);
                        isFoundRef = true;
                    }
                }

                if (idx > middle && isFoundRef == true) {
                    if (x0[idx] - avgAfterMiddle > selAfterMiddle) {
                        if (secondMaximal < x0[idx]) {
                            secondMaximal = x0[idx];
                            secondIdx = idx;
                        }
                    }
                }
                else{
                }

                if (k == vector.size() - 1) {
                    if (secondIdx != 0 && (secondIdx - refIdx) > LBOUND_BETWEEN_LINE && (secondIdx - refIdx) < UBOUND_BETWEEN_LINE) {
                        Log.i(TAG, "Second: " + secondIdx);
                        
                        int value = 1;
                        if(testLineVoteMap.containsKey(secondIdx) == true){
                            value = (Integer) testLineVoteMap.get(secondIdx);
                            value++;
                        }
                        testLineVoteMap.put(secondIdx, value);
                        
                        Point point = new Point(secondIdx, i);
                        Imgproc.circle(matROI, point, 1, new Scalar(0, 0, 255), 1);
                        //check += FOUND_REWARD;
                    } else {
                        Log.i(TAG, "Failed" );
                        check -= SECOND_LINE_UNFOUND_PENALTY;
                    }
                    break;
                }
            }
        }
            
        if (!testLineVoteMap.isEmpty()){

            int maxSecondIdx = 0;
            int tempMaxSecondIdxNum = 0;
            for (Object key : testLineVoteMap.keySet()) {
                int value = (Integer)testLineVoteMap.get(key);
                if( value > tempMaxSecondIdxNum){
                    tempMaxSecondIdxNum = value;
                    maxSecondIdx = (Integer) key;
                }
                Log.i(TAG, key + " : " + testLineVoteMap.get(key));
            }

            for (Object key : testLineVoteMap.keySet()) {
                int candidateIdx = (Integer) key;
                if( Math.abs(candidateIdx - maxSecondIdx) <= ALLOWED_TEST_LINE_WIDTH) {
                    int value = (Integer) testLineVoteMap.get(key);
                    check += (value * FOUND_REWARD);
                }
            }
        }

        Log.i(TAG, "Validatity: " + String.valueOf(validatity));
        if(validatity < VALID_THRESHOLD)
            check = -1000;
        else{
//        	int result2 = check > DETECT_THRESHOLD ? 0:1;
//        	PreferenceControl.setTestResult(result2);
        }
	    Log.i(TAG, "Check: " + String.valueOf(check));
	        
        String file_name = "PIC_" + ts + "_" + picNum++ + ".sob";
        String file_name2= "PIC_" + ts + "_" + picNum++ + ".sob";
        String file_name3= "PIC_" + ts + "_" + picNum++ + ".sob";
        File file = new File(mainStorage, file_name);
        File file2 = new File(mainStorage, file_name2);
        File file3 = new File(mainStorage, file_name3);
        try {
            out = new FileOutputStream(file, true);
            roiBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            
            out2 = new FileOutputStream(file2, true);
            Bitmap labelBmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
	        Utils.matToBitmap(matROI, labelBmp);
	        labelBmp.compress(Bitmap.CompressFormat.JPEG, 50, out2);
            
            out3 = new FileOutputStream(file3, true);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out3); 
            // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (out2 != null) {
                    out2.close();
                }
                if (out3 != null) {
                    out3.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
            

        Log.i(TAG, "Check: " + String.valueOf(check));
        return (int)check;
        
  
        
//        Bundle countBundle = new Bundle();
//        countBundle.putFloat("check", check);
//        Message msg = new Message();
//        msg.what = MainActivity.SHOW_PREDICTION_MSG;
//        msg.setData(countBundle);
//        ((MainActivity) activity).mHandler.sendMessage(msg);
//
//
//        String picturePath = datatransmission.file.getAbsolutePath();
//        String roiPath = picturePath.substring(0, picturePath.lastIndexOf(".")).concat("_2.jpg");
//
//        File file = new File(roiPath); // the File to save to
//        FileOutputStream fout = null;
//
//        try {
//            fout = new FileOutputStream(file);
//            Bitmap labelBmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
//            Utils.matToBitmap(matROI, labelBmp);
//            labelBmp.compress(Bitmap.CompressFormat.JPEG, 50, fout);
//            countBundle = new Bundle();
//            countBundle.putString("picturePath", file.getAbsolutePath());
//
//            msg = new Message();
//            msg.what = MainActivity.PICTURE_PREVIEW_MSG;
//            msg.setData(countBundle);
//            ((MainActivity) activity).mHandler.sendMessage(msg);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


        

    }

 
    
//    public int testStripDetection2(Bitmap bitmap){
//        Bitmap roiBmp = Bitmap.createBitmap(bitmap, ROI_X_MIN + xmin + 3, ROI_Y_MIN + ymin + 3, xmax - xmin - 6, ymax -ymin-6);
//        int width = roiBmp.getWidth();
//        int height = roiBmp.getHeight();
//        Log.i(TAG, "width: " + width + " , height: " + height);
//        
//        int half_width = width/2;
//        
//        final float eps = (float) -0.000001;
//        float [] x0 = new float[width];
//        float [] diff = new float[width-1];
//        float [] pivot = new float[width-2];
//        float check = 0;
//        float validatity = 0;
//        
//        for(int i = 0; i < height; i++){
//            float maximum = 0;
//            float minimum = 255;
//            float sumAll= 0;
//            float sumAfterMiddle = 0;
//            float maximumAfterMiddle = 0;
//            float minimumAfterMiddle = 255;
//            Vector vector = new Vector();
//            for (int j = 0; j < width; j++) {
//                //int pixel = image.getRGB(j, i);
//                int pixel = roiBmp.getPixel(j, i);
//                int value = 255 - ((pixel >> 16) & 0xff);
//                //System.out.print(value + " ");
//                x0[j] = value;
//                sumAll += x0[j];
//                if(j >= half_width){
//                    sumAfterMiddle += x0[j];
//                }
//
//                if( j > 0 ){
//                    diff[j-1] = x0[j] - x0[j-1];
//                    if (diff[j-1] == 0)
//                        diff[j-1] = eps;
//                }
//
//                if( j > 1 ){
//                    pivot[j-2] = diff[j-2] * diff[j-1];
//                    if( pivot[j-2] < 0 && diff[j-2] > 0 ){
//                        vector.add(j-1);
//                    }
//                }
//
//                if(x0[j] > maximum)
//                    maximum = x0[j];
//
//                if(x0[j] < minimum)
//                    minimum = x0[j];
//
//                if(j >= half_width){
//                    if(x0[j] > maximumAfterMiddle)
//                        maximumAfterMiddle = x0[j];
//
//                    if(x0[j] < minimumAfterMiddle)
//                        minimumAfterMiddle = x0[j];
//                }
//
//            } 
//            float avgAll = sumAll /width;
//            //System.out.println("");
//            if( (maximum - minimum) < MINIMAL_EFFECTIVE_RANGE ){
//            	if(avgAll > LBOUND_EFFECTIVE_GRAYSCALE){
//            		check -= NO_LINE_PENALTY;
//            		//validatity -= NO_LINE_PENALTY ;
//            	}
//                continue;
//            }
//
//           
//            float avgAfterMiddle = sumAfterMiddle / half_width;
//            float sel = (maximum-minimum)/5;
//            float selAfterMiddle = (maximumAfterMiddle-minimumAfterMiddle)/5;
//            float refCandidate = 0;
//            boolean isFoundRef = false;
//            int refIdx = 0;
//            float secondMaximal = 0;
//            int secondIdx = 0;
//
////            Log.i(TAG, "Avg: " + String.valueOf(avgAll));
////            Log.i(TAG, "avgAfterMiddle: " + String.valueOf(avgAfterMiddle));
////            Log.i(TAG, "Sel: " + String.valueOf(sel));
////            Log.i(TAG, "selAfterMiddle: " + String.valueOf(selAfterMiddle));
//
//            Vector candidateVector = new Vector();
//            for(int k= 0; k < vector.size(); k++){
//                int idx = (Integer)vector.get(k);
//
//                if( idx > LBOUND_FIRST_LINE_RANGE && idx <= UBOUND_FIRST_LINE_RANGE){
//                    if( x0[idx] - avgAll > sel){
//                        candidateVector.add(idx);
////                        Log.i(TAG, "Reference in Id:" + idx);
//                    }
//                }
//                else if(idx > UBOUND_FIRST_LINE_RANGE && isFoundRef == false) {
//                    for(int m = 0; m < candidateVector.size(); m++){
//                        int tempIdx = (Integer)candidateVector.get(m);
//                        if( x0[tempIdx] > refCandidate){
//                            refCandidate = x0[tempIdx];
//                            refIdx = tempIdx;
//                        }
//                    }
//                    if(refIdx == 0){
//                    	//validatity -= FIRST_LINE_UNFOUND_PENALTY ;
//                        check -= FIRST_LINE_UNFOUND_PENALTY;
//                        break;
//                    }
////                    Log.i(TAG, ("Maximum in Idx" + refIdx);
//                    isFoundRef = true;
//                }
//
//                else if(idx > half_width && isFoundRef == true) {
//                    if(x0[idx] - avgAfterMiddle > selAfterMiddle){
//                        //System.out.println(vector.get(k));
//                        if(secondMaximal < x0[idx]){
//                            secondMaximal = x0[idx];
//                            secondIdx = idx;
//                        }
//                    }
//                }
//
//                if(k == vector.size()-1){
//                    if(secondIdx != 0 && (secondIdx - refIdx) > LBOUND_BETWEEN_LINE && (secondIdx - refIdx) < UBOUND_BETWEEN_LINE){
////                        Log.i(TAG, "Second: " + idx);
//                        check += FOUND_REWARD;
//                    }
//                    else{
////                        Log.i(TAG, "Failed: " + secondIdx);
//                        check -= SECOND_LINE_UNFOUND_PENALTY;
//                    }
//                }
//
//            }
//        }
//        
//        if(validatity < -15){
//        	check = -1000;
//        }
//        else{
//        	int result2 = check > 0 ? 0:1;
//        	PreferenceControl.setTestResult(result2);
//        }
//	        Log.i(TAG, "Check: " + String.valueOf(check));
//	        
//	        String file_name = "PIC_" + ts + "_" + 2 + ".sob";
//	        String file_name2= "PIC_" + ts + "_" + 3 + ".sob";
//	        File file = new File(mainStorage, file_name);
//	        File file2 = new File(mainStorage, file_name2);
//	        
//	        try {
//	            out = new FileOutputStream(file, true);
//	            roiBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
//	            out2 = new FileOutputStream(file2, true);
//	            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out2); 
//	            // bmp is your Bitmap instance
//	            // PNG is a lossless format, the compression factor (100) is ignored
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        } finally {
//	            try {
//	                if (out != null) {
//	                    out.close();
//	                }
//	                if (out2 != null) {
//	                    out2.close();
//	                }
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	            }
//	        }
//	        
//	        
//        return (int)check;
//    }
    
//    public boolean roiDetection(Bitmap bitmap){
//
//        boolean result = false;
//        Mat matOrigin = new Mat ();
//        Utils.bitmapToMat(bitmap, matOrigin);
//
//        //Mat matOrigin = Imgcodecs.imread(filePath);
//        Mat matROI = matOrigin.submat(ROI_Y_MIN, ROI_Y_MAX, ROI_X_MIN, ROI_X_MAX);
//
//        //matOrigin.release();
//        Mat matClone = new Mat(matROI.cols(),matROI.rows(), CvType.CV_8UC1);
//        Imgproc.cvtColor(matROI, matClone, Imgproc.COLOR_RGB2GRAY);
//
//        Mat matFilter = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC3);
//
//        int filterSize = 8;
//
//        Mat kernel = new Mat(filterSize, filterSize, CvType.CV_32F);
//        kernel.setTo(new Scalar((double)1 /(filterSize * filterSize)));
//
//        Imgproc.filter2D(matClone, matFilter, -1, kernel);
//
//        kernel.release();
//        matROI.release();
//        Mat matCanny = new Mat(matClone.cols(), matClone.rows(), CvType.CV_8UC1);
//        Imgproc.Canny(matFilter, matCanny, 20, 100, 3, true);
//        matFilter.release();
//        Mat matLines = new Mat();
//
//        int houghThreshold = 20;
//        int minLineSize = 10;
//        int lineGap = 10;
//        Imgproc.HoughLinesP(matCanny, matLines, 1, Math.PI/180, houghThreshold, lineGap , minLineSize);
//
//        matCanny.release();
//        Log.i(TAG, "Num of lines: " + matLines.cols());   // Warning: The number of lines is different from java version.
//
//        xmin = ROI_X_MAX-ROI_X_MIN;
//        xmax = 0;
//        ymin = ROI_Y_MAX-ROI_Y_MIN;
//        ymax = 0;
//
//        for (int x = 0; x < matLines.cols(); x++)
//        {
//            double[] vec = matLines.get(0, x);
//            double  x1 = vec[0],
//                    y1 = vec[1],
//                    x2 = vec[2],
//                    y2 = vec[3];
//
////            Log.i(TAG, "x1: "+ x1 + ", x2: " + x2 + ", y1: " + y1 + ", y2: " + y2);
//            if( xmin > (int) Math.min(x1, x2))
//                xmin = (int) Math.min(x1, x2);
//            if( xmax < (int) Math.max(x1, x2))
//                xmax = (int) Math.max(x1, x2);
//            if( ymin > (int) Math.min(y1, y2))
//                ymin = (int) Math.min(y1, y2);
//            if( ymax < (int) Math.max(y1, y2))
//                ymax = (int) Math.max(y1, y2);
//
//        }
//
//        Log.i(TAG, "xmin: "+ xmin + ", xmax: " + xmax + ", ymin: " + ymin + ", ymax: " + ymax);
//        for(int i = 0; i < 2; i++) {
//            if (ymax - ymin > 25 && ymax - ymin < 35) {
//                if (xmax - xmin < 80) {
//                    if (xmin > 55)
//                        xmin = xmax - 90;
//                    else if (xmax < 110)
//                        xmax = xmin + 90;
//                    else {
//                    }
//                }
//                else if(xmax - xmin > 100){
//                    if(Math.abs(xmin) < Math.abs(160-xmax))
//                        xmin = xmax - 90;
//                    else{
//                        xmax = xmin + 90;
//                    }
//                }
//                else{
//                }
//            }
//
//            if (xmax - xmin > 70) {
//                if (ymax - ymin < 25) {
//                    if (ymin > 50)
//                        ymin = ymax - 30;
//                    else if (ymax < 50)
//                        ymax = ymin + 30;
//                    else {
//                    }
//                } else if (ymax - ymin > 35) {
//                    if (Math.abs(ymin) < Math.abs(100 - ymax))
//                        ymin = ymax - 30;
//                    else {
//                        ymax = ymin + 30;
//                    }
//                }
//                else{
//                }
//            }
//        }
//        Log.i(TAG, "xmin: " + xmin + ", xmax: " + xmax + ", ymin: " + ymin + ", ymax: " + ymax);
//        if( ymax-ymin < 25 || xmax-xmin < 50){
//           /* Handle exceptions*/
//            xmin = DEFAULT_X_MIN; xmax = DEFAULT_X_MAX; ymin = DEFAULT_Y_MIN; ymax = DEFAULT_Y_MAX;
//        }
//        else{
//            result = true;
//        }
//
//        Point p1 = new Point(ROI_X_MIN + xmin, ROI_Y_MIN + ymin);
//        Point p2 = new Point(ROI_X_MIN + xmin, ROI_Y_MIN + ymax);
//        Point p3 = new Point(ROI_X_MIN + xmax, ROI_Y_MIN + ymin);
//        Point p4 = new Point(ROI_X_MIN + xmax, ROI_Y_MIN + ymax);
//
//        Imgproc.line(matOrigin, p1, p2, new Scalar(255,0,0), 3);
//        Imgproc.line(matOrigin, p2, p4, new Scalar(255,0,0), 3);
//        Imgproc.line(matOrigin, p4, p3, new Scalar(255,0,0), 3);
//        Imgproc.line(matOrigin, p3, p1, new Scalar(255,0,0), 3);
//
////        matROI = matClone.submat(ymin + 3, ymax - 3, xmin+3, xmax-3);
////        Bitmap bmp = Bitmap.createBitmap(matROI.cols(), matROI.rows(), Bitmap.Config.ARGB_4444);
////        Utils.matToBitmap(matROI, bmp);
//
//        Bitmap bmp = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(matOrigin, bmp);
//
//        //((BluetoothListener) activity).setImgPreview(bmp);
//        return result;
//    }
//    
//
//
//    public int testStripDetectionOld(Bitmap bitmap){
//        Bitmap roiBmp = Bitmap.createBitmap(bitmap, ROI_X_MIN + xmin + 3, ROI_Y_MIN + ymin + 3, xmax - xmin - 6, ymax -ymin-6);
//        int width = roiBmp.getWidth();
//        int height = roiBmp.getHeight();
//        Log.i(TAG, "width: " + width + " , height: " + height);
//
//        final float eps = (float) -0.000001;
//        float [] x0 = new float[width];
//        float [] diff = new float[width-1];
//        float [] pivot = new float[width-2];
//        float check = 0;
//
//        for(int i = 0; i < height; i++){
//            float maximum = 0;
//            float minimum = 255;
//            float sum = 0;
//            Vector vector = new Vector();
//            for (int j = 0; j < width; j++) {
//                //int pixel = image.getRGB(j, rowNum[i]);
//                int pixel = roiBmp.getPixel(j, i);
//                int value = 255 - ((pixel >> 16) & 0xff);
//                x0[j] = value;
//                sum += x0[j];
//
//                if( j > 0 ){
//                    diff[j-1] = x0[j] - x0[j-1];
//                    if (diff[j-1] == 0)
//                        diff[j-1] = eps;
//                }
//
//                if( j > 1 ){
//                    pivot[j-2] = diff[j-2] * diff[j-1];
//                    if( pivot[j-2] < 0 && diff[j-2] > 0 ){
//                        vector.add(j-1);
//                    }
//                }
//
//                if(x0[j] > maximum)
//                    maximum = x0[j];
//
//                if(x0[j] < minimum)
//                    minimum = x0[j];
//
//            }
//            if( (maximum - minimum) < 50 )
//                continue;
//
////            Log.i(TAG, "Vector size: " + vector.size());
//
//            float average = sum/width;
//            float sel = (maximum-minimum)/4;
//            boolean isFoundRef = false;
//            int refIdx = 0;
//            Log.i(TAG, "Sel: " + String.valueOf(sel));
//
//            for(int k= 0; k < vector.size(); k++){
//                int idx = (Integer) vector.get(k);
//
////                if(x0[idx] == maximum){
////                    Log.i(TAG, "Maximum in Id:" + idx);
////                    isFoundRef = true;
////                    refIdx = idx;
////                    if(k == vector.size()-1){
////                        check -= 1;
////                    }
////                }
//
//                if( idx > 25 && idx < 40){
//                    if( x0[idx] - average > sel){
//                        Log.i(TAG, "Reference in Id:" + idx);
//                        isFoundRef = true;
//                        refIdx = idx;
//                        if(k == vector.size()-1){
//                            check -= 1;
//                        }
//                    }
//                }
//
//
//                else if(isFoundRef == true) {
//                    if(x0[idx] - average > sel){
////                        Log.i(TAG, String.valueOf((int) vector.get(k)));
//                        if(idx > 5 && idx < width-6){
//                            if( (idx - refIdx) > 35 && (idx - refIdx) < 45){
//                                if(x0[idx] - x0[idx-5] > sel/3 && x0[idx] - x0[idx+5] > sel/3){
//                                    //System.out.println(idx);
//                                    check += 5;
//                                }
//                                else{
//                                    check -= 1;
//                                }
//                                break;
//                            }
//                        }
//                    }
//                    else if(k == vector.size()-1){
//                        check -= 1;
//                    }
//                }
//                else if( k == vector.size()-1){
//                    Log.i(TAG, "No maximum found!");
//                }
//            }
//        }
//        Log.i(TAG, "Check: " + String.valueOf(check));
//        
//        String file_name = "PIC_" + ts + "_" + 2 + ".sob";
//        String file_name2= "PIC_" + ts + "_" + 3 + ".sob";
//        File file = new File(mainStorage, file_name);
//        File file2 = new File(mainStorage, file_name2);
//        
//        try {
//            out = new FileOutputStream(file, true);
//            roiBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out2 = new FileOutputStream(file2, true);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out2); 
//            // bmp is your Bitmap instance
//            // PNG is a lossless format, the compression factor (100) is ignored
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//                if (out2 != null) {
//                    out2.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        
//        int result2 = check > 0 ? 0:1;
//        PreferenceControl.setTestResult(result2);
//        
//        
//        
//        return (int)check;
//    }
}
