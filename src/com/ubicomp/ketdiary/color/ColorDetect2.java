package com.ubicomp.ketdiary.color;

import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import android.util.Log;

public class ColorDetect2 {
	
	private static final String TAG = "ColorDetect";
	public int result_color = 0;
	public static svm_model model;

	public ColorDetect2() {
		// TODO Auto-generated constructor stub
	}
	
	public int colorDetect(int[] readings) {
		/* Color sensor readings:
		 * readings[0]=Red   
		 * readings[1]=Green
		 * readings[2]=Blue
		 * readings[3]=Brightness
		 
		
		int[][] indices = {
				{1,2,3,4,5,6,7,8,9,10,11}
		};*/
		
		double[][] values = new double[1][12];
		
		float tx= (float) ((-0.14282)*(float)(readings[0])+(1.54924)*(float)(readings[1])+ 
   				(-0.95641)*(float)(readings[2]));
		float ty=(float) ((-0.32466)*(float)(readings[0])+(1.57837)*(float)(readings[1])+ 
   				(-0.73191)*(float)(readings[2]));
   		float tz= (float) ((-0.68202)*(float)(readings[0])+(0.77073)*(float)(readings[1])+ 
   				(0.56332)*(float)(readings[2]));
   		float cx = tx/(tx+ty+tz);
        float cy = ty/(tx+ty+tz);
		
		values[0][0] = readings[0];
		values[0][1] = readings[1];
		values[0][2] = readings[2];
		values[0][3] = readings[3];
	
		values[0][4] = readings[3] - readings[0];
		values[0][5] = readings[3] - readings[1];
		values[0][6] = readings[3] - readings[2];
		values[0][7] = readings[0] - readings[1];
		values[0][8] = readings[0] - readings[2];
		values[0][9] = readings[1] - readings[2];
		
		values[0][10] = (float)((int)(tx/(tx+ty+tz)*10000));
		values[0][11] = (float)((int)(ty/(tx+ty+tz)*10000));
		
		String rt = "";
		for(int i=0;i<values[0].length;i++) {
			rt += values[0][i] + " ";
		}
		
		Log.i(TAG, rt);
		
		/*
		try {
			result_color = classifyJarLib(values[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return result_color;
		
	}
	
	public static int classifyJarLib(double[] values) throws IOException {
		int M_LENGTH = 11;
		
		model = svm.svm_load_model("");
		
		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		
		svm_node[] x = new svm_node[M_LENGTH];
		for(int j=0;j<M_LENGTH;j++)
		{
			x[j] = new svm_node();
			x[j].index = j+1;
			x[j].value = values[j];
		}
		
		double v = svm.svm_predict(model,x);
		Log.i("Libsvm", v + " ");
		return (int)v;
	}
}
