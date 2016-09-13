package com.inmobi.surprise.lib.ads;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.surprise.lib.util.Constants;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davendar.ojha on 7/5/16.
 */
public class SponsoredAdFetcher implements InMobiNative.NativeAdListener, AdFetcher {
    InMobiNative nativeAd;
    AdListener adListener;

    public SponsoredAdFetcher(AdListener adListener) {
        this.adListener = adListener;
    }

    @Override
    public void fetchAds() {
        nativeAd = new InMobiNative(Constants.SPONSOR_P_ID, this);
        nativeAd.load();
    }


    @Override
    public void onAdLoadSucceeded(InMobiNative inMobiNative) {
        try {
            JSONObject content = new JSONObject((String) inMobiNative.getAdContent());
            Log.e("SponsoredAdFetcher", "Placed ad unit (" + inMobiNative.hashCode() +
                    ") at position " + content);
            String url = content.getJSONObject("screenshots").getString("url");
            adListener.adLoaded(new SurpriseAd(inMobiNative, "cta", url, "title", AdType.SPONSORED_AD));
        } catch (JSONException e) {
            Log.e("SponsoredAdFetcher", "error", e);
        }
    }

    @Override
    public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
        adListener.adFailed(AdType.SPONSORED_AD, inMobiAdRequestStatus);
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
