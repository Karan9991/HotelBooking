package com.example.singlehotel.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.singlehotel.R;
import com.example.singlehotel.activity.Login;
import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.activity.ViewImage;
import com.example.singlehotel.response.ProfileRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Events;
import com.example.singlehotel.util.GlobalBus;
import com.example.singlehotel.util.Method;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private CircleImageView imageView;
    private MaterialCardView cardViewPass;
    private ConstraintLayout conMain, conNoData;
    private MaterialButton buttonLogin, buttonLoginLogout;
    private MaterialTextView textViewName, textViewNotLogin;
    private ImageView imageViewLoginType, imageViewEdit, imageViewData;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);

        GlobalBus.getBus().register(this);

        method = new Method(getActivity());

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.profile));
        }

        conMain = view.findViewById(R.id.con_profile);
        conNoData = view.findViewById(R.id.con_not_login);
        progressBar = view.findViewById(R.id.progressbar_profile);
        imageView = view.findViewById(R.id.imageView_pro);
        imageViewLoginType = view.findViewById(R.id.imageView_loginType_pro);
        textViewName = view.findViewById(R.id.textView_name_pro);
        buttonLogin = view.findViewById(R.id.button_not_login);
        buttonLoginLogout = view.findViewById(R.id.button_login_profile);
        imageViewData = view.findViewById(R.id.imageView_not_login);
        textViewNotLogin = view.findViewById(R.id.textView_not_login);
        imageViewEdit = view.findViewById(R.id.imageView_edit_profile);
        cardViewPass = view.findViewById(R.id.cardView_changePassword_pro);

        progressBar.setVisibility(View.GONE);
        data(false, false);
        conMain.setVisibility(View.GONE);

        buttonLogin.setOnClickListener(v -> {
            if (method.isLogin()) {
                logout();
            } else {
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finishAffinity();
            }
        });

        callData();

        return view;

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                profile(method.userId());
            } else {
                data(true, true);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                textViewNotLogin.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                textViewNotLogin.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
                if (method.isLogin()) {
                    buttonLogin.setText(getResources().getString(R.string.logout));
                } else {
                    buttonLogin.setText(getResources().getString(R.string.login));
                }
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void getData(Events.ProfileUpdate profileUpdate) {
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.profile));
        }
        data(false, false);
        conMain.setVisibility(View.GONE);
        callData();
    }

    public void profile(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "user_profile");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProfileRP> call = apiService.getProfile(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProfileRP>() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onResponse(@NotNull Call<ProfileRP> call, @NotNull Response<ProfileRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {

                        try {

                            ProfileRP profileRP = response.body();

                            assert profileRP != null;
                            if (profileRP.getStatus().equals("1")) {

                                if (profileRP.getSuccess().equals("1")) {

                                    String loginType = method.getLoginType();
                                    if (loginType.equals("google") || loginType.equals("facebook")) {
                                        cardViewPass.setVisibility(View.GONE);
                                        imageViewLoginType.setVisibility(View.VISIBLE);
                                        if (loginType.equals("google")) {
                                            imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.google_user_pro));
                                        } else {
                                            imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.fb_user_pro));
                                        }
                                    } else {
                                        cardViewPass.setVisibility(View.VISIBLE);
                                        imageViewLoginType.setVisibility(View.GONE);
                                    }

                                    Glide.with(getActivity().getApplicationContext()).load(profileRP.getUser_image())
                                            .placeholder(R.drawable.user_profile).into(imageView);

                                    imageView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ViewImage.class)
                                            .putExtra("path", profileRP.getUser_image())));

                                    textViewName.setText(profileRP.getName());

                                    imageViewEdit.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                                            new EditProfileFragment(), getResources().getString(R.string.edit_profile))
                                            .addToBackStack(getResources().getString(R.string.edit_profile))
                                            .commitAllowingStateLoss());

                                    cardViewPass.setOnClickListener(v -> {
                                        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("name", profileRP.getName());
                                        bundle.putString("image", profileRP.getUser_image());
                                        changePasswordFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                                                changePasswordFragment, getResources().getString(R.string.change_pass))
                                                .addToBackStack(getResources().getString(R.string.change_pass)).commitAllowingStateLoss();
                                    });

                                    if (method.isLogin()) {
                                        buttonLoginLogout.setText(getResources().getString(R.string.logout));
                                    } else {
                                        buttonLoginLogout.setText(getResources().getString(R.string.login));
                                    }

                                    buttonLoginLogout.setOnClickListener(v -> logout());

                                    conMain.setVisibility(View.VISIBLE);

                                } else {
                                    method.suspend(profileRP.getMsg());
                                }

                            } else if (profileRP.getStatus().equals("2")) {
                                method.suspend(profileRP.getMessage());
                            } else {
                                data(true, false);
                                method.alertBox(profileRP.getMessage());
                            }

                        } catch (Exception e) {
                            data(true, false);
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                            Log.d("exception_error", e.toString());
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<ProfileRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    data(true, false);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    //alert message box
    public void logout() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.DialogTitleTextStyle);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.logout_message));
        builder.setPositiveButton(getResources().getString(R.string.logout),
                (arg0, arg1) -> {
                    if (method.getLoginType().equals("google")) {

                        // Configure sign-in to request the user's ID, email address, and basic
                        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        // Build a GoogleSignInClient with the options specified by gso.
                        //Google login
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(getActivity(), task -> {
                                    method.editor.putBoolean(method.prefLogin, false);
                                    method.editor.commit();
                                    startActivity(new Intent(getActivity(), Login.class));
                                    getActivity().finishAffinity();
                                });

                    } else if (method.getLoginType().equals("facebook")) {
                        LoginManager.getInstance().logOut();
                        method.editor.putBoolean(method.prefLogin, false);
                        method.editor.commit();
                        startActivity(new Intent(getActivity(), Login.class));
                        getActivity().finishAffinity();
                    } else {
                        method.editor.putBoolean(method.prefLogin, false);
                        method.editor.commit();
                        startActivity(new Intent(getActivity(), Login.class));
                        getActivity().finishAffinity();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
