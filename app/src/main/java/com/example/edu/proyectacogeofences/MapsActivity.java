package com.example.edu.proyectacogeofences;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Marker marker;
    private BBDDActivity bbddActivity;
    User user;
    MainActivity mainActivity;

    double lat;
    double lng;


    LatLng latLngPulsacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mainActivity = new MainActivity();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);


        // 1. Comprobamos si tiene concedidos los permisos de red, ubicacion
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {


            // 2. Consultar la red activa y determinar si hay conexión a Internet.
            if (isNetDisponible()) {
                Toast.makeText(this, "conexion correcta internet", Toast.LENGTH_LONG).show();


            } else {
                String mensaje = "Esta aplicacion no se puede usar sin acceso a la red";

                popUpConfirmacionConexiones(mensaje);
                Toast.makeText(this, "necesitas tener internet", Toast.LENGTH_LONG).show();
            }

            // Comprobamos que la ubicacion esta establecida ya
            if (ConexionGPSok(location)) {
                Toast.makeText(this, "conexion con GPS correcta", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "conexion con GPS INCORRECTA", Toast.LENGTH_LONG).show();
                String mensaje = "Esta aplicacion no se puede usar sin ubicacion del GPS";

                popUpConfirmacionConexiones(mensaje);

                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }


            Toast.makeText(this, " permisos de ubicacion correctos", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "necesitas conceder permisos de ubicacion", Toast.LENGTH_LONG).show();

            // Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            //  startActivity(intent);
        }

        // Verificamos si los servicios de internet estan correctos
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status == ConnectionResult.SUCCESS) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.show();
        }

        Log.d("status", String.valueOf(status));

        user = mainActivity.getUser();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(MapsActivity.this);

        // Modificar mapas y marcadores
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Cargamos nuestra ubicacion por defecto
        MiUbicacion();

    }

    @Override
    public void onLocationChanged(Location location) {

        // Actualizamos la posicion
        MiUbicacion();
/*
        lng=loc.getLongitude();
        lat=loc.getLatitude();

        LatLng myPosition = new LatLng(loc.getLatitude(), loc.getLongitude());

        Toast.makeText(this,"mi posicion es: "+myPosition,Toast.LENGTH_LONG).show();
        ActualizarMarcador(loc);

        Log.d("lat", String.valueOf(lat));
        Log.d("lng", String.valueOf(lng));
*/
    }

    @Override
    public void onMapClick(final LatLng latLng) {

        // Toast.makeText(this, "estas tocando el mapa", Toast.LENGTH_LONG).show();

        //final LatLng latLngPulsacion=latLng;

        final String title = "";

        TextView tv = new TextView(this);

        tv.setText(title);
        // tv.setPadding(40, 40, 40, 40);
        tv.setHeight(10);
        tv.setWidth(10);
        tv.setTextSize(15);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);


        if (latLng != null) {

            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage("Seguro que quieres añadir este marcador? Introduce titulo.")
                    .setCustomTitle(tv)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        // Si confirmamos que si queremos guardar el punto de interes...
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Aqui estamos cuando acepta que quiere guardar un marcador de interes

                            //setLatLngPulsacion(latLng);

                            // mandamos a la activity de rellenar los datos del geofending de interes
                            Intent intentBBDD = new Intent(MapsActivity.this, BBDDActivity.class);

                            // pasamos los valores de interes
                            //String s="me cago en su puta madre";

                            //intentBBDD.putExtra(s,"s");
                            startActivity(intentBBDD);


                            Toast.makeText(MapsActivity.this, "latitud de la pulsacion " + latLng.latitude, Toast.LENGTH_SHORT).show();
                            Toast.makeText(MapsActivity.this, "longitud de la pulsacion " + latLng.longitude, Toast.LENGTH_SHORT).show();

                            /*
                             * cuando acepta que quiere guardar el marcador le mandamos a la activiti de relleno de info
                             * y le tenemos que conectar con los servicios REST de google para cargarle la informacion en
                             * la pantalla de la call, poblacion ...
                            */


                            // Añadimos el marcador al mapa
                            //AniadirMarcadorPulsacion(latLng);

                            //jsonDatosGeofence(latLng);

                        }
                    }).setNegativeButton(android.R.string.no, null).show();
        }

