package com.inmobi.surprise.lib.location;

public interface LocationManager {

    android.location.Location getLastKnownLocation() throws LocationException;

    void start();

    void stop();
}
