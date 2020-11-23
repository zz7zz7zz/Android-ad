package com.module.ad.base;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
    广告配置
 */
public class AdConfig {

    //------------------------------------------------------------------------------------
    private HashMap<Integer, AdPlaceHolderConfig> adPlaceHolderListMap = new HashMap();
    private HashMap<Integer, Integer> adPlaceHolderRequestIndexMap = new HashMap();

    //------------------------------------------------------------------------------------
    public HashMap<String,int[]> preload_map_activity = new HashMap<>();
    public HashMap<String,int[]> preload_map_fragment = new HashMap<>();
    public HashMap<String,int[]> preload_map_event    = new HashMap<>();

    //------------------------------------------------------------------------------------
    public HashMap<String,int[]> show_map_activity = new HashMap<>();
    public HashMap<String,int[]> show_map_fragment = new HashMap<>();
    public HashMap<String,int[]> show_map_event    = new HashMap<>();

    public boolean isEnable(int adPlaceHolder){
        AdPlaceHolderConfig mAdPlaceHolderConfig= adPlaceHolderListMap.get(adPlaceHolder);
        return null != mAdPlaceHolderConfig && null != mAdPlaceHolderConfig.adProviderList && mAdPlaceHolderConfig.adProviderList.size() > 0 ;
    }

    public AdPlaceHolderConfig getAdPlaceHolderConfig(int adPlaceHolder){
        return adPlaceHolderListMap.get(adPlaceHolder);
    }

    public AdProvider getRequestAdProvider(int adPlaceHolder){
        return getRequestAdProvider(adPlaceHolder,null);
    }

    public AdProvider getRequestAdProvider(int adPlaceHolder, String adProviderName){
        AdPlaceHolderConfig mAdPlaceHolderConfig= adPlaceHolderListMap.get(adPlaceHolder);
        if(null != mAdPlaceHolderConfig && null != mAdPlaceHolderConfig.adProviderList && mAdPlaceHolderConfig.adProviderList.size() > 0){
            int size = mAdPlaceHolderConfig.adProviderList.size();
            for (int i = 0 ;i<size;i++){
                int index = getRequestIndex(adPlaceHolder);
                if(index < 0 && index >= size){
                    index = 0;
                }
                index = (i+index)%size;

                AdProvider adProvider = mAdPlaceHolderConfig.adProviderList.get(index);
                if(!TextUtils.isEmpty(adProviderName)){
                    if(adProviderName.equals(adProvider.adProviderName)){
                        setRequestIndex(adPlaceHolder,index);
                        return adProvider;
                    }
                }else{
                    setRequestIndex(adPlaceHolder,index);
                    return mAdPlaceHolderConfig.adProviderList.get(index);
                }
            }
        }
        return null;
    }

    public int getAdProviderSize(int adPlaceHolder){
        AdPlaceHolderConfig mAdPlaceHolderConfig= adPlaceHolderListMap.get(adPlaceHolder);
        return null != mAdPlaceHolderConfig && null != mAdPlaceHolderConfig.adProviderList ? mAdPlaceHolderConfig.adProviderList.size() : 0;
    }

    public int getRequestIndex(int adPlaceHolder){
        Integer index = adPlaceHolderRequestIndexMap.get(adPlaceHolder);
        if(null != index){
            return index;
        }
        return 0;
    }

    public int setRequestIndexNext(int adPlaceHolder){
        int size = 0;
        AdPlaceHolderConfig mAdPlaceHolderConfig= adPlaceHolderListMap.get(adPlaceHolder);
        if(null != mAdPlaceHolderConfig && null != mAdPlaceHolderConfig.adProviderList && mAdPlaceHolderConfig.adProviderList.size() > 0){
            size = mAdPlaceHolderConfig.adProviderList.size();
        }
        int newIndex = (size > 0) ? (getRequestIndex(adPlaceHolder) + 1) % size : 0;
        adPlaceHolderRequestIndexMap.put(adPlaceHolder,newIndex);
        return newIndex;
    }

