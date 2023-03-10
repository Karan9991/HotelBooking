package com.example.singlehotel.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.singlehotel.R;
import com.example.singlehotel.activity.ViewImage;
import com.example.singlehotel.item.ReviewList;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private List<ReviewList> reviewLists;
    private int lastPosition = -1;

    public ReviewAdapter(Activity activity, String type, List<ReviewList> reviewLists) {
        this.activity = activity;
        this.type = type;
        this.reviewLists = reviewLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.review_adapter, parent, false);
        return new ReviewAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (!reviewLists.get(position).getUser_image().equals("")) {
            Glide.with(activity).load(reviewLists.get(position).getUser_image())
                    .placeholder(R.drawable.user_profile).into(holder.imageView);
        } else {
            // make sure Glide doesn't load anything into this view until told otherwise
            Glide.with(activity).clear(holder.imageView);
        }

        holder.imageView.setOnClickListener(v -> activity.startActivity(new Intent(activity, ViewImage.class)
                .putExtra("path", reviewLists.get(position).getUser_image())));

        holder.textViewName.setText(reviewLists.get(position).getUser_name());
        holder.textViewDate.setText(reviewLists.get(position).getDt_rate());
        holder.textViewMsg.setText(reviewLists.get(position).getMessage());
        holder.ratingView.setRating(Float.parseFloat(reviewLists.get(position).getRate()));

        if (position > lastPosition) {
            if (position % 2 == 0) {
                holder.con.setBackgroundColor(activity.getResources().getColor(R.color.background_item_one_review_adapter));
            } else {
                holder.con.setBackgroundColor(activity.getResources().getColor(R.color.background_item_two_review_adapter));
            }
        }

    }

    @Override
    public int getItemCount() {
        return reviewLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout con;
        private RatingView ratingView;
        private CircleImageView imageView;
        private MaterialTextView textViewName, textViewDate, textViewMsg;

        public ViewHolder(View itemView) {
            super(itemView);

            con = itemView.findViewById(R.id.con_review_adapter);
            imageView = itemView.findViewById(R.id.imageView_review_adapter);
            ratingView = itemView.findViewById(R.id.ratingBar_review_adapter);
            textViewName = itemView.findViewById(R.id.textView_name_review_adapter);
            textViewDate = itemView.findViewById(R.id.textView_date_review_adapter);
            textViewMsg = itemView.findViewById(R.id.textView_review_adapter);

        }
    }
}
