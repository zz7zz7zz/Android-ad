package com.module.ad.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.module.ad.base.AdConfig;
import com.module.ad.base.AdEntity;
import com.module.ad.base.AdType;
import com.module.ad.base.IAd;
import com.module.ad.base.IAdListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
    广告统一入口
 */
public class AdMain {

    private static String TAG = "AdMain";

    //------------------------------------------------------------------------------------
    private WeakReference<Activity> mCurrentActivity;

    //------------------------------------------------------------------------------------
    private static AdMain INS;
    private HashMap<String, IAd> adMap = new HashMap<>();
    private HashMap<Integer,ArrayList<AdEntity>> adObjectMap = new HashMap();
    private IAdListener listener;

    private static final int MSG_WAIT_SHOW_AD = 1;
    private ArrayList<AdShowTask> adShowTaskArrayList = new ArrayList<>();
    private AdHandler  mHandler = new AdHandler(this);

    private static final class AdShowTask {
        public Context context;
        public int adPlaceHolder;
        public String adProviderName;
        public ViewGroup adViewParent;
        public long timeOutMillis;
        public OnAdShowResultListener listener;

        public AdShowTask(Context context, int adPlaceHolder, String adProviderName, ViewGroup adViewParent, long timeOutMillis, OnAdShowResultListener listener) {
            this.context = context;
            this.adPlaceHolder = adPlaceHolder;
            this.adProviderName = adProviderName;
            this.adViewParent = adViewParent;
            this.timeOutMillis = timeOutMillis;
            this.listener = listener;
        }
    }

    private static final class AdHandler extends Handler{
        private WeakReference<AdMain> adMainWeakReference;

        public AdHandler(AdMain adMain) {
            this.adMainWeakReference = new WeakReference<>(adMain);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AdMain adMain = adMainWeakReference.get();
            if (null == adMain) {
                return;
            }

            switch (msg.what) {
                case MSG_WAIT_SHOW_AD:
                    AdShowTask obj = (AdShowTask) msg.obj;
                    adMain.show(obj.context,obj.adPlaceHolder,obj.adProviderName,obj.adViewParent,-1,obj.listener);

                    adMain.adShowTaskArrayList.remove(obj);

                    //Log.v(TAG,"end wating to show handleMessage");
                    break;
            }
        }
    };


