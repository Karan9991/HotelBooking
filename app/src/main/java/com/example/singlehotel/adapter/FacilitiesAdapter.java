package com.example.singlehotel.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.R;
import com.example.singlehotel.item.FacilitiesList;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private List<FacilitiesList> facilitiesLists;

    public FacilitiesAdapter(Activity activity, String type, List<FacilitiesList> facilitiesLists) {
        this.activity = activity;
        this.type = type;
        this.facilitiesLists = facilitiesLists;
    }

    @NotNull
    @Override
    public FacilitiesAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.facilities_adapter, parent, false);

        return new FacilitiesAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.textViewName.setText(facilitiesLists.get(position).getFacilities().trim());
        if (position == facilitiesLists.size() - 1) {
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return facilitiesLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private MaterialTextView textViewName;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_facilities_adapter);
            textViewName = itemView.findViewById(R.id.textView_facilities_adapter);

        }
    }
}
