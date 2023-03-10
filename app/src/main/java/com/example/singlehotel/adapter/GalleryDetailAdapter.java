package com.example.singlehotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.singlehotel.item.GalleryDetailList;
import com.example.singlehotel.R;
import com.example.singlehotel.util.TouchImageView;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GalleryDetailAdapter extends PagerAdapter {

    private Activity activity;
    private String type;
    private LayoutInflater layoutInflater;
    private List<GalleryDetailList> galleryDetailLists;

    public GalleryDetailAdapter(Activity activity, String type, List<GalleryDetailList> galleryDetailLists) {
        this.activity = activity;
        this.type = type;
        this.galleryDetailLists = galleryDetailLists;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.gallery_detail_adapter, container, false);

        TouchImageView imageView = view.findViewById(R.id.imageView_gallery_detail_adapter);

        Glide.with(activity).load(galleryDetailLists.get(position).getWallpaper_image())
                .placeholder(R.drawable.placeholder_portable)
                .into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return galleryDetailLists.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
