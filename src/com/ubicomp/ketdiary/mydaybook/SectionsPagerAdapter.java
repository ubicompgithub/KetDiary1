package com.ubicomp.ketdiary.mydaybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	private Database myConstant;
	
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        myConstant = new Database();
    }

    @Override
    public Fragment getItem(int position) {
    	Fragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putInt(SectionFragment.FRAGMENT_MONTH, position + myConstant.START_MONTH - 1);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }
	

}
