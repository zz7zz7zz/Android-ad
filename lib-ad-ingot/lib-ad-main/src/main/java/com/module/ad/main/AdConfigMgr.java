package com.module.ad.main;

import android.content.Context;

public class AdConfigMgr {

    public AdConfig adConfig;
    private static AdConfigMgr INS;

    private AdConfigMgr() {
    }

    public static AdConfigMgr getInstance() {
        if (null == INS) {
            synchronized (AdConfigMgr.class) {
                // when more than two threads run into the first null check same time, to avoid instanced more than one
                // time, it needs to be checked again.
                if (INS == null) {
                    INS = new AdConfigMgr();
                }
            }
        }
        return INS;
    }

    //------------------------------------------------------------------------------------
    public void restore(Context context){
        adConfig = AdConfig.getAdConfigFromAssert(context);
        if(null == adConfig){
            adConfig = new AdConfig();
        }
    }

}
