package com.raccoon.infinityarts.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import com.raccoon.infinityarts.advertising.AdsAllInOne;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.raccoon.infinityarts.R;
import static android.content.Context.MODE_PRIVATE;

public class AllActivitiesInOne extends Fragment {
    public TabLayout tabLayout;
    public ViewPager viewPager;
    static InterstitialAd mInterstitialAd;
    final ViewGroup nullParent = null;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.all_in_one, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if(Internet_Connection()) {

            viewPager = mActivity.findViewById(R.id.viewpager_all_in_one);
            tabLayout = mActivity.findViewById(R.id.tabs);

            if (viewPager != null && tabLayout != null) {

                viewPager.setOffscreenPageLimit(3);
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);

                View view1 = mActivity.getLayoutInflater().inflate(R.layout.customtab, nullParent);
                view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab1_background);

                View view2 = mActivity.getLayoutInflater().inflate(R.layout.customtab, nullParent);
                view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab2_background);

                View view3 = mActivity.getLayoutInflater().inflate(R.layout.customtab, nullParent);
                view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab3_background);

                /*View view4 = mActivity.getLayoutInflater().inflate(R.layout.customtab, nullParent);
                view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab4_background);*/

                tabLayout.getTabAt(0).setCustomView(view1);
                tabLayout.getTabAt(1).setCustomView(view2);
                tabLayout.getTabAt(2).setCustomView(view3);
                /*tabLayout.getTabAt(3).setCustomView(view4);*/
                tabLayout.getTabAt(0).getCustomView().setSelected(true);

                isStoragePermissionGranted();

                ShowBanner();

                InterstitialInitialization();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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
                                mActivity.finishAffinity();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder .setTitle(R.string.internet_error)
                    .setMessage(R.string.internet_error_des)
                    .setCancelable(true)
                    .setPositiveButton(R.string.internet_error_reload_bt, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = mActivity.getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage( mActivity.getBaseContext().getPackageName());
                            if(i != null)
                            {
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                mActivity.finishAffinity();
                            }
                        }
                    })
                    .setNegativeButton(R.string.internet_error_exit_bt, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                            mActivity.finishAffinity();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void InterstitialInitialization(){
        /* * * TURN IT ON BEFORE PUBLISHING * * */
        mInterstitialAd = new InterstitialAd(mActivity);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.InterstitialID));
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);//THIS IS FOR THE INTERSTITIAL INCLUDE LATER
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getChildFragmentManager());
        adapter.addFragment(new ForAvengers(), null);//DARK SIDE
        adapter.addFragment(new ForVillains(), null);//JEDI ORDER1
        adapter.addFragment(new ForOthers(), null);//SHIPS
        //adapter.addFragment(new ForSoldiers(), null);//SOLDIERS
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0, false);
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

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            //return null;
        }
    }
    public static InterstitialAd getAd() {
        return mInterstitialAd;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mActivity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    private boolean Internet_Connection()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void ShowBanner(){
        if(Ads_status() && mActivity != null)
            AdsAllInOne.showBanner(mActivity);
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
