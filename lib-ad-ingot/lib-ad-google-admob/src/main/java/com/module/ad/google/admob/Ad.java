package com.module.ad.google.admob;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.module.ad.base.AdEntity;
import com.module.ad.base.AdType;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

import java.util.Map;

public class Ad implements IAd {

    @Override
    public void onInit(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                if(null == initializationStatus){
                    return;
                }

                Map<String, AdapterStatus> retStatusMap =  initializationStatus.getAdapterStatusMap();
                if(null != retStatusMap){
                    for (Map.Entry<String, AdapterStatus> entry : retStatusMap.entrySet()) {
                        Log.v("GoogleAd"," key " + entry.getKey() +
                                " value state " + entry.getValue().getInitializationState()+
                                " desc "+entry.getValue().getDescription() +
                                " latency "+entry.getValue().getLatency() );
                    }
                }
            }
        });
    }

    @Override
    public void onAdPreload(Context context, AdEntity adEntity, IAdListener listener) {
        Log.v("Ad","Google onAdPreload()");
        if(adEntity.adProvider.adType == AdType.TYPE_BANNER){
            new GoogleBanner().onAdPreload(context,adEntity,listener);
        }else if(adEntity.adProvider.adType == AdType.TYPE_NATIVE){
            new GoogleNative().onAdPreload(context,adEntity,listener);
        }else if(adEntity.adProvider.adType == AdType.TYPE_INTERSTITIAL){
            new GoogleInterstitial().onAdPreload(context,adEntity,listener);
        }else if(adEntity.adProvider.adType == AdType.TYPE_REWAED){
            new GoogleRewardVideo().onAdPreload(context,adEntity,listener);
        }
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent){
        Log.v("Ad","Google onAdShow()");
    }
}
