package com.module.ad.main;

import android.content.Context;

public class AdShowEvent {

    //----------------------------------------------------------------------------------------
    public static void onActivityCreated(Context context, String activityName){
        String key = activityName + "_Created";
        onActivityShow(context,key);
    }

    public static void onActivityResumed(Context context, String activityName){
        String key = activityName + "_Resumed";
        onActivityShow(context,key);
    }

    public static void onActivityPaused(Context context, String activityName){
        String key = activityName + "_Paused";
        onActivityShow(context,key);
    }

    public static void onActivityStopped(Context context, String activityName){
        String key = activityName + "_Stopped";
        onActivityShow(context,key);
    }

    public static void onActivityDestroyed(Context context, String activityName){
        String key = activityName + "_Destroyed";
        onActivityShow(context,key);
    }

    private static void onActivityShow(Context context, String key){
        int[] adPlaceHolderArray = AdConfigMgr.getInstance().adConfig.show_map_activity.get(key);
        if(null != adPlaceHolderArray && adPlaceHolderArray.length>0){
            for (int i=0;i<adPlaceHolderArray.length;i++){
                AdMain.getInstance().show(AdMain.getInstance().getCurrentActivity(),adPlaceHolderArray[i],null,null);
            }
        }
    }

    //----------------------------------------------------------------------------------------
    public static void onFragmentCreated(Context context, String FragmentName){
        String key = FragmentName + "_Created";
        onFragmentShow(context,key);
    }

    public static void onFragmentResumed(Context context, String FragmentName){
        String key = FragmentName + "_Resumed";
        onFragmentShow(context,key);
    }

    public static void onFragmentPaused(Context context, String FragmentName){
        String key = FragmentName + "_Paused";
        onFragmentShow(context,key);
    }

    public static void onFragmentStopped(Context context, String FragmentName){
        String key = FragmentName + "_Stopped";
        onFragmentShow(context,key);
    }

    public static void onFragmentDestroyed(Context context, String FragmentName){
        String key = FragmentName + "_Destroyed";
        onFragmentShow(context,key);
    }

    private static void onFragmentShow(Context context, String key){
        int[] adPlaceHolderArray = AdConfigMgr.getInstance().adConfig.show_map_fragment.get(key);
        if(null != adPlaceHolderArray && adPlaceHolderArray.length>0){
            for (int i=0;i<adPlaceHolderArray.length;i++){
                AdMain.getInstance().show(AdMain.getInstance().getCurrentActivity(),adPlaceHolderArray[i],null,null);
            }
        }
    }
    //------------------------------------------
    public static void onEvent(Context context, String event){
        int[] adPlaceHolderArray = AdConfigMgr.getInstance().adConfig.show_map_event.get(event);
        if(null != adPlaceHolderArray && adPlaceHolderArray.length>0){
            for (int i=0;i<adPlaceHolderArray.length;i++){
                AdMain.getInstance().show(AdMain.getInstance().getCurrentActivity(),adPlaceHolderArray[i],null,null);
            }
        }
    }

}
