package com.ubicomp.ketdiary.noUse;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import android.os.Environment;
import android.util.Log;

public class ColorDetect {
	//SensingFork sensingFork = null; 
	int food_color = 0;
	
	public static final int COLOR_NUMBER = 5;
	public static final int COLOR_RESIS_READING_LENGTH = 5;
	public static final int COLOR_SENSING_WINDOW_LENGTH = 5;
	public static final int COLOR_READING_STDV_THRESHOLD = 100;
	public static final int BIGHT_READING_STDV_THRESHOLD = 200;
	public static final int RESIS_READING_STDV_THRESHOLD = 100;
	public static final int NOT_YET = 999;
	
	Queue<int[]> readingsQueue=new LinkedList<int[]>();
	int[] colorReadingsSum = new int[COLOR_RESIS_READING_LENGTH];
	public int[] colorReadingsAvg = new int[COLOR_RESIS_READING_LENGTH];
	public boolean colorConverged = false;
	
	public static svm_model model;
	
	public ColorDetect( ) {
		//this.sensingFork = sensingFork;
		model = null;
	}
	
	
	public static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	public static int atoi(String s)
	{
		return Integer.parseInt(s);
	}
	
	public int forkReadingCheck(int[] readings) {

		int[] mod_readings = readings.clone();
		
		mod_readings[4] = mod_readings[8];

		readingsQueue.add(mod_readings);

		for(int i=0; i<COLOR_RESIS_READING_LENGTH; i++) {
			colorReadingsSum[i] += mod_readings[i];
		}

		// Calculate standard deviations 
		double[] stdvs = new double[COLOR_RESIS_READING_LENGTH];
		if(readingsQueue.size() >= COLOR_SENSING_WINDOW_LENGTH) {

			for(int i=0; i<COLOR_RESIS_READING_LENGTH; i++) {
				colorReadingsAvg[i] = (int)(colorReadingsSum[i]/COLOR_SENSING_WINDOW_LENGTH);
			}

			Iterator<int[]> iter = readingsQueue.iterator();
			while(iter.hasNext())
	        {
				int[] iteratorValues = iter.next();
				for(int i=0; i<COLOR_RESIS_READING_LENGTH; i++) {
					stdvs[i] += Math.sqrt(Math.pow(colorReadingsAvg[i]-iteratorValues[i], 2));
				}

	        }

			if(((stdvs[0]/COLOR_SENSING_WINDOW_LENGTH)<COLOR_READING_STDV_THRESHOLD)&&
			   ((stdvs[1]/COLOR_SENSING_WINDOW_LENGTH)<COLOR_READING_STDV_THRESHOLD)&&
			   ((stdvs[2]/COLOR_SENSING_WINDOW_LENGTH)<COLOR_READING_STDV_THRESHOLD)&&
			   ((stdvs[3]/COLOR_SENSING_WINDOW_LENGTH)<BIGHT_READING_STDV_THRESHOLD)&&
			   ((stdvs[4]/COLOR_SENSING_WINDOW_LENGTH)<RESIS_READING_STDV_THRESHOLD)) {
								
				Log.i("CheckingQ", "*** "+readingsQueue.size()+ " " + (stdvs[0]/COLOR_SENSING_WINDOW_LENGTH) + " " 
						+ (stdvs[1]/COLOR_SENSING_WINDOW_LENGTH)+ " " + (stdvs[2]/COLOR_SENSING_WINDOW_LENGTH) + " " 
						+ (stdvs[3]/COLOR_SENSING_WINDOW_LENGTH)+ " " + (stdvs[4]/COLOR_SENSING_WINDOW_LENGTH));
				
				Iterator<int[]> iter2 = readingsQueue.iterator();
				String rt = "";
				int max = 0;
				int maxv = 0;
				int[] results = new int[COLOR_SENSING_WINDOW_LENGTH];
				while(iter2.hasNext())
		        {
					int[] iteratorValues = iter2.next();
					int current_result = colorDetect(iteratorValues); 
					results[current_result]++;
					if(maxv < results[current_result]) {
						max = current_result;
						maxv = results[current_result];
					}
					
					rt += current_result+" ";

		        }
				
				Log.i("Poll",rt+"==="+max+"");

				colorConverged = true;
				bufferClean();
				return max;
			}
			else {
				Log.i("CheckingQ", readingsQueue.size()+ " " + (stdvs[0]/COLOR_SENSING_WINDOW_LENGTH) + " " 
					+ (stdvs[1]/COLOR_SENSING_WINDOW_LENGTH)+ " " + (stdvs[2]/COLOR_SENSING_WINDOW_LENGTH) + " " 
					+ (stdvs[3]/COLOR_SENSING_WINDOW_LENGTH)+ " " + (stdvs[4]/COLOR_SENSING_WINDOW_LENGTH));
				removeTail();
				return colorDetect(readings);
			}
		}
		//else
			//Log.i("CheckingQ", readingsQueue.size()+" --- ");
		return colorDetect(readings);
	}

	public void bufferClean() {
		readingsQueue.clear();
		//colorConverged = false;
		colorReadingsSum = new int[COLOR_RESIS_READING_LENGTH]; 
		colorReadingsAvg = new int[COLOR_RESIS_READING_LENGTH];
	}
	
	public void removeTail() {
		// Remove head of queue
		int[] head = readingsQueue.poll();
		for(int i=0; i<COLOR_RESIS_READING_LENGTH; i++) {
			colorReadingsSum[i] -= head[i];
		}
	}
	
