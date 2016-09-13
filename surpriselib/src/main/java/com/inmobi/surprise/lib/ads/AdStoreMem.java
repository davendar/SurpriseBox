package com.inmobi.surprise.lib.ads;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by davendar.ojha on 7/27/16.
 */
public class AdStoreMem {
    ConcurrentHashMap<String, Integer> adMap;
    ConcurrentHashMap<String, String> urlTitleMap;
    LinkedBlockingQueue<SurpriseAd> bufferOfSurpriseAds;

    public AdStoreMem() {
        adMap = new ConcurrentHashMap<>();
        urlTitleMap = new ConcurrentHashMap<>();
        bufferOfSurpriseAds = new LinkedBlockingQueue<>();
    }

    public void clearMemory() {
        adMap.clear();
        urlTitleMap.clear();
        bufferOfSurpriseAds.clear();
    }

    public void storeAd(SurpriseAd surpriseAd) {
        if (isUnique(surpriseAd)) {
            bufferOfSurpriseAds.offer(surpriseAd);
        }
    }

    public void getBufferAds(List<SurpriseAd> adList) {
        bufferOfSurpriseAds.drainTo(adList);
    }

    public boolean isEnoughAdsInStore() {
        return adMap.size() > 1;
    }

    private boolean isUnique(SurpriseAd surpriseAd) {
        if (adMap.putIfAbsent(surpriseAd.url, surpriseAd.adType) == null) {
            return urlTitleMap.putIfAbsent(surpriseAd.appName, surpriseAd.url) == null;
        }
        return false;
    }
}
