package com.digiclack.wallpapers;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {



    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    //Fragments

    NewWallpapersFragment newWallpapersFragment;
    PopularWallpapersFragment popularWallpapersFragment;
    WallpaperChangerFragment wallpaperChangerFragment;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        //Initializing viewPager
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        //Initializing the tablayout
        tabLayout = (TabLayout) v.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position, false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

        return v;

    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        newWallpapersFragment = new NewWallpapersFragment();
        popularWallpapersFragment = new PopularWallpapersFragment();
        wallpaperChangerFragment = new WallpaperChangerFragment();
        adapter.addFragment(newWallpapersFragment, "New");
        adapter.addFragment(popularWallpapersFragment, "Popular");
        adapter.addFragment(wallpaperChangerFragment, "Auto Change");

        viewPager.setAdapter(adapter);
    }
}
