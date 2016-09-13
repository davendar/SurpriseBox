package com.inmobi.surprise.lib.ads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by davendar.ojha on 7/13/16.
 */
public class SlotManager {
    private ConcurrentHashMap<Integer, Integer> slots = new ConcurrentHashMap<>();
    private int startingIndex = -1;
    private AtomicInteger currentCount = new AtomicInteger(startingIndex);

    public int getSlotType(int position) {
        if (slots.size() <= position) {
            return AdSlot.BIG_CARD;
        } else {
            return slots.get(position);
        }
    }

    public List<SurpriseAd> arrangeAccordingToSlot(List<SurpriseAd> adList) {
        List<SurpriseAd> tempList = new ArrayList<>(adList.size());
        List<SurpriseAd> inmobiAdList = new ArrayList<>(adList.size());
        List<SurpriseAd> fanAdList = new ArrayList<>(adList.size());
        for (SurpriseAd surpriseAd : adList) {
            if (null != surpriseAd.inMobiNative) {
                inmobiAdList.add(surpriseAd);
            } else {
                fanAdList.add(surpriseAd);
            }
        }
        tempList.addAll(inmobiAdList);
        tempList.addAll(fanAdList);
        return createSlots(tempList, inmobiAdList.size(), fanAdList.size());
    }

    public void resetSlotManager() {
        currentCount.set(startingIndex);
        slots.clear();
    }

    private List<SurpriseAd> createSlots(List<SurpriseAd> surpriseAdList, int inmobiNativeCount, int fanCount) {
        if (slots.size() == 0) {
            if (fanCount != 0) {
                fanCount = fanCount - 1;
                int lastPosition = surpriseAdList.size() - 1;
                SurpriseAd surpriseAd = surpriseAdList.get(lastPosition);
                surpriseAdList.remove(lastPosition);
                surpriseAdList.add(0, surpriseAd);
            } else if (inmobiNativeCount != 0) {
                inmobiNativeCount = inmobiNativeCount - 1;
            }
            slots.put(currentCount.incrementAndGet(), AdSlot.BIG_CARD);
        }
        while (inmobiNativeCount > 1) {
            populateTwoInMobiAds();
            inmobiNativeCount = inmobiNativeCount - 2;
        }
        int remainingAdCount = fanCount + inmobiNativeCount;
        for (int i = 0; i < remainingAdCount; i++) {
            slots.put(currentCount.incrementAndGet(), AdSlot.BIG_CARD);
        }
        return surpriseAdList;
    }

    private void populateTwoInMobiAds() {
        int slotSize = slots.size();
        if (slotSize >= 4) {
            int lastSlot = slots.get(slotSize - 1);
            int fourthLastSlot = slots.get(slotSize - 4);
            if (lastSlot == AdSlot.BIG_CARD || fourthLastSlot == AdSlot.BIG_CARD) {
                slots.put(currentCount.incrementAndGet(), AdSlot.SMALL_CARD);
                slots.put(currentCount.incrementAndGet(), AdSlot.SMALL_CARD);
                return;
            }
            slots.put(currentCount.incrementAndGet(), AdSlot.BIG_CARD);
            slots.put(currentCount.incrementAndGet(), AdSlot.BIG_CARD);
            return;
        }
        slots.put(currentCount.incrementAndGet(), AdSlot.SMALL_CARD);
        slots.put(currentCount.incrementAndGet(), AdSlot.SMALL_CARD);
    }
}
