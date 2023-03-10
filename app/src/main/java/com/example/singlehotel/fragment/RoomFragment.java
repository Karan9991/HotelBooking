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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.activity.RoomDetail;
import com.example.singlehotel.adapter.RoomAdapter;
import com.example.singlehotel.interfaces.OnClick;
import com.example.singlehotel.item.RoomList;
import com.example.singlehotel.R;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.EndlessRecyclerViewScrollListener;
import com.example.singlehotel.util.Method;
import com.example.singlehotel.response.RoomRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<RoomList> roomLists;
    private RoomAdapter roomAdapter;
    private ConstraintLayout conNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1, totalArraySize = 0;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.room_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.room));
        }

        roomLists = new ArrayList<>();

        onClick = (position, type, id, title) -> startActivity(new Intent(getActivity(), RoomDetail.class)
                .putExtra("room_id", id)
                .putExtra("title", title)
                .putExtra("position", position));
        method = new Method(getActivity(), onClick);

        conNoData = view.findViewById(R.id.con_noDataFound);
        progressBar = view.findViewById(R.id.progressBar_room_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_room_fragment);

        conNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
                    roomAdapter.hideHeader();
                }
            }
        });

        callData();

        return view;
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            room();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    public void room() {

        if (getActivity() != null) {

            if (roomAdapter == null) {
                roomLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("method_name", "get_room_list");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<RoomRP> call = apiService.getRoom(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<RoomRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<RoomRP> call, @NotNull Response<RoomRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {

                        try {

                            RoomRP roomRP = response.body();

                            assert roomRP != null;
                            if (roomRP.getStatus().equals("1")) {

                                if (roomRP.getRoomLists().size() == 0) {
                                    if (roomAdapter != null) {
                                        roomAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
//                                    roomLists.addAll(roomRP.getRoomLists());

                                    totalArraySize = totalArraySize + roomRP.getRoomLists().size();
                                    for (int i = 0; i < roomRP.getRoomLists().size(); i++) {
                                        roomLists.add(roomRP.getRoomLists().get(i));

                                        if (Constant.appRP != null && Constant.nativeAdPos != 0 && Constant.appRP.isNative_ad()) {
                                            int abc = roomLists.lastIndexOf(null);
                                            if (((roomLists.size() - (abc + 1)) % Constant.nativeAdPos == 0) && (roomRP.getRoomLists().size() - 1 != i || totalArraySize != 1000)) {
                                                roomLists.add(null);
                                            }
                                        }
                                    }
                                }

                                if (roomAdapter == null) {
                                    if (roomLists.size() != 0) {
                                        roomAdapter = new RoomAdapter(getActivity(), roomLists, "room", onClick);
                                        recyclerView.setAdapter(roomAdapter);
                                    } else {
                                        conNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    roomAdapter.notifyDataSetChanged();
                                }

                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(roomRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<RoomRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    progressBar.setVisibility(View.VISIBLE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
