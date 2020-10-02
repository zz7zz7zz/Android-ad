package com.module.ad.google.admob;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

public class Ad implements IAd {

    @Override
    public void onInit(Context context) {

    }

    @Override
    public void onAdPreload(Context context, AdEntity adEntity, IAdListener listener) {
        Log.v("Ad","Google onAdPreload()");
    }

    @Override
    public void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent){
        Log.v("Ad","Google onAdShow()");
    }
}
