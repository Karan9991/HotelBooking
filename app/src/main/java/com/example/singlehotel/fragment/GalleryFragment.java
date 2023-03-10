package com.example.singlehotel.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.activity.GalleryDetail;
import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.adapter.GalleryAdapter;
import com.example.singlehotel.interfaces.OnClick;
import com.example.singlehotel.item.GalleryList;
import com.example.singlehotel.R;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.EndlessRecyclerViewScrollListener;
import com.example.singlehotel.util.Method;
import com.example.singlehotel.response.GalleryCatRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private List<GalleryList> galleryLists;
    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private ConstraintLayout conNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1, totalArraySize = 0;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.gallery_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.gallery));
        }

        galleryLists = new ArrayList<>();

        onClick = (position, type, id, title) -> startActivity(new Intent(getActivity(), GalleryDetail.class)
                .putExtra("id", id)
                .putExtra("title", title)
                .putExtra("position", position));
        method = new Method(getActivity(), onClick);

        conNoData = view.findViewById(R.id.con_noDataFound);
        progressBar = view.findViewById(R.id.progressBar_gallery_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_gallery_fragment);

        conNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (galleryAdapter != null) {
                    if (galleryAdapter.getItemViewType(position) == 1) {
                        return 1;
                    } else {
                        return 2;
                    }
                }
                return 2;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    galleryAdapter.hideHeader();
                }
            }
        });

        callData();

        return view;
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            catGallery();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void catGallery() {

        if (getActivity() != null) {

            if (galleryAdapter == null) {
                galleryLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("method_name", "get_category");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<GalleryCatRP> call = apiService.getGallery(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<GalleryCatRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<GalleryCatRP> call, @NotNull Response<GalleryCatRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {

                        try {

                            GalleryCatRP galleryCatRP = response.body();

                            assert galleryCatRP != null;
                            if (galleryCatRP.getStatus().equals("1")) {

                                if (galleryCatRP.getGalleryLists().size() == 0) {
                                    if (galleryAdapter != null) {
                                        galleryAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    totalArraySize = totalArraySize + galleryCatRP.getGalleryLists().size();
                                    for (int i = 0; i < galleryCatRP.getGalleryLists().size(); i++) {
                                        galleryLists.add(galleryCatRP.getGalleryLists().get(i));

                                        if (Constant.appRP != null && Constant.nativeAdPos != 0 && Constant.appRP.isNative_ad()) {
                                            int abc = galleryLists.lastIndexOf(null);
                                            if (((galleryLists.size() - (abc + 1)) % Constant.nativeAdPos == 0) && (galleryCatRP.getGalleryLists().size() - 1 != i || totalArraySize != 1000)) {
                                                galleryLists.add(null);
                                            }
                                        }
                                    }
                                }

                                if (galleryAdapter == null) {
                                    if (galleryLists.size() != 0) {
                                        galleryAdapter = new GalleryAdapter(getActivity(), "gallery", galleryLists, onClick);
                                        recyclerView.setAdapter(galleryAdapter);
                                    } else {
                                        conNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    galleryAdapter.notifyDataSetChanged();
                                }
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(galleryCatRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<GalleryCatRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("error_fail", t.toString());
                    progressBar.setVisibility(View.VISIBLE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
