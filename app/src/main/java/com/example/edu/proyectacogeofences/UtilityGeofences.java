package com.example.edu.proyectacogeofences;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UtilityGeofences implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GeofencingRequest.Builder geofencingRequestBuilder;
    private GoogleApiClient googleApiClient;

    public void addGeofences(final List<Area1> listaAreas) {
        if (listaAreas != null && listaAreas.size() > 0) {
            final ArrayList<Geofence> geofences = new ArrayList<>(listaAreas.size());

            for (int i = 0; i < listaAreas.size(); i++) {
                final Area1 area = listaAreas.get(i);

                geofences.add(new Geofence.Builder()
                        .setRequestId(String.valueOf(area.titulo))
                        .setCircularRegion(area.lat, area.lng, area.radio)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }

            geofencingRequestBuilder = new GeofencingRequest.Builder();

            geofencingRequestBuilder.addGeofences(geofences);

            GoogleApiClient.Builder b = new GoogleApiClient.Builder(App.getAppContext());
                    b.addApi(LocationServices.API);
                    b.addConnectionCallbacks(this);
                    b.addOnConnectionFailedListener(this);

            googleApiClient = b.build();

            googleApiClient.connect();
        }
    }

    public void addGeofences(final Area1 area) {
        ArrayList<Area1> listaArea = new ArrayList<>();
        listaArea.add(area);
        addGeofences(listaArea);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        final Intent intent = new Intent(App.getAppContext(), GeofenceTransitionsIntentService.class);
        final PendingIntent pendingIntent = PendingIntent.getService(App.getAppContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d("xxx", "onConnected");
        // COMPROBAR SI SE PIDEN PERMISOS, SI SE PIDEN NO HAY PROBLEMA (DEBERIA SER ASI)
        if (ActivityCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequestBuilder.build(), pendingIntent).setResultCallback(this);
        Log.d("xxx", "añado geofences");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }


}
