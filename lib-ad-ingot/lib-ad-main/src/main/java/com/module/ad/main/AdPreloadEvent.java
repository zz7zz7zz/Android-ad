package com.module.ad.main;

import android.content.Context;

public class AdPreloadEvent {

    //----------------------------------------------------------------------------------------
    public static void onActivityCreated(Context context, String activityName){
        String key = activityName + "_Created";
        onActivityPreload(context,key);
    }

    public static void onActivityResumed(Context context, String activityName){
        String key = activityName + "_Resumed";
        onActivityPreload(context,key);
    }

    public static void onActivityPaused(Context context, String activityName){
        String key = activityName + "_Paused";
        onActivityPreload(context,key);
    }

    public static void onActivityStopped(Context context, String activityName){
        String key = activityName + "_Stopped";
        onActivityPreload(context,key);
    }

    public static void onActivityDestroyed(Context context, String activityName){
        String key = activityName + "_Destroyed";
        onActivityPreload(context,key);
    }

    private static void onActivityPreload(Context context, String key){
        int[] adPlaceHolderArray = AdConfigMgr.getInstance().adConfig.preload_map_activity.get(key);
        if(null != adPlaceHolderArray && adPlaceHolderArray.length>0){
            for (int i=0;i<adPlaceHolderArray.length;i++){
                AdMain.getInstance().preLoad(context,adPlaceHolderArray[i],key);
            }
        }
    }

    //----------------------------------------------------------------------------------------
    public static void onFragmentCreated(Context context, String FragmentName){
        String key = FragmentName + "_Created";
        onFragmentPreload(context,key);
    }

    public static void onFragmentResumed(Context context, String FragmentName){
        String key = FragmentName + "_Resumed";
        onFragmentPreload(context,key);
    }

    public static void onFragmentPaused(Context context, String FragmentName){
        String key = FragmentName + "_Paused";
        onFragmentPreload(context,key);
    }

    public static void onFragmentStopped(Context context, String FragmentName){
        String key = FragmentName + "_Stopped";
        onFragmentPreload(context,key);
    }

    public static void onFragmentDestroyed(Context context, String FragmentName){
        String key = FragmentName + "_Destroyed";
        onFragmentPreload(context,key);
    }

    private static void onFragmentPreload(Context context, String key){
        int[] adPlaceHolderArray = AdConfigMgr.getInstance().adConfig.preload_map_fragment.get(key);
        if(null != adPlaceHolderArray && adPlaceHolderArray.length>0){
            for (int i=0;i<adPlaceHolderArray.length;i++){
                AdMain.getInstance().preLoad(context,adPlaceHolderArray[i],key);
            }
        }
    }
    //------------------------------------------
    public static void onEvent(Context context, String event){
        int[] adPlaceHolderArray = AdConfigMgr.getInstance().adConfig.preload_map_event.get(event);
        if(null != adPlaceHolderArray && adPlaceHolderArray.length>0){
            for (int i=0;i<adPlaceHolderArray.length;i++){
                AdMain.getInstance().preLoad(context,adPlaceHolderArray[i],event);
            }
        }
    }

}
