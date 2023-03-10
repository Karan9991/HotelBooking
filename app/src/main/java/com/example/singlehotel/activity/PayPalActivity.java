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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.singlehotel.R;
import com.example.singlehotel.response.PaymentCheckOutRP;
import com.example.singlehotel.response.PaypalTokenRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalActivity extends AppCompatActivity {

    String strUserId, strRoomId, strGateway, strAmount, strAdult, strChild, strCheckIn, strCheckOut;
    Button btnPay;
    BraintreeFragment mBraintreeFragment;
    String authToken = "";
    ProgressDialog pDialog;
    Method method;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        method = new Method(PayPalActivity.this);
        method.forceRTLIfSupported();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment_paypal));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        strUserId = intent.getStringExtra("user_id");
        strRoomId = intent.getStringExtra("room_id");
        strGateway = intent.getStringExtra("gateway");
        strAmount = intent.getStringExtra("payment_amount");
        strAdult = intent.getStringExtra("adults");
        strChild = intent.getStringExtra("children");
        strCheckIn = intent.getStringExtra("check_in_date");
        strCheckOut = intent.getStringExtra("check_out_date");

        pDialog = new ProgressDialog(this);
        btnPay = findViewById(R.id.btn_pay);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!authToken.isEmpty()) {
                    makePaymentFromBraintree();
                } else {
                    showError(getString(R.string.paypal_payment_error_3));
                }
            }
        });

        generateToken();
    }

    private void generateToken() {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PayPalActivity.this));
        jsObj.addProperty("method_name", "get_braintree_token");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaypalTokenRP> call = apiService.paypalToken(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaypalTokenRP>() {
            @Override
            public void onResponse(@NotNull Call<PaypalTokenRP> call, @NotNull Response<PaypalTokenRP> response) {
                try {
                    PaypalTokenRP paypalTokenRP = response.body();

                    assert paypalTokenRP != null;

                    if (paypalTokenRP.getSuccess().equals("1")) {
                        authToken = paypalTokenRP.getAuthToken();
                        initBraintree(authToken);
                    } else {
                        method.alertBox(paypalTokenRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<PaypalTokenRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    private void initBraintree(String authToken) {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, authToken);
            mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                    String nNonce = paymentMethodNonce.getNonce();
                    checkoutNonce(nNonce);
                }
            });
            mBraintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {
                    showError(getString(R.string.paypal_payment_error_2));
                }
            });

            mBraintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    showError(error.getMessage());
                }
            });
            Toast.makeText(PayPalActivity.this, getString(R.string.proceed_with_payment), Toast.LENGTH_SHORT).show();
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
            showError(getString(R.string.paypal_payment_error_1));
        }
    }

    private void makePaymentFromBraintree() {
        PayPal.requestOneTimePayment(mBraintreeFragment, getPaypalRequest(strAmount));
    }

    private PayPalRequest getPaypalRequest(@Nullable String amount) {
        PayPalRequest request = new PayPalRequest(amount);
        request.currencyCode(Constant.appRP.getCurrency_code());
        request.intent(PayPalRequest.INTENT_SALE);
        return request;
    }

    private void checkoutNonce(String paymentNonce) {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PayPalActivity.this));
        jsObj.addProperty("payment_nonce", paymentNonce);
        jsObj.addProperty("payment_amount", strAmount);
        jsObj.addProperty("method_name", "braintree_checkout");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentCheckOutRP> call = apiService.paypalCheckOut(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaymentCheckOutRP>() {
            @Override
            public void onResponse(@NotNull Call<PaymentCheckOutRP> call, @NotNull Response<PaymentCheckOutRP> response) {
                try {
                    PaymentCheckOutRP paypalCheckOutRP = response.body();

                    assert paypalCheckOutRP != null;
                    if (paypalCheckOutRP.getSuccess().equals("1")) {
                        String paymentId = paypalCheckOutRP.getPaypal_payment_id(); //objJson.getString("transaction_id")
                        if (method.isNetworkAvailable()) {
                            new Transaction(PayPalActivity.this)
                                    .purchasedItem(strUserId, strRoomId, strGateway, strAmount, paymentId, strAdult, strChild, strCheckIn, strCheckOut);
                        } else {
                            showError(getString(R.string.internet_connection));
                        }
                    } else {
                        showError(paypalCheckOutRP.getMsg());
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

    private void showError(String Title) {
        new AlertDialog.Builder(PayPalActivity.this)
                .setTitle(getString(R.string.paypal_payment_error_4))
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
