package com.module.ad.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.module.ad.base.AdConfig;

import java.io.InputStream;

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
        adConfig = getAdConfigFromSharePre(context);
        if(null != adConfig){
            return;
        }

        adConfig = getAdConfigFromAssert(context);
        if(null != adConfig){
            return;
        }

        if(null == adConfig){
            adConfig = new AdConfig();
        }
    }

    public void save(Context context,String config){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ad",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("config", config);
        editor.apply();
    }

    private String get(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ad",Context.MODE_PRIVATE);
        return sharedPreferences.getString("config", "");
    }

    private AdConfig getAdConfigFromSharePre(final Context mContext){
        String config = get(mContext);
        if(!TextUtils.isEmpty(config)){
            return AdConfig.parse(config);
        }
        return null;
    }

    private AdConfig getAdConfigFromAssert(final Context mContext){
        AssetManager manager = mContext.getAssets();
        InputStream is = null;
        try {
            is = manager.open("ad_config.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);

            return AdConfig.parse(new String(buffer, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
