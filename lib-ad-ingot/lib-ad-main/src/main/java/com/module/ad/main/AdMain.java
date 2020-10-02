package com.module.ad.main;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

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
    public void preLoad(int adPlaceHolder){

    }

    public void preLoad(Context context, int adPlaceHolder, String adProvider){
        IAd ad = adMap.get(adProvider);
        if(null != ad){
            //ad.onAdPreload(adPlaceHolder,-1);

            ad.onAdPreload(context,new AdEntity(adPlaceHolder,2,"testy63txaom86",360,144,10),listener);
        }
    }

    //----------------------- 展示 -----------------------
    public void show(int adPlaceHolder){

    }

    public void show(Context context, int adPlaceHolder, String adProvider, ViewGroup adViewParent){
//        IAd ad = adMap.get(adProvider);
//        if(null != ad){
//           // ad.onAdShow(adPlaceHolder,-1);
//            ad.onAdShow(context,adPlaceHolder,listener,1,"testw6vs28auh3",adViewParent);
//        }

        ArrayList<AdEntity> ret = adObjectMap.get(adPlaceHolder);
        if(null != ret && ret.size() > 0){
            AdEntity adEntity = ret.remove(0);
            adEntity.ad.onAdShow(context,adEntity,listener,adViewParent);
        }
    }

    //----------------------- 判断是否存在 -----------------------
    public void isExist(int adPlaceHolder){

    }


    public void isExist(int adPlaceHolder,String adProvider){

    }

    //----------------------- 广告监听器 -----------------------
    private IAdListener listener = new IAdListener(){
        @Override
        public void onRequest(int adPlaceHolder, int adType, String adUnitId) {
            Log.v("Testing",String.format("onRequest adPlaceHolder=%d adType=%d adUnitId=%s",adPlaceHolder,adType,adUnitId));
        }

        @Override
        public void onResponse(boolean isSuccess, int adPlaceHolder, int adType, String adUnitId,AdEntity adEntity) {
            Log.v("Testing",String.format("onResponse isSuccess=%b adPlaceHolder=%d adType=%d adUnitId=%s",isSuccess,adPlaceHolder,adType,adUnitId));
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
        public void onImpression(int adPlaceHolder, int adType, String adUnitId) {
            Log.v("Testing",String.format("onImpression adPlaceHolder=%d adType=%d adUnitId=%s",adPlaceHolder,adType,adUnitId));
        }

        @Override
        public void onClick(int adPlaceHolder, int adType, String adUnitId) {
            Log.v("Testing",String.format("onClick adPlaceHolder=%d adType=%d adUnitId=%s",adPlaceHolder,adType,adUnitId));
        }

        @Override
        public void onReward(int adPlaceHolder, int adType, String adUnitId) {
            Log.v("Testing",String.format("onReward adPlaceHolder=%d adType=%d adUnitId=%s",adPlaceHolder,adType,adUnitId));
        }
    };

}
