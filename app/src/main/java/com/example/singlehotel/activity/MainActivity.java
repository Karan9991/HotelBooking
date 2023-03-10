package com.example.singlehotel.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.example.singlehotel.BuildConfig;
import com.example.singlehotel.R;
import com.example.singlehotel.fragment.BookingHistoryFragment;
import com.example.singlehotel.fragment.FacilitiesFragment;
import com.example.singlehotel.fragment.GalleryFragment;
import com.example.singlehotel.fragment.HomeFragment;
import com.example.singlehotel.fragment.LocationFragment;
import com.example.singlehotel.fragment.ProfileFragment;
import com.example.singlehotel.fragment.RoomFragment;
import com.example.singlehotel.fragment.SettingFragment;
import com.example.singlehotel.response.AppRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.Method;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Method method;
    private ConsentForm form;
    private DrawerLayout drawer;
    @SuppressLint("StaticFieldLeak")
    public static MaterialToolbar toolbar;
    private NavigationView navigationView;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        method = new Method(MainActivity.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.home));

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

        progressBar = findViewById(R.id.progressBar_main);
        navigationView = findViewById(R.id.nav_view);
        linearLayout = findViewById(R.id.linearLayout_adView_main);

        navigationView.setNavigationItemSelectedListener(this);

        if (method.isNetworkAvailable()) {
            appDetail();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        navigationView.getMenu().findItem(R.id.booking_history).setVisible(method.isLogin());

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                String title;
                if (!(getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()) instanceof SupportRequestManagerFragment)) {
                    title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()).getTag();
                } else {
                    title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1).getTag();
                }
                if (title != null) {
                    toolbar.setTitle(title);
                }
                super.onBackPressed();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //Checking if the item is in checked state or not, if not make it in checked state
        item.setChecked(!item.isChecked());

        //Closing drawer on item click
        drawer.closeDrawers();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.home:
                backStackRemove();
                selectDrawerItem(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new HomeFragment(), getResources().getString(R.string.home)).commitAllowingStateLoss();
                return true;

            case R.id.room:
                backStackRemove();
                selectDrawerItem(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new RoomFragment(), getResources().getString(R.string.room)).commitAllowingStateLoss();
                return true;

            case R.id.location:
                backStackRemove();
                selectDrawerItem(2);
                toolbar.setTitle(getResources().getString(R.string.location));
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new LocationFragment(), getResources().getString(R.string.location)).commitAllowingStateLoss();
                return true;

            case R.id.gallery:
                backStackRemove();
                selectDrawerItem(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new GalleryFragment(), getResources().getString(R.string.gallery)).commitAllowingStateLoss();
                return true;

            case R.id.facilities:
                backStackRemove();
                selectDrawerItem(4);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new FacilitiesFragment(), getResources().getString(R.string.facilities)).commitAllowingStateLoss();
                return true;

            case R.id.profile:
                backStackRemove();
                selectDrawerItem(5);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new ProfileFragment(), getResources().getString(R.string.profile)).commitAllowingStateLoss();
                return true;

            case R.id.booking_history:
                backStackRemove();
                selectDrawerItem(6);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new BookingHistoryFragment(), getResources().getString(R.string.booking_his_title)).commitAllowingStateLoss();
                return true;

            case R.id.setting:
                backStackRemove();
                selectDrawerItem(7);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main,
                        new SettingFragment(), getResources().getString(R.string.setting)).commitAllowingStateLoss();
                return true;

            default:
                return true;
        }

    }

    public void appDetail() {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("method_name", "get_app_details");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AppRP> call = apiService.getAppData(API.toBase64(jsObj.toString()));
        Log.e("dataa",""+API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AppRP>() {
            @Override
            public void onResponse(@NotNull Call<AppRP> call, @NotNull Response<AppRP> response) {
                int statusCode = response.code();
                try {

                    Constant.appRP = response.body();
                    assert Constant.appRP != null;

                    method.initializeAds();

                    if (Constant.appRP.getStatus().equals("1")) {

                        if (Constant.appRP.getApp_update_status().equals("true")) {
                            if (Constant.appRP.getApp_new_version() > BuildConfig.VERSION_CODE) {
                                showAdDialog(Constant.appRP.getApp_update_desc(),
                                        Constant.appRP.getApp_redirect_url(),
                                        Constant.appRP.getCancel_update_status());
                            }
                        }

                        if (Constant.appRP.getInterstitial_ad_click().equals("")) {
                            Constant.interstitialAdShow = 0;
                        } else {
                            Constant.interstitialAdShow = Integer.parseInt(Constant.appRP.getInterstitial_ad_click());
                        }

                        if (Constant.appRP.getNative_ad_position().equals("")) {
                            Constant.nativeAdPos = 0;
                        } else {
                            Constant.nativeAdPos = Integer.parseInt(Constant.appRP.getNative_ad_position());
                        }

                        if(method.isAdmobFBAds()) {
                            checkForConsent();
                        } else {
                            method.showBannerAd(linearLayout);
                        }

                        try {
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                                    getResources().getString(R.string.home)).commitAllowingStateLoss();
                            selectDrawerItem(0);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        method.alertBox(Constant.appRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<AppRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("error_fail", t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void selectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    public void deselectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setCheckable(false);
        navigationView.getMenu().getItem(position).setChecked(false);
    }

    public void backStackRemove() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void checkForConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant.appRP.getPublisher_id()};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Method.personalizationAd = true;
                        method.showBannerAd(linearLayout);
                        break;
                    case NON_PERSONALIZED:
                        Method.personalizationAd = false;
                        method.showBannerAd(linearLayout);
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            Method.personalizationAd = true;
                            method.showBannerAd(linearLayout);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(Constant.appRP.getPrivacy_policy_link());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Method.personalizationAd = true;
                                method.showBannerAd(linearLayout);
                                break;
                            case NON_PERSONALIZED:
                            case UNKNOWN:
                                Method.personalizationAd = false;
                                method.showBannerAd(linearLayout);
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    private boolean getBannerAdType() {
        return Constant.appRP.getBanner_ad_type().equals("admob");
    }

    private void showAdDialog(String description, String link, String isCancel) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

        MaterialTextView textViewDescription = dialog.findViewById(R.id.textView_description_dialog_update);
        MaterialButton buttonUpdate = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (isCancel.equals("true")) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        textViewDescription.setText(description);

        buttonUpdate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
