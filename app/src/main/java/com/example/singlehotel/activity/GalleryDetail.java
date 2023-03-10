package com.example.singlehotel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.singlehotel.R;
import com.example.singlehotel.adapter.GalleryDetailAdapter;
import com.example.singlehotel.adapter.GalleryListAdapter;
import com.example.singlehotel.interfaces.OnClick;
import com.example.singlehotel.item.GalleryDetailList;
import com.example.singlehotel.response.GalleryListRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.EndlessRecyclerViewScrollListener;
import com.example.singlehotel.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryDetail extends AppCompatActivity {

    private Method method;
    private OnClick onClick;
    private String catId;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private Boolean isOver = false;
    private ConstraintLayout conNoData;
    private int paginationIndex = 1;
    private List<GalleryDetailList> galleryLists;
    private GalleryDetailAdapter galleryDetailAdapter;
    private GalleryListAdapter galleryListAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        onClick = (position, type, id, title) -> viewPager.setCurrentItem(position);
        method = new Method(GalleryDetail.this, onClick);
        method.forceRTLIfSupported();

        galleryLists = new ArrayList<>();

        Intent in = getIntent();
        catId = in.getStringExtra("id");
        String title = in.getStringExtra("title");
        int position = in.getIntExtra("position", 0);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_gallery_detail);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conNoData = findViewById(R.id.con_noDataFound);
        viewPager = findViewById(R.id.viewPager_gallery_detail);
        progressBar = findViewById(R.id.progressBar_gallery_detail);
        recyclerView = findViewById(R.id.recyclerView_gallery_detail);

        LinearLayout linearLayout = findViewById(R.id.ll_gallery_detail);
        method.showBannerAd(linearLayout);

        conNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GalleryDetail.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    galleryListAdapter.hideHeader();
                }
            }
        });

        callData();

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            galleryDetail(catId);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private void galleryDetail(String cat_id) {

        if (galleryListAdapter == null || galleryDetailAdapter == null) {
            galleryLists.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(GalleryDetail.this));
        jsObj.addProperty("page", paginationIndex);
        jsObj.addProperty("cat_id", cat_id);
        jsObj.addProperty("method_name", "get_cat_by_gallery_id");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GalleryListRP> call = apiService.getGalleryList(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<GalleryListRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<GalleryListRP> call, @NotNull Response<GalleryListRP> response) {
                int statusCode = response.code();

                try {

                    GalleryListRP galleryListRP = response.body();

                    assert galleryListRP != null;
                    if (galleryListRP.getStatus().equals("1")) {

                        if (galleryListRP.getGalleryDetailLists().size() == 0) {
                            if (galleryListAdapter != null) {
                                galleryListAdapter.hideHeader();
                                isOver = true;
                            }
                        } else {
                            galleryLists.addAll(galleryListRP.getGalleryDetailLists());
                        }

                        if (galleryListAdapter == null || galleryDetailAdapter == null) {

                            if (galleryLists.size() != 0) {

                                galleryListAdapter = new GalleryListAdapter(GalleryDetail.this, "gallery_detail", galleryLists, onClick);
                                recyclerView.setAdapter(galleryListAdapter);

                                galleryDetailAdapter = new GalleryDetailAdapter(GalleryDetail.this, "gallery_detail", galleryLists);
                                viewPager.setAdapter(galleryDetailAdapter);
                                viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

                                setCurrentItem(0);

                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                            }

                        } else {
                            galleryListAdapter.notifyDataSetChanged();
                            galleryDetailAdapter.notifyDataSetChanged();
                        }


                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        method.alertBox(galleryListRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<GalleryListRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("error_fail", t.toString());
                progressBar.setVisibility(View.VISIBLE);
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

