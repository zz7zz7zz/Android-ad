package com.module.ad.main;

import com.module.ad.base.IAd;

import java.util.HashMap;

/**
    广告统一入口
 */
public class AdMain {

    private static String TAG = "AdMain";

    //------------------------------------------------------------------------------------
    private static AdMain INS;
    private HashMap<String, IAd> adMap = new HashMap<>();

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

    public void preLoad(int adPlaceHolder,String adProvider){
        IAd ad = adMap.get(adProvider);
        if(null != ad){
            ad.onAdPreload(adPlaceHolder,-1);
        }
    }

    //----------------------- 展示 -----------------------
    public void show(int adPlaceHolder){

    }

    public void show(int adPlaceHolder,String adProvider){
        IAd ad = adMap.get(adProvider);
        if(null != ad){
            ad.onAdShow(adPlaceHolder,-1);
        }
    }

    //----------------------- 判断是否存在 -----------------------
    public void isExist(int adPlaceHolder){

    }


    public void isExist(int adPlaceHolder,String adProvider){

    }

}
