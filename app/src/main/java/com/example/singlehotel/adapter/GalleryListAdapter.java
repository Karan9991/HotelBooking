package com.example.singlehotel.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.interfaces.OnClick;
import com.example.singlehotel.item.GalleryDetailList;
import com.example.singlehotel.R;
import com.example.singlehotel.util.Method;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GalleryListAdapter extends RecyclerView.Adapter {

    private Method method;
    private String type;
    private Activity activity;
    private List<GalleryDetailList> galleryDetailLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public GalleryListAdapter(Activity activity, String type, List<GalleryDetailList> galleryDetailLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.galleryDetailLists = galleryDetailLists;
        method = new Method(activity, onClick);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.gallery_recyclerview_adapter, parent, false);
            return new GalleryListAdapter.ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_horizontal_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            Glide.with(activity).load(galleryDetailLists.get(position).getWallpaper_image_thumb())
                    .placeholder(R.drawable.placeholder_portable)
                    .into(viewHolder.imageView);

            viewHolder.imageView.setOnClickListener(v -> method.onClickAd(position, type, galleryDetailLists.get(position).getId(), ""));

        }

    }

    @Override
    public int getItemCount() {
        return galleryDetailLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == galleryDetailLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_gr_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_horizontal_loading);
        }
    }

}