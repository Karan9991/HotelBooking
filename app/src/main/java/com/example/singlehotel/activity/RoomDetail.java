package com.example.singlehotel.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.singlehotel.R;
import com.example.singlehotel.adapter.ReviewAdapter;
import com.example.singlehotel.adapter.RoomAmenities;
import com.example.singlehotel.adapter.SliderRoomDetailAdapter;
import com.example.singlehotel.response.ReviewRP;
import com.example.singlehotel.response.RoomDetailRP;
import com.example.singlehotel.response.UserReviewRP;
import com.example.singlehotel.response.UserReviewSubmitRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.EnchantedViewPager;
import com.example.singlehotel.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomDetail extends AppCompatActivity {

    private Method method;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private WebView webViewDes, webViewRules;
    private ImageView imageViewRating;
    private RatingView ratingView;
    private RecyclerView recyclerViewRa;
    private EnchantedViewPager viewPager;
    private MaterialCardView cardViewBookNow;
    private ConstraintLayout conMain, conNoData, conRating;
    private SliderRoomDetailAdapter sliderRoomDetailAdapter;
    private MaterialTextView textViewRoomName, textViewPrice, textViewTotalRate;

    private Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    final Handler handler = new Handler();
    private Runnable Update;
    private InputMethodManager imm;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        Intent intent = getIntent();
        String roomId = intent.getStringExtra("room_id");
        String title = intent.getStringExtra("title");
        int position = intent.getIntExtra("position", 0);

        method = new Method(RoomDetail.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(RoomDetail.this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_rd);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conMain = findViewById(R.id.con_rd);
        progressBar = findViewById(R.id.progressBar_rd);
        conNoData = findViewById(R.id.con_noDataFound);
        viewPager = findViewById(R.id.viewPager_rd);
        textViewRoomName = findViewById(R.id.textView_roomName_rd);
        textViewPrice = findViewById(R.id.textView_price_rd);
        textViewTotalRate = findViewById(R.id.textView_totalRating_rd);
        webViewDes = findViewById(R.id.webView_des_rd);
        webViewRules = findViewById(R.id.webView_rules_rd);
        ratingView = findViewById(R.id.ratingBar_rd);
        conRating = findViewById(R.id.con_rating_rd);
        recyclerViewRa = findViewById(R.id.recyclerView_roomAmenities_rd);
        cardViewBookNow = findViewById(R.id.cardView_bookNow_rd);
        imageViewRating = findViewById(R.id.imageView_rating_rd);

        textViewTotalRate.setTypeface(null);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        recyclerViewRa.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_ra = new LinearLayoutManager(RoomDetail.this);
        recyclerViewRa.setLayoutManager(layoutManager_ra);
        recyclerViewRa.setFocusable(false);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_rd);
        method.showBannerAd(linearLayout);

        if (method.isNetworkAvailable()) {
            roomDetail(roomId);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void roomDetail(String room_id) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RoomDetail.this));
        jsObj.addProperty("room_id", room_id);
        jsObj.addProperty("method_name", "get_single_room");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RoomDetailRP> call = apiService.getRoomDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RoomDetailRP>() {
            @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
            @Override
            public void onResponse(@NotNull Call<RoomDetailRP> call, @NotNull Response<RoomDetailRP> response) {
                int statusCode = response.code();

                try {

                    RoomDetailRP roomDetailRP = response.body();

                    assert roomDetailRP != null;
                    if (roomDetailRP.getStatus().equals("1")) {

                        if (roomDetailRP.getRoomSliders().size() != 0) {

                            int columnWidth = method.getScreenWidth();
                            viewPager.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2 + 80));

                            viewPager.useScale();
                            viewPager.removeAlpha();

                            sliderRoomDetailAdapter = new SliderRoomDetailAdapter(RoomDetail.this, "room_detail_slider", roomDetailRP.getRoomSliders());
                            viewPager.setAdapter(sliderRoomDetailAdapter);

                            Update = () -> {
                                if (viewPager.getCurrentItem() == (sliderRoomDetailAdapter.getCount() - 1)) {
                                    viewPager.setCurrentItem(0, true);
                                } else {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                }
                            };

                            if (sliderRoomDetailAdapter.getCount() > 1) {
                                timer = new Timer(); // This will create a new Thread
                                timer.schedule(new TimerTask() { // task to be scheduled
                                    @Override
                                    public void run() {
                                        handler.post(Update);
                                    }
                                }, DELAY_MS, PERIOD_MS);
                            }

                        }

                        textViewRoomName.setText(roomDetailRP.getRoom_name());
                        textViewPrice.setText(getString(R.string.currency,Constant.appRP.getCurrency_code(),Method.convertDec(roomDetailRP.getRoom_price())));
                        ratingView.setRating(Float.parseFloat(roomDetailRP.getRate_avg()));
                        textViewTotalRate.setText("(" + roomDetailRP.getTotal_rate() + ")");

                        String mimeType = "text/html";
                        String encoding = "utf-8";

                        webViewDes.setBackgroundColor(Color.TRANSPARENT);
                        webViewDes.setFocusableInTouchMode(false);
                        webViewDes.setFocusable(false);
                        webViewDes.getSettings().setDefaultTextEncodingName("UTF-8");
                        webViewDes.getSettings().setJavaScriptEnabled(true);

                        String textDes = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/poppins_medium.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + roomDetailRP.getRoom_description()
                                + "</body></html>";

                        webViewDes.loadDataWithBaseURL(null, textDes, mimeType, encoding, null);

                        webViewRules.setBackgroundColor(Color.TRANSPARENT);
                        webViewRules.setFocusableInTouchMode(false);
                        webViewRules.setFocusable(false);
                        webViewRules.getSettings().setDefaultTextEncodingName("UTF-8");
                        webViewRules.getSettings().setJavaScriptEnabled(true);

                        String textRules = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/poppins_medium.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + roomDetailRP.getRoom_rules()
                                + "</body></html>";

                        webViewRules.loadDataWithBaseURL(null, textRules, mimeType, encoding, null);

                        if (roomDetailRP.getRoomAmenitiesLists().size() != 0) {
                            RoomAmenities roomAmenities = new RoomAmenities(RoomDetail.this, "room_amenities", roomDetailRP.getRoomAmenitiesLists());
                            recyclerViewRa.setAdapter(roomAmenities);
                        }

                        conMain.setVisibility(View.VISIBLE);

                        conRating.setOnClickListener(v -> dialogReviewList(roomDetailRP.getId()));

                        imageViewRating.setOnClickListener(v -> dialogReview(roomDetailRP.getId()));

                        cardViewBookNow.setOnClickListener(v -> {
                            if (method.isLogin()) {
                                startActivity(new Intent(RoomDetail.this, BookRoom.class)
                                        .putExtra("id", roomDetailRP.getId())
                                        .putExtra("room_price",roomDetailRP.getRoom_price())
                                        .putExtra("child_allow",roomDetailRP.getChildren_allow())
                                        .putExtra("adult_allow",roomDetailRP.getAdults_allow())
                                        .putExtra("room_name",roomDetailRP.getRoom_name()));
                            } else {
                                Method.loginBack = true;
                                startActivity(new Intent(RoomDetail.this, Login.class));
                            }
                        });

                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        method.alertBox(roomDetailRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<RoomDetailRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("error_fail", t.toString());
                conNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void dialogReviewList(String roomId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        Dialog dialog = new Dialog(RoomDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review_list);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        ImageView imageViewClose = dialog.findViewById(R.id.imageView_dialog_review);
        MaterialTextView textViewTotalReview = dialog.findViewById(R.id.textView_total_dialog_review);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView_dialog_review);

        imageViewClose.setOnClickListener(v -> dialog.dismiss());

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RoomDetail.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RoomDetail.this));
        jsObj.addProperty("room_id", roomId);
        jsObj.addProperty("method_name", "get_rating");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ReviewRP> call = apiService.getReview(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<ReviewRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ReviewRP> call, @NotNull Response<ReviewRP> response) {
                int statusCode = response.code();

                try {

                    ReviewRP reviewRP = response.body();

                    assert reviewRP != null;
                    if (reviewRP.getStatus().equals("1")) {

                        textViewTotalReview.setTypeface(null);
                        textViewTotalReview.setText(reviewRP.getTotal_rate());

                        if (reviewRP.getReviewLists().size() != 0) {

                            ReviewAdapter reviewAdapter = new ReviewAdapter(RoomDetail.this, "review", reviewRP.getReviewLists());
                            recyclerView.setAdapter(reviewAdapter);
                            dialog.show();

                        } else {
                            method.alertBox(getResources().getString(R.string.no_review_found));
                        }

                    } else {
                        method.alertBox(reviewRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<ReviewRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("error_fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void dialogReview(String roomId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        Dialog dialog = new Dialog(RoomDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

        ImageView imageViewClose = dialog.findViewById(R.id.imageView_dialog_rating);
        RatingView ratingBar = dialog.findViewById(R.id.ratingBar_dialog_rating);
        TextInputEditText editText = dialog.findViewById(R.id.editText_dialog_rating);
        MaterialButton buttonSubmit = dialog.findViewById(R.id.button_dialog_rating);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RoomDetail.this));
        jsObj.addProperty("room_id", roomId);
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("method_name", "get_user_rating");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserReviewRP> call = apiService.getUserReview(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<UserReviewRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<UserReviewRP> call, @NotNull Response<UserReviewRP> response) {
                int statusCode = response.code();

                try {

                    UserReviewRP userReviewRP = response.body();

                    assert userReviewRP != null;
                    if (userReviewRP.getStatus().equals("1")) {

                        if (userReviewRP.getSuccess().equals("1")) {

                            ratingBar.setRating(Float.parseFloat(userReviewRP.getUser_rate()));
                            editText.setText(userReviewRP.getUser_msg());
                            dialog.show();

                        } else {
                            method.alertBox(userReviewRP.getMsg());
                        }

                    } else if (userReviewRP.getStatus().equals("2")) {
                        method.suspend(userReviewRP.getMessage());
                    } else {
                        method.alertBox(userReviewRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<UserReviewRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("error_fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });


        imageViewClose.setOnClickListener(v -> dialog.dismiss());

        buttonSubmit.setOnClickListener(v -> {

            String message = editText.getText().toString();

            editText.setError(null);

            if (message.equals("") || message.isEmpty()) {
                editText.requestFocus();
                editText.setError(getResources().getString(R.string.please_enter_review));
            } else if (ratingBar.getRating() == 0) {
                method.alertBox(getResources().getString(R.string.please_rate));
            } else {

                editText.clearFocus();
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                if (method.isNetworkAvailable()) {
                    if (method.isLogin()) {
                        submitRating(dialog, roomId, String.valueOf(ratingBar.getRating()), message);
                    } else {
                        Method.loginBack = true;
                        startActivity(new Intent(RoomDetail.this, Login.class));
                    }
                } else {
                    method.alertBox(getResources().getString(R.string.internet_connection));
                }

            }

        });

    }

    private void submitRating(Dialog dialog, String roomId, String rate, String message) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RoomDetail.this));
        jsObj.addProperty("room_id", roomId);
        jsObj.addProperty("rate", rate);
        jsObj.addProperty("message", message);
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("method_name", "user_rating");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserReviewSubmitRP> call = apiService.submitUserReview(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<UserReviewSubmitRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<UserReviewSubmitRP> call, @NotNull Response<UserReviewSubmitRP> response) {
                int statusCode = response.code();

                try {

                    UserReviewSubmitRP userReviewSubmitRP = response.body();

                    assert userReviewSubmitRP != null;
                    if (userReviewSubmitRP.getStatus().equals("1")) {

                        if (userReviewSubmitRP.getSuccess().equals("1")) {

                            ratingView.setRating(Float.parseFloat(userReviewSubmitRP.getRate_avg()));
                            textViewTotalRate.setText("(" + userReviewSubmitRP.getTotal_rate() + ")");

                            method.alertBox(userReviewSubmitRP.getMsg());

                            dialog.dismiss();

                        } else {
                            method.alertBox(userReviewSubmitRP.getMsg());
                        }

                    } else if (userReviewSubmitRP.getStatus().equals("2")) {
                        method.suspend(userReviewSubmitRP.getMessage());
                    } else {
                        method.alertBox(userReviewSubmitRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<UserReviewSubmitRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("error_fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

