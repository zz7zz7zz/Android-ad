package com.module.ad.huawei;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class HwRewardVideo implements IAd {

    private RewardAd rewardAd;

    @Override
    public void onInit(Context context) {
        HwAds.init(context);
    }

    @Override
    public void onAdPreload(final Context context, final AdEntity adEntity, final IAdListener listener){
        if(null != listener){
            listener.onRequest(adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        rewardAd = new RewardAd(context, adEntity.adProvider.adUnitId);
        RewardAdLoadListener _listener= new RewardAdLoadListener() {
            @Override
            public void onRewardedLoaded() {
                // 激励广告加载成功
                adEntity.ad = HwRewardVideo.this;
                if(null != listener){
                    listener.onResponse(context,true,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                }
            }
            @Override
            public void onRewardAdFailedToLoad(int errorCode) {
                // 激励广告加载失败
                if(null != listener){
                    listener.onResponse(context,false,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,null);
                }
            }
        };
        rewardAd.loadAd(new AdParam.Builder().build(), _listener);
    }

    @Override
    public void onAdShow(Context context, final AdEntity adEntity,final IAdListener listener, ViewGroup adViewParent) {
        if (context instanceof Activity && null != rewardAd && rewardAd.isLoaded()) {
            rewardAd.show((Activity) context, new RewardAdStatusListener() {
                @Override
                public void onRewardAdOpened() {
                    // 激励广告被打开
                }
                @Override
                public void onRewardAdFailedToShow(int errorCode) {
                    // 激励广告展示失败
                }
                @Override
                public void onRewardAdClosed() {
                    // 激励广告被关闭
                }
                @Override
                public void onRewarded(Reward reward){
                    // 激励广告奖励达成
                    // TODO 发放奖励
                    if(null != listener){
                        listener.onReward(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                    }
                }
            });

            if(null != listener){
                listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
            }
        }
    }
}
