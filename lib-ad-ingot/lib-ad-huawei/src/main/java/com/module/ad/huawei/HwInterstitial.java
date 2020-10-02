package com.module.ad.huawei;

import android.content.Context;
import android.view.ViewGroup;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.InterstitialAd;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class HwInterstitial implements IAd {

    private InterstitialAd interstitialAd;

    @Override
    public void onInit(Context context) {
        HwAds.init(context);
    }

    @Override
    public void onAdPreload(Context context, final AdEntity adEntity, final IAdListener listener) {
        if(null != listener){
            listener.onRequest(adEntity.adPlaceHolder,adEntity.adType,adEntity.adUnitId);
        }

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdId(adEntity.adUnitId);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // 广告获取成功调用
                adEntity.ad = HwInterstitial.this;
                if(null != listener){
                    listener.onResponse(true,adEntity.adPlaceHolder,adEntity.adType,adEntity.adUnitId,adEntity);
                }
            }
            @Override
            public void onAdFailed(int errorCode) {
                // 广告获取失败时调用
                if(null != listener){
                    listener.onResponse(false,adEntity.adPlaceHolder,adEntity.adType,adEntity.adUnitId,null);
                }
            }
            @Override
            public void onAdClosed() {
                // 广告关闭时调用
            }
            @Override
            public void onAdClicked() {
                // 广告点击时调用
                if(null != listener){
                    listener.onClick(adEntity.adPlaceHolder,adEntity.adType,adEntity.adUnitId);
                }
            }
            @Override
            public void onAdLeave() {
                //广告离开时调用
            }
            @Override
            public void onAdOpened() {
                // 广告打开时调用
            }
            @Override
            public void onAdImpression() {
                // 广告曝光时调用
//                if(null != listener){
//                    listener.onImpression(adPlaceHolder,adType,adUnitId);
//                }
            }
        });

        AdParam adParam = new AdParam.Builder().build();
        interstitialAd.loadAd(adParam);
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent) {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();

            if(null != listener){
                listener.onImpression(adEntity.adPlaceHolder,adEntity.adType,adEntity.adUnitId);
            }
        }
    }
}
