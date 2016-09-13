package com.inmobi.surprise.lib.android.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by davendar.ojha on 7/7/16.
 */
public class LoginAdapter extends FragmentPagerAdapter {
    public LoginAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new SecondFragment();
                return fragment;
            default:
                fragment = new ThirdFragment();
                return fragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
