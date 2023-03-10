package com.example.singlehotel.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.R;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.example.singlehotel.response.LocationRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationFragment extends Fragment {

    private Method method;
    private Menu menu;
    private ProgressBar progressBar;
    private SupportMapFragment mapFragment;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.location_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.location));
        }

        method = new Method(getActivity());

        progressBar = view.findViewById(R.id.progressBar_location_fragment);
        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_location));

        progressBar.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            location();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.location_menu, menu);
        this.menu = menu;
        MenuItem mapLocation = menu.findItem(R.id.map_location);
        mapLocation.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void location() {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "get_location");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<LocationRP> call = apiService.getLocation(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<LocationRP>() {
                @Override
                public void onResponse(@NotNull Call<LocationRP> call, @NotNull Response<LocationRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {
                        try {

                            LocationRP locationRP = response.body();

                            assert locationRP != null;

                            if (locationRP.getStatus().equals("1")) {

                                if (menu != null) {
                                    MenuItem mapLocation = menu.findItem(R.id.map_location);
                                    mapLocation.setVisible(true);
                                    mapLocation.setOnMenuItemClickListener(item -> {
                                        if (method.isAppInstalled()) {
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse("http://maps.google.com/maps?daddr=" + locationRP.getHotel_lat() + "," + locationRP.getHotel_long()));
                                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                method.alertBox(getResources().getString(R.string.wrong));
                                            }
                                        } else {
                                            method.alertBox(getResources().getString(R.string.map_not_install));
                                        }
                                        return false;
                                    });
                                }

                                mapFragment.getMapAsync(googleMap -> {
                                    try {
                                        // Add a marker in Sydney, Australia,
                                        // and move the map's camera to the same location.
                                        LatLng latLng = new LatLng(Double.parseDouble(locationRP.getHotel_lat())
                                                , Double.parseDouble(locationRP.getHotel_long()));
                                        googleMap.addMarker(new MarkerOptions().position(latLng)
                                                .title(""));
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                                    } catch (Exception e) {
                                        Log.d("error_map", e.toString());
                                    }
                                });

                            } else {
                                method.alertBox(locationRP.getMessage());
                            }


                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<LocationRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}


