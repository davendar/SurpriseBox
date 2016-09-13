package com.inmobi.surprise.lib.ads;

import com.inmobi.ads.InMobiNative;

import com.facebook.ads.NativeAd;

/**
 * Created by davendar.ojha on 7/5/16.
 */
public class SurpriseAd {
    private  int slotType;
    private float aspectRatio;
    public final InMobiNative inMobiNative;
    public final String url;
    public final NativeAd fanNativeAd;
    public final String appName;
    public final String cta;
    public final long createdAt;
    public final int adType;


    public SurpriseAd(InMobiNative inMobiNative, String cta, String url, String app_name, int adType) {
        this.inMobiNative = inMobiNative;
        this.url = url;
        this.appName = app_name;
        fanNativeAd = null;
        this.cta = cta;
        createdAt = System.currentTimeMillis();
        this.adType = adType;
    }

    public SurpriseAd(NativeAd fanNativeAd, int adType) {
        this.fanNativeAd = fanNativeAd;
        this.url = fanNativeAd.getAdCoverImage().getUrl();
        this.appName = fanNativeAd.getAdTitle();
        inMobiNative = null;
        cta = fanNativeAd.getAdCallToAction();
        createdAt = System.currentTimeMillis();
        this.adType = adType;
    }

    public int getSlotType() {
        return slotType;
    }

    public void setSlotType(int slotType) {
        this.slotType = slotType;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}
