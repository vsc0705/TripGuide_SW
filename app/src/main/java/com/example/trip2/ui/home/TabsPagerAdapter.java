package com.example.trip2.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(@NonNull FragmentManager fm) { super(fm); }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PidListFragment pidListFragment = new PidListFragment();
                return pidListFragment;
            case 1:
                PidWirteFragment pidWirteFragment = new PidWirteFragment();
                return pidWirteFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "PID"; // ChatsFragment
            case 1:
                return "WRITE"; // ttttRequestsFragment
            default:
                return null;
        }
    }
}
