package com.digiclack.wallpapers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digiclack.unsplash.Unsplash;
import com.digiclack.unsplash.api.Order;
import com.digiclack.unsplash.models.Photo;
import com.digiclack.unsplash.models.SearchResults;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WallpaperChangerFragment extends Fragment {

    TextView autoWallpaperStatusTv, changeIntervalTv;
    Button autoWallpaperSettingsBtn;
    public WallpaperChangerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.wallpaper_changer_fragment, container, false);


        autoWallpaperStatusTv = v.findViewById(R.id.status_auto_wallpaper_tv);
        autoWallpaperSettingsBtn = v.findViewById(R.id.auto_wallpaper_settings_btn);
        changeIntervalTv = v.findViewById(R.id.change_interval_tv);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean statusPref = SP.getBoolean("auto_wallpaper_switch", false);

        if(statusPref) {
            autoWallpaperStatusTv.setText(" Active");
        } else {
            autoWallpaperStatusTv.setText(" Not Active");
            autoWallpaperStatusTv.setTextColor(Color.RED);
        }

        int intervalPref = Integer.parseInt(SP.getString("wallpaper_change_interval", "1440"));


        if(intervalPref<60){
            changeIntervalTv.setText("Every " + intervalPref + " Mins");
        } else if (intervalPref==60){
            changeIntervalTv.setText("Every " + intervalPref/60 + " Hour");

        } else if (intervalPref>60 && intervalPref<10000){
            changeIntervalTv.setText("Every " + intervalPref/60 + " Hours");
        } else if(intervalPref>=10080) {
            changeIntervalTv.setText("Every " + intervalPref/10080 + " Week");
        }

        autoWallpaperSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),AutoWallpaperSettingsActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });



        return v;
    }





}
