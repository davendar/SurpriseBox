package com.inmobi.surprise.lib.util;

import android.util.Log;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class RetryableQueue<T, W extends Retryable<T>> {

    private static final String TAG = "RetryableQueue";

    private final DelayQueue<W> workQueue = new DelayQueue<W>();
    private final ExecutorService workerPool;
    private final Runnable runnableWorker;

    public RetryableQueue() {
        workerPool = Executors.newSingleThreadScheduledExecutor();
        runnableWorker = new Runnable() {

            @Override
            public void run() {
                if (!workerPool.isShutdown()) {
                    W retryable;
                    try {
                        retryable = workQueue.take();
                        doWork(retryable);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException for Retryable", e);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception for Retryable", e);
                    }
                }
            }
        };
    }

    protected abstract void doWork(W work);

    public void start() {
        workerPool.submit(getWorker());
    }

    private Runnable getWorker() {
        return runnableWorker;
    }

    public W peek() {
        return workQueue.peek();
    }

    public boolean remove(final W work) {
        return workQueue.remove(work);
    }

    public void stop() {
        workQueue.clear();
        workerPool.shutdownNow();
    }

    public void offer(W work) {
        if (workerPool.isShutdown()) {
            return;
        }
        workQueue.offer(work);
        workerPool.submit(getWorker());
    }

    public int size() {
        return workQueue.size();
    }
}