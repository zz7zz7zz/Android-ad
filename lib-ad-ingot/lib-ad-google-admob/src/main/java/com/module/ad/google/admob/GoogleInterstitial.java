package com.module.ad.google.admob;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class GoogleInterstitial implements IAd {

    private InterstitialAd mInterstitialAd;

    @Override
    public void onInit(Context context) {

    }

    @Override
    public void onAdPreload(final Context context, final AdEntity adEntity, final IAdListener listener) {
        if(null != listener){
            listener.onRequest(adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(adEntity.adProvider.adUnitId);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                // 广告获取成功调用
                adEntity.ad = GoogleInterstitial.this;
                adEntity.ad_resp_time_millis = System.currentTimeMillis();
                adEntity.ad_ttl = adEntity.adProvider.adTtl;
                if(null != listener){
                    listener.onResponse(context,true, adEntity.scenario, adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                if(null != listener){
                    listener.onResponse(context,false,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.

                if(null != listener){
                    listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                }
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
                // Code to be executed when the interstitial ad is closed.
            }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent) {
        if (null != mInterstitialAd && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

//            if(null != listener){
//                listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
//            }
        }
    }
}
