package com.inmobi.surprise.lib.ads;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.surprise.lib.util.Constants;
import com.inmobi.surprise.lib.util.AndroidUtils;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by davendar.ojha on 7/5/16.
 */
public class CommerceAdFetcher implements InMobiNative.NativeAdListener, AdFetcher {
    InMobiNative nativeAd;
    AdListener adListener;
    HashMap<String, String> extrasMap = new HashMap<>();

    public CommerceAdFetcher(AdListener adListener) {
        this.adListener = adListener;
//        extrasMap.put("mk-carrier", "27.50.16.1");
//        extrasMap.put("formatnew","native");
    }

    @Override
    public void fetchAds() {
        nativeAd = new InMobiNative(Constants.COMMERCE_P_ID, this);
        nativeAd.setExtras(extrasMap);
        nativeAd.load();
    }


    @Override
    public void onAdLoadSucceeded(InMobiNative inMobiNative) {
        try {
            JSONObject content = new JSONObject((String) inMobiNative.getAdContent());
            Log.e("CommerceAdFetcher", "Placed COMMERCE unit (" + inMobiNative.hashCode() +
                    ") at position " + content);
            String url = content.getJSONObject("screenshots").getString("url");
            String cta = content.getString("cta");
            cta = AndroidUtils.toCamelCase(cta);
            String title = content.getString("title");
            adListener.adLoaded(new SurpriseAd(inMobiNative, cta, url, title, AdType.COMMERCE_AD));
        } catch (JSONException e) {
            Log.e("CommerceAdFetcher", "COMMERCE " + e.toString());
        }
    }

    @Override
    public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
        adListener.adFailed(AdType.COMMERCE_AD, inMobiAdRequestStatus);
        Log.e("CommerceAdFetcher", "Failed to load ad. " + "COMMERCE" + " msg " + inMobiAdRequestStatus.getMessage());
    }

    @Override
    public void onAdDismissed(InMobiNative inMobiNative) {

    }

    @Override
    public void onAdDisplayed(InMobiNative inMobiNative) {

    }

    @Override
    public void onUserLeftApplication(InMobiNative inMobiNative) {

    }
}
