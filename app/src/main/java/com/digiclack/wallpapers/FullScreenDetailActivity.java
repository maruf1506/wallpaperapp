package com.digiclack.wallpapers;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digiclack.unsplash.Unsplash;
import com.digiclack.unsplash.models.Download;
import com.digiclack.unsplash.models.Photo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class FullScreenDetailActivity extends AppCompatActivity {

    private static String TAG = "FullScreenDetailActivity";
    private String CLIENT_ID;
    public ImageView imageView;
    ProgressBar progressBar;
    String photoUrl, splashUrl;
    TextView photoByTv;
    TextView onSplashTextView;
    boolean downloaded = false;
    private Unsplash unsplash;
    private String photoId;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        CLIENT_ID = getString(R.string.unsplash_access_key);;
        unsplash = new Unsplash(CLIENT_ID);
        photoId = getIntent().getStringExtra("PHOTO_ID");

        imageView = findViewById(R.id.imageView);
        photoByTv = findViewById(R.id.photo_by);
        onSplashTextView = findViewById(R.id.on_splash_tv);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);




        //InterstitialAd
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_admob_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        unsplash.getPhoto(photoId, new Unsplash.OnPhotoLoadedListener() {
            @Override
            public void onComplete(Photo photo) {

                photoUrl = photo.getUrls().getRegular();
                splashUrl = photo.getLinks().getHtml();
                if (photoUrl.length() > 1) {
                    Picasso.get().load(photoUrl).into(imageView);
                    if (progressBar.isShown()) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    photoByTv.setText("Photo by " + photo.getUser().getFirstName() + " " + photo.getUser().getLastName() + " ");

                    onSplashTextView.setText(Html.fromHtml("<p>on <u>Unsplash</u></p>"));
                    onSplashTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    onSplashTextView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(splashUrl));
                            startActivity(browserIntent);
                        }
                    });
                }

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_screen, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_set_wallpaper:

                //shows interstitial ad when "set as wallpaper" is clicked
                showAd();

                Toast.makeText(this, "Please wait... Setting Wallpaper", Toast.LENGTH_SHORT).show();
                new SetAsWallpaperAsync(getApplicationContext(), photoUrl).execute();

                return true;

            case R.id.download_wallpaper:

                //shows interstitial ad when "download" is clicked
                showAd();

                //triggering dowload -- api guideline recommendation
                unsplash.getPhotoDownloadLink(photoId, new Unsplash.OnLinkLoadedListener() {
                    @Override
                    public void onComplete(Download downloadLink) {

                        photoUrl = downloadLink.getUrl();
                        isStoragePermissionGranted();

                    }

                    @Override
                    public void onError(String error) {

                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    private void downloadPhotoPicasso() {
        String storagePath = Environment.getExternalStorageDirectory().getPath() + "/Download/";
//Log.d("Strorgae in view",""+storagePath);
        File f = new File(storagePath);
        if (!f.exists()) {
            f.mkdirs();
        }
//storagePath.mkdirs();
        if (!f.exists()) {
            f.mkdirs();
        }
//Log.d("Storage ",""+pathname);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(photoUrl);
        if (!downloaded) {
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir("/Download", uri.getLastPathSegment()+".jpg");
            Long referese = dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
            downloaded=true;
        } else {
            Toast.makeText(this, "Saved to gallery", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                downloadPhotoPicasso();

                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Toast.makeText(this, "Download Failed! Permission not granted.", Toast.LENGTH_SHORT).show();

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            downloadPhotoPicasso();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

}