    public int setRequestIndex(int adPlaceHolder,int newIndex){
        adPlaceHolderRequestIndexMap.put(adPlaceHolder,newIndex);
        return newIndex;
    }

    //------------------------------------------------------------------------------------
    public static AdConfig parse(String config){
        AdConfig ret = null;
        try {
            JSONObject configJson = new JSONObject(config);
            JSONArray adPlaceHolderListJA =configJson.optJSONArray("adPlaceHolderList");
            int length = null != adPlaceHolderListJA ? adPlaceHolderListJA.length() : 0;
            if(length > 0){
                ret = new AdConfig();
                for (int i = 0; i<length; i++){
                    JSONObject adPlaceHolderConfigJson = adPlaceHolderListJA.optJSONObject(i);
                    if(null != adPlaceHolderConfigJson){
                        int adPlaceHolder = adPlaceHolderConfigJson.optInt("adPlaceHolder");
                        if(adPlaceHolder > 0){
                            AdPlaceHolderConfig adPlaceHolderConfig = new AdPlaceHolderConfig();

                            adPlaceHolderConfig.adFreqType = adPlaceHolderConfigJson.optInt("adFreqType");
                            adPlaceHolderConfig.adFreq = adPlaceHolderConfigJson.optInt("adFreq");
                            adPlaceHolderConfig.adOffset = adPlaceHolderConfigJson.optInt("adOffset");
                            adPlaceHolderConfig.adBufferSize = adPlaceHolderConfigJson.optInt("adBufferSize");

                            JSONArray adProviderListJA =adPlaceHolderConfigJson.optJSONArray("adProviderList");
                            int length2 = null != adProviderListJA ? adProviderListJA.length() : 0;
                            if(length2 > 0){
                                adPlaceHolderConfig.adProviderList = new ArrayList<>(length2);
                                for (int j = 0; j<length2; j++){
                                    JSONObject adProviderJson = adProviderListJA.optJSONObject(j);
                                    if(null != adProviderJson){
                                        AdProvider adProvider = new AdProvider();
                                        adProvider.adProviderName   = adProviderJson.optString("adProviderName");
                                        adProvider.adUnitId         = adProviderJson.optString("adUnitId");
                                        adProvider.adType           = adProviderJson.optInt("adType");
                                        adProvider.adTtl            = adProviderJson.optInt("adTtl");

                                        adProvider.banner_size_width  = adProviderJson.optInt("banner_size_width");
                                        adProvider.banner_size_height = adProviderJson.optInt("banner_size_height");
                                        adProvider.banner_refresh_freq= adProviderJson.optInt("banner_refresh_freq");

                                        adPlaceHolderConfig.adProviderList.add(adProvider);
                                    }
                                }
                            }

                            JSONArray adReuseOrderJA =adPlaceHolderConfigJson.optJSONArray("adReuseList");
                            int length3 = null != adReuseOrderJA ? adReuseOrderJA.length() : 0;
                            if(length3 > 0){
                                adPlaceHolderConfig.adReuseList = new ArrayList<>(length3);
                                for (int k = 0; k<length3; k++){
                                    JSONObject adReusePlaceHolderJson = adReuseOrderJA.optJSONObject(k);
                                    if(null != adReusePlaceHolderJson){
                                        AdReusePlaceHolder adReusePlaceHolder = new AdReusePlaceHolder();
                                        adReusePlaceHolder.adPlaceHolder   = adReusePlaceHolderJson.optInt("adPlaceHolder");
                                        adReusePlaceHolder.adProviderName  = adReusePlaceHolderJson.optString("adProviderName");
                                        adReusePlaceHolder.adType           = adReusePlaceHolderJson.optInt("adType");
                                        adPlaceHolderConfig.adReuseList.add(adReusePlaceHolder);
                                    }
                                }
                            }

                            if(null != adPlaceHolderConfig.adProviderList && adPlaceHolderConfig.adProviderList.size() >=0){
                                ret.adPlaceHolderListMap.put(adPlaceHolder,adPlaceHolderConfig);
                            }
                        }
                    }
                }
            }

            //-------------------------解析预加载时机-------------------------------
            JSONObject preloadJA =configJson.optJSONObject("preload");
            if(null != preloadJA){
                JSONArray preload_map_activity_ja = preloadJA.optJSONArray("Activity");
                length = null != preload_map_activity_ja ? preload_map_activity_ja.length() : 0;
                if(length > 0){
                    for (int i = 0; i<length; i++){
                        Iterator<String> keys = preload_map_activity_ja.optJSONObject(i).keys();
                        while (keys.hasNext()) {
                            String activity_event = keys.next();
                            JSONArray activity_event_adPlaceHolder_ja = preload_map_activity_ja.optJSONObject(i).optJSONArray(activity_event);
                            int length2 = null != activity_event_adPlaceHolder_ja ? activity_event_adPlaceHolder_ja.length() : 0;
                            if(length2 > 0){
                                int[] activity_event_ad_place_holder_array=new int[length2];
                                for (int j = 0; j<length2; j++){
                                    activity_event_ad_place_holder_array[j]=activity_event_adPlaceHolder_ja.optInt(j);
                                }

                                ret.preload_map_activity.put(activity_event,activity_event_ad_place_holder_array);
                            }
                        }
                    }
                }

                JSONArray preload_map_fragment_ja = preloadJA.optJSONArray("Fragment");
                length = null != preload_map_fragment_ja ? preload_map_fragment_ja.length() : 0;
                if(length > 0){
                    for (int i = 0; i<length; i++){
                        Iterator<String> keys = preload_map_fragment_ja.optJSONObject(i).keys();
                        while (keys.hasNext()) {
                            String fragment_event = keys.next();
                            JSONArray fragment_event_adPlaceHolder_ja = preload_map_fragment_ja.optJSONObject(i).optJSONArray(fragment_event);
                            int length2 = null != fragment_event_adPlaceHolder_ja ? fragment_event_adPlaceHolder_ja.length() : 0;
                            if(length2 > 0){
                                int[] fragment_event_ad_place_holder_array=new int[length2];
                                for (int j = 0; j<length2; j++){
                                    fragment_event_ad_place_holder_array[j]=fragment_event_adPlaceHolder_ja.optInt(j);
                                }

                                ret.preload_map_fragment.put(fragment_event,fragment_event_ad_place_holder_array);
                            }
                        }
                    }
                }

                JSONArray preload_map_event_ja = preloadJA.optJSONArray("Event");
                length = null != preload_map_event_ja ? preload_map_event_ja.length() : 0;
                if(length > 0){
                    for (int i = 0; i<length; i++){
                        Iterator<String> keys = preload_map_event_ja.optJSONObject(i).keys();
                        while (keys.hasNext()) {
                            String event = keys.next();

                            JSONArray event_adPlaceHolder_ja = preload_map_event_ja.optJSONObject(i).optJSONArray(event);
                            int length2 = null != event_adPlaceHolder_ja ? event_adPlaceHolder_ja.length() : 0;
                            if(length2 > 0){
                                int[] event_ad_place_holder_array=new int[length2];
                                for (int j = 0; j<length2; j++){
                                    event_ad_place_holder_array[j]=event_adPlaceHolder_ja.optInt(j);
                                }

                                ret.preload_map_event.put(event,event_ad_place_holder_array);
                            }
                        }
                    }
                }
            }

            //-------------------------解析展示时机-------------------------------
            JSONObject showJA =configJson.optJSONObject("show");
            if(null != showJA){
                JSONArray show_map_activity_ja = showJA.optJSONArray("Activity");
                length = null != show_map_activity_ja ? show_map_activity_ja.length() : 0;
                if(length > 0){
                    for (int i = 0; i<length; i++){
                        Iterator<String> keys = show_map_activity_ja.optJSONObject(i).keys();
                        while (keys.hasNext()) {
                            String activity_event = keys.next();
                            JSONArray activity_event_adPlaceHolder_ja = show_map_activity_ja.optJSONObject(i).optJSONArray(activity_event);
                            int length2 = null != activity_event_adPlaceHolder_ja ? activity_event_adPlaceHolder_ja.length() : 0;
                            if(length2 > 0){
                                int[] activity_event_ad_place_holder_array=new int[length2];
                                for (int j = 0; j<length2; j++){
                                    activity_event_ad_place_holder_array[j]=activity_event_adPlaceHolder_ja.optInt(j);
                                }

                                ret.show_map_activity.put(activity_event,activity_event_ad_place_holder_array);
                            }
                        }
                    }
                }

                JSONArray show_map_fragment_ja = showJA.optJSONArray("Fragment");
                length = null != show_map_fragment_ja ? show_map_fragment_ja.length() : 0;
                if(length > 0){
                    for (int i = 0; i<length; i++){
                        Iterator<String> keys = show_map_fragment_ja.optJSONObject(i).keys();
                        while (keys.hasNext()) {
                            String fragment_event = keys.next();
                            JSONArray fragment_event_adPlaceHolder_ja = show_map_fragment_ja.optJSONObject(i).optJSONArray(fragment_event);
                            int length2 = null != fragment_event_adPlaceHolder_ja ? fragment_event_adPlaceHolder_ja.length() : 0;
                            if(length2 > 0){
                                int[] fragment_event_ad_place_holder_array=new int[length2];
                                for (int j = 0; j<length2; j++){
                                    fragment_event_ad_place_holder_array[j]=fragment_event_adPlaceHolder_ja.optInt(j);
                                }

                                ret.show_map_fragment.put(fragment_event,fragment_event_ad_place_holder_array);
                            }
                        }
                    }
                }

                JSONArray show_map_event_ja = showJA.optJSONArray("Event");
                length = null != show_map_event_ja ? show_map_event_ja.length() : 0;
                if(length > 0){
                    for (int i = 0; i<length; i++){
                        Iterator<String> keys = show_map_event_ja.optJSONObject(i).keys();
                        while (keys.hasNext()) {
                            String event = keys.next();

                            JSONArray event_adPlaceHolder_ja = show_map_event_ja.optJSONObject(i).optJSONArray(event);
                            int length2 = null != event_adPlaceHolder_ja ? event_adPlaceHolder_ja.length() : 0;
                            if(length2 > 0){
                                int[] event_ad_place_holder_array=new int[length2];
                                for (int j = 0; j<length2; j++){
                                    event_ad_place_holder_array[j]=event_adPlaceHolder_ja.optInt(j);
                                }

                                ret.show_map_event.put(event,event_ad_place_holder_array);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    //------------------------------------------------------------------------------------
    public static final class AdPlaceHolderConfig{

        public static final int AD_FREQ_TYPE_COUNT = 1;
        public static final int AD_FREQ_TYPE_SECOND = 2;

        public int adFreqType;//1，按次数；2，按时间
        public int adFreq;
        public int adOffset;
        public int adBufferSize;
        public ArrayList<AdProvider> adProviderList;
        public ArrayList<AdReusePlaceHolder> adReuseList;
    }

    public static final class AdProvider{
        public String adProviderName;
        public String adUnitId;
        public int adType;
        public int adTtl;

        //华为banner属性才有的字段
        public int banner_size_width;
        public int banner_size_height;
        public int banner_refresh_freq;

    }

    public static final class AdReusePlaceHolder{
        public int adPlaceHolder;
        public String adProviderName;
        public int adType;
    }

}
