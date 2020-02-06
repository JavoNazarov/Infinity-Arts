package com.raccoon.infinityarts.advertising;

import android.app.Activity;
import android.view.View;
import com.raccoon.infinityarts.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdsAllInOne {

    public static void showBanner(final Activity activity)
    {
        final AdView banner = activity.findViewById(R.id.banner_all_in_one);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        banner.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                setupContentViewPadding(activity,banner.getHeight());
            }
        });

    }

    private static void setupContentViewPadding(Activity activity, int padding)
    {
        View view  = activity.findViewById(R.id.coordinator_all_in_one);
        if(view != null)
            view.setPadding(view.getPaddingLeft(),view.getPaddingTop(),view.getPaddingRight(),padding);
    }
}