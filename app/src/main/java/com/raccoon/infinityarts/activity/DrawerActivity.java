package com.raccoon.infinityarts.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.raccoon.infinityarts.fragments.AllActivitiesInOne;
import com.raccoon.infinityarts.fragments.CategoriesFragment;
import com.raccoon.infinityarts.inappbilling.BillingManager;
import com.raccoon.infinityarts.R;
import com.raccoon.infinityarts.fragments.SearchFragment;
import com.raccoon.infinityarts.util.IabHelper;
import com.raccoon.infinityarts.util.IabResult;
import com.raccoon.infinityarts.util.Inventory;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import java.util.List;
import hotchemi.android.rate.AppRate;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static String searchQuery;
    BillingManager billingManager;
    IabHelper mHelper;
    public boolean mIsPremium = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String base64EncodedPublicKey = getResources().getString(R.string.base64EncodedPublicKey);

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        SetupIabHelper();

        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //InAppBilling initialization
        billingManager = new BillingManager(DrawerActivity.this, new MyBillingUpdateListener());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        DefaultFragmentToShow();

        Show_if_Update_Available();

        Rate_the_App_pop_up_Dialog();
    }

    private void DefaultFragmentToShow(){
        setTitle(R.string.title_classic4);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment_switch_container, new AllActivitiesInOne());
        tx.commit();
    }

    public void SetupIabHelper(){
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d("result", "is not success");
                }
                else{
                    CheckUserPurchaseHistory();
                }
            }
        });
    }

    public void CheckUserPurchaseHistory(){
        try{
            mHelper.queryInventoryAsync(mGotInventoryListener);
        }
        catch (Exception ex){
            //Log.d("Ads_status", ex.toString());
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                Log.d("result", "failure occured");
            }
            else {
                // does the user have the premium upgrade?
                mIsPremium = inventory.hasPurchase(getResources().getString(R.string.SKU_PREMIUM));
                if(mIsPremium){
                    Ads_status();
                    Log.d("Ads_status1", "removed");
                }
                // update UI accordingly
            }
        }
    };

    private void Show_if_Update_Available(){
        AppUpdater appUpdater = new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setCancelable(false)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY);
        appUpdater.start();
    }

    private void Rate_the_App_pop_up_Dialog()
    {
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(2) // default 10
                .setRemindInterval(1) // default 1
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else{
            if (exit) {
                finishAffinity();
            } else {
                StyleableToast.makeText(this, this.getString(R.string.on_back_pressed), Toast.LENGTH_SHORT, R.style.MyToast).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }
    }
    private void facebook_open()
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.facebook_link)));
        startActivity(browserIntent);
    }

    private void ag_website_open()
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.website_link)));
        startActivity(browserIntent);
    }

    private void rate_app()
    {

        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_classic) {

            setTitle(R.string.title_classic4);
            AllActivitiesInOne all_activities_in_one = new AllActivitiesInOne();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch_container, all_activities_in_one).commit();

        } else if (id == R.id.nav_categories) {

            setTitle(R.string.title_categories);
            CategoriesFragment menu2 = new CategoriesFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch_container, menu2).commit();

        } /*else if (id == R.id.nav_about) {

            setTitle(R.string.title_about);
            AboutFragment menu1 = new AboutFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch_container, menu1).commit();

        } */else if (id == R.id.nav_facebook) {

            facebook_open();

        } else if (id == R.id.nav_website) {

            ag_website_open();

        } else if (id == R.id.nav_rate_us) {

            rate_app();
        } else if(id== R.id.remove_ads){

            billingManager.initiatePurchaseFlow( getResources().getString(R.string.SKU_PREMIUM), null, BillingClient.SkuType.INAPP);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.dashboard, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Return true to allow the action view to expand

                        return true;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        int index = fragmentManager.getBackStackEntryCount() - 1;

                        if(index!=-1){
                            String tag = fragmentManager.getBackStackEntryAt(index).getName();

                            switch (tag) {
                                case "classic4":
                                    fragmentManager.beginTransaction().replace(R.id.fragment_switch_container, new AllActivitiesInOne()).addToBackStack("home").commit();
                                    break;
                            }
                        }
                        return true;
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // text submitted to the search view get query
                SearchFragment menu1 = new SearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_switch_container, menu1).addToBackStack("classic4").commit();
                searchQuery = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // on text change don't show any results
                return false;
            }


        });

        return super.onCreateOptionsMenu(menu);
    }

    public void Ads_status(){
        SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.MY_PREFS_NAME), MODE_PRIVATE).edit();
        editor.putBoolean(getResources().getString(R.string.banner_and_interstitial_is_active), false);
        editor.apply();
    }
    public void Restart_app(){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        if(i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }



    class MyBillingUpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
        }
        @Override
        public void onConsumeFinished(String token, int result) {
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            Ads_status();
            Restart_app();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try
            {
                mHelper.dispose();
            } catch (Exception ex){
                Log.d("exception", "mhelper error occured");
            }
        }
        mHelper = null;
    }
}
