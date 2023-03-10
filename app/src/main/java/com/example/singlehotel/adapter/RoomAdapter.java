package com.example.singlehotel.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.example.singlehotel.R;
import com.example.singlehotel.interfaces.OnClick;
import com.example.singlehotel.item.RoomList;
import com.example.singlehotel.util.Constant;
import com.example.singlehotel.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.textview.MaterialTextView;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.wortise.ads.natives.GoogleNativeAd;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private int columnWidth;
    private List<RoomList> roomLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_ADS = -1;

    public RoomAdapter(Activity activity, List<RoomList> roomLists, String type, OnClick onClick) {
        this.activity = activity;
        this.roomLists = roomLists;
        this.type = type;
        method = new Method(activity, onClick);
        columnWidth = method.getScreenWidth();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.room_adapter, parent, false);
            return new RoomAdapter.ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType == VIEW_TYPE_ADS) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_ads, parent, false);
            return new AdOption(v);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.imageViewRoom.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));
            viewHolder.view.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));

            Glide.with(activity).load(roomLists.get(position).getRoom_image_thumb())
                    .placeholder(R.drawable.placeholder_landscape)
                    .into(viewHolder.imageViewRoom);

            viewHolder.textViewRoomName.setText(roomLists.get(position).getRoom_name());
            viewHolder.textViewPrice.setText(activity.getString(R.string.currency,Constant.appRP.getCurrency_code(),Method.convertDec(roomLists.get(position).getRoom_price())));
            viewHolder.textViewTotalRate.setText("(" + roomLists.get(position).getTotal_rate() + ")");
            viewHolder.ratingBar.setRating(Float.parseFloat(roomLists.get(position).getRate_avg()));

            viewHolder.textViewTotalRate.setTypeface(null);

            viewHolder.constraintLayout.setOnClickListener(v -> method.onClickAd(position, type, roomLists.get(position).getId(), roomLists.get(position).getRoom_name()));

        } else if (holder.getItemViewType() == VIEW_TYPE_ADS) {
            AdOption adOption = (AdOption) holder;
            if (adOption.rl_native_ad.getChildCount() == 0 && Constant.appRP.isNative_ad() && !adOption.isAdRequested) {

                adOption.isAdRequested = true;

                switch (Constant.appRP.getNative_ad_type()) {
                    case Constant.AD_TYPE_ADMOB:
                    case Constant.AD_TYPE_FACEBOOK:

                        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);

                        AdLoader adLoader = new AdLoader.Builder(activity, Constant.appRP.getNative_ad_id())
                                .forNativeAd(nativeAd -> {
                                    populateUnifiedNativeAdView(nativeAd, adView);
                                    adOption.rl_native_ad.removeAllViews();
                                    adOption.rl_native_ad.addView(adView);
                                    adOption.card_view.setVisibility(View.VISIBLE);
                                })
                                .build();

                        AdRequest.Builder builder = new AdRequest.Builder();
                        if (Method.personalizationAd) {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                        }
                        adLoader.loadAd(builder.build());

                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        StartAppNativeAd nativeAd = new StartAppNativeAd(activity);

                        nativeAd.loadAd(new NativeAdPreferences()
                                .setAdsNumber(1)
                                .setAutoBitmapDownload(true)
                                .setPrimaryImageSize(2), new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                try {
                                    if (nativeAd.getNativeAds().size() > 0) {
                                        RelativeLayout nativeAdView = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);

                                        ImageView icon = nativeAdView.findViewById(R.id.icon);
                                        TextView title = nativeAdView.findViewById(R.id.title);
                                        TextView description = nativeAdView.findViewById(R.id.description);
                                        Button button = nativeAdView.findViewById(R.id.button);

                                        Glide.with(activity)
                                                .load(nativeAd.getNativeAds().get(0).getImageUrl())
                                                .into(icon);
                                        title.setText(nativeAd.getNativeAds().get(0).getTitle());
                                        description.setText(nativeAd.getNativeAds().get(0).getDescription());
                                        button.setText(nativeAd.getNativeAds().get(0).isApp() ? "Install" : "Open");

                                        adOption.rl_native_ad.removeAllViews();
                                        adOption.rl_native_ad.addView(nativeAdView);
                                        adOption.card_view.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailedToReceiveAd(Ad ad) {
                                adOption.isAdRequested = false;
                            }
                        });
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.appRP.getNative_ad_id(), activity);
                        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                            @Override
                            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                nativeAdView.setPadding(0, 0, 0, 10);
                                nativeAdView.setBackgroundColor(Color.WHITE);
                                adOption.rl_native_ad.removeAllViews();
                                adOption.rl_native_ad.addView(nativeAdView);
                                adOption.card_view.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                adOption.isAdRequested = false;
                            }

                            @Override
                            public void onNativeAdClicked(final MaxAd ad) {
                            }
                        });

                        nativeAdLoader.loadAd();
                        break;
                    case Constant.AD_TYPE_WORTISE:
                        GoogleNativeAd googleNativeAd = new GoogleNativeAd(activity, Constant.appRP.getNative_ad_id(), new GoogleNativeAd.Listener() {
                            @Override
                            public void onNativeClicked(@NonNull GoogleNativeAd googleNativeAd) {

                            }

                            @Override
                            public void onNativeFailed(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.wortise.ads.AdError adError) {
                                adOption.isAdRequested = false;
                            }

                            @Override
                            public void onNativeImpression(@NonNull GoogleNativeAd googleNativeAd) {

                            }

                            @Override
                            public void onNativeLoaded(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                                NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_wortise, null);
                                populateUnifiedNativeAdView(nativeAd, adView);
                                adOption.rl_native_ad.removeAllViews();
                                adOption.rl_native_ad.addView(adView);
                                adOption.card_view.setVisibility(View.VISIBLE);
                            }
                        });
                        googleNativeAd.load();
                        break;
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return roomLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == roomLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (roomLists.size() == position) {
            return VIEW_TYPE_LOADING;
        } else if (roomLists.get(position) == null) {
            return VIEW_TYPE_ADS;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private RatingView ratingBar;
        private ImageView imageViewRoom;
        private ConstraintLayout constraintLayout;
        private MaterialTextView textViewRoomName, textViewPrice, textViewTotalRate;

        public ViewHolder(View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.con_room_adapter);
            imageViewRoom = itemView.findViewById(R.id.imageView_room_adapter);
            view = itemView.findViewById(R.id.view_room_adapter);
            textViewRoomName = itemView.findViewById(R.id.textView_roomName_room_adapter);
            textViewPrice = itemView.findViewById(R.id.textView_roomPrice_room_adapter);
            textViewTotalRate = itemView.findViewById(R.id.textView_totalRate_room_adapter);
            ratingBar = itemView.findViewById(R.id.ratingBar_room_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

    public class AdOption extends RecyclerView.ViewHolder {

        private CardView card_view;
        private final RelativeLayout rl_native_ad;
        private boolean isAdRequested;

        public AdOption(View itemView) {
            super(itemView);
            card_view = itemView.findViewById(R.id.card_view);
            rl_native_ad = itemView.findViewById(R.id.rl_native_ad);
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }
}
