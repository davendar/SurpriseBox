package com.inmobi.surprise.lib.location;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GmsLocationManager implements LocationManager, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;


    public GmsLocationManager(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    public android.location.Location getLastKnownLocation() throws LocationException {
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            return null;
        }
        return location;
    }

    @Override
    public void start() {
        googleApiClient.connect();
    }

    @Override
    public void stop() {
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int event) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }
}
