package com.example.singlehotel.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.singlehotel.interfaces.OnClick;
import com.example.singlehotel.item.HomeBanner;
import com.example.singlehotel.R;
import com.example.singlehotel.util.EnchantedViewPager;
import com.example.singlehotel.util.Method;
import com.bumptech.glide.Glide;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private Method method;
    private Activity activity;
    private String type;
    private List<HomeBanner> sliderLists;
    private LayoutInflater inflater;

    public SliderAdapter(Activity activity, String type, List<HomeBanner> sliderLists, OnClick onClick) {
        this.activity = activity;
        this.sliderLists = sliderLists;
        this.type = type;
        method = new Method(activity, onClick);
        // TODO Auto-generated constructor stub
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

        Glide.with(activity).load(sliderLists.get(position).getBanner_image())
                .placeholder(R.drawable.placeholder_landscape).into(imageView);
        
        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public int getCount() {
        return sliderLists.size();
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

