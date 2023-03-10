package com.example.singlehotel.util;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import com.example.singlehotel.activity.SplashScreen;
import com.example.singlehotel.R;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class YouApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(YouApplication.this, initializationStatus -> {

        });

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("9287c58a-6dba-442a-80c7-37aa41b89ef0");
        OneSignal.setNotificationOpenedHandler(new NotificationExtenderExample());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/poppins_medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

    class NotificationExtenderExample implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {

            try {

                String id = result.getNotification().getAdditionalData().getString("id");
                String roomTitle = result.getNotification().getAdditionalData().getString("room_title");
                String url = result.getNotification().getAdditionalData().getString("external_link");

                Intent intent;
                if (id.equals("0") && !url.equals("false") && !url.trim().isEmpty()) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                } else {
                    intent = new Intent(YouApplication.this, SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    intent.putExtra("room_title", roomTitle);
                }
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
