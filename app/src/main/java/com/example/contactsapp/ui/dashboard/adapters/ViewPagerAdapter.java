package com.example.contactsapp.ui.dashboard.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.contactsapp.ui.dashboard.DashboardActivity;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_PAGES = 3;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DashboardActivity.fragments.get(position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
