package com.example.singlehotel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.singlehotel.R;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BookingRoomDetail extends AppCompatActivity {

    Method method;
    MaterialToolbar toolbar;
    String stringName, stringEmail, stringPhone, stringUserId, stringRoomType, stringAdult, stringChild, stringCheckIn,
            stringCheckOut, stringStay, stringTotalCost, stringRoomId;
    TextView textViewName, textViewEmail, textViewPhone, textViewRoomType, textViewChild, textViewAdult,
            textViewCheckIn, textViewCheckOut, textViewStay, textViewTotalCost;
    RadioGroup radioGroupPayment;
    RadioButton radioButtonPaypal,radioButtonStripe,radioButtonRazorpay;
    MaterialCardView materialCardViewPay;
    TextView textViewNoGatewayFound;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        method = new Method(BookingRoomDetail.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.booking_title));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent=getIntent();
        stringName = intent.getStringExtra("name");
        stringEmail = intent.getStringExtra("email");
        stringPhone = intent.getStringExtra("phone");
        stringUserId = intent.getStringExtra("user_id");
        stringRoomType = intent.getStringExtra("room_type");
        stringAdult = intent.getStringExtra("room_adult");
        stringChild = intent.getStringExtra("room_child");
        stringCheckIn = intent.getStringExtra("room_in");
        stringCheckOut = intent.getStringExtra("room_out");
        stringStay = intent.getStringExtra("room_stay");
        stringTotalCost = intent.getStringExtra("room_cost");
        stringRoomId = intent.getStringExtra("room_id");

        textViewName = findViewById(R.id.book_name);
        textViewEmail = findViewById(R.id.book_email);
        textViewPhone = findViewById(R.id.book_phone);
        textViewRoomType = findViewById(R.id.book_room_type);
        textViewChild = findViewById(R.id.book_child);
        textViewAdult = findViewById(R.id.book_adult);
        textViewCheckIn = findViewById(R.id.book_check_in);
        textViewCheckOut = findViewById(R.id.book_check_out);
        textViewStay = findViewById(R.id.book_stay);
        textViewTotalCost = findViewById(R.id.book_cost);

        radioGroupPayment= findViewById(R.id.radioGrp);
        radioButtonPaypal= findViewById(R.id.rdPaypal);
        radioButtonStripe= findViewById(R.id.rdStripe);
        radioButtonRazorpay= findViewById(R.id.rdRazorPay);
        materialCardViewPay=findViewById(R.id.material_pay);
        textViewNoGatewayFound=findViewById(R.id.no_gateway_found);

        textViewName.setText(stringName);
        textViewEmail.setText(stringEmail);
        textViewPhone.setText(stringPhone);
        textViewRoomType.setText(stringRoomType);
        textViewChild.setText(stringChild);
        textViewAdult.setText(stringAdult);
        textViewCheckIn.setText(stringCheckIn);
        textViewCheckOut.setText(stringCheckOut);
        textViewStay.setText(getString(R.string.booking_stay_title,stringStay));
        textViewTotalCost.setText(getString(R.string.booking_total_cost,Constant.appRP.getCurrency_code(),Method.convertDec(stringTotalCost)));

        if (!Constant.appRP.isBraintree_on_off()){
            radioButtonPaypal.setVisibility(View.GONE);
        } if (!Constant.appRP.isRazorpay_on_off()){
            radioButtonRazorpay.setVisibility(View.GONE);
        } if (!Constant.appRP.isStripe_on_off()){
            radioButtonStripe.setVisibility(View.GONE);

        }
        if (!Constant.appRP.isBraintree_on_off() && !Constant.appRP.isRazorpay_on_off() && !Constant.appRP.isStripe_on_off()){
            textViewNoGatewayFound.setVisibility(View.VISIBLE);
            materialCardViewPay.setVisibility(View.GONE);
        }

        materialCardViewPay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                int radioSelected = radioGroupPayment.getCheckedRadioButtonId();
                if (radioSelected != -1) {
                    switch (radioSelected) {
                        case R.id.rdPaypal:
                            Intent intentPayPal = new Intent(BookingRoomDetail.this, PayPalActivity.class);
                            intentPayPal.putExtra("user_id", method.userId());
                            intentPayPal.putExtra("room_id", stringRoomId);
                            intentPayPal.putExtra("gateway", "Paypal");
                            intentPayPal.putExtra("payment_amount", stringTotalCost);
                            intentPayPal.putExtra("adults", stringAdult);
                            intentPayPal.putExtra("children", stringChild);
                            intentPayPal.putExtra("check_in_date", stringCheckIn);
                            intentPayPal.putExtra("check_out_date", stringCheckOut);
                            startActivity(intentPayPal);
                            break;
                        case R.id.rdStripe:
                            Intent intentStripe = new Intent(BookingRoomDetail.this, StripeActivity.class);
                            intentStripe.putExtra("user_id", method.userId());
                            intentStripe.putExtra("room_id", stringRoomId);
                            intentStripe.putExtra("gateway", "Stripe");
                            intentStripe.putExtra("payment_amount", stringTotalCost);
                            intentStripe.putExtra("adults", stringAdult);
                            intentStripe.putExtra("children", stringChild);
                            intentStripe.putExtra("check_in_date", stringCheckIn);
                            intentStripe.putExtra("check_out_date", stringCheckOut);
                            startActivity(intentStripe);
                            break;
                        case R.id.rdRazorPay:
                            Intent intentRazor = new Intent(BookingRoomDetail.this, RazorPayActivity.class);
                            intentRazor.putExtra("user_id", method.userId());
                            intentRazor.putExtra("room_id", stringRoomId);
                            intentRazor.putExtra("gateway", "RazorPay");
                            intentRazor.putExtra("payment_amount", stringTotalCost);
                            intentRazor.putExtra("adults", stringAdult);
                            intentRazor.putExtra("children", stringChild);
                            intentRazor.putExtra("check_in_date", stringCheckIn);
                            intentRazor.putExtra("check_out_date", stringCheckOut);
                            startActivity(intentRazor);
                            break;
                    }
                } else {
                    Toast.makeText(BookingRoomDetail.this, getString(R.string.select_gateway), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}