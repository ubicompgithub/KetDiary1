package com.ubicomp.ketdiary.system.check;

public class TimeBlock {

	public final static int MAX = 2, MIN = 0;
	public final static int MORNING = 0;
	public final static int NOON = 1;
	public final static int NIGHT = 2;

	public static boolean hasBlock(int block) {
		return block >= MIN && block <= MAX;
	}

	public static int getTimeBlock(int hour_24) {
		if (hour_24 < 12)
			return MORNING;
		else if (hour_24 < 20)
			return NOON;
		else
			return NIGHT;
	}

	public static boolean isEmpty(int timeblock, int cur_hour) {
		switch (timeblock) {
		case MORNING:
			if (cur_hour >= 12)
				return false;
			break;
		case NOON:
			if (cur_hour >= 20)
				return false;
			break;
		case NIGHT:
			if (cur_hour >= 24)
				return false;
			break;
		}
		return true;
	}
}
