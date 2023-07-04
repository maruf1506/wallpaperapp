package com.digiclack.wallpapers;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class AutoWallpaperSettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static String TAG = "AutoWallpaperSettingsActivity";
    boolean wifi;
    int interval=1440;
    int jobID= 4343;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();



        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        settings.registerOnSharedPreferenceChangeListener(this);
    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.auto_wallpaper_preferences);


        }


    }

    // XML Preference change listener
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        switch (key) {
            case "auto_wallpaper_switch":
                boolean autoWallpaperSwitch = SP.getBoolean("auto_wallpaper_switch", false);
                if (autoWallpaperSwitch) {
                    Log.d(TAG, "autowallpaper enabled");
                    ScheduleAutomaticWallpaper();
                    Toast.makeText(this, "Auto-wallpaper enabled", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(this, "Auto-wallpaper disabled", Toast.LENGTH_SHORT).show();
                    cancelJob();

                }
                break;

            case "wifi_switch":
                 wifi = SP.getBoolean("wifi_switch", true);
                if (wifi) {
                    Toast.makeText(this, "Connectivity wifi only", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Connectivity any network", Toast.LENGTH_SHORT).show();

                }
                break;

            case "wallpaper_change_interval":
                interval = Integer.parseInt(SP.getString("wallpaper_change_interval", "1440"));
                Toast.makeText(this, "Interval set", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"interval time set "+interval+" mins");
                break;
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(jobID);
        Log.d(TAG, "Job cancelled");
    }


    public void ScheduleAutomaticWallpaper() {

        JobInfo info;
        if(wifi) {
            ComponentName componentName = new ComponentName(this, MyJobSchedulerService.class);
             info = new JobInfo.Builder(jobID, componentName)
                    .setPersisted(true) //starts jobscheduler after restart
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPeriodic(interval * 60 * 1000)
                    .build();
        } else {
            ComponentName componentName = new ComponentName(this, MyJobSchedulerService.class);
             info = new JobInfo.Builder(jobID, componentName)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(interval * 60 * 1000)
                    .build();
        }

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,TabActivity.class);
        startActivity(i);
        finish();

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