/*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        TextView tv = new TextView(this);
        String title="";
        tv.setText(title);
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        EditText et = new EditText(this);
        String etStr = et.getText().toString();

        alertDialogBuilder.setView(et);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("¿Quieres añadir este marcador?, introduce titulo del marcador");
        alertDialogBuilder.setCustomTitle(tv);

        final boolean isError = false;
        if (isError)
            alertDialogBuilder.setIcon(R.drawable.ic_action_navigation_menu);
        // alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "Yes" Button
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isError)
                            finish();
                        else {
                            Intent intent = new Intent(
                                    MapsActivity.this,
                                    MapsActivity.class);
                            startActivity(intent);
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would
            // not display the 'Force Close' message
            e.printStackTrace();
        }
*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onClick(View v) {
    }

    // mensaje para que vuelva al MainActivity y conecte los datos de internet antes de accederal mapa
    public void popUpConfirmacionConexiones(String mensaje) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(mensaje);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // mandamos a la activity principal para que acceda con internet
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private boolean ConexionGPSok(Location location) {

        boolean status;

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            // AniadirMarcador(lat, lng);
            Log.d("lat ConexionGPSok", String.valueOf(lat));
            Log.d("lng ConexionGPSok", String.valueOf(lng));
            Log.d("location", String.valueOf(location));
            status = true;

        } else {
            status = false;
        }

        return status;
    }

    // Con este metodo consultamos si la red esta activa y determinar si hay conexión a Internet.
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    // Recogemos los datos de google en formato json
    private void jsonDatosGeofence(LatLng latLng) {

        final double latPulsacion = latLng.latitude;
        final double lngPulsacion = latLng.longitude;


        // location.getProvider() -> nos dice quién nos ha facilitado esta información

        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + latPulsacion + "," + lngPulsacion;

        // Importante habilitar el permiso de acceso a internet para hacer la conexión
        // <uses-permission android:name="android.permission.INTERNET" />

        HiloRutaWS hiloRutaWS = new HiloRutaWS(this);
        String respuesta = null;
        try {
            respuesta = hiloRutaWS.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Llamando a get() nos devuelve el objeto que devuelve el hilo (ya reconoce que es de
        // tipo String), pero haciendo esto convertimos este código en síncrono, porque
        // el hilo principal se espera a que termine el hilo.
        try {
            // Con .get() se queda colgado hasta que termina
            // Lo suyo es no utilizar casi nunca ese .get()

            if (respuesta == null) {
                Toast.makeText(this, "Error en la llamada", Toast.LENGTH_LONG).show();
            } else {
                JSONObject jsonObject = new JSONObject(respuesta);

                String direccionCompleta = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");

                String poblacion = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                String calle = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                String numero = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                String codPostal = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                String localidad = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                String pais = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");

                Toast.makeText(this, "La Direccion Completa de la pulsacion es: " + direccionCompleta, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MiUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ActualizarMarcador(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);

            // Log.d("location manager", String.valueOf(locationManager));
        }
    }

    private void AniadirMarcadorPulsacion(LatLng latLng) {
        float zoom = 36;

        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLng, zoom);

        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.animateCamera(miUbicacion);


        Log.d("lat", String.valueOf(lat));
        Log.d("lng", String.valueOf(lng));
        Log.d("coordenadas", String.valueOf(latLng));
    }

    private void AniadirMarcador(double lat, double lng) {
        // Recogemos el zoom
        float zoom = 16;

        // recogemos la lat y lng juntas
        LatLng latLng = new LatLng(lat, lng);
        // le decimos a la camara de google que se posicione en la lat, lng y zoom
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLng, zoom);

        // Comprobamos si hay un marcador y si esta lo borramos para posicionar el nuevo
        if (marker != null) {
            marker.remove();

        }

        // Añadimos el marcador de la ubicacion actual
        //marker = mMap.addMarker(new MarkerOptions().position(latLng).title("mi posicion actual").icon(BitmapDescriptorFactory.fromResource(R.id.android_pay_dark)));
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Mi Ubicación").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.animateCamera(miUbicacion);

        Log.d("lat", String.valueOf(lat));
        Log.d("lng", String.valueOf(lng));
        Log.d("coordenadas", String.valueOf(latLng));
    }

    private void ActualizarMarcador(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            AniadirMarcador(lat, lng);
            Log.d("lat", String.valueOf(lat));
            Log.d("lng", String.valueOf(lng));
            Log.d("location", String.valueOf(location));
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public LatLng getLatLngPulsacion() {
        return latLngPulsacion;
    }

    public void setLatLngPulsacion(LatLng latLngPulsacion) {
        this.latLngPulsacion = latLngPulsacion;
    }
}// FIN MapsActivity


// ******************* DOCUMENTACION Y METODOS *******************
/*
 // Pasamos los parametros correspondientes
                            Intent intentParametros = new Intent(MapsActivity.this, BBDDActivity.class);
                            intentParametros.putExtra(String.valueOf(lat),"lat");
                            intentParametros.putExtra(String.valueOf(lng), "lng");
                            Toast.makeText(MapsActivity.this,"valor de lat antes de pasar a BBDDActivity: "+lat,Toast.LENGTH_LONG).show();
                            Toast.makeText(MapsActivity.this,"valor de lng antes de pasar a BBDDActivity: "+lng,Toast.LENGTH_LONG).show();
                            startActivity(intentParametros);


* */