package com.module.ad.base;

public class AdEntity {

    //request 广告位
    public String scenario;//触发请求广告原因
    public int adPlaceHolder;

    //request 广告商
    public AdConfig.AdProvider adProvider;

    //response
    public IAd ad;

    //show 广告位
    public int showAdPlaceHolder;

    public AdEntity(String scenario, int adPlaceHolder, AdConfig.AdProvider adProvider) {
        this.scenario = scenario;
        this.adPlaceHolder = adPlaceHolder;
        this.adProvider = adProvider;
    }
}
