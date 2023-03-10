package com.example.singlehotel.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.singlehotel.R;
import com.example.singlehotel.response.CheckAvailBookingRoomRP;
import com.example.singlehotel.response.ProfileRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRoom extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private boolean isDate = false;
    private MaterialButton button;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private String[] numberAdults;
    private String[] numberChildren;
    private DatePickerDialog datePickerDialog;
    private MaterialTextView textViewArrivalDate, textViewDepartureDate, textViewRoomCost, textViewTotalRoomCost;
    private AppCompatSpinner spinnerAdults, spinnerChildren;
    private TextInputEditText editTextName, editTextEmail, editTextPhoneNo;
    private int year, month, day, arrYear, arrMonth, arrDay;
    private String roomId, roomCost, childAllow, adultAllow, adults, children, arrivalDate, departureDate,roomName;
    int numOfDays ;
    int price;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_room);

        method = new Method(BookRoom.this);
        method.forceRTLIfSupported();

        roomId = getIntent().getStringExtra("id");
        roomCost = getIntent().getStringExtra("room_price");
        childAllow = getIntent().getStringExtra("child_allow");
        adultAllow = getIntent().getStringExtra("adult_allow");
        roomName=getIntent().getStringExtra("room_name");

        progressDialog = new ProgressDialog(BookRoom.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        numberAdults = new String[]{getResources().getString(R.string.adults), "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        numberChildren = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        toolbar = findViewById(R.id.toolbar_book_room);
        toolbar.setTitle(getResources().getString(R.string.book_now));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextName = findViewById(R.id.editText_name_book_room);
        editTextEmail = findViewById(R.id.editText_email_book_room);
        editTextPhoneNo = findViewById(R.id.editText_phoneNo_book_room);
        spinnerAdults = findViewById(R.id.spinner_adults_book_room);
        spinnerChildren = findViewById(R.id.spinner_children_book_room);
        textViewArrivalDate = findViewById(R.id.textView_arrivalDate_booking);
        textViewDepartureDate = findViewById(R.id.textView_departureDate_booking);
        button = findViewById(R.id.button_book_room);
        textViewRoomCost = findViewById(R.id.textView_room_cost);
        textViewTotalRoomCost = findViewById(R.id.textView_room_total_cost);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_book_room);
        method.showBannerAd(linearLayout);

        textViewRoomCost.setText(getString(R.string.room_cost, Constant.appRP.getCurrency_code(), Method.convertDec(roomCost)));

        spinnerAdults.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_book_room));
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                }
                adults = numberAdults[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerChildren.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                }
                children = numberChildren[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        textViewArrivalDate.setOnClickListener(v -> {

            datePickerDialog = new DatePickerDialog(BookRoom.this, R.style.datePicker, (view, year, month, dayOfMonth) -> {

                arrYear = year;
                arrMonth = month;
                arrDay = dayOfMonth;

                isDate = true;

                arrivalDate = selectDate(dayOfMonth, month, year);
                textViewArrivalDate.setText(arrivalDate);
            }, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        textViewDepartureDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            calendar.set(arrYear, arrMonth, arrDay + 1);
            long startTime = calendar.getTimeInMillis();

            if (isDate) {
                datePickerDialog = new DatePickerDialog(BookRoom.this, R.style.datePicker, (view, year, month, dayOfMonth) -> {
                    departureDate = selectDate(dayOfMonth, month, year);
                    textViewDepartureDate.setText(departureDate);

                    try {
                        Date depDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(departureDate);
                        Date arriDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(arrivalDate);

                        long diff = depDate.getTime() - arriDate.getTime();
                         numOfDays = (int) (diff / (1000 * 60 * 60 * 24));

                         price = numOfDays * (int) Double.parseDouble(roomCost);


                        textViewTotalRoomCost.setText(getString(R.string.total_room_cost, numOfDays, Constant.appRP.getCurrency_code(), Method.convertDec(roomCost), Constant.appRP.getCurrency_code(), Method.convertDec(String.valueOf(price))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }, arrYear, arrMonth, arrDay + 1);
                datePickerDialog.getDatePicker().setMinDate(startTime);
                datePickerDialog.show();


            } else {
                method.alertBox(getResources().getString(R.string.please_select_first_arrivalDate));
            }
        });

        button.setOnClickListener(v -> submit());

        adultsSpinner();
        childrenSpinner();

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                profile(method.userId());
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public String selectDate(int day, int month, int year) {

        String monthYear;
        String dayMonth;

        if (month + 1 < 10) {
            monthYear = "0" + (month + 1);
        } else {
            monthYear = String.valueOf(month + 1);
        }
        if (day < 10) {
            dayMonth = "0" + day;
        } else {
            dayMonth = String.valueOf(day);
        }

        return dayMonth + "-" + monthYear + "-" + year;

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void adultsSpinner() {

        List<String> adults = new ArrayList<String>(); // Spinner Drop down elements
        Collections.addAll(adults, numberAdults);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BookRoom.this, android.R.layout.simple_spinner_item, adults);  // Creating adapter for spinner
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Drop down layout style - list view with radio button
        spinnerAdults.setAdapter(dataAdapter); // attaching data adapter to spinner
    }

    public void childrenSpinner() {

        List<String> children = new ArrayList<String>(); // Spinner Drop down elements
        Collections.addAll(children, numberChildren);
        ArrayAdapter<String> dataAdapterChildren = new ArrayAdapter<String>(BookRoom.this, android.R.layout.simple_spinner_item, children); // Creating adapter for spinner
        dataAdapterChildren.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Drop down layout style - list view with radio button
        spinnerChildren.setAdapter(dataAdapterChildren);// attaching data adapter to spinner
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void submit() {

        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNo = editTextPhoneNo.getText().toString();

        if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
            editTextPhoneNo.requestFocus();
            editTextPhoneNo.setError(getResources().getString(R.string.please_enter_name));
        } else if (adults.equals(getResources().getString(R.string.adults)) || adults.equals("")) {
            method.alertBox(getResources().getString(R.string.please_select_adults));
        } else if (arrivalDate == null || arrivalDate.equals("")) {
            method.alertBox(getResources().getString(R.string.please_select_arrivalDate));
        } else if (departureDate == null || departureDate.equals("")) {
            method.alertBox(getResources().getString(R.string.please_select_departureDate));
        } else if (Integer.parseInt(adultAllow) < Integer.parseInt(adults) || Integer.parseInt(childAllow) < Integer.parseInt(children)) {
            method.alertBox(getResources().getString(R.string.please_select_adults_children, adultAllow, childAllow));
        } else {

            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextPhoneNo.clearFocus();
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                checkBooking(roomId, arrivalDate, departureDate);
            }

        }

    }

    public void profile(String userId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(BookRoom.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("method_name", "user_profile");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ProfileRP> call = apiService.getProfile(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<ProfileRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ProfileRP> call, @NotNull Response<ProfileRP> response) {
                int statusCode = response.code();

                try {

                    ProfileRP profileRP = response.body();

                    assert profileRP != null;
                    if (profileRP.getStatus().equals("1")) {

                        if (profileRP.getSuccess().equals("1")) {

                            editTextEmail.setText(profileRP.getEmail());
                            editTextName.setText(profileRP.getName());
                            editTextPhoneNo.setText(profileRP.getPhone());
                        }

                    } else if (profileRP.getStatus().equals("2")) {
                        method.suspend(profileRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<ProfileRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void checkBooking(String roomId, String arrivalDate, String departureDate) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(BookRoom.this));
        jsObj.addProperty("room_id", roomId);
        jsObj.addProperty("check_in_date", arrivalDate);
        jsObj.addProperty("check_out_date", departureDate);
        jsObj.addProperty("method_name", "check_booking");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CheckAvailBookingRoomRP> call = apiService.roomBooking(API.toBase64(jsObj.toString()));
        Log.e("dattaa", "" + API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CheckAvailBookingRoomRP>() {
            @Override
            public void onResponse(@NotNull Call<CheckAvailBookingRoomRP> call, @NotNull Response<CheckAvailBookingRoomRP> response) {
                try {

                    CheckAvailBookingRoomRP bookingRoomRP = response.body();

                    assert bookingRoomRP != null;


                    if (bookingRoomRP.getSuccess().equals("1")) {
                        Intent intent=new Intent(BookRoom.this,BookingRoomDetail.class);
                        intent.putExtra("name",editTextName.getText().toString());
                        intent.putExtra("email",editTextEmail.getText().toString());
                        intent.putExtra("phone",editTextPhoneNo.getText().toString());
                        intent.putExtra("user_id",method.userId());
                        intent.putExtra("room_type",roomName);
                        intent.putExtra("room_adult",adults);
                        intent.putExtra("room_child",children);
                        intent.putExtra("room_in",arrivalDate);
                        intent.putExtra("room_out",departureDate);
                        intent.putExtra("room_stay",String.valueOf(numOfDays));
                        intent.putExtra("room_cost",String.valueOf(price));
                        intent.putExtra("room_id",roomId);
                        startActivity(intent);
                    } else {
                        method.alertBox(bookingRoomRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<CheckAvailBookingRoomRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }
}