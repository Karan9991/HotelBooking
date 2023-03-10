package com.example.singlehotel.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.item.RoomAmenitiesList;
import com.example.singlehotel.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RoomAmenities extends RecyclerView.Adapter<RoomAmenities.ViewHolder> {

    private Activity activity;
    private String type;
    private List<RoomAmenitiesList> roomAmenitiesLists;

    public RoomAmenities(Activity activity, String type, List<RoomAmenitiesList> roomAmenitiesLists) {
        this.activity = activity;
        this.type = type;
        this.roomAmenitiesLists = roomAmenitiesLists;
    }

    @NotNull
    @Override
    public RoomAmenities.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.room_amenities_adapter, parent, false);

        return new RoomAmenities.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomAmenities.ViewHolder holder, final int position) {
        holder.textViewAmenities.setText(roomAmenitiesLists.get(position).getRoom_amenities().trim());
    }

    @Override
    public int getItemCount() {
        return roomAmenitiesLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewAmenities;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewAmenities = itemView.findViewById(R.id.textView_name_room_amenities_adapter);

        }
    }
}