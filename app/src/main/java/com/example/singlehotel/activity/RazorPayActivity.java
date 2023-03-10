package com.example.singlehotel.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singlehotel.R;
import com.example.singlehotel.response.PaymentCheckOutRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {

    String strUserId, strRoomId, strGateway, strAmount, strAdult, strChild, strCheckIn, strCheckOut;
    Button btnPay;
    Method method;
    String orderId = "";
    ProgressDialog pDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        method = new Method(RazorPayActivity.this);
        method.forceRTLIfSupported();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment_razorpay));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        pDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        strUserId = intent.getStringExtra("user_id");
        strRoomId = intent.getStringExtra("room_id");
        strGateway = intent.getStringExtra("gateway");
        strAmount = intent.getStringExtra("payment_amount");
        strAdult = intent.getStringExtra("adults");
        strChild = intent.getStringExtra("children");
        strCheckIn = intent.getStringExtra("check_in_date");
        strCheckOut = intent.getStringExtra("check_out_date");

        btnPay = findViewById(R.id.btn_pay);

        Checkout.preload(getApplicationContext());

        getOrderId();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderId.isEmpty()) {
                    getOrderId();
                } else {
                    startPayment();
                }
            }
        });

    }

    private void getOrderId() {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RazorPayActivity.this));
        double big = Double.parseDouble(strAmount);
        int amount = (int) (big) * 100;
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("amount", amount);
        jsObj.addProperty("method_name", "razorpay_order_id_get");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentCheckOutRP> call = apiService.paypalCheckOut(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaymentCheckOutRP>() {
            @Override
            public void onResponse(@NotNull Call<PaymentCheckOutRP> call, @NotNull Response<PaymentCheckOutRP> response) {
                try {
                    PaymentCheckOutRP paymentCheckOutRP = response.body();

                    assert paymentCheckOutRP != null;
                    if (paymentCheckOutRP.getSuccess().equals("1")) {
                        orderId = paymentCheckOutRP.getRazorpay_order_id();
                        if (method.isNetworkAvailable()) {
                            startPayment();
                        } else {
                            showError(getString(R.string.internet_connection));
                        }
                    } else {
                        showError(paymentCheckOutRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<PaymentCheckOutRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID(Constant.appRP.getRazorpay_key());

        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", getString(R.string.razor_desc));
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", Constant.appRP.getCurrency_code());
            double big = Double.valueOf(strAmount);
            int amount = (int) (big) * 100;
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            //preFill.put("email", method.userId());
            //     preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            showError("Error in payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(RazorPayActivity.this)
                .setTitle(getString(R.string.razor_payment_error_1))
                .setMessage(Title)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            if (method.isNetworkAvailable()) {
                new Transaction(RazorPayActivity.this)
                        .purchasedItem(strUserId, strRoomId, strGateway, strAmount, razorpayPaymentID, strAdult, strChild, strCheckIn, strCheckOut);
            } else {
                showError(getString(R.string.internet_connection));
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            showError("Payment failed: " + code + " " + response);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
