package com.module.ad.huawei;

import android.content.Context;
import android.view.ViewGroup;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class HwBanner implements IAd {

    private BannerView bannerView;

    @Override
    public void onInit(Context context) {
        HwAds.init(context);
    }

    public void onAdPreload(final Context context, final AdEntity adEntity, final IAdListener listener){
        if(null != listener){
            listener.onRequest(adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        bannerView = new BannerView(context);
        bannerView.setAdId(adEntity.adProvider.adUnitId);
        bannerView.setBannerAdSize(new BannerAdSize(adEntity.adProvider.banner_size_width, adEntity.adProvider.banner_size_height));
        bannerView.setBannerRefresh(adEntity.adProvider.banner_refresh_freq);
        bannerView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // 广告获取成功调用
                adEntity.ad = HwBanner.this;
                if(null != listener){
                    listener.onResponse(context,true, adEntity.scenario, adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                }
            }
            @Override
            public void onAdFailed(int errorCode) {
                // 广告获取失败时调用
                if(null != listener){
                    listener.onResponse(context,false,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,null);
                }
            }
            @Override
            public void onAdOpened() {
                // 广告打开时调用
            }
            @Override
            public void onAdClicked() {
                // 广告点击时调用
                if(null != listener){
                    listener.onClick(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                }
            }
            @Override
            public void onAdLeave() {
                // 广告离开应用时调用
            }
            @Override
            public void onAdClosed() {
                // 广告关闭时调用
            }
        });

        // 创建广告请求，获取广告
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent) {
        if(null != bannerView){
            adViewParent.removeView(bannerView);
            adViewParent.addView(bannerView);

            if(null != listener){
                listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
            }
        }
    }
}
