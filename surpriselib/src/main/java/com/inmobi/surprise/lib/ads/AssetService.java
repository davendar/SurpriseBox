package com.inmobi.surprise.lib.ads;

import com.inmobi.surprise.lib.util.Retryable;
import com.inmobi.surprise.lib.util.RetryableQueue;

import android.content.Context;
import android.graphics.Bitmap;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by davendar.ojha on 8/3/16.
 */
public class AssetService {
    private Context context;
    private AssetFetchListener assetFetchListener;
    private final RetryableQueue<FetchWorkRequest, FetchWork> fetchQueue
            = new RetryableQueue<FetchWorkRequest, FetchWork>() {
        @Override
        protected void doWork(FetchWork work) {
            long timeElapsed = System.currentTimeMillis() - work.getCreatedAt();
            if (timeElapsed > 5000) {
                return;
            }
            cacheAsset(work);
        }
    };

    public AssetService(Context context) {
        this.context = context;
        fetchQueue.start();
    }

    public void cacheAsset(SurpriseAd surpriseAd, AssetFetchListener assetFetchListener) {
        this.assetFetchListener = assetFetchListener;
        fetchQueue.offer(new FetchWork(surpriseAd));
    }


    private void cacheAsset(FetchWork fetchWork) {
        try {
            Bitmap bitmap = Picasso.with(context).load(fetchWork.getSurpriseAd().url).get();
            if (null != bitmap) {
                fetchWork.getSurpriseAd().setAspectRatio(((float) bitmap.getWidth() / bitmap.getHeight()));
                //Warm-up the cache
                Picasso.with(context).load(fetchWork.getSurpriseAd().url).fetch();
            }
            try {
                assetFetchListener.onAssetFetched(fetchWork.getSurpriseAd());
            } catch (Exception e) {
                //TODO Log
            }
        } catch (IOException e) {
            fetchWork.delay();
            fetchQueue.offer(fetchWork);
        }
    }


    private static final class FetchWork extends Retryable<FetchWorkRequest> {
        FetchWork(SurpriseAd surpriseAd) {
            super(new FetchWorkRequest(surpriseAd), 100);
        }

        SurpriseAd getSurpriseAd() {
            return getItem().surpriseAd;
        }
    }

    private static final class FetchWorkRequest {
        private final SurpriseAd surpriseAd;

        FetchWorkRequest(SurpriseAd surpriseAd) {
            this.surpriseAd = surpriseAd;
        }
    }
}
