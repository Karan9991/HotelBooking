package com.example.singlehotel.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("publisher_id")
    private String publisher_id;

    @SerializedName("banner_ad")
    private boolean banner_ad;

    @SerializedName("banner_ad_id")
    private String banner_ad_id;

    @SerializedName("banner_ad_type")
    private String banner_ad_type ;

    @SerializedName("interstitial_ad")
    private boolean interstitial_ad;

    @SerializedName("currency_code")
    private String currency_code;

    @SerializedName("stripe_on_off")
    private boolean stripe_on_off;

    @SerializedName("stripe_secret_key")
    private String stripe_secret_key;

    @SerializedName("stripe_publishable_key")
    private String stripe_publishable_key;

    @SerializedName("razorpay_on_off")
    private boolean razorpay_on_off;

    @SerializedName("razorpay_key")
    private String razorpay_key;

    @SerializedName("razorpay_secret")
    private String razorpay_secret;

    @SerializedName("braintree_on_off")
    private boolean braintree_on_off;

    @SerializedName("interstitial_ad_id")
    private String interstitial_ad_id;

    @SerializedName("braintree_merchant_id")
    private String braintree_merchant_id;

    @SerializedName("braintree_public_key")
    private String braintree_public_key;

    @SerializedName("braintree_private_key")
    private String braintree_private_key;

    @SerializedName("braintree_merchant_account_id")
    private String braintree_merchant_account_id;

    @SerializedName("interstitial_ad_click")
    private String interstitial_ad_click;

    @SerializedName("interstitial_ad_type")
    private String interstitial_ad_type ;

    @SerializedName("privacy_policy_link")
    private String privacy_policy_link;

    @SerializedName("app_update_status")
    private String app_update_status ;

    @SerializedName("app_new_version")
    private int app_new_version ;

    @SerializedName("app_update_desc")
    private String app_update_desc ;

    @SerializedName("app_redirect_url")
    private String app_redirect_url ;

    @SerializedName("cancel_update_status")
    private String cancel_update_status ;

    @SerializedName("startapp_app_id")
    private String startapp_app_id;

    @SerializedName("nativ_ad_type")
    private String native_ad_type;

    @SerializedName("nativ_ad")
    private boolean native_ad = false;

    @SerializedName("nativ_ad_id")
    private String native_ad_id;

    @SerializedName("nativ_ad_position")
    private String native_ad_pos;

    @SerializedName("wortise_app_id")
    private String wortise_app_id;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public boolean isBanner_ad() {
        return banner_ad;
    }

    public String getBanner_ad_id() {
        return banner_ad_id;
    }

    public String getBanner_ad_type() {
        return banner_ad_type;
    }

    public boolean isInterstitial_ad() {
        return interstitial_ad;
    }

    public String getInterstitial_ad_id() {
        return interstitial_ad_id;
    }

    public String getInterstitial_ad_click() {
        return interstitial_ad_click;
    }

    public String getInterstitial_ad_type() {
        return interstitial_ad_type;
    }

    public String getPrivacy_policy_link() {
        return privacy_policy_link;
    }

    public String getApp_update_status() {
        return app_update_status;
    }

    public int getApp_new_version() {
        return app_new_version;
    }

    public String getApp_update_desc() {
        return app_update_desc;
    }

    public String getApp_redirect_url() {
        return app_redirect_url;
    }

    public String getCancel_update_status() {
        return cancel_update_status;
    }

    public String getStartapp_app_id() {
        return startapp_app_id;
    }

    public String getNative_ad_position() {
        return native_ad_pos;
    }

    public String getNative_ad_id() {
        return native_ad_id;
    }

    public boolean isNative_ad() {
        return native_ad;
    }

    public String getNative_ad_type() {
        return native_ad_type;
    }

    public String getWortise_app_id() {
        return wortise_app_id;
    }

    public boolean isStripe_on_off() {
        return stripe_on_off;
    }

    public void setStripe_on_off(boolean stripe_on_off) {
        this.stripe_on_off = stripe_on_off;
    }

    public boolean isRazorpay_on_off() {
        return razorpay_on_off;
    }

    public void setRazorpay_on_off(boolean razorpay_on_off) {
        this.razorpay_on_off = razorpay_on_off;
    }

    public String getStripe_secret_key() {
        return stripe_secret_key;
    }

    public void setStripe_secret_key(String stripe_secret_key) {
        this.stripe_secret_key = stripe_secret_key;
    }

    public String getStripe_publishable_key() {
        return stripe_publishable_key;
    }

    public void setStripe_publishable_key(String stripe_publishable_key) {
        this.stripe_publishable_key = stripe_publishable_key;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getRazorpay_key() {
        return razorpay_key;
    }

    public void setRazorpay_key(String razorpay_key) {
        this.razorpay_key = razorpay_key;
    }

    public String getRazorpay_secret() {
        return razorpay_secret;
    }

    public void setRazorpay_secret(String razorpay_secret) {
        this.razorpay_secret = razorpay_secret;
    }

    public boolean isBraintree_on_off() {
        return braintree_on_off;
    }

    public void setBraintree_on_off(boolean braintree_on_off) {
        this.braintree_on_off = braintree_on_off;
    }

    public String getBraintree_merchant_id() {
        return braintree_merchant_id;
    }

    public void setBraintree_merchant_id(String braintree_merchant_id) {
        this.braintree_merchant_id = braintree_merchant_id;
    }

    public String getBraintree_public_key() {
        return braintree_public_key;
    }

    public void setBraintree_public_key(String braintree_public_key) {
        this.braintree_public_key = braintree_public_key;
    }

    public String getBraintree_private_key() {
        return braintree_private_key;
    }

    public void setBraintree_private_key(String braintree_private_key) {
        this.braintree_private_key = braintree_private_key;
    }

    public String getBraintree_merchant_account_id() {
        return braintree_merchant_account_id;
    }

    public void setBraintree_merchant_account_id(String braintree_merchant_account_id) {
        this.braintree_merchant_account_id = braintree_merchant_account_id;
    }
}
