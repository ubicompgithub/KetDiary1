package com.ubicomp.ketdiary.system.check;

import java.util.Calendar;

import com.ubicomp.ketdiary.system.PreferenceControl;

public class StartDateCheck {
	public static boolean afterStartDate() {
		Calendar now = Calendar.getInstance();
		Calendar start_date = PreferenceControl.getStartDate();

		return now.after(start_date);
	}
}