    private AdMain() {
        //1.写死初始化
//        init("google.admob");
//        init("huawei");

        //2.根据配置动态初始化
        String[] ad_ingot_dep_modules = BuildConfig.ad_ingot_dep_modules;
        for (String moduleName:ad_ingot_dep_modules) {
            Log.v(TAG,"AdMain() init ad module " + moduleName);
            init(moduleName);
        }
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

        initSDK(application);
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

    private void initSDK(Context context){
        //初始化SDK
        for (Map.Entry<String, IAd> entry : adMap.entrySet()) {
            entry.getValue().onInit(context);
        }
    }

    //----------------------- 1.加载 -----------------------
    public int preLoad(Context context,int adPlaceHolder,String scenario){
        int retCode = 0;

        AdConfig adConfig = AdConfigMgr.getInstance().adConfig;
        AdConfig.AdPlaceHolderConfig adPlaceHolderConfig = adConfig.getAdPlaceHolderConfig(adPlaceHolder);
        String fileName = "ad_ingot";
        String key = String.format("placeholder_%d_adFreqType_%d",adPlaceHolder,adPlaceHolderConfig.adFreqType);
        if(adPlaceHolderConfig.adFreqType == AdConfig.AdPlaceHolderConfig.AD_FREQ_TYPE_COUNT){
            long value = AdSharePreUtil.getLong(context,fileName,key) + 1;
            AdSharePreUtil.asynPutLong(context,fileName,key,value);

            if(!(value == adPlaceHolderConfig.adOffset || (value - adPlaceHolderConfig.adOffset) % adPlaceHolderConfig.adFreq == 0)){
                retCode = -11;
            }
        }else if(adPlaceHolderConfig.adFreqType == AdConfig.AdPlaceHolderConfig.AD_FREQ_TYPE_SECOND){
            long old = AdSharePreUtil.getLong(context,fileName,key);
            long now = System.currentTimeMillis();
            if((old == 0 && now > adPlaceHolderConfig.adOffset) || now > (old + adPlaceHolderConfig.adFreq * 1000)){
                AdSharePreUtil.asynPutLong(context,fileName,key,now);
            }else{
                retCode = -12;
            }
        }

        if(retCode == 0){
            retCode = innerPreLoad(context,adPlaceHolder,null,scenario,false);
        }
        Log.v(TAG,"preLoad retCode " + retCode);
        return retCode;
    }

    public int forcePreLoad(Context context,int adPlaceHolder,String scenario){
        int retCode = innerPreLoad(context,adPlaceHolder,null,scenario,true);
        Log.v(TAG,"forcePreLoad retCode " + retCode);
        return retCode;
    }

    private int innerPreLoad(Context context, int adPlaceHolder, String adProviderName, String scenario,boolean force){
        AdConfig adConfig = AdConfigMgr.getInstance().adConfig;

        AdConfig.AdProvider adProvider = null;
        IAd ad = null;
        int adProviderSize = adConfig.getAdProviderSize(adPlaceHolder,adProviderName);
        int findCount = 0;
        while(true){

            adProvider = adConfig.getRequestAdProvider(adPlaceHolder,adProviderName);
            findCount ++;

            if (adProviderSize == findCount){//找了一遍
                if(null == adProvider){
                    adConfig.setRequestIndex(adPlaceHolder,0);
                    resetPreloadTime(context,adPlaceHolder,force);
                    return -2;
                }

                ad = adMap.get(adProvider.adProviderName);
                if(null == ad){
                    adConfig.setRequestIndex(adPlaceHolder,0);
                    resetPreloadTime(context,adPlaceHolder,force);
                    return -3;
                }
            }else{
                if(null == adProvider){
                    int index = AdConfigMgr.getInstance().adConfig.getRequestIndex(adPlaceHolder);
                    AdConfigMgr.getInstance().adConfig.setRequestIndex(adPlaceHolder,index+1);
                    Log.v(TAG,"preLoad getRequestAdProvider " + -2 + " findCount " + findCount);
                    continue;
                }

                ad = adMap.get(adProvider.adProviderName);
                if(null == ad){
                    int index = AdConfigMgr.getInstance().adConfig.getRequestIndex(adPlaceHolder);
                    AdConfigMgr.getInstance().adConfig.setRequestIndex(adPlaceHolder,index+1);
                    Log.v(TAG,"preLoad adMap.get " + -3 + " findCount " + findCount);
                    continue;
                }
            }

            break;
        }

        AdEntity mAdEntity =new AdEntity(scenario,adPlaceHolder,adProvider);
        mAdEntity.force = force;
        ad.onAdPreload(context, mAdEntity, proxyListener);

        return 0;
    }

    //重置加载时机
    private void resetPreloadTime(Context context,int adPlaceHolder,boolean force){
        AdConfig adConfig = AdConfigMgr.getInstance().adConfig;
        if(!force){
            AdConfig.AdPlaceHolderConfig adPlaceHolderConfig = adConfig.getAdPlaceHolderConfig(adPlaceHolder);
            String fileName = "ad_ingot";
            String key = String.format("placeholder_%d_adFreqType_%d",adPlaceHolder,adPlaceHolderConfig.adFreqType);
            if(adPlaceHolderConfig.adFreqType == AdConfig.AdPlaceHolderConfig.AD_FREQ_TYPE_COUNT){
                long value = AdSharePreUtil.getLong(context,fileName,key)-1;
                AdSharePreUtil.asynPutLong(context,fileName,key,value);
            }else if(adPlaceHolderConfig.adFreqType == AdConfig.AdPlaceHolderConfig.AD_FREQ_TYPE_SECOND){
                long now = System.currentTimeMillis() - adPlaceHolderConfig.adFreq * 1000 - 1;
                AdSharePreUtil.asynPutLong(context,fileName,key,now);
            }
        }
    }

    //----------------------- 2.展示 -----------------------
    public void show(Context context,int adPlaceHolder,ViewGroup adViewParent, OnAdShowResultListener listener){
        show(context,adPlaceHolder,null,adViewParent,-1,listener);
    }

    public void show(Context context,int adPlaceHolder,ViewGroup adViewParent,long timeOutMillis, OnAdShowResultListener listener){
        show(context,adPlaceHolder,null,adViewParent,timeOutMillis,listener);
    }

    public void show(final Context context, final int adPlaceHolder, final String adProviderName, final ViewGroup adViewParent, long timeOutMillis, OnAdShowResultListener listener){
        if(context instanceof Activity && ((Activity)context).isFinishing()){
            return;
        }

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
            if(context instanceof Activity && null == adViewParent && (adEntity.adProvider.adType == AdType.TYPE_BANNER || adEntity.adProvider.adType == AdType.TYPE_NATIVE)){
                final AdEntity finalAdEntity = adEntity;
                AdDialog.show((Activity) context, new AdDialog.IAdDialogListener() {
                    @Override
                    public void onShow(ViewGroup ...adViewParent) {
                        finalAdEntity.showFromDialog = true;
                        finalAdEntity.ad.onAdShow(context, finalAdEntity, proxyListener,adViewParent[0]);

                        //以下逻辑可以展示多个广告
                        //show(context,adPlaceHolder,adProviderName,adViewParent[1]);
                    }
                });
            }else{
                adEntity.ad.onAdShow(context,adEntity, proxyListener,adViewParent);
            }

            if(null != listener){
                listener.onSuccess();
            }
        }else{

            //最多等待多少秒再Check一次（timeOutMillis设置为-1了）
            if(timeOutMillis > 0){
//                Runnable delayShowRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        show(context,adPlaceHolder,adProviderName,adViewParent,-1);
//                    }
//                };
//                mHandler.postDelayed(delayShowRunnable,timeOutMillis);

                AdShowTask obj = new AdShowTask(context, adPlaceHolder, adProviderName, adViewParent, timeOutMillis,listener);

                Message msg = mHandler.obtainMessage();
                msg.what = MSG_WAIT_SHOW_AD;
                msg.obj = obj;
                mHandler.sendMessageDelayed(msg,timeOutMillis);

                adShowTaskArrayList.add(obj);

                //Log.v(TAG,"start wating to show sendMessage");
                forcePreLoad(context, adPlaceHolder,"show_not_ad_preload");
                return;
            }

            if(null != listener){
                listener.onFailed();
            }
        }
    }

