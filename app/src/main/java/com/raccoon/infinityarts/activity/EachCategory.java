package com.raccoon.infinityarts.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;
import com.raccoon.infinityarts.advertising.AdsEachCategory;
import com.raccoon.infinityarts.R;
import com.raccoon.infinityarts.fragments.SelectedCategoryDifferentJSONs;

public class EachCategory extends AppCompatActivity {

    public ViewPager viewPager;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_category);
        Toolbar toolbar = findViewById(R.id.each_category_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(Internet_Connection())
        {

            viewPager = findViewById(R.id.viewpager_each_category);

            relativeLayout = findViewById(R.id.each_category_root);

            if(viewPager != null) {
                setupViewPager(viewPager);

                isStoragePermissionGranted();

                if(Ads_status())
                    AdsEachCategory.showBanner(EachCategory.this); //THIS IS FOR THE BANNER INCLUDE LATER

            }
            Intent intent= getIntent();
            Bundle b = intent.getExtras();
            if(b!=null)
            {
                String toolbar_title =(String) b.get("toolbar_title");
                setTitle(toolbar_title);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle(R.string.server_error)
                        .setMessage(R.string.server_error_des)
                        .setCancelable(true)
                        .setPositiveButton(R.string.server_error_pos_bt, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory(Intent.CATEGORY_HOME);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                finishAffinity();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder .setTitle(R.string.internet_error)
                    .setMessage(R.string.internet_error_des)
                    .setCancelable(true)
                    .setPositiveButton(R.string.internet_error_reload_bt, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage( getBaseContext().getPackageName());
                            if(i != null)
                            {
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(R.string.internet_error_exit_bt, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //deleteCache(getApplicationContext());
                            EachCategory.this.finish();

                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                            finishAffinity();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new SelectedCategoryDifferentJSONs(), null);//Categories

        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            //return null;
        }
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean Internet_Connection()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null)
        {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        else
            return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public boolean Ads_status(){
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.MY_PREFS_NAME), MODE_PRIVATE);
        return prefs.getBoolean(getResources().getString(R.string.banner_and_interstitial_is_active), true);
    }
}
