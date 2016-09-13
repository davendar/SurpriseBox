package com.inmobi.surprise.lib.ads;

import java.util.List;

/**
 * Created by davendar.ojha on 7/4/16.
 */
public interface AdConsumer {

    void adAvailable(List<SurpriseAd> surpriseAd);
}
