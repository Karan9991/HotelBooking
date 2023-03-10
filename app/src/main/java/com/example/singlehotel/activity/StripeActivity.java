package com.example.singlehotel.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
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
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.PaymentSheetResultCallback;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StripeActivity extends AppCompatActivity {

    String strUserId,strRoomId,strGateway,strAmount,strAdult,strChild,strCheckIn,strCheckOut;
    Button btnPay;
    ProgressDialog pDialog;
    private String paymentToken = "";
    private PaymentSheet paymentSheet;
    private String customerId;
    private String ephemeralKeySecret;
    private String paymentId;
    Method method;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        method = new Method(StripeActivity.this);
        method.forceRTLIfSupported();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment_stripe));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        pDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        strUserId= intent.getStringExtra("user_id");
        strRoomId= intent.getStringExtra("room_id");
        strGateway= intent.getStringExtra("gateway");
        strAmount= intent.getStringExtra("payment_amount");
        strAdult= intent.getStringExtra("adults");
        strChild= intent.getStringExtra("children");
        strCheckIn= intent.getStringExtra("check_in_date");
        strCheckOut= intent.getStringExtra("check_out_date");

        PaymentConfiguration.init(this, Constant.appRP.getStripe_publishable_key());
        paymentSheet = new PaymentSheet((ComponentActivity) StripeActivity.this, new PaymentSheetResultCallback() {
            @Override
            public void onPaymentSheetResult(@NotNull PaymentSheetResult paymentSheetResult) {
                onPaymentSheetResult1(paymentSheetResult);
            }
        });

        btnPay = findViewById(R.id.btn_pay);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (method.isNetworkAvailable()) {
                    getToken();
                } else {
                    Toast.makeText(StripeActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
                paymentToken,
                new PaymentSheet.Configuration(
                        getString(R.string.app_name),
                        new PaymentSheet.CustomerConfiguration(
                                customerId,
                                ephemeralKeySecret
                        )
                )
        );

    }

    private void onPaymentSheetResult1(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            showError(getString(R.string.paypal_payment_error_2));
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            showError(getString(R.string.paypal_payment_error_1));
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            if (method.isNetworkAvailable()) {
                new Transaction(StripeActivity.this)
                        .purchasedItem(strUserId,strRoomId,strGateway,strAmount,paymentId,strAdult,strChild,strCheckIn,strCheckOut);
            } else {
                showError(StripeActivity.this.getString(R.string.internet_connection));
            }
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(StripeActivity.this)
                .setTitle(getString(R.string.stripe_payment_error_1))
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

    private void getToken() {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(StripeActivity.this));
        jsObj.addProperty("amount", strAmount);
        jsObj.addProperty("method_name", "stripe_token_get");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentCheckOutRP> call = apiService.paypalCheckOut(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaymentCheckOutRP>() {
            @Override
            public void onResponse(@NotNull Call<PaymentCheckOutRP> call, @NotNull Response<PaymentCheckOutRP> response) {
                try {
                    PaymentCheckOutRP paymentCheckOutRP = response.body();

                    assert paymentCheckOutRP != null;
                    if (paymentCheckOutRP.getSuccess().equals("1")) {
                        if (method.isNetworkAvailable()) {
                        paymentToken = paymentCheckOutRP.getStripe_payment_token();
                        ephemeralKeySecret = paymentCheckOutRP.getStripe_ephemeralKey();
                        customerId = paymentCheckOutRP.getStripe_customer();
                        paymentId = paymentCheckOutRP.getStripe_id();
                        if (paymentToken.isEmpty() && ephemeralKeySecret.isEmpty() && customerId.isEmpty() && paymentId.isEmpty()) {
                            showError(getString(R.string.stripe_token_error));
                        } else {
                            presentPaymentSheet();
                        }
                        } else {
                            showError(getString(R.string.internet_connection));
                        }
                    } else {
                        showError(paymentCheckOutRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.stripe_token_error));
                }

                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<PaymentCheckOutRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(getResources().getString(R.string.stripe_token_error));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
