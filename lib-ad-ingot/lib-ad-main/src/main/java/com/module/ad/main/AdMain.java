package com.module.ad.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private IAdListener listener;

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
    public void init(Application application){
        AdConfigMgr.getInstance().restore(application);
        setActivityLifecycleCallbacks(application);
    }

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

    //----------------------- 1.加载 -----------------------
    public void preLoad(Context context,int adPlaceHolder,String scenario){
        preLoad(context,adPlaceHolder,null,scenario);
    }

    public void preLoad(Context context, int adPlaceHolder, String adProviderName, String scenario){
        AdConfig adConfig = AdConfigMgr.getInstance().adConfig;
        AdConfig.AdProvider adProvider = adConfig.getRequestAdProvider(adPlaceHolder,adProviderName);
        if(null == adProvider){
            return;
        }

        IAd ad = adMap.get(adProvider.adProviderName);
        if(null == ad){
            return;
        }

        ad.onAdPreload(context,new AdEntity(scenario,adPlaceHolder,adProvider), proxyListener);
    }

    //----------------------- 2.展示 -----------------------
    public void show(Context context,int adPlaceHolder,ViewGroup adViewParent){
        show(context,adPlaceHolder,null,adViewParent);
    }

    public void show(final Context context, final int adPlaceHolder, final String adProviderName, ViewGroup adViewParent){

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
            //如果想展示Banner/Native又没有父布局，则通过对话框模拟插屏进行展示
            if(context instanceof Activity && null == adViewParent && (adEntity.adProvider.adType == 1 || adEntity.adProvider.adType == 2)){
                final AdEntity finalAdEntity = adEntity;
                AdDialog.show((Activity) context, new AdDialog.IAdDialogListener() {
                    @Override
                    public void onShow(ViewGroup ...adViewParent) {
                        finalAdEntity.ad.onAdShow(context, finalAdEntity, proxyListener,adViewParent[0]);

                        //以下逻辑可以展示多个广告
                        //show(context,adPlaceHolder,adProviderName,adViewParent[1]);
                    }
                });
            }else{
                adEntity.ad.onAdShow(context,adEntity, proxyListener,adViewParent);
            }
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

    //----------------------- 3.判断是否存在 -----------------------
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

    //----------------------- 4.设置监听器 -----------------------
    public void setListener(IAdListener listener) {
        this.listener = listener;
    }

    //----------------------- 5.设置监听器 -----------------------
    public void setActivityLifecycleCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                AdPreloadEvent.onActivityCreated(activity.getApplicationContext(),activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                AdPreloadEvent.onActivityResumed(activity.getApplicationContext(),activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                AdPreloadEvent.onActivityPaused(activity.getApplicationContext(),activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                AdPreloadEvent.onActivityStopped(activity.getApplicationContext(),activity.getClass().getSimpleName());
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                AdPreloadEvent.onActivityDestroyed(activity.getApplicationContext(),activity.getClass().getSimpleName());
            }
        });
    }

    //----------------------- 内部广告监听器 -----------------------
    private IAdListener proxyListener = new IAdListener(){
        @Override
        public void onRequest(String scenario, int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onRequest scenario=%s adPlaceHolder=%d adType=%d adUnitId=%s", scenario,adPlaceHolder,adType,adUnitId));

            if(null != listener){
                listener.onRequest(scenario,adPlaceHolder,adType,adUnitId);
            }
        }

        @Override
        public void onResponse(Context context, boolean isSuccess, String scenario, int adPlaceHolder, int adType, String adUnitId, AdEntity adEntity) {
            Log.v(TAG,String.format("onResponse isSuccess=%b scenario=%s adPlaceHolder=%d adType=%d adUnitId=%s",isSuccess, scenario,adPlaceHolder,adType,adUnitId));

            if(isSuccess){
                ArrayList<AdEntity> ret = adObjectMap.get(adPlaceHolder);
                if(null == ret){
                    ret = new ArrayList<>();
                    adObjectMap.put(adPlaceHolder,ret);
                }
                ret.add(adEntity);
            }else{

                int index = AdConfigMgr.getInstance().adConfig.getRequestIndex(adPlaceHolder);
                int size = AdConfigMgr.getInstance().adConfig.getAdProviderSize(adPlaceHolder);
                if(index < size-1){
                    //转到下一个广告商
                    AdConfigMgr.getInstance().adConfig.setRequestIndex(adPlaceHolder,index+1);
                    preLoad(context,adPlaceHolder, scenario);
                    return;
                }else{
                    AdConfigMgr.getInstance().adConfig.setRequestIndex(adPlaceHolder,0);
                }
            }

            if(null != listener){
                listener.onResponse(context,isSuccess, scenario,adPlaceHolder,adType,adUnitId,adEntity);
            }
        }

        @Override
        public void onImpression(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onImpression showAdPlaceHolder=%d adPlaceHolder=%d adType=%d adUnitId=%s",showAdPlaceHolder,adPlaceHolder,adType,adUnitId));

            if(null != listener){
                listener.onImpression(showAdPlaceHolder,adPlaceHolder,adType,adUnitId);
            }
        }

        @Override
        public void onClick(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onClick showAdPlaceHolder=%d adPlaceHolder=%d adType=%d adUnitId=%s",showAdPlaceHolder,adPlaceHolder,adType,adUnitId));

            if(null != listener){
                listener.onClick(showAdPlaceHolder,adPlaceHolder,adType,adUnitId);
            }
        }

        @Override
        public void onReward(int showAdPlaceHolder, int adPlaceHolder, int adType, String adUnitId) {
            Log.v(TAG,String.format("onReward showAdPlaceHolder=%d adPlaceHolder=%d adType=%d adUnitId=%s",showAdPlaceHolder,adPlaceHolder,adType,adUnitId));

            if(null != listener){
                listener.onReward(showAdPlaceHolder,adPlaceHolder,adType,adUnitId);
            }
        }
    };

}
