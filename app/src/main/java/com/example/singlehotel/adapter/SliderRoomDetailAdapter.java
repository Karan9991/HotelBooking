package com.example.singlehotel.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.singlehotel.item.RoomSlider;
import com.example.singlehotel.R;
import com.example.singlehotel.util.EnchantedViewPager;
import com.bumptech.glide.Glide;

import java.util.List;

public class SliderRoomDetailAdapter extends PagerAdapter {

    private Activity activity;
    private String type;
    private List<RoomSlider> roomSliders;
    private LayoutInflater inflater;

    public SliderRoomDetailAdapter(Activity activity, String type, List<RoomSlider> roomSliders) {
        this.activity = activity;
        this.roomSliders = roomSliders;
        this.type = type;
        inflater = activity.getLayoutInflater();
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.slider_adapter, container, false);
        assert imageLayout != null;

        ImageView imageView = imageLayout.findViewById(R.id.imageView_slider_adapter);
        imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);

        Glide.with(activity).load(roomSliders.get(position).getImage_name())
                .placeholder(R.drawable.placeholder_landscape).into(imageView);

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public int getCount() {
        return roomSliders.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}

