package com.example.singlehotel.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.singlehotel.R;
import com.example.singlehotel.activity.MainActivity;
import com.example.singlehotel.item.ContactList;
import com.example.singlehotel.response.ContactRP;
import com.example.singlehotel.response.DataRP;
import com.example.singlehotel.rest.ApiClient;
import com.example.singlehotel.rest.ApiInterface;
import com.example.singlehotel.util.API;
import com.example.singlehotel.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsFragment extends Fragment {

    public MaterialToolbar toolbar;
    private Method method;
    private Spinner spinner;
    private InputMethodManager imm;
    private ConstraintLayout con, conNoData;
    private MaterialButton buttonSubmit;
    private ProgressDialog progressDialog;
    private List<ContactList> contactLists;
    private String contactType, contactId;
    private TextInputEditText editTextName, editTextEmail, editTextPhoneNO, editTextMessage;
    private MaterialTextView textViewTitle, textViewAdd, textViewEmail, textViewPhone;

    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.contactus_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.contact_us));
        }

        method = new Method(getActivity());

        contactLists = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        con = view.findViewById(R.id.con_contactUS);
        conNoData = view.findViewById(R.id.con_noDataFound);
        textViewTitle = view.findViewById(R.id.textView_title_contactUS);
        textViewAdd = view.findViewById(R.id.textView_add_contactUS);
        textViewEmail = view.findViewById(R.id.textView_email_contactUS);
        textViewPhone = view.findViewById(R.id.textView_phone_contactUS);
        spinner = view.findViewById(R.id.spinner_contact_us);
        editTextName = view.findViewById(R.id.editText_name_contactUS);
        editTextEmail = view.findViewById(R.id.editText_email_contactUS);
        editTextPhoneNO = view.findViewById(R.id.editText_phoneNo_contactUS);
        editTextMessage = view.findViewById(R.id.editText_des_contactUS);
        buttonSubmit = view.findViewById(R.id.button_contactUs);

        con.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                getContact(method.userId());
            } else {
                getContact("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void getContact(String userId) {

        if (getActivity() != null) {

            contactLists.clear();

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "get_contact");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ContactRP> call = apiService.getContactSub(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ContactRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<ContactRP> call, @NotNull Response<ContactRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {

                        try {

                            ContactRP contactRP = response.body();

                            assert contactRP != null;
                            if (contactRP.getStatus().equals("1")) {

                                textViewTitle.setText(contactRP.getHotel_name());
                                textViewAdd.setText(contactRP.getHotel_address());
                                textViewEmail.setText(contactRP.getHotel_email());
                                textViewPhone.setText(contactRP.getHotel_phone());

                                editTextName.setText(contactRP.getName());
                                editTextEmail.setText(contactRP.getEmail());
                                editTextPhoneNO.setText(contactRP.getPhone());

                                contactLists.add(new ContactList("", getResources().getString(R.string.select_contact_subject)));
                                contactLists.addAll(contactRP.getContactLists());

                                // Spinner Drop down elements
                                List<String> strings = new ArrayList<String>();
                                for (int i = 0; i < contactLists.size(); i++) {
                                    strings.add(contactLists.get(i).getSubject());
                                }

                                // Creating adapter for spinner_cat
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, strings);
                                // Drop down layout style - list view with radio button
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                // attaching data adapter to spinner_cat
                                spinner.setAdapter(dataAdapter);

                                con.setVisibility(View.VISIBLE);

                                // Spinner click listener
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position == 0) {
                                            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_contactUs));
                                        } else {
                                            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                                        }
                                        contactType = contactLists.get(position).getSubject();
                                        contactId = contactLists.get(position).getId();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                buttonSubmit.setOnClickListener(v -> form());

                            } else if (contactRP.getStatus().equals("2")) {
                                method.suspend(contactRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(contactRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<ContactRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    public void form() {

        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNO = editTextPhoneNO.getText().toString();
        String message = editTextMessage.getText().toString();

        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPhoneNO.setError(null);
        editTextMessage.setError(null);

        if (contactType.equals(getResources().getString(R.string.select_contact_subject)) || contactType.equals("") || contactType.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_subject));
        } else if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (phoneNO.equals("") || phoneNO.isEmpty()) {
            editTextPhoneNO.requestFocus();
            editTextPhoneNO.setError(getResources().getString(R.string.please_enter_phone));
        } else if (message.equals("") || message.isEmpty()) {
            editTextMessage.requestFocus();
            editTextMessage.setError(getResources().getString(R.string.please_enter_message));
        } else {

            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextPhoneNO.clearFocus();
            editTextMessage.clearFocus();
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPhoneNO.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                contactUs(name, email, phoneNO, message, contactId);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    public void contactUs(String sendName, String sendEmail, String sendPhoneNo, String sendMessage, String contact_subject) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("contact_name", sendName);
            jsObj.addProperty("contact_email", sendEmail);
            jsObj.addProperty("contact_phone_no", sendPhoneNo);
            jsObj.addProperty("contact_msg", sendMessage);
            jsObj.addProperty("contact_subject", contact_subject);
            jsObj.addProperty("method_name", "user_contact_us");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.submitContact(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {
                    int statusCode = response.code();

                    if (getActivity() != null) {

                        try {

                            DataRP dataRP = response.body();

                            assert dataRP != null;
                            if (dataRP.getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("1")) {
                                    editTextName.setText("");
                                    editTextEmail.setText("");
                                    editTextPhoneNO.setText("");
                                    editTextMessage.setText("");
                                    spinner.setSelection(0);

                                    conformDialog(dataRP.getMsg());

                                } else {
                                    method.alertBox(dataRP.getMsg());
                                }
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
                    Log.e("fail", t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void conformDialog(String message) {

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_contact_us);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

        MaterialTextView textViewMessage = dialog.findViewById(R.id.textView_message_dialog_contactUS);
        MaterialButton button = dialog.findViewById(R.id.button_dialog_contactUS);

        textViewMessage.setText(message);

        button.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

}
