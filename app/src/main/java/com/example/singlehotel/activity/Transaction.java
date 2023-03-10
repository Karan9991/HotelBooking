package com.example.singlehotel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.viewpager.widget.ViewPager;

import com.example.singlehotel.R;
import com.example.singlehotel.response.BookingRoomRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Transaction {
    ProgressDialog pDialog;
    Activity mContext;
    Method method;

    public Transaction(Activity context) {
        this.mContext = context;
        pDialog = new ProgressDialog(mContext);
        method = new Method(mContext);
        method.forceRTLIfSupported();
    }

    public void purchasedItem(String userId, String roomId, String gatewayName, String amount, String paymentId, String noAdult, String noChild, String checkIn, String checkOut) {
        pDialog.show();
        pDialog.setMessage(mContext.getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(mContext));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("room_id", roomId);
        jsObj.addProperty("gateway", gatewayName);
        jsObj.addProperty("payment_amount", amount);
        jsObj.addProperty("payment_id", paymentId);
        jsObj.addProperty("adults", noAdult);
        jsObj.addProperty("children", noChild);
        jsObj.addProperty("check_in_date", checkIn);
        jsObj.addProperty("check_out_date", checkOut);
        jsObj.addProperty("method_name", "room_booking");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BookingRoomRP> call = apiService.roomBooking2(API.toBase64(jsObj.toString()));
        Log.e("bookkk", "" + API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<BookingRoomRP>() {
            @Override
            public void onResponse(@NotNull Call<BookingRoomRP> call, @NotNull Response<BookingRoomRP> response) {
                try {

                    BookingRoomRP bookingRoomRP = response.body();

                    assert bookingRoomRP != null;

                    if (bookingRoomRP.getSingleHotelApp().getSuccess().equals("1")) {
                        bookingDialog(bookingRoomRP.getSingleHotelApp().getMsg());
                    } else {
                        method.alertBox(bookingRoomRP.getSingleHotelApp().getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(mContext.getResources().getString(R.string.failed_try_again));
                }

                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<BookingRoomRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(mContext.getResources().getString(R.string.failed_try_again));
            }
        });
    }

    private void bookingDialog(String message) {

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_booking);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

        MaterialTextView textView_message = dialog.findViewById(R.id.textView_message_dialog_booking);
        MaterialButton button = dialog.findViewById(R.id.button_dialog_booking);

        textView_message.setText(message);

        button.setOnClickListener(v -> {
            dialog.dismiss();
            mContext.startActivity(new Intent(mContext, MainActivity.class));
            mContext.finishAffinity();
        });

        dialog.show();

    }

}
