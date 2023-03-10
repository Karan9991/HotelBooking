package com.example.singlehotel.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.singlehotel.activity.AboutUs;
import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.adapter.SliderAdapter;
import com.example.singlehotel.interfaces.OnClick;
import com.example.singlehotel.R;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.EnchantedViewPager;
import com.example.singlehotel.util.Method;
import com.example.singlehotel.response.HomeRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private Animation myAnim;
    private ProgressBar progressBar;
    private SliderAdapter sliderAdapter;
    private EnchantedViewPager viewPager;
    private ConstraintLayout conMain, conNoData;
    private MaterialTextView textViewTitle, textViewAdd, textViewEmail, textViewPhone;
    private ImageView imageViewFacebook, imageViewWhatsApp, imageViewTwitter, imageViewInstagram, imageViewYouTube, imageViewWeb;

    private Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    final Handler handler = new Handler();
    private Runnable Update;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.home_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }

        onClick = (position, type, id, title) -> {
            switch (type) {
                case "room":
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new RoomFragment(),
                            getResources().getString(R.string.room)).addToBackStack(getResources().getString(R.string.room))
                            .commitAllowingStateLoss();
                    break;
                case "location":
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new LocationFragment(),
                            getResources().getString(R.string.location)).addToBackStack(getResources().getString(R.string.location))
                            .commitAllowingStateLoss();
                    break;
                case "facility":
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new FacilitiesFragment(),
                            getResources().getString(R.string.facilities)).addToBackStack(getResources().getString(R.string.facilities))
                            .commitAllowingStateLoss();
                    break;
                case "gallery":
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new GalleryFragment(),
                            getResources().getString(R.string.gallery)).addToBackStack(getResources().getString(R.string.gallery))
                            .commitAllowingStateLoss();
                    break;
                case "contactUS":
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, new ContactUsFragment(),
                            getResources().getString(R.string.contact_us)).addToBackStack(getResources().getString(R.string.contact_us))
                            .commitAllowingStateLoss();
                    break;
                case "aboutUs":
                    startActivity(new Intent(getActivity(), AboutUs.class));
                    break;
            }

        };
        method = new Method(getActivity(), onClick);

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        conMain = view.findViewById(R.id.con_home);
        conNoData = view.findViewById(R.id.con_noDataFound);
        progressBar = view.findViewById(R.id.progressbar_home);
        viewPager = view.findViewById(R.id.viewPager_home);
        textViewTitle = view.findViewById(R.id.textView_title_home);
        textViewAdd = view.findViewById(R.id.textView_add_home);
        textViewEmail = view.findViewById(R.id.textView_email_home);
        textViewPhone = view.findViewById(R.id.textView_phone_home);
        imageViewFacebook = view.findViewById(R.id.imageView_facebook_home);
        imageViewWhatsApp = view.findViewById(R.id.imageView_whatsApp_home);
        imageViewTwitter = view.findViewById(R.id.imageView_twitter_home);
        imageViewInstagram = view.findViewById(R.id.imageView_instagram_home);
        imageViewYouTube = view.findViewById(R.id.imageView_youtube_home);
        imageViewWeb = view.findViewById(R.id.imageView_web_home);
        textViewAdd = view.findViewById(R.id.textView_add_home);
        MaterialCardView cardViewRoom = view.findViewById(R.id.cardView_room_home);
        MaterialCardView cardViewLocation = view.findViewById(R.id.cardView_location_home);
        MaterialCardView cardViewFacilities = view.findViewById(R.id.cardView_facilities_home);
        MaterialCardView cardViewGallery = view.findViewById(R.id.cardView_gallery_home);
        MaterialCardView cardViewContactUs = view.findViewById(R.id.cardView_contactUs_home);
        MaterialCardView cardViewAboutUs = view.findViewById(R.id.cardView_aboutUs_home);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        cardViewRoom.setOnClickListener(v -> method.onClickAd(0, "room", "", ""));

        cardViewLocation.setOnClickListener(v -> method.onClickAd(0, "location", "", ""));

        cardViewFacilities.setOnClickListener(v -> method.onClickAd(0, "facility", "", ""));

        cardViewGallery.setOnClickListener(v -> method.onClickAd(0, "gallery", "", ""));

        cardViewContactUs.setOnClickListener(v -> method.onClickAd(0, "contactUS", "", ""));

        cardViewAboutUs.setOnClickListener(v -> method.onClickAd(0, "aboutUs", "", ""));

        if (method.isNetworkAvailable()) {
            home();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    public void home() {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "get_home");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeRP> call = apiService.getHome(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeRP>() {
                @Override
                public void onResponse(@NotNull Call<HomeRP> call, @NotNull Response<HomeRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {

                        try {

                            HomeRP homeRP = response.body();
                            assert homeRP != null;

                            if (homeRP.getStatus().equals("1")) {

                                textViewTitle.setText(homeRP.getHotel_name());
                                textViewAdd.setText(homeRP.getHotel_address());
                                textViewEmail.setText(homeRP.getHotel_email());
                                textViewPhone.setText(homeRP.getHotel_phone());

                                if (homeRP.getHomeBanners().size() != 0) {

                                    int columnWidth = method.getScreenWidth();
                                    viewPager.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2 + 80));

                                    viewPager.useScale();
                                    viewPager.removeAlpha();

                                    sliderAdapter = new SliderAdapter(getActivity(), "slider", homeRP.getHomeBanners(), onClick);
                                    viewPager.setAdapter(sliderAdapter);

                                    Update = () -> {
                                        if (viewPager.getCurrentItem() == (sliderAdapter.getCount() - 1)) {
                                            viewPager.setCurrentItem(0, true);
                                        } else {
                                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                        }
                                    };

                                    if (sliderAdapter.getCount() > 1) {
                                        timer = new Timer(); // This will create a new Thread
                                        timer.schedule(new TimerTask() { // task to be scheduled
                                            @Override
                                            public void run() {
                                                handler.post(Update);
                                            }
                                        }, DELAY_MS, PERIOD_MS);
                                    }

                                }

                                conMain.setVisibility(View.VISIBLE);

                                imageViewFacebook.setOnClickListener(v -> {
                                    imageViewFacebook.startAnimation(myAnim);
                                    String string = homeRP.getFacebook_url();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_facebook_link));
                                    } else {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(string));
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            method.alertBox(getResources().getString(R.string.wrong));
                                        }
                                    }
                                });

                                imageViewWhatsApp.setOnClickListener(v -> {
                                    imageViewWhatsApp.startAnimation(myAnim);
                                    String string = homeRP.getWhatsapp_url();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_whatsApp_link));
                                    } else {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(string));
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            method.alertBox(getResources().getString(R.string.wrong));
                                        }
                                    }
                                });

                                imageViewTwitter.setOnClickListener(v -> {
                                    imageViewTwitter.startAnimation(myAnim);
                                    String string = homeRP.getTwitter_url();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_twitter_link));
                                    } else {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(string));
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            method.alertBox(getResources().getString(R.string.wrong));
                                        }
                                    }
                                });

                                imageViewInstagram.setOnClickListener(v -> {
                                    imageViewInstagram.startAnimation(myAnim);
                                    String string = homeRP.getInstagram_url();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_instagram_link));
                                    } else {
                                        try {
                                            Uri uri = Uri.parse(string);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            intent.setPackage("com.instagram.android");
                                            startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(string)));
                                            } catch (Exception e1) {
                                                method.alertBox(getResources().getString(R.string.wrong));
                                            }
                                        }
                                    }
                                });

                                imageViewYouTube.setOnClickListener(v -> {
                                    imageViewYouTube.startAnimation(myAnim);
                                    String string = homeRP.getYoutube_url();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_youtube_link));
                                    } else {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(string));
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            method.alertBox(getResources().getString(R.string.wrong));
                                        }
                                    }
                                });

                                imageViewWeb.setOnClickListener(v -> {
                                    imageViewWeb.startAnimation(myAnim);
                                    String string = homeRP.getWebsite_url();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_web_link));
                                    } else {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(string));
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            method.alertBox(getResources().getString(R.string.wrong));
                                        }
                                    }
                                });


                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(homeRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<HomeRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
