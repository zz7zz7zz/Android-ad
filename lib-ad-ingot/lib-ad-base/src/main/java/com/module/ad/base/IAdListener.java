package com.module.ad.base;

import android.content.Context;

public interface IAdListener {

    void onRequest(int adPlaceHolder, int adType, String adUnitId);

    void onResponse(Context context, boolean isSuccess, int adPlaceHolder, int adType, String adUnitId, AdEntity adEntity);

    void onImpression(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId);

    void onClick(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId);

    void onReward(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId);
}
