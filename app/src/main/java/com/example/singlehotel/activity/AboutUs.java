package com.example.singlehotel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.singlehotel.R;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.example.singlehotel.response.AboutUsRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutUs extends AppCompatActivity {

    private Method method;
    private ProgressBar progressBar;
    private WebView webView;
    private ImageView imageView;
    private ConstraintLayout conMain, conNoData;
    private MaterialCardView cardViewEmail, cardViewWebsite, cardViewPhone;
    private MaterialTextView textViewAppName, textViewAppVersion, textViewAppAuthor, textViewAppContact,
            textViewAppEmail, textViewAppWebsite;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        method = new Method(AboutUs.this);
        method.forceRTLIfSupported();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_about_us);
        toolbar.setTitle(getResources().getString(R.string.about_us));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conMain = findViewById(R.id.con_about_us);
        conNoData = findViewById(R.id.con_noDataFound);
        progressBar = findViewById(R.id.progressBar_about_us);
        imageView = findViewById(R.id.app_logo_about_us);
        webView = findViewById(R.id.webView_about_us);
        cardViewEmail = findViewById(R.id.cardView_email_about);
        cardViewWebsite = findViewById(R.id.cardView_website_about);
        cardViewPhone = findViewById(R.id.cardView_phone_about);
        textViewAppName = findViewById(R.id.textView_app_name_about_us);
        textViewAppVersion = findViewById(R.id.textView_app_version_about_us);
        textViewAppAuthor = findViewById(R.id.textView_app_author_about_us);
        textViewAppContact = findViewById(R.id.textView_app_contact_about_us);
        textViewAppEmail = findViewById(R.id.textView_app_email_about_us);
        textViewAppWebsite = findViewById(R.id.textView_app_website_about_us);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_about_us);
        method.showBannerAd(linearLayout);

        if (method.isNetworkAvailable()) {
            aboutUs();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void aboutUs() {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AboutUs.this));
        jsObj.addProperty("method_name", "app_about");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AboutUsRP> call = apiService.getAboutUs(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AboutUsRP>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(@NotNull Call<AboutUsRP> call, @NotNull Response<AboutUsRP> response) {
                int statusCode = response.code();
                try {
                    AboutUsRP aboutUsRp = response.body();
                    assert aboutUsRp != null;

                    if (aboutUsRp.getStatus().equals("1")) {

                        textViewAppName.setText(aboutUsRp.getApp_name());

                        Glide.with(AboutUs.this).load(aboutUsRp.getApp_logo())
                                .placeholder(R.drawable.placeholder_landscape)
                                .into(imageView);

                        textViewAppVersion.setText(aboutUsRp.getApp_version());
                        textViewAppAuthor.setText(aboutUsRp.getApp_author());
                        textViewAppContact.setText(aboutUsRp.getApp_contact());
                        textViewAppEmail.setText(aboutUsRp.getApp_email());
                        textViewAppWebsite.setText(aboutUsRp.getApp_website());

                        webView.setBackgroundColor(Color.TRANSPARENT);
                        webView.setFocusableInTouchMode(false);
                        webView.setFocusable(false);
                        webView.getSettings().setDefaultTextEncodingName("UTF-8");
                        webView.getSettings().setJavaScriptEnabled(true);
                        String mimeType = "text/html";
                        String encoding = "utf-8";

                        String text = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/poppins_medium.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + aboutUsRp.getApp_description()
                                + "</body></html>";

                        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                        conMain.setVisibility(View.VISIBLE);

                        cardViewEmail.setOnClickListener(v -> {
                            try {
                                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{aboutUsRp.getApp_email()});
                                emailIntent.setData(Uri.parse("mailto:?subject="));
                                startActivity(emailIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                method.alertBox(getResources().getString(R.string.wrong));
                            }
                        });

                        cardViewWebsite.setOnClickListener(v -> {
                            try {
                                String url = aboutUsRp.getApp_website();
                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    url = "http://" + url;
                                }
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                            } catch (Exception e) {
                                method.alertBox(getResources().getString(R.string.wrong));
                            }
                        });

                        cardViewPhone.setOnClickListener(v -> {
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + aboutUsRp.getApp_contact()));
                                startActivity(callIntent);
                            } catch (Exception e) {
                                method.alertBox(getResources().getString(R.string.wrong));
                            }
                        });

                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        method.alertBox(aboutUsRp.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<AboutUsRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                conNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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
