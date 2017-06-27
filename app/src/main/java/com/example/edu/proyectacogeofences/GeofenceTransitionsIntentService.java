package com.example.edu.proyectacogeofences;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;
import java.util.Random;

/**
 * Created by daniel on 31/05/17.
 */

public class GeofenceTransitionsIntentService extends IntentService{

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null && geofencingEvent.hasError()) {
            //generarNotificacion(App.getAppContext(), "ERROR de geofence: "+geofencingEvent.getErrorCode());
            generarNotificacion(App.getAppContext(), "Se ha desactivado el GPS, la aplicacion no le avisara de los puntos de Interes");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        String geo = "";
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            final List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            for(Geofence geofence: triggeringGeofences){
                geo += geofence.getRequestId() + " ";
            }
            // Send notification and log the transition details.
            generarNotificacion(App.getAppContext(), "Has entrado en: "+geo);
        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            final List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            for(Geofence geofence: triggeringGeofences){
                geo += geofence.getRequestId() + " ";
            }
            generarNotificacion(App.getAppContext(), "Has salido de: "+geo);
        } else {
            generarNotificacion(App.getAppContext(), "Transición no contemplada");
        }
    }

    public static void generarNotificacion(Context context, String mensaje){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.favicon)
                .setContentTitle("GeoPoint")
                .setContentText(mensaje)
                .setAutoCancel(true);

        mBuilder.setVibrate(new long[]{250, 500, 250, 500});
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(randInt(), mBuilder.build());
    }

    private static int randInt(){
        Random rand = new Random();
        return rand.nextInt();
    }
}
