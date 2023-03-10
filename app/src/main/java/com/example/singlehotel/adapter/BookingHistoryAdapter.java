package com.example.singlehotel.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.R;
import com.example.singlehotel.item.BookingList;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.Method;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    Activity activity;
    String type;
    List<BookingList> bookingLists;

    public BookingHistoryAdapter(Activity activity, String type, List<BookingList> bookingLists) {
        this.activity = activity;
        this.type = type;
        this.bookingLists = bookingLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.booking_his_adapter, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewBookRoomType.setText(bookingLists.get(position).getRoom_name());
        holder.textViewBookDate.setText(bookingLists.get(position).getDate());
        holder.textViewBookAdult.setText(bookingLists.get(position).getAdults_allowed());
        holder.textViewBookChild.setText(bookingLists.get(position).getChildren_allowed());
        holder.textViewBookGateway.setText(bookingLists.get(position).getGateway());
        holder.textViewBookAmount.setText(activity.getString(R.string.booking_his_cost,Constant.appRP.getCurrency_code(), Method.convertDec(bookingLists.get(position).getPayment_amount())));
        holder.textViewBookPaymentId.setText(bookingLists.get(position).getPayment_id());
        holder.textViewBookCheckIn.setText(bookingLists.get(position).getCheck_in_date());
        holder.textViewBookCheckOut.setText(bookingLists.get(position).getCheck_out_date());

    }

    @Override
    public int getItemCount() {
        return bookingLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewBookRoomType, textViewBookDate, textViewBookAdult, textViewBookChild, textViewBookGateway,
                textViewBookAmount, textViewBookPaymentId, textViewBookCheckIn, textViewBookCheckOut;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewBookRoomType = itemView.findViewById(R.id.book_his_room_type);
            textViewBookDate = itemView.findViewById(R.id.book_his_date);
            textViewBookAdult = itemView.findViewById(R.id.book_his_adult);
            textViewBookChild = itemView.findViewById(R.id.book_his_child);
            textViewBookGateway = itemView.findViewById(R.id.book_his_gateway);
            textViewBookAmount = itemView.findViewById(R.id.book_his_amount);
            textViewBookPaymentId = itemView.findViewById(R.id.book_his_payment_id);
            textViewBookCheckIn = itemView.findViewById(R.id.book_his_check_in);
            textViewBookCheckOut = itemView.findViewById(R.id.book_his_check_out);

        }
    }
}
