package com.raccoon.infinityarts.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.InterstitialAd;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import com.raccoon.infinityarts.R;
import com.raccoon.infinityarts.model.Image;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import static android.content.Context.MODE_PRIVATE;

public class SlideshowDialogFragment extends DialogFragment implements View.OnClickListener {

    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblTitle, lblDate;
    private int selectedPosition = 0;
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    public boolean isFABOpen = false;
    public boolean can_cancel_download;
    public boolean can_cancel_set_as_wallpaper;
    public boolean downloaded;
    public boolean set_wallpaper;
    public static String url_of_any_image = "null";
    public static String name_of_any_image = "null";
    private Activity mActivity;

    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView()!=null)
            viewPager = getView().findViewById(R.id.viewpager);

        fab = getView().findViewById(R.id.fab);
        fab1 = getView().findViewById(R.id.fab1);
        fab2 = getView().findViewById(R.id.fab2);
        fab3 = getView().findViewById(R.id.fab3);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        can_cancel_download = true;
        can_cancel_set_as_wallpaper = true;

        lblTitle = getView().findViewById(R.id.title);
        lblDate = getView().findViewById(R.id.date);

        images = (ArrayList<Image>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab)
        {
            if(!isFABOpen){
                showFABMenu();
                rotateFabForward();
            }else{
                closeFABMenu();
                rotateFabBackward();
            }
        }
        else if(v.getId() == R.id.fab1)
        {
            share_app();
        }
        else if(v.getId() == R.id.fab2)
        {
            if(isStoragePermissionGranted())
            {
                can_cancel_download = false;
                downloaded = false;

                fab2.setImageResource(R.drawable.loading_circle_icon);
                ViewCompat.animate(fab2)
                        .rotation(7200.0F)
                        .withLayer()
                        .setDuration(15000)
                        .start();
                fab2.setClickable(false);

                final DownloadFile downloadFile = new DownloadFile();
                downloadFile.execute(url_of_any_image);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!downloaded)
                        {
                            if(fab2!=null) {
                                ViewCompat.animate(fab2)
                                        .rotation(0.0F)
                                        .withLayer()
                                        .setDuration(0L)
                                        .start();
                                downloadFile.cancel(true);
                                fab2.setImageResource(R.drawable.download_icon_floating);
                                fab2.setClickable(true);
                                StyleableToast.makeText(getContext(), getContext().getString(R.string.time_out_error),
                                        Toast.LENGTH_LONG, R.style.MyToast).show();
                            }
                        }
                    }
                }, 15500);

            }
            else
                StyleableToast.makeText(getContext(), getContext().getString(R.string.storage_error), Toast.LENGTH_LONG, R.style.MyToast).show();
        }
        else if(v.getId() == R.id.fab3)
        {
            can_cancel_set_as_wallpaper = false;
            set_wallpaper = false;

            fab3.setImageResource(R.drawable.loading_circle_icon);
            ViewCompat.animate(fab3)
                    .rotation(7200.0F)
                    .withLayer()
                    .setDuration(15000)
                    .start();
            fab3.setClickable(false);


            final SetWallpaperAsyncTask setWallpaperAsyncTask = new SetWallpaperAsyncTask();
            setWallpaperAsyncTask.execute("");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!set_wallpaper)
                    {
                        if(fab3!=null)
                        {
                            ViewCompat.animate(fab3)
                                    .rotation(0.0F)
                                    .withLayer()
                                    .setDuration(0L)
                                    .start();
                            setWallpaperAsyncTask.cancel(true);
                            fab3.setImageResource(R.drawable.set_as_wallpaper_floating);
                            fab3.setClickable(true);
                            StyleableToast.makeText(getContext(), getContext().getString(R.string.time_out_error),
                                    Toast.LENGTH_LONG, R.style.MyToast).show();
                        }
                    }
                }
            }, 15500);
        }
    }

    public void share_app()
    {

        Intent sharingIntent = new Intent();

        sharingIntent.setAction(Intent.ACTION_SEND);

        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Infinity Arts");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url_of_any_image);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    private class SetWallpaperAsyncTask extends AsyncTask<String, Void, String> {

        boolean check_error = false;
        @Override
        protected String doInBackground(String... params) {

            if(fab3 != null)
            {
                String URL = SlideshowDialogFragment.url_of_any_image;
                setWallpaper(URL);
                return "Executed";
            }else
                return "Failed";


        }

        @Override
        protected void onPostExecute(String result) {

                if(!check_error)
                {
                    ViewCompat.animate(fab3)
                            .rotation(0.0F)
                            .withLayer()
                            .setDuration(0L)
                            .start();
                    set_wallpaper = true;

                    if (fab3 != null)
                    {
                        fab3.setImageResource(R.drawable.set_as_wallpaper_floating);
                        //Toast.makeText(getContext(), "Wallpaper is set",
                                //Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(getContext(), getContext().getString(R.string.wallpaper_set), Toast.LENGTH_LONG, R.style.MyToast).show();
                        fab3.setClickable(true);
                    }
                    can_cancel_set_as_wallpaper = true;
                }
                else
                {
                    set_wallpaper = false;
                    can_cancel_set_as_wallpaper = true;
                }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        private void setWallpaper(String url) {
            try {
                WallpaperManager wpm = WallpaperManager.getInstance(getContext());
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins);
            } catch (Exception e) {
                check_error = true;
            }
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new Dialog(mActivity, getTheme()){
            @Override
            public void onBackPressed() {
                dismiss();
                fab3 = null;
                fab2 = null;
                new DownloadFile().cancel(true);
                new SetWallpaperAsyncTask().cancel(true);

            }
        };
    }

    private class DownloadFile extends AsyncTask<String,Integer,Long> {
        ProgressDialog mProgressDialog = new ProgressDialog(mActivity);
        String absolute_path;

        @Override
        protected void onPreExecute() {

            if(fab2!=null) {
                super.onPreExecute();
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            }


        }
        @Override
        protected Long doInBackground(String... aurl) {
            if(fab2!=null) {
                int count;
                try {
                    URL url = new URL(aurl[0]);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    String targetFileName = name_of_any_image + ".jpg";//Change name and subname
                    int lenghtOfFile = conexion.getContentLength();
                    String PATH = Environment.getExternalStorageDirectory() + "/" + "Infinity Arts" + "/";
                    absolute_path = PATH + targetFileName;
                    File folder = new File(PATH);
                    if (!folder.exists()) {
                        folder.mkdir();//If there is no folder it will be created.
                    }
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(PATH + targetFileName);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress((int) (total * 100 / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    can_cancel_download = true;
                }
            }
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
            if(fab2!=null) {
                mProgressDialog.setProgress(progress[0]);
                if (mProgressDialog.getProgress() == mProgressDialog.getMax()) {
                    ViewCompat.animate(fab2)
                            .rotation(0.0F)
                            .withLayer()
                            .setDuration(0L)
                            .start();

                    fab2.setClickable(true);
                    fab2.setImageResource(R.drawable.download_icon_floating);

                    StyleableToast.makeText(getContext(), getContext().getString(R.string.saved_folder), Toast.LENGTH_LONG, R.style.MyToast).show();
                    scanFile(absolute_path);

                    downloaded = true;
                    can_cancel_download = true;
                }
            }
        }
        private void scanFile(String path) {

            MediaScannerConnection.scanFile(mActivity,
                    new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {
                            //Log.i("TAG", "Finished scanning " + path);
                        }
                    });
        }

    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_65)).setDuration(100);
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_130)).setDuration(100);
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_195)).setDuration(100);
    }

    public void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0).setDuration(100);
        fab2.animate().translationY(0).setDuration(100);
        fab3.animate().translationY(0).setDuration(100);
    }

    public void rotateFabForward() {
        ViewCompat.animate(fab)
                .rotation(-45.0F)
                .withLayer()
                .setDuration(50L)
                .start();
    }

    public void rotateFabBackward() {
        ViewCompat.animate(fab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(50L)
                .start();
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);

            if(Ads_status()) {
                InterstitialAd ad = AllActivitiesInOne.getAd();
                if (ad != null && ad.isLoaded()) {
                    ad.show();
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        Image image = images.get(position);
        lblTitle.setText(image.getName());
        lblDate.setText(image.getTimestamp());
        url_of_any_image = image.getLarge();
        name_of_any_image = image.getName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    private class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        private MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = view.findViewById(R.id.image_preview);
            final ProgressBar progressBar = view.findViewById(R.id.progress);
            Image image = images.get(position);

            Glide.with(mActivity).load(image.getLarge())
                    .thumbnail(0.5f)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .skipMemoryCache(true)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == (obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public boolean Ads_status(){
        SharedPreferences prefs = mActivity.getSharedPreferences(getResources().getString(R.string.MY_PREFS_NAME), MODE_PRIVATE);
        return prefs.getBoolean(getResources().getString(R.string.banner_and_interstitial_is_active), true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}
