package com.ubicomp.ketdiary.db;

import java.util.Vector;

public class DBTip {
	public static DBTip inst = new DBTip();
	private Vector<String> vec_str;
	public DBTip(){
		vec_str = new Vector<String>();
		vec_str.add("愷他命屬中樞神經抑制劑，副作用包括心搏過速、血壓上升");
		vec_str.add("愷他命副作用包括震顫、肌肉緊張而呈強直性、陣攣性運動");
		vec_str.add("研究顯示濫用愷他命，會罹患慢性間質性膀胱炎");
		vec_str.add("濫用愷他命會使膀胱容量變小，產生頻尿、小便疼痛、血尿、下腹部疼痛等症狀");
		vec_str.add("濫用愷他命會出現尿量減少、水腫等腎功能不全的症狀，甚至須進行膀胱重建手術");
		vec_str.add("愷他命藥效約可維持1小時，但影響吸食者感覺、協調及判斷力則可長達16至24小時");
	}
	
	public String getTip(){
		int rand_id = (int)(Math.random()*vec_str.size());
		return vec_str.get(rand_id);
	}
}
