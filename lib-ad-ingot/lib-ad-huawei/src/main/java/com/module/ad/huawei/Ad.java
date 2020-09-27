package com.module.ad.huawei;

import android.util.Log;

import com.module.ad.base.IAd;

public class Ad implements IAd {
    @Override
    public void onAdPreload(int adUnit, int adType) {
        Log.v("Ad","Huawei onAdPreload()");
    }

    @Override
    public void onAdShow(int adUnit, int adType) {
        Log.v("Ad","Huawei onAdShow()");
    }
}
