package com.ubicomp.ketdiary.noUse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.dialog.CheckResultDialog;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class Storytelling2Fragment extends Fragment {

	private View view;
	private ListView listView;
	private TextView textview;
	private CheckResultDialog msgBox;
	private RelativeLayout fragment_layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_3, container,
				false);	
		fragment_layout = (RelativeLayout)view.findViewById(R.id.fragment3_layout);
		textview = (TextView)view.findViewById(R.id.chooseResult);
		listView = (ListView)view.findViewById(R.id.listView2);
		 String[] arr = new String[]{
	             "A","B","C","D","E","F","G"
	     };
	     ArrayAdapter<String> adapter = 
	            new ArrayAdapter<String>(getActivity(),
	                android.R.layout.simple_list_item_1,arr);
	     listView.setAdapter(adapter);
	        

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		msgBox = new CheckResultDialog(fragment_layout);
		
		long curTime = System.currentTimeMillis();
		long testTime = PreferenceControl.getLatestTestCompleteTime();
		long pastTime = curTime - testTime;
		int note_state = PreferenceControl.getAfterTestState();
		
		if(PreferenceControl.getCheckResult() && pastTime < MainActivity.WAIT_RESULT_TIME){ //還沒察看結果且時間還沒到
		
		}
		else if(PreferenceControl.getCheckResult() && pastTime > MainActivity.WAIT_RESULT_TIME){//還沒察看結果且時間到了
			msgBox.initialize();
			msgBox.show();	
		}
		else{
			
		}
		//LoadingDialogControl.dismiss();
		//ClickLog.Log(ClickLogId.STORYTELLING_ENTER);
		//getView().setFocusableInTouchMode(true);
		//getView().requestFocus();

		//RecordCheckTask task = new RecordCheckTask();
		//task.execute();
	}
}




