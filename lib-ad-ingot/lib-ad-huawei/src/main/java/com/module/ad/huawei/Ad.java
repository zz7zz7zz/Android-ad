package com.module.ad.huawei;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.huawei.hms.ads.HwAds;
import com.module.ad.base.AdEntity;
import com.module.ad.base.AdType;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class Ad implements IAd {

    @Override
    public void onInit(Context context) {
        HwAds.init(context);
    }

    @Override
    public void onAdPreload(Context context, AdEntity adEntity, IAdListener listener) {
        Log.v("Ad","Huawei onAdPreload()");
        if(adEntity.adProvider.adType == AdType.TYPE_BANNER){
            new HwBanner().onAdPreload(context,adEntity,listener);
        }else if(adEntity.adProvider.adType == AdType.TYPE_NATIVE){
            new HwNative().onAdPreload(context,adEntity,listener);
        }else if(adEntity.adProvider.adType == AdType.TYPE_INTERSTITIAL){
            new HwInterstitial().onAdPreload(context,adEntity,listener);
        }else if(adEntity.adProvider.adType == AdType.TYPE_REWAED){
            new HwRewardVideo().onAdPreload(context,adEntity,listener);
        }
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent) {
        Log.v("Ad","Huawei onAdShow()");
    }
}
