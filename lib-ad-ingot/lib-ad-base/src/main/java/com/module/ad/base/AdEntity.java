package com.module.ad.base;

public class AdEntity {

    //request 广告位
    public int adPlaceHolder;

    //request 广告商
    public AdConfig.AdProvider adProvider;

    //response
    public IAd ad;

    //show 广告位
    public int showAdPlaceHolder;

    public AdEntity(int adPlaceHolder, AdConfig.AdProvider adProvider) {
        this.adPlaceHolder = adPlaceHolder;
        this.adProvider = adProvider;
    }
}
