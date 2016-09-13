package com.inmobi.surprise.lib.ads;

import com.inmobi.ads.InMobiAdRequestStatus;

/**
 * Created by davendar.ojha on 7/5/16.
 */
public interface AdListener {
    void adLoaded(SurpriseAd surpriseAd);

    void adFailed(int type, InMobiAdRequestStatus inMobiAdRequestStatus);
}
