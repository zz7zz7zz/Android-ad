package com.module.ad.huawei;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.DislikeAdListener;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class HwNative implements IAd , LifecycleObserver {

    private NativeAd globalNativeAd;

    @Override
    public void onInit(Context context) {
        HwAds.init(context);
    }

    @Override
    public void onAdPreload(final Context context, final AdEntity adEntity, final IAdListener listener){
        if(null != listener){
            listener.onRequest(adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        NativeAdLoader.Builder builder = new NativeAdLoader.Builder(context, adEntity.adProvider.adUnitId);

        builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                // Call this method when an ad is successfully loaded.

                globalNativeAd = nativeAd;
                adEntity.ad = HwNative.this;
                if(null != listener){
                    listener.onResponse(context,true,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                }
                // Display native ad.
//                showNativeAd(nativeAd);

            }
        }).setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int errorCode) {
                // Call this method when an ad fails to be loaded.
                if(null != listener){
                    listener.onResponse(context,false,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,null);
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();

                if(null != listener){
                    listener.onClick(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                }
            }
        });

        NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT) // Set custom attributes.
                .build();

        NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();

        nativeAdLoader.loadAd(new AdParam.Builder().build());
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, final ViewGroup adViewParent) {
        if(null != listener){
            listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

//        // Destroy the original native ad.
//        if (null != globalNativeAd) {
//            globalNativeAd.destroy();
//        }
//        globalNativeAd = nativeAd;

        // Obtain NativeView.
        final NativeView nativeView = (NativeView) LayoutInflater.from(context).inflate(R.layout.native_video_template, null);

        // Register and populate a native ad material view.
        initNativeAdView(globalNativeAd, nativeView);
        globalNativeAd.setDislikeAdListener(new DislikeAdListener() {
            @Override
            public void onAdDisliked() {
                // Call this method when an ad is closed.
                adViewParent.removeView(nativeView);
            }
        });

        // Add NativeView to the app UI.
        adViewParent.removeAllViews();
        adViewParent.addView(nativeView);

        attach(context);
    }

    /**
     * Register and populate a native ad material view.
     *
     * @param nativeAd   native ad object that contains ad materials.
     * @param nativeView native ad view to be populated into.
     */
    private void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {
        // Register a native ad material view.
        nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
        nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
        nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
        nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

        // Populate a native ad material view.
        ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }
        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

        // Obtain a video controller.
        VideoOperator videoOperator = nativeAd.getVideoOperator();

        // Check whether a native ad contains video materials.
        if (videoOperator.hasVideo()) {
            // Add a video lifecycle event listener.
            videoOperator.setVideoLifecycleListener(videoLifecycleListener);
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);
    }

    private VideoOperator.VideoLifecycleListener videoLifecycleListener = new VideoOperator.VideoLifecycleListener() {
        @Override
        public void onVideoStart() {
        }

        @Override
        public void onVideoPlay() {
        }

        @Override
        public void onVideoEnd() {
            // If there is a video, load a new native ad only after video playback is complete.
        }
    };

    //------------------------------------------------------------------------
    public void attach(Context mContext){
        if(mContext instanceof FragmentActivity){
            ((FragmentActivity)(mContext)).getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        if(null != globalNativeAd){
            globalNativeAd.destroy();
        }
    }
}
