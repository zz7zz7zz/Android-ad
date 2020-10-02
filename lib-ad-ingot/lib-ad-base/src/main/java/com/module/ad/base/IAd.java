package com.module.ad.base;

import android.content.Context;
import android.view.ViewGroup;

public interface IAd {

    void onInit(Context context);

    void onAdPreload(Context context, AdEntity adEntity, IAdListener listener);

    void onAdShow(Context context, AdEntity adEntity, IAdListener listener, ViewGroup adViewParent);

}
