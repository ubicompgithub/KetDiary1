/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubicomp.ketdiary.noUse;

import java.io.File;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ubicomp.ketdiary.file.MainStorage;

/**
 *
 * @author larry
 */
public class TestStripDetection {

    private static int xOffset = 115;
    private static int yOffset = 105;
    private static int sampleLen = 80;
    private static int sampleWidth = 25; 
    
    
    private Bitmap img = null;
    private Bitmap subImg = null;
   // private int[] subImg = null;
    
    
    public TestStripDetection(){

    	File mainStorageDir = MainStorage.getMainStorageDirectory();
        
        img = BitmapFactory.decodeFile(mainStorageDir.getPath() + File.separator + "Avon.jpg");
        subImg =  Bitmap.createBitmap(img, xOffset, yOffset, sampleLen, sampleWidth);
        
        //img.getPixels(subImg, 0, 0, xOffset, yOffset, sampleWidth, sampleLen);
        //subImg = img.(xOffset, yOffset, sampleLen, sampleWidth);
        //writePixelGrayToFile(subImg);
        //createPositiveSample(subImg);
        //writePixelGrayToFile(subImg);
        boolean result = checkResult(subImg);

        Log.d("Result", " " + result);
    }
    
    public void printPixelGrayScale(int pixel){
        int value = (pixel >> 16) & 0xff;
        System.out.println("gray: " + value);
    }
    
    /*public void writePixelGrayToFile(){
            int w = image.getWidth();
            int h = image.getHeight();
            System.out.println("width: " + w + " , height: " + h);
 
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int pixel = image.getRGB(j, i);
                    int value = (pixel >> 16) & 0xff;
                    writer.write(String.valueOf(value));
                    if(j != w-1)
                        writer.write(" ");
                }
                writer.write("\n");
            }
            writer.close();
        }
    }*/
    
    public boolean checkResult(Bitmap image){
        int w = image.getWidth();
        int h = image.getHeight();
        System.out.println("width: " + w + " , height: " + h);
        
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
            
            System.out.println("Vector size: " + String.valueOf(vector.size()));

            float average = sum/w;
            float sel = (maximum-minimum)/3;
            boolean isFoundMax = false;
            int maxIdx = 0;
            System.out.println("Sel: " + String.valueOf(sel));
            for(int k= 0; k < vector.size(); k++){
                int idx = (Integer) vector.get(k);
                if(x0[idx] == maximum){
                    System.out.println("Maximum in Id:" + String.valueOf(idx));
                    isFoundMax = true;
                    maxIdx = idx;
                    if(k == vector.size()-1){
                        check -= 1;
                    }
                }
                else if(isFoundMax == true) {
                    if(x0[idx] - average > sel){
                        System.out.println(vector.get(k));
                        if(idx > 5 && idx < w-6){
                            if( (idx - maxIdx) > 30 && (idx - maxIdx) < 45){
                                if(x0[idx] - x0[idx-5] > sel/3 && x0[idx] - x0[idx+5] > sel/3){
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
        Log.d("Check" , String.valueOf(check));
        if(check > 0)
            return true;
        else
            return false;
    }
    /*
    private void marchThroughImage(Bitmap image) {
        int w = image.getWidth();
        int h = image.getHeight();
        System.out.println("width, height: " + w + ", " + h);
 
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                System.out.println("x,y: " + j + ", " + i);
                int pixel = image.getRGB(j, i);
                //printPixelARGB(pixel);
                System.out.println("");
            }
        }
    }
    
    public void createPositiveSample(Bitmap image){
        int w = image.getWidth();
        int h = image.getHeight();
        
        for (int i = 0; i < h; i++) {
            int pixel = image.getRGB((w/2)-1, i);
            for (int j = w/2; j < w; j++) {
                image.setRGB(j, i, pixel);
            }
        }
    }*/

}
