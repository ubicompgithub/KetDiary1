package com.ubicomp.ketdiary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;

public class StorytellingFragment extends Fragment {

	private View view;
	private ListView listView;
	private TextView textview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_3, container,
				false);	
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
		//LoadingDialogControl.dismiss();
		//ClickLog.Log(ClickLogId.STORYTELLING_ENTER);
		//getView().setFocusableInTouchMode(true);
		//getView().requestFocus();

		//RecordCheckTask task = new RecordCheckTask();
		//task.execute();
	}
}




