package com.inmobi.surprise.lib.util;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Retryable<T> implements Delayed {

    private final long createdAt = System.currentTimeMillis();
    private final long baseDelayMs;
    private long delayMs = 0;
    private final T item;
    private long retries = 0;

    public Retryable(T item) {
        this(item, 100);
    }

    public Retryable(T item, int delayMs) {
        this.item = item;
        this.baseDelayMs = delayMs;
    }

    public Retryable<T> delay() {
        delay(true);
        return this;
    }

    public Retryable<T> delay(boolean isRetry) {
        delayMs = ((1 << retries) * baseDelayMs);
        if (isRetry) {
            retries++;
        }
        return this;
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert((createdAt + delayMs) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        Retryable item = (Retryable) delayed;
        long diff = (createdAt + delayMs) - (item.createdAt + item.delayMs);
        return (diff < 0 ? -1 : (diff > 0 ? 1 : 0));
    }

    @Override
    public String toString() {
        return item.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Retryable<?> retryable = (Retryable<?>) obj;

        return compareTo(retryable) == 0;
    }

    @Override
    public int hashCode() {
        int result = (int) (createdAt ^ (createdAt >>> 32));
        result = 31 * result + (int) (baseDelayMs ^ (baseDelayMs >>> 32));
        result = 31 * result + (int) (delayMs ^ (delayMs >>> 32));
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (int) (retries ^ (retries >>> 32));
        return result;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public T getItem() {
        return item;
    }

    public long getRetries() {
        return retries;
    }

    public void setRetries(long retries) {
        this.retries = retries;
    }

    public long getBaseDelayMs() {
        return baseDelayMs;
    }

    public long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }
}