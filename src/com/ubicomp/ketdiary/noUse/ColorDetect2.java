package com.ubicomp.ketdiary.noUse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import android.util.Log;

public class ColorDetect2 {
	
	private static final String TAG = "ColorDetect";
	public int result_color = 0;
	public static svm_model model = null;
	
	public static svm_model model1 = null;
	public static svm_model model2 = null;

	public ColorDetect2() {
		
		
		try{
			FileWriter fw1 = new FileWriter("/storage/sdcard0/model1.txt", false);
			BufferedWriter bw1 = new BufferedWriter(fw1); 
			bw1.write("svm_type c_svc\nkernel_type polynomial\ndegree 3\ngamma 0.25\ncoef0 0\nnr_class 2\ntotal_sv 2\nrho -3.91442\nlabel 0 1\nnr_sv 1 1\nSV\n4.286751187782065e-019 1:768 2:1032 3:768 4:2560 \n-4.286751187782065e-019 1:768 2:1288 3:768 4:3072 \n");
			bw1.newLine();
			bw1.close();
			
			FileWriter fw2 = new FileWriter("/storage/sdcard0/model2.txt", false);
			BufferedWriter bw2 = new BufferedWriter(fw2); //將BufferedWeiter與FileWrite物件做連結
			bw2.write("svm_type c_svc\nkernel_type polynomial\ndegree 3\ngamma 0.25\ncoef0 0\nnr_class 2\ntotal_sv 2\nrho -3.77756\nlabel 0 1\nnr_sv 1 1\nSV\n1.380649886196357e-019 1:768 2:1288 3:1024 4:3072 \n-1.380649886196357e-019 1:768 2:1800 3:1024 4:3584 \n");
			bw2.newLine();
			bw2.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
	public static int colorDetect(int[] readings) {
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
		
		String rt = "1.0 ";
		for(int i=0;i<values[0].length;i++) {
			rt += (i+1)+":"+values[0][i] + " ";
		}
		
		Log.i(TAG, rt);
		
		
		try {
			classifyJarLib(values[0]);
			//result_color = 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
		
	}
	
	public static String colorDetect2(int[] readings) {
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
		
		String rt = "1.0 ";
		for(int i=0;i<values[0].length;i++) {
			rt += (i+1)+":"+values[0][i] + " ";
		}
		
		Log.i(TAG, rt);
		
		
		try {
			classifyJarLib(values[0]);
			//result_color = 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rt;
		
	}
	
	public static int classifyJarLib(double[] values) throws IOException {
		int M_LENGTH = 12;
		
		//model = svm.svm_load_model("");
		try {
			model = svm.svm_load_model( "/storage/sdcard0/DrugfreeDiary/libsvm_model.txt" );
			//model2 = svm.svm_load_model( "/storage/sdcard0/model2.txt" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
