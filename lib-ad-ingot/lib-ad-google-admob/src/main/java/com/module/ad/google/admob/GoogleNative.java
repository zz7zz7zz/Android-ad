package com.module.ad.google.admob;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class GoogleNative implements IAd , LifecycleObserver {

    private UnifiedNativeAd mUnifiedNativeAd;

    @Override
    public void onInit(Context context) {

    }

    @Override
    public void onAdPreload(final Context context, final AdEntity adEntity, final IAdListener listener) {
        if(null != listener){
            listener.onRequest(adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        AdLoader adLoader = new AdLoader.Builder(context, adEntity.adProvider.adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.

                        mUnifiedNativeAd = unifiedNativeAd;
                        adEntity.ad = GoogleNative.this;
                        adEntity.ad_resp_time_millis = System.currentTimeMillis();
                        adEntity.ad_ttl = adEntity.adProvider.adTtl;
                        if(null != listener){
                            listener.onResponse(context,true,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                        }
                    }
                })
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                        if(null != listener){
                            listener.onResponse(context,false,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }

                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        if(null != listener){
                            listener.onClick(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
            adLoader.loadAd(new AdRequest.Builder().build());

    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent) {
        if(null != listener){
            listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        //方法一：使用谷歌提供的模板
        final ViewGroup nativeView = (ViewGroup) LayoutInflater.from(context).inflate(adEntity.showFromDialog ? R.layout.google_native_template_medium : R.layout.google_native_template_small, null);
        NativeTemplateStyle styles = new
                NativeTemplateStyle.Builder().withMainBackgroundColor(new ColorDrawable(Color.WHITE)).build();

        TemplateView template = nativeView.findViewById(R.id.my_template);
        template.setStyles(styles);
        template.setNativeAd(mUnifiedNativeAd);

        // Add NativeView to the app UI.
        adViewParent.removeAllViews();
        adViewParent.addView(nativeView);

        attach(context);

        //方法二：使用自定义的模板
    }

    //------------------------------------------------------------------------
    public void attach(Context mContext){
        if(mContext instanceof FragmentActivity){
            ((FragmentActivity)(mContext)).getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        if(null != mUnifiedNativeAd){
            mUnifiedNativeAd.destroy();
        }
    }
}
