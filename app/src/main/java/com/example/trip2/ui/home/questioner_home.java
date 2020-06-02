package com.example.trip2.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trip2.R;
import com.google.android.material.tabs.TabLayout;


public class questioner_home extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsPagerAdapter mTabsPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_questioner_home, container, false);

        mViewPager = view.findViewById(R.id.tabs_pager);
        mTabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mTabsPagerAdapter);

        mTabLayout = view.findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }
}
