package com.example.singlehotel.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singlehotel.R;
import com.example.singlehotel.activity.Login;
import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.adapter.BookingHistoryAdapter;
import com.example.singlehotel.item.BookingList;
import com.example.singlehotel.response.BookingHisRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryFragment extends Fragment {

    Method method;
    RecyclerView recyclerViewList;
    ProgressBar progressBar;
    LinearLayout layoutNoFound;
    private List<BookingList> bookingLists;
    BookingHistoryAdapter bookingHistoryAdapter;
    ConstraintLayout conNoData;
    MaterialButton buttonLogin;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.my_booking_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.booking_his_title));
        }
        method = new Method(requireActivity());
        method.forceRTLIfSupported();

        bookingLists = new ArrayList<>();

        recyclerViewList = view.findViewById(R.id.recyclerView_gallery_detail);
        progressBar = view.findViewById(R.id.progressBar_gallery_detail);
        layoutNoFound = view.findViewById(R.id.lay_no);
        conNoData = view.findViewById(R.id.con_not_login);
        buttonLogin = view.findViewById(R.id.button_not_login);

        recyclerViewList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerViewList.setLayoutManager(layoutManager);

        layoutNoFound.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if (method.isLogin()) {
            bookingHistory();
            conNoData.setVisibility(View.GONE);
        } else {
            conNoData.setVisibility(View.VISIBLE);
        }

        buttonLogin.setOnClickListener(v -> {
            if (method.isLogin()) {
                bookingHistory();
            } else {
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finishAffinity();
            }
        });


        return view;
    }

    public void bookingHistory() {


        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(requireActivity()));
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("method_name", "booking_history");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BookingHisRP> call = apiService.getBookHis(API.toBase64(jsObj.toString()));
        Log.e("hisss", "" + API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<BookingHisRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<BookingHisRP> call, @NotNull Response<BookingHisRP> response) {
                try {

                    BookingHisRP bookingHisRP = response.body();

                    assert bookingHisRP != null;
                    if (bookingHisRP.getStatus().equals("1")) {
                        bookingLists=bookingHisRP.getBookingLists();
                        if (bookingLists.size() != 0) {
                            bookingHistoryAdapter = new BookingHistoryAdapter(requireActivity(), "", bookingLists);
                            recyclerViewList.setAdapter(bookingHistoryAdapter);
                        } else {
                            layoutNoFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        layoutNoFound.setVisibility(View.VISIBLE);
                        method.alertBox(bookingHisRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }


                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<BookingHisRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressBar.setVisibility(View.VISIBLE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }
}
