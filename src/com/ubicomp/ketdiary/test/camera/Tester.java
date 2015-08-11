package com.ubicomp.ketdiary.test.camera;

/** Interface defines what should a brac test caller do */
public interface Tester {
	public static final int _GPS = 0;
	public static final int _BT = 1;
	public static final int _CAMERA = 2;

	/**
	 * update if the initializing tasks are done
	 * 
	 * @param type
	 *            type of the task
	 */
	public void updateInitState(int type);

	/**
	 * update if the processing tasks are done
	 * 
	 * @param type
	 *            type of the task
	 */
	public void updateDoneState(int type);
}
