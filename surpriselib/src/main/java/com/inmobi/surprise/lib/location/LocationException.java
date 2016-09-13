package com.inmobi.surprise.lib.location;

public class LocationException extends Exception {

    public LocationException(String detailMessage) {
        super(detailMessage);
    }

    public LocationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
