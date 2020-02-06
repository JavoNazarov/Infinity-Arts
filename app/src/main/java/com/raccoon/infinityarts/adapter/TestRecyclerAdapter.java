package com.raccoon.infinityarts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.raccoon.infinityarts.R;
import com.squareup.picasso.Picasso;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    private String[] imageUrls = new String[]{
            "http://marvel.geekart.club/Categories/Category_Images/Iron_Man.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Captain.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Hulk.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Thor.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Spider-man.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Guardians.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Doctor_Strange.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Scarlet_Witch.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Black_Panther.jpg",
            "http://marvel.geekart.club/Categories/Category_Images/Thanos_art.jpg",
    };
    private String[] categoryNames = new String[]{
            "Iron Man",
            "Captain",
            "Hulk",
            "Thor",
            "Spider-man",
            "Guardians of Galaxy",
            "Doctor Strange",
            "Scarlet Witch",
            "Black Panther",
            "Thanos",
    };

    public TestRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(inflater.inflate(R.layout.item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(imageUrls[position % imageUrls.length]).into(viewHolder.getBackgroundImage());
        viewHolder.getTextView().setText(categoryNames[position]);

        // # CAUTION:
        // Important to call this method
        viewHolder.getBackgroundImage().reuse();
    }

    @Override
    public int getItemCount() {
        return 10;
    }
    /**
     * # CAUTION:
     * ViewHolder must extend from ParallaxViewHolder
     */
    public static class ViewHolder extends ParallaxViewHolder {

        private final TextView textView;

        private ViewHolder(View v) {
            super(v);

            textView = v.findViewById(R.id.label);
        }

        @Override
        public int getParallaxImageId() {
            return R.id.backgroundImage;
        }

        private TextView getTextView() {
            return textView;
        }
    }
}
