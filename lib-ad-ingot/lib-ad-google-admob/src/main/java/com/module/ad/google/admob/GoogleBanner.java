package com.module.ad.google.admob;


import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

class GoogleBanner implements IAd  , LifecycleObserver {

    private AdView adView;

    @Override
    public void onInit(Context context) {

    }

    @Override
    public void onAdPreload(final Context context, final AdEntity adEntity, final IAdListener listener) {
        if(null != listener){
            listener.onRequest(adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        adView = new AdView(context);
        adView.setAdSize(new AdSize(adEntity.adProvider.banner_size_width, adEntity.adProvider.banner_size_height));
        adView.setAdUnitId(adEntity.adProvider.adUnitId);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

                // 广告获取成功调用
                adEntity.ad = GoogleBanner.this;
                if(null != listener){
                    listener.onResponse(context,true, adEntity.scenario, adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                if(null != listener){
                    listener.onResponse(context,false,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,null);
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                if(null != listener){
                    listener.onClick(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                }
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent) {
        if(null != adView){
            adViewParent.removeView(adView);
            adViewParent.addView(adView);

            if(null != listener){
                listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
            }
        }
    }

    //------------------------------------------------------------------------
    public void attach(Context mContext){
        if(mContext instanceof FragmentActivity){
            ((FragmentActivity)(mContext)).getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){
        if (adView != null) {
            adView.resume();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){
        if (adView != null) {
            adView.pause();
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        if (adView != null) {
            adView.destroy();
        }
    }
}
