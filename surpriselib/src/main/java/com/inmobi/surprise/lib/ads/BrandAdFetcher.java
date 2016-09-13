package com.inmobi.surprise.lib.ads;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.surprise.lib.util.AndroidUtils;
import com.inmobi.surprise.lib.util.Constants;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davendar.ojha on 7/5/16.
 */
public class BrandAdFetcher implements InMobiNative.NativeAdListener, AdFetcher {
    InMobiNative nativeAd;
    AdListener adListener;

    public BrandAdFetcher(AdListener adListener) {
        this.adListener = adListener;
    }

    @Override
    public void fetchAds() {
        nativeAd = new InMobiNative(Constants.BRAND_P_ID, this);
        nativeAd.load();
    }


    @Override
    public void onAdLoadSucceeded(InMobiNative inMobiNative) {
        try {
            JSONObject content = new JSONObject((String) inMobiNative.getAdContent());
//            Log.d("BrandAdFetcher", "Placed BRAND AD unit (" + inMobiNative.hashCode() +
//                    ") at position " + content);
            String url = content.getJSONObject("image_xhdpi").getString("url");
            String title = content.getString("title");
            String cta = content.getString("cta_install");
            cta = AndroidUtils.toCamelCase(cta);
            adListener.adLoaded(new SurpriseAd(inMobiNative, cta, url, title, AdType.BRAND_AD));
        } catch (JSONException e) {
            Log.e("BrandAdFetcher", e.toString());
        }
    }

    @Override
    public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
        adListener.adFailed(AdType.BRAND_AD, inMobiAdRequestStatus);
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
