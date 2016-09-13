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
public class AppAdFetcher implements InMobiNative.NativeAdListener, AdFetcher {
    InMobiNative nativeAd;
    AdListener adListener;

    public AppAdFetcher(AdListener adListener) {
        this.adListener = adListener;
    }

    @Override
    public void fetchAds() {
        nativeAd = new InMobiNative(Constants.APP_P_ID, this);
        try {
            nativeAd.load();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    @Override
    public void onAdLoadSucceeded(InMobiNative inMobiNative) {
        try {
            JSONObject content = new JSONObject((String) inMobiNative.getAdContent());
//            Log.d("AppAdFetcher", "Placed APP AD unit (" + inMobiNative.hashCode() +
//                    ") at position " + content);
            String url = content.getJSONObject("image_xhdpi").getString("url");
            String app_name = content.getString("title");
            String cta = content.getString("cta_install");
            cta = AndroidUtils.toCamelCase(cta);
//            Log.e("AppAdFetcher", "PROCCESSING DONE At " + System.currentTimeMillis());
            adListener.adLoaded(new SurpriseAd(inMobiNative, cta, url, app_name, AdType.APP_AD));
        } catch (JSONException e) {
            Log.e("AppAdFetcher", e.toString());
        }
    }

    @Override
    public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
        adListener.adFailed(AdType.APP_AD, inMobiAdRequestStatus);
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
