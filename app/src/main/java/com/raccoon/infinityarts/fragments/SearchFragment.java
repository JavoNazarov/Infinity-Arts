package com.raccoon.infinityarts.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.raccoon.infinityarts.advertising.AdsSearch;
import com.raccoon.infinityarts.R;
import com.raccoon.infinityarts.activity.DrawerActivity;
import com.raccoon.infinityarts.adapter.GalleryAdapter;
import com.raccoon.infinityarts.app.AppController;
import com.raccoon.infinityarts.model.Image;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {

    private static ArrayList<Image> images;
    private GalleryAdapter mAdapter;
    public RecyclerView recyclerView;
    public View rootView;
    public TextView use_only_latin_message;
    private FragmentActivity mActivity;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Ads_status()){
            AdsSearch.showBanner(mActivity);
        }

        recyclerView = rootView.findViewById(R.id.recycler_view);
        use_only_latin_message = rootView.findViewById(R.id.use_only_latin_id);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getContext(), images);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages();
    }

    public void fetchImages() {

        JsonArrayRequest req = new JsonArrayRequest(getResources().getString(R.string.endpoint_search),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        boolean image_found = false;
                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("large"));
                                image.setMedium(url.getString("large"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                String whole_name = object.getString("name").toLowerCase();
                                DrawerActivity drawerActivity = new DrawerActivity();
                                String substring_search = drawerActivity.searchQuery;
                                if (substring_search!=null) {
                                    substring_search = substring_search.toLowerCase();
                                    substring_search = substring_search.replaceAll("\\s+", "_");
                                }

                                if (whole_name != null && substring_search != null && substring_search.length() <= whole_name.length() && whole_name.contains(substring_search)) {
                                    images.add(image);
                                    image_found = true;
                                }

                            } catch (JSONException e) {
                                //do nothing
                            }
                        }

                        if (!image_found) {
                            use_only_latin_message.setVisibility(View.VISIBLE);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    public boolean Ads_status(){
        SharedPreferences prefs = mActivity.getSharedPreferences(getResources().getString(R.string.MY_PREFS_NAME), MODE_PRIVATE);
        return prefs.getBoolean(getResources().getString(R.string.banner_and_interstitial_is_active), true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

}