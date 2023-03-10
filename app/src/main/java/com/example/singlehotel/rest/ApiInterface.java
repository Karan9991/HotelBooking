package com.example.singlehotel.rest;

import com.example.singlehotel.response.AboutUsRP;
import com.example.singlehotel.response.AppRP;
import com.example.singlehotel.response.BookingHisRP;
import com.example.singlehotel.response.BookingRoomRP;
import com.example.singlehotel.response.CheckAvailBookingRoomRP;
import com.example.singlehotel.response.ContactRP;
import com.example.singlehotel.response.DataRP;
import com.example.singlehotel.response.FacilitiesRP;
import com.example.singlehotel.response.FaqRP;
import com.example.singlehotel.response.GalleryCatRP;
import com.example.singlehotel.response.GalleryListRP;
import com.example.singlehotel.response.HomeRP;
import com.example.singlehotel.response.LocationRP;
import com.example.singlehotel.response.LoginRP;
import com.example.singlehotel.response.PaymentCheckOutRP;
import com.example.singlehotel.response.PaypalTokenRP;
import com.example.singlehotel.response.PrivacyPolicyRP;
import com.example.singlehotel.response.ProfileRP;
import com.example.singlehotel.response.RegisterRP;
import com.example.singlehotel.response.ReviewRP;
import com.example.singlehotel.response.RoomDetailRP;
import com.example.singlehotel.response.RoomRP;
import com.example.singlehotel.response.TermsConditionsRP;
import com.example.singlehotel.response.UserReviewRP;
import com.example.singlehotel.response.UserReviewSubmitRP;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiInterface {

    //get app data
    @POST("api.php")
    @FormUrlEncoded
    Call<AppRP> getAppData(@Field("data") String data);

    //get about us
    @POST("api.php")
    @FormUrlEncoded
    Call<AboutUsRP> getAboutUs(@Field("data") String data);

    //get privacy policy
    @POST("api.php")
    @FormUrlEncoded
    Call<PrivacyPolicyRP> getPrivacyPolicy(@Field("data") String data);

    //get terms condition
    @POST("api.php")
    @FormUrlEncoded
    Call<TermsConditionsRP> getTermsCondition(@Field("data") String data);

    //get faq
    @POST("api.php")
    @FormUrlEncoded
    Call<FaqRP> getFaq(@Field("data") String data);

    //login
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLogin(@Field("data") String data);

    //register
    @POST("api.php")
    @FormUrlEncoded
    Call<RegisterRP> getRegister(@Field("data") String data);

    //forgot password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> getForgotPass(@Field("data") String data);

    //login check
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLoginDetail(@Field("data") String data);

    //get profile detail
    @POST("api.php")
    @FormUrlEncoded
    Call<ProfileRP> getProfile(@Field("data") String data);

    //edit profile
    @POST("api.php")
    @Multipart
    Call<DataRP> editProfile(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //update password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> updatePassword(@Field("data") String data);

    //get contact subject
    @POST("api.php")
    @FormUrlEncoded
    Call<ContactRP> getContactSub(@Field("data") String data);

    //submit contact
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitContact(@Field("data") String data);

    //home
    @POST("api.php")
    @FormUrlEncoded
    Call<HomeRP> getHome(@Field("data") String data);

    //room
    @POST("api.php")
    @FormUrlEncoded
    Call<RoomRP> getRoom(@Field("data") String data);

    //get booking history
    @POST("api.php")
    @FormUrlEncoded
    Call<BookingHisRP> getBookHis(@Field("data") String data);

    //room detail
    @POST("api.php")
    @FormUrlEncoded
    Call<RoomDetailRP> getRoomDetail(@Field("data") String data);

    //review
    @POST("api.php")
    @FormUrlEncoded
    Call<ReviewRP> getReview(@Field("data") String data);

    //user review
    @POST("api.php")
    @FormUrlEncoded
    Call<UserReviewRP> getUserReview(@Field("data") String data);

    //submit review
    @POST("api.php")
    @FormUrlEncoded
    Call<UserReviewSubmitRP> submitUserReview(@Field("data") String data);

    //check avail booking
    @POST("api.php")
    @FormUrlEncoded
    Call<CheckAvailBookingRoomRP> roomBooking(@Field("data") String data);

    //booking now
    @POST("api.php")
    @FormUrlEncoded
    Call<BookingRoomRP> roomBooking2(@Field("data") String data);

    //paypal token
    @POST("api.php")
    @FormUrlEncoded
    Call<PaypalTokenRP> paypalToken(@Field("data") String data);

    //paypal check out
    @POST("api.php")
    @FormUrlEncoded
    Call<PaymentCheckOutRP> paypalCheckOut(@Field("data") String data);

    //get gallery
    @POST("api.php")
    @FormUrlEncoded
    Call<GalleryCatRP> getGallery(@Field("data") String data);

    //get gallery detail
    @POST("api.php")
    @FormUrlEncoded
    Call<GalleryListRP> getGalleryList(@Field("data") String data);

    //facilities
    @POST("api.php")
    @FormUrlEncoded
    Call<FacilitiesRP> getFacilities(@Field("data") String data);

    //location
    @POST("api.php")
    @FormUrlEncoded
    Call<LocationRP> getLocation(@Field("data") String data);

}
