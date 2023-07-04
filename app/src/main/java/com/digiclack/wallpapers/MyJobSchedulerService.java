package com.digiclack.wallpapers;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.digiclack.unsplash.Unsplash;
import com.digiclack.unsplash.models.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyJobSchedulerService extends JobService {
    public MyJobSchedulerService() {
    }

    private String TAG="MyJobSchedulerService";
    private String CLIENT_ID;
    private Unsplash unsplash;
    private String photoUrl;

    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "Job started");

        CLIENT_ID = getApplicationContext().getString(R.string.unsplash_access_key);;

        unsplash = new Unsplash(CLIENT_ID);
        unsplash.getRandomPhoto(null, null, null, "wallpaper", null, null, null, new Unsplash.OnPhotoLoadedListener() {
            @Override
            public void onComplete(Photo photo) {
                photoUrl = photo.getUrls().getFull();
                new SetAsWallpaperAsync2(getApplicationContext(), params).execute();

            }

            @Override
            public void onError(String error) {

            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }


    public class SetAsWallpaperAsync2 extends AsyncTask<Object, Void, Bitmap> {

        private Context context;
        JobParameters params;

        public SetAsWallpaperAsync2(Context context, JobParameters params) {

            this.context = context;
            this.params = params;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {

            URL url = null;
            HttpURLConnection connection = null;
            InputStream input = null;
            try {
                url = new URL(photoUrl);

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);

                connection.connect();

                input = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmapFrmUrl = BitmapFactory.decodeStream(input);

            return bitmapFrmUrl;

        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null) {

                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(context);
                try {

                    myWallpaperManager.setBitmap(result);
                    jobFinished(params, false);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
        }
    }
}
