package com.module.ad.base;

public interface IAd {

    void onAdPreload(int adUnit,int adType);

    void onAdShow(int adUnit,int adType);

}
