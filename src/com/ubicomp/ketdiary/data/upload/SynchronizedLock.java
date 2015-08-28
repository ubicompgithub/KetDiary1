package com.ubicomp.ketdiary.data.upload;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import android.annotation.SuppressLint;

/**
 * Lock for DataUploader
 * 
 * @author Stanley Wang
 * @see DataUploadeer
 */
public class SynchronizedLock implements java.util.concurrent.locks.Lock {

	@SuppressLint("UseValueOf")
	private Integer lockInteger = new Integer(0);

	public static final SynchronizedLock sharedLock = new SynchronizedLock();

	@Override
	public void lock() {
		synchronized (lockInteger) {
			lockInteger = 1;
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		synchronized (lockInteger) {
			return lockInteger == 0;
		}
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return false;
	}

	@Override
	public void unlock() {
		synchronized (lockInteger) {
			lockInteger = 0;
		}
	}

}
