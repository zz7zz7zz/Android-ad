package com.module.ad.main;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.module.ad.base.AdConfig;
import com.module.ad.base.AdEntity;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
    广告统一入口
 */
public class AdMain {

    private static String TAG = "AdMain";

    //------------------------------------------------------------------------------------
    private static AdMain INS;
    private HashMap<String, IAd> adMap = new HashMap<>();
    private HashMap<Integer,ArrayList<AdEntity>> adObjectMap = new HashMap();

    private AdMain() {
        init("google.admob");
        init("huawei");
    }

    public static AdMain getInstance() {
        if (null == INS) {
            synchronized (AdMain.class) {
                // when more than two threads run into the first null check same time, to avoid instanced more than one
                // time, it needs to be checked again.
                if (INS == null) {
                    INS = new AdMain();
                }
            }
        }
        return INS;
    }

    public static void destroy() {
        if(null != INS){
            INS = null;
        }
    }


    //------------------------------------------------------------------------------------
    private IAd init(String adProvider){
        IAd ret = adMap.get(adProvider);
        if(null == ret){
            try {
                Class<?> mClass=Class.forName(String.format("com.module.ad.%s.Ad",adProvider));
                ret= (IAd) mClass.newInstance();
                if(null != ret){
                    adMap.put(adProvider,ret);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private IAd get(String adProvider){
        IAd ret = adMap.get(adProvider);
        return ret;
    }

    //----------------------- 加载 -----------------------
    public void preLoad(Context context,int adPlaceHolder){
        preLoad(context,adPlaceHolder,null);
    }

    public void preLoad(Context context, int adPlaceHolder, String adProviderName){
        AdConfig adConfig = AdConfigMgr.getInstance().adConfig;
        AdConfig.AdProvider adProvider = adConfig.getRequestAdProvider(adPlaceHolder,adProviderName);
        if(null == adProvider){
            return;
        }

        IAd ad = adMap.get(adProvider.adProviderName);
        if(null == ad){
            return;
        }

        ad.onAdPreload(context,new AdEntity(adPlaceHolder,adProvider),listener);
    }

    //----------------------- 展示 -----------------------
    public void show(Context context,int adPlaceHolder,ViewGroup adViewParent){
        show(context,adPlaceHolder,null,adViewParent);
    }

    public void show(Context context, int adPlaceHolder, String adProviderName, ViewGroup adViewParent){

        AdEntity adEntity = getShowAdEntity(adPlaceHolder,adProviderName);

        if(null == adEntity){
            AdConfig adConfig = AdConfigMgr.getInstance().adConfig;
            AdConfig.AdPlaceHolderConfig adPlaceHolderConfig = adConfig.getAdPlaceHolderConfig(adPlaceHolder);
            if(null != adPlaceHolderConfig && null != adPlaceHolderConfig.adReuseList){
                for (int i = 0;i<adPlaceHolderConfig.adReuseList.size();i++){
                    adEntity = getShowAdEntity(adPlaceHolderConfig.adReuseList.get(i).adPlaceHolder,adPlaceHolderConfig.adReuseList.get(i).adProviderName);
                    if(null != adEntity){
                        break;
                    }
                }
            }
        }

        if(null != adEntity){
            adEntity.showAdPlaceHolder = adPlaceHolder;
            adEntity.ad.onAdShow(context,adEntity,listener,adViewParent);
        }
    }

    private AdEntity getShowAdEntity(int adPlaceHolder, String adProviderName){
        ArrayList<AdEntity> ret = adObjectMap.get(adPlaceHolder);
        if(null != ret){
            for (int i = 0;i<ret.size();i++){
                if(!TextUtils.isEmpty(adProviderName)){
                    if(adProviderName.equals(ret.get(i).adProvider.adProviderName)){
                        return ret.remove(i);
                    }
                }else{
                    return ret.remove(i);
                }
            }
        }
        return null;
    }

    //----------------------- 判断是否存在 -----------------------
    public boolean isExist(int adPlaceHolder){
        return isExist(adPlaceHolder,null);
    }


    public boolean isExist(int adPlaceHolder,String adProviderName){
        ArrayList<AdEntity> ret = adObjectMap.get(adPlaceHolder);
        if(null != ret){
            for (int i = 0;i<ret.size();i++){
                if(!TextUtils.isEmpty(adProviderName)){
                    if(adProviderName.equals(ret.get(i).adProvider.adProviderName)){
                        return true;
                    }
                }else{
                    return true;
                }
            }
        }

        return false;
    }

    //----------------------- 广告监听器 -----------------------
    private IAdListener listener = new IAdListener(){
        @Override
        public void onRequest(int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onRequest adPlaceHolder=%d adType=%d adUnitId=%s",adPlaceHolder,adType,adUnitId));
        }

        @Override
        public void onResponse(boolean isSuccess, int adPlaceHolder, int adType, String adUnitId,AdEntity adEntity) {
            Log.v(TAG,String.format("onResponse isSuccess=%b adPlaceHolder=%d adType=%d adUnitId=%s",isSuccess,adPlaceHolder,adType,adUnitId));
            if(!isSuccess){
                return;
            }

            ArrayList<AdEntity> ret = adObjectMap.get(adPlaceHolder);
            if(null == ret){
                ret = new ArrayList<>();
                adObjectMap.put(adPlaceHolder,ret);
            }
            ret.add(adEntity);
        }

        @Override
        public void onImpression(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onImpression showAdPlaceHolder=%d adPlaceHolder=%d adType=%d adUnitId=%s",showAdPlaceHolder,adPlaceHolder,adType,adUnitId));
        }

        @Override
        public void onClick(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onClick showAdPlaceHolder=%d adPlaceHolder=%d adType=%d adUnitId=%s",showAdPlaceHolder,adPlaceHolder,adType,adUnitId));
        }

        @Override
        public void onReward(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onReward showAdPlaceHolder=%d adPlaceHolder=%d adType=%d adUnitId=%s",showAdPlaceHolder,adPlaceHolder,adType,adUnitId));
        }
    };

}
