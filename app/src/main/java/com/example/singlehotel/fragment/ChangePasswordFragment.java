package com.example.singlehotel.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.singlehotel.R;
import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.response.DataRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private Method method;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private TextInputEditText editTextOldPassword, editTextPassword, editTextConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_password_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.change_pass));
        }

        String name = getArguments().getString("name");
        String image = getArguments().getString("image");

        method = new Method(getActivity());

        progressDialog = new ProgressDialog(getActivity());

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        CircleImageView imageView = view.findViewById(R.id.imageView_cp_fragment);
        MaterialTextView textViewName = view.findViewById(R.id.textView_name_cp_fragment);
        editTextOldPassword = view.findViewById(R.id.editText_old_password_cp_fragment);
        editTextPassword = view.findViewById(R.id.editText_password_cp_fragment);
        editTextConfirmPassword = view.findViewById(R.id.editText_confirm_password_cp_fragment);
        MaterialButton button = view.findViewById(R.id.button_edit_cp_fragment);

        textViewName.setText(name);

        Glide.with(getActivity().getApplicationContext()).load(image)
                .placeholder(R.drawable.user_profile)
                .into(imageView);

        button.setOnClickListener(v -> save());

        return view;

    }

    private void save() {

        String oldPassword = editTextOldPassword.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        editTextOldPassword.setError(null);
        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);

        if (oldPassword.equals("") || oldPassword.isEmpty()) {
            editTextOldPassword.requestFocus();
            editTextOldPassword.setError(getResources().getString(R.string.please_enter_old_password));
        } else if (password.equals("") || password.isEmpty()) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_new_password));
        } else if (confirmPassword.equals("") || confirmPassword.isEmpty()) {
            editTextConfirmPassword.requestFocus();
            editTextConfirmPassword.setError(getResources().getString(R.string.please_enter_new_confirm_password));
        } else if (!password.equals(confirmPassword)) {
            method.alertBox(getResources().getString(R.string.new_password_not_match));
        } else {
            if (method.isNetworkAvailable()) {

                editTextOldPassword.clearFocus();
                editTextPassword.clearFocus();
                editTextConfirmPassword.clearFocus();
                imm.hideSoftInputFromWindow(editTextOldPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextConfirmPassword.getWindowToken(), 0);

                passwordUpdate(method.userId(), oldPassword, password);

            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    private void passwordUpdate(String userId, String oldPassword, String password) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("old_password", oldPassword);
            jsObj.addProperty("new_password", password);
            jsObj.addProperty("method_name", "change_password");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.updatePassword(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                    if (getActivity() != null) {

                        try {
                            DataRP dataRP = response.body();
                            assert dataRP != null;

                            if (dataRP.getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("1")) {
                                    editTextOldPassword.setText("");
                                    editTextPassword.setText("");
                                    editTextConfirmPassword.setText("");
                                }
                                method.alertBox(dataRP.getMsg());
                            } else if (dataRP.getStatus().equals("2")) {
                                method.suspend(dataRP.getMessage());
                            } else {
                                method.alertBox(dataRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
