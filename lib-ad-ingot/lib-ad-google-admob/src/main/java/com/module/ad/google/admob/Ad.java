package com.module.ad.google.admob;

import android.util.Log;

import com.module.ad.base.IAd;

public class Ad implements IAd {

    @Override
    public void onAdPreload(int adUnit, int adType) {
        Log.v("Ad","Google onAdPreload()");
    }

    @Override
    public void onAdShow(int adUnit, int adType) {
        Log.v("Ad","Google onAdShow()");
    }
}
