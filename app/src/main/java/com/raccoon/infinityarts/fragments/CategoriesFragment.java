package com.raccoon.infinityarts.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import com.raccoon.infinityarts.R;
import com.raccoon.infinityarts.activity.EachCategory;
import com.raccoon.infinityarts.activity.RecyclerItemClickListener;
import com.raccoon.infinityarts.adapter.TestRecyclerAdapter;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

public class CategoriesFragment extends Fragment {

    public static String json_of_category;
    public static String toolbar_title;
    private Activity mActivity;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParallaxRecyclerView recyclerView = mActivity.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new TestRecyclerAdapter(mActivity));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mActivity, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        json_of_category = "http://marvel.geekart.club/Categories/Characters.json";
                        toolbar_title= "";

                        if(position == 0)
                        {
                           // json_of_category = "http://marvel.geekart.club/Categories/Iron_Man.json";
                            toolbar_title= "Iron_Man";
                        }else if(position == 1){
                          //  json_of_category = "http://marvel.geekart.club/Categories/Captain.json";
                            toolbar_title= "Captain";
                        }else if(position == 2){
                           // json_of_category = "http://marvel.geekart.club/Categories/Hulk.json";
                            toolbar_title= "Hulk";
                        }else if(position == 3){
                           // json_of_category = "http://marvel.geekart.club/Categories/Thor.json";
                            toolbar_title= "Thor";
                        }else if(position == 4){
                           // json_of_category = "http://marvel.geekart.club/Categories/Spider-man.json";
                            toolbar_title= "Spider-man";
                        }else if(position == 5){
                           // json_of_category = "http://marvel.geekart.club/Categories/Guardians.json";
                            toolbar_title= "Guardians";
                        }else if(position == 6){
                          //  json_of_category = "http://marvel.geekart.club/Categories/Doctor_Strange.json";
                            toolbar_title= "Doctor_Strange";
                        }else if(position == 7){
                            //json_of_category = "http://marvel.geekart.club/Categories/Scarlet_Witch.json";
                            toolbar_title= "Scarlet_Witch";
                        }else if(position == 8){
                           // json_of_category = "http://marvel.geekart.club/Categories/Black_Panther.json";
                            toolbar_title= "Black_Panther";
                        }else if(position == 9){
                           // json_of_category = "http://marvel.geekart.club/Categories/Thanos.json";
                            toolbar_title= "Thanos";
                        }
                        /*else if(position == 10){

                            toolbar_title= "Thanos";
                        }*/

                        Intent intent = new Intent(mActivity, EachCategory.class);
                        intent.putExtra("toolbar_title", toolbar_title);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }
    public String getJsonURL(){
        return json_of_category;
    }
    public String getToolbar_title(){
        return toolbar_title;
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
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