	public int classify(float[][] values, int[][] indices) {
        // Svm classification
       /*float[][] values = {
        				{512,1288,1792,3584,227,3072,2296,1792, (float)0.0862441607716, (float) 0.229723899585},
                        {3328,3592,512,8192,222,4864,4600,7680,(float) 0.47908955941,(float) 0.438929944713},
        				{1024,776,256,2304,236,1280,1528,2048,(float) 0.519941797813,(float) 0.451912529747},
                        {512,776,512,2304,251,1792,1528,1792,(float) 0.343660933105,(float) 0.367549769107}
        };
        
        //blue, red, yellow, black
        
        int[][] indices = {
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10},
        				{1,2,3,4,5,6,7,8,9,10}
        };
        */

        
        int[] groundTruth = null;
        int[] labels = {0};
        double[] probs = new double[1];
        int isProb = 0; // Not probability prediction
        String modelFileLoc = Environment.getExternalStorageDirectory()+"/SensingFork/chmodel";
        int[] count = new int[COLOR_NUMBER];

        if (callSVM(values, indices, groundTruth, isProb, modelFileLoc, labels, probs) != 0) {
                //Log.d(sensingFork. TAG, "Classification is incorrect");
        }
        else {
        	//Log.i(sensingFork.TAG, "clasified: "+labels[0]);
        	
        	/*int maxv = count[0];
        	int max = 0;
        	for (int i=0; i< count.length-1;i++){
        		if(maxv<count[i+1]){
        			max = i+1;
        			maxv=count[i+1];
        		}
        	}
       
        	Log.i(sensingFork.TAG, "ans "+max+" "+count[max]);*/
        	
        	/*TextView sensingText = (TextView)sensingFork.findViewById(R.id.de);
        	sensingText.setVisibility(View.VISIBLE);
        	sensingText.setText("Food "+ (max+1) );
        	sensingText.setTypeface(Typeface.createFromAsset(sensingFork.getAssets(), "fonts/SCHOOLA.TTF"));
        	new CountDownTimer(2000,1000){
        		public void onFinish(){
                	TextView sensingText = (TextView)sensingFork.findViewById(R.id.de);
        			sensingText.setVisibility(View.INVISIBLE);
        		}
        		
        		public void onTick(long millisUntilFinished){
    				
    			}
        	}.start();*/
        	
        	//Toast.makeText(sensingFork, "Classification is done, the result is food" + m, 3000).show();
        }
        return labels[0]-1;
    }
    
    /**
     * classify generate labels for features.
     * Return:
     * 	-1: Error
     * 	0: Correct
     */
	public int callSVM(float values[][], int indices[][], int groundTruth[], int isProb, String modelFile,
    		int labels[], double probs[]) {
    	// SVM type
    	final int C_SVC = 0;
    	final int NU_SVC = 1;
    	final int ONE_CLASS_SVM = 2;
    	final int EPSILON_SVR = 3;
    	final int NU_SVR = 4;
    	
    	// For accuracy calculation
    	int correct = 0;
    	int total = 0;
    	float error = 0;
    	float sump = 0, sumt = 0, sumpp = 0, sumtt = 0, sumpt = 0;
    	float MSE, SCC, accuracy;  	

    	int num = values.length;
    	int svm_type = C_SVC;
    	if (num != indices.length)
    		return -1;
    	// If isProb is true, you need to pass in a real double array for probability array
        int r = 0;
        		//sensingFork.parseDoClassificationNative(values, indices, isProb, modelFile, labels, probs);
        
        // Calculate accuracy
        if (groundTruth != null) {
        	if (groundTruth.length != indices.length) {
        		return -1;
        	}
        	for (int i = 0; i < num; i++) {
            	int predict_label = labels[i];
            	int target_label = groundTruth[i];
            	if(predict_label == target_label)
            		++correct;
    	        error += (predict_label-target_label)*(predict_label-target_label);
    	        sump += predict_label;
    	        sumt += target_label;
    	        sumpp += predict_label*predict_label;
    	        sumtt += target_label*target_label;
    	        sumpt += predict_label*target_label;
    	        ++total;
            }
            
        	if (svm_type==NU_SVR || svm_type==EPSILON_SVR)
        	{
        		MSE = error/total; // Mean square error
        		SCC = ((total*sumpt-sump*sumt)*(total*sumpt-sump*sumt)) / ((total*sumpp-sump*sump)*(total*sumtt-sumt*sumt)); // Squared correlation coefficient
        	}
        	accuracy = (float)correct/total*100;
            //Log.d(sensingFork.TAG, "Classification accuracy is " + accuracy);
        }       
        
        return r;
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
		
		//Log.i(SensingFork.TAG, rt);
		
		try {
			food_color = classifyJarLib(values[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parseResultColor(food_color);
		
	}
	
	public static int classifyJarLib(double[] values) throws IOException {
		int M_LENGTH = 11;
		
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
	
	public static int classifyJarLib(svm_node[] x) throws IOException {
		int M_LENGTH = 11;
		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
				
		double v = svm.svm_predict(model,x);
		Log.i("Libsvm", v + " ");
		return 0;
	}
	
	private int parseResultColor(int result) {
		if(true) { // US
			switch(result) {
			case 1:
				return 2;
			case 2:
				return 0;
			case 3:
				return 3;
			case 4:
				return 1;
			case 5:
				return 4;
			}
		}
		else if(1 == 2 ) {// CH
			switch(result) {
			case 1:
				return 3;
			case 2:
				return 4;
			case 3:
				return 1;
			case 4:
				return 3;
			case 5:
				return 2;
			case 6:
				return 1;
			case 7:
				return 1;
			case 8:
				return 2;
			case 9:
				return 0;
			case 10:
				return 0;
			}
		}
		else  {
			switch(result) { // JP
			case 1:
				return 3;
			case 2:
				return 0;
			case 3:
				return 4;
			case 4:
				return 4;
			case 5:
				return 1;
			case 6:
				return 1;
			case 7:
				return 2;
			}
		}
		return 0;
	}
}
