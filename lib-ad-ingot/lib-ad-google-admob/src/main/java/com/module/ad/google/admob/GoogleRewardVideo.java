package com.module.ad.google.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class GoogleRewardVideo implements IAd {

    private RewardedAd rewardedAd;

    @Override
    public void onInit(Context context) {

    }

    @Override
    public void onAdPreload(final Context context, final AdEntity adEntity, final IAdListener listener) {
        if(null != listener){
            listener.onRequest(adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
        }

        rewardedAd = new RewardedAd(context,adEntity.adProvider.adUnitId);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                // 广告获取成功调用
                adEntity.ad = GoogleRewardVideo.this;
                if(null != listener){
                    listener.onResponse(context,true, adEntity.scenario, adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,adEntity);
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
                if(null != listener){
                    listener.onResponse(context,false,adEntity.scenario,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId,null);
                }
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    @Override
    public void onAdShow(final Context context, final AdEntity adEntity, final IAdListener listener, ViewGroup adViewParent) {
        if (null != rewardedAd && rewardedAd.isLoaded()) {
            Activity activityContext = (Activity) context;
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.

                    if(null != listener){
                        listener.onImpression(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                    }
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.

                    if(null != listener){
                        listener.onReward(adEntity.showAdPlaceHolder,adEntity.adPlaceHolder,adEntity.adProvider.adType,adEntity.adProvider.adUnitId);
                    }
                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    // Ad failed to display.
                }
            };
            rewardedAd.show(activityContext, adCallback);
        } else {
            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
        }
    }
}
