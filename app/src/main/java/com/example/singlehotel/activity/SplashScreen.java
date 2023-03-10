package com.example.singlehotel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.singlehotel.R;
import com.example.singlehotel.response.LoginRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreen extends AppCompatActivity {

    private Method method;
    private ProgressBar progressBar;
    private Boolean isCancelled = false;
    private String id = "0", roomTitle;
    private static int SPLASH_TIME_OUT = 2000;// splash screen timer

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        method = new Method(SplashScreen.this);
        method.forceRTLIfSupported();
        method.login();
        switch (method.getTheme()) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        setContentView(R.layout.activity_splash_screen);

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        method.changeStatusBarColor();

        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
            roomTitle = getIntent().getStringExtra("room_title");
        }

        progressBar = findViewById(R.id.progressBar_splash_screen);
        progressBar.setVisibility(View.GONE);

        splashScreen();


    }

    public void splashScreen() {
        if (method.isNetworkAvailable()) {
            new Handler().postDelayed(() -> {
                if (!isCancelled) {
                    if (method.isLogin()) {
                        login(method.userId(), method.getLoginType());
                    } else {
                        callActivity();
                    }
                }
            }, SPLASH_TIME_OUT);
        } else {
            callActivity();
        }
    }

    public void login(String userId, String type) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("method_name", "user_status");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLoginDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {
                int statusCode = response.code();
                try {
                    LoginRP loginRP = response.body();
                    assert loginRP != null;

                    if (loginRP.getStatus().equals("1")) {

                        if (loginRP.getSuccess().equals("1")) {

                            if (type.equals("google")) {
                                if (GoogleSignIn.getLastSignedInAccount(SplashScreen.this) != null) {
                                    callActivity();
                                } else {
                                    method.editor.putBoolean(method.prefLogin, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();
                                }
                            } else if (type.equals("facebook")) {

                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                                if (isLoggedIn) {
                                    callActivity();
                                } else {

                                    LoginManager.getInstance().logOut();

                                    method.editor.putBoolean(method.prefLogin, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();

                                }

                            } else {
                                callActivity();
                            }
                        } else {
                            method.suspend(loginRP.getMsg());
                        }

                    } else if (loginRP.getStatus().equals("2")) {
                        method.suspend(loginRP.getMessage());
                    } else {
                        method.alertBox(loginRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void callActivity() {

        if (!id.equals("0")) {
            startActivity(new Intent(SplashScreen.this, RoomDetail.class)
                    .putExtra("room_id", id)
                    .putExtra("title", roomTitle)
                    .putExtra("position", 0));
        } else {
            if (method.pref.getBoolean(method.showLogin, true)) {
                method.editor.putBoolean(method.showLogin, false);
                method.editor.commit();
                startActivity(new Intent(SplashScreen.this, Login.class));
            } else {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }
        }
        finishAffinity();

    }

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }

}