    private AdEntity getShowAdEntity(int adPlaceHolder, String adProviderName){
        ArrayList<AdEntity> ret = adObjectMap.get(adPlaceHolder);
        if(null != ret){

            long currentTimeMillis = System.currentTimeMillis();
            Iterator<AdEntity> it = ret.iterator();
            while (it.hasNext()) {
                AdEntity item = it.next();
                if( currentTimeMillis > (item.ad_resp_time_millis + item.ad_ttl * 1000)){
                    it.remove();
                }
            }

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

                AdShowEvent.onActivityCreated(activity.getApplicationContext(),activity.getClass().getSimpleName());

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

                // 更新当前的Activity
                if(activity != getCurrentUnfinishedActivity()){
                    mCurrentActivity = new WeakReference<>(activity);
                }

                AdPreloadEvent.onActivityResumed(activity.getApplicationContext(),activity.getClass().getSimpleName());

                AdShowEvent.onActivityResumed(activity.getApplicationContext(),activity.getClass().getSimpleName());

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

                AdPreloadEvent.onActivityPaused(activity.getApplicationContext(),activity.getClass().getSimpleName());

                AdShowEvent.onActivityPaused(activity.getApplicationContext(),activity.getClass().getSimpleName());

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

                AdPreloadEvent.onActivityStopped(activity.getApplicationContext(),activity.getClass().getSimpleName());

                AdShowEvent.onActivityStopped(activity.getApplicationContext(),activity.getClass().getSimpleName());

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

                AdPreloadEvent.onActivityDestroyed(activity.getApplicationContext(),activity.getClass().getSimpleName());

                AdShowEvent.onActivityDestroyed(activity.getApplicationContext(),activity.getClass().getSimpleName());

                // 返回键退出应用时调用
//                if (activity instanceof MainActivity) {
//                    mCurrentActivity = null;
//                }

                if(activity == getCurrentActivity()){
                    mCurrentActivity = null;
                }
            }
        });
    }

    //获取当前未退出的Activity实例
    private Activity getCurrentUnfinishedActivity(){
        Activity ret = null;
        if(null != mCurrentActivity){
            ret = mCurrentActivity.get();
        }
        if(null != ret && ret.isFinishing()){
            ret = null;
        }
        return ret;
    }

    public Activity getCurrentActivity() {
        return  null == mCurrentActivity ? null : mCurrentActivity.get();
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
                    innerPreLoad(context,adPlaceHolder,null, scenario,adEntity.force);
                    return;
                }else{
                    AdConfigMgr.getInstance().adConfig.setRequestIndex(adPlaceHolder,0);
                    resetPreloadTime(context,adPlaceHolder,adEntity.force);
                }
            }

            if(null != listener){
                listener.onResponse(context,isSuccess, scenario,adPlaceHolder,adType,adUnitId,adEntity);
            }

            //有消息都情况下，有没有广告都要回调一下；有广告则展示广告，没有广告可以做其它的事情(比如在对话框中展示广告可以关闭对话框，Toast提示等)
            for (int i = 0; i < adShowTaskArrayList.size(); i++) {
                if(adShowTaskArrayList.get(i).adPlaceHolder == adPlaceHolder){
                    AdShowTask obj = adShowTaskArrayList.remove(i);

                    mHandler.removeMessages(MSG_WAIT_SHOW_AD,obj);
                    show(obj.context,obj.adPlaceHolder,obj.adProviderName,obj.adViewParent,-1,obj.listener);

                    //Log.v(TAG,"end wating to show onResponse");
                    break;
                }
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
