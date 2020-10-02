package com.module.ad.base;

public class AdEntity {

    //request
    public int adPlaceHolder;
    public int adType;
    public String adUnitId;

    public int banner_size_width;
    public int banner_size_Height;
    public int banner_refresh_freq;

    //response
    public IAd ad;

    public AdEntity(int adPlaceHolder, int adType, String adUnitId, int banner_size_width, int banner_size_Height, int banner_refresh_freq) {
        this.adPlaceHolder = adPlaceHolder;
        this.adType = adType;
        this.adUnitId = adUnitId;
        this.banner_size_width = banner_size_width;
        this.banner_size_Height = banner_size_Height;
        this.banner_refresh_freq = banner_refresh_freq;
    }
}
