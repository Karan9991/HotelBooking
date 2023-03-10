package com.example.singlehotel.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.adapter.FacilitiesAdapter;
import com.example.singlehotel.R;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.example.singlehotel.response.FacilitiesRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacilitiesFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private WebView webView;
    private RecyclerView recyclerView;
    private FacilitiesAdapter facilitiesAdapter;
    private ConstraintLayout conMain, conNoData;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.facilities_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.facilities));
        }

        method = new Method(getActivity());

        conMain = view.findViewById(R.id.con_facilities_fragment);
        conNoData = view.findViewById(R.id.con_noDataFound);
        progressBar = view.findViewById(R.id.progressBar_facilities_fragment);
        webView = view.findViewById(R.id.webView_facilities_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_facilities_fragment);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        if (method.isNetworkAvailable()) {
            facilities();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    public void facilities() {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "get_facilities");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<FacilitiesRP> call = apiService.getFacilities(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<FacilitiesRP>() {
                @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
                @Override
                public void onResponse(@NotNull Call<FacilitiesRP> call, @NotNull Response<FacilitiesRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {

                        try {

                            FacilitiesRP facilitiesRP = response.body();

                            assert facilitiesRP != null;
                            if (facilitiesRP.getStatus().equals("1")) {

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
                                        + facilitiesRP.getHotel_info()
                                        + "</body></html>";

                                webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                                if (facilitiesRP.getFacilitiesLists().size() != 0) {
                                    facilitiesAdapter = new FacilitiesAdapter(getActivity(), "facilities", facilitiesRP.getFacilitiesLists());
                                    recyclerView.setAdapter(facilitiesAdapter);
                                }

                                conMain.setVisibility(View.VISIBLE);

                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(facilitiesRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<FacilitiesRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("error_fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
