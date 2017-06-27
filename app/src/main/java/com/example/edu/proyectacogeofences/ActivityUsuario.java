package com.example.edu.proyectacogeofences;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ActivityUsuario extends AppCompatActivity
        implements
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback,
        LocationListener,
        View.OnClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener
        // ,NavigationView.OnNavigationItemSelectedListener
{

    MainActivity mainActivity;
    UtilityGeofences utilityGeofences;
    private User user;
    Geofence geofence;
    Geofence geofenceAmigo;
    Area1 area;
    Area1 areaAmigo;

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    Marker marker;

    Marker markerActuUbi;
    LocationManager locationManager;
    DrawerLayout drawer;

    double latActual;
    double lngActual;

    int idGeofencesPulsado;
    String tituloGeofencePulsado;
    double latGeofencesPulsado;
    double lngGeofencesPulsado;
    ListView listaResultado;
    ArrayList<Geofence> listaGeofences;
    ArrayList<Marker> listaMarcadores = new ArrayList<>();
    ArrayList<Area1> listaArea = new ArrayList<>();

    // AMIGOS
    Marker markerAmigo;
    double latAmigoGeofencesPulsado;
    double lngAmigoGeofencesPulsado;
    int idGeofencesAmigoPulsado;
    String nombreAmigo;
    String tituloAmigoGeofencePulsado;
    int idAmigoPulsado;
    ListView lvAmigos;
    ListView lvResultadoAmigos;
    ArrayList<Geofence> listaAmigoGeofences;
    ArrayList<Marker> listaAmigoMarcadores = new ArrayList<>();
    ArrayList<Area1> listaAmigoArea = new ArrayList<>();

    LocationManager lm;

    private Button notificacion, btnRregMiUbi, bbdd, btnAmigos,btnAniadirAmigo, btnMisGeoPointAtras;
    private TextView tvCabeceraListas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        mainActivity = new MainActivity();
        utilityGeofences = new UtilityGeofences();

        // location.getProvider() -> nos dice quién nos ha facilitado esta información
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Recogemos el usuario
        user = App.getUser();

        // REGISTRAR MI UBICACION
        btnRregMiUbi = (Button) findViewById(R.id.btnRegUbi);
        btnRregMiUbi.setOnClickListener(this);

        // LOCALIZARME
        btnAmigos = (Button) findViewById(R.id.btnAmigos);
        btnAmigos.setOnClickListener(this);

        btnAniadirAmigo=(Button)findViewById(R.id.btnAniadirAmigo);
        btnAniadirAmigo.setOnClickListener(this);

        btnMisGeoPointAtras=(Button)findViewById(R.id.btnMisGoePoint);
        btnMisGeoPointAtras.setOnClickListener(this);

        tvCabeceraListas=(TextView)findViewById(R.id.tvCabeceraListas);

        // Recogemos la lista donde mostrar los geofences
        listaResultado = (ListView) findViewById(R.id.listView1);
        lvAmigos = (ListView) findViewById(R.id.lvNombreAmigos);
        lvResultadoAmigos = (ListView) findViewById(R.id.lvGeofenceAmigo);


        //listaPrueba = new ArrayList<>();
        listaGeofences = new ArrayList<>();
        listaAmigoGeofences = new ArrayList<>();

        // Si los permisos son correctos
        if (verificarProvedorGPS() && isNetDisponible() && verificarPermisosManifest()) {
            // Preparamos el mapa
            iniciarMapa();
            
            // Recogemos el intent para saber de donde viene
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                // Comprobar si viene de el btnAmigos
                if (bundle.getBoolean("Amigos")) {
                    CargarAmigosYSusGeofences();
                    btnAniadirAmigo.setVisibility(View.VISIBLE);
                    btnMisGeoPointAtras.setVisibility(View.VISIBLE);
                    btnRregMiUbi.setVisibility(View.INVISIBLE);
                    btnAmigos.setVisibility(View.INVISIBLE);
                    tvCabeceraListas.setText("Mis Amigos");
                }else if(bundle.getBoolean("AniadirAmigos")){
                    popUpAniadirAmigo();
                    CargarAmigosYSusGeofences();
                    btnMisGeoPointAtras.setVisibility(View.VISIBLE);
                    btnAmigos.setVisibility(View.INVISIBLE);
                }else if(bundle.getBoolean("MisGeoPoint")){
                    CargarMisGeofences(user.getId());
                    tvCabeceraListas.setText("Mis GeoPoint");
                }
            } else {// Si no viene del btnAmigos
                // Cargamos la lista de item del usuario con los respectivos geofences
                CargarMisGeofences(user.getId());
                tvCabeceraListas.setText("Mis GeoPoint");
            }
        } else {
            btnAmigos.setEnabled(false);
            btnRregMiUbi.setEnabled(true);
            String mensaje = "LA FUNCION DE INTERNET Y GPS NO PUEDEN ESTAR DESHABILITADAS, POR FAVOR HABILITELAS";
            mostrarAvisoServicioDeshabilitado(mensaje);
        }

        lvAmigos.setVisibility(View.INVISIBLE);
        lvResultadoAmigos.setVisibility(View.INVISIBLE);

/*
        // NOTIFICACIONES
        notificacion = (Button) findViewById(R.id.notificacion);
        notificacion.setOnClickListener(this);

        // UBICARME
        ubicarme = (Button) findViewById(R.id.ubicarme);
        ubicarme.setOnClickListener(this);

        // BBDD
        bbdd = (Button) findViewById(R.id.bbdd);
        bbdd.setOnClickListener(this);
*/


        // Cargamos los geofences del usuario en el listView


/*
        // MENU LATERAL //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
*/
/*

        // MENU LATERAL //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
*/
    }




    //  ******************  PROCESAR DATOS PHP  ***************************
    public void enviarRecibirDatos(String URL) {

        //Toast.makeText(getApplicationContext(), "" + URL, Toast.LENGTH_LONG).show();
        Log.d("url: ", "" + URL);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        Log.i("sizejson", "" + ja.length());
                        CargarListView(ja);
                        //Toast.makeText(getApplicationContext(), "respuesta CON datos", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "respuesta SIN datos", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la respuesta enviarRecibirDatos", Toast.LENGTH_LONG).show();
                Log.e("enviarRecibirDatos", "Error en la respuesta");
            }
        });
        queue.add(stringRequest);
    }

    public void enviarRecibirDatosAmigos(String URL) {

        //Toast.makeText(getApplicationContext(), "" + URL, Toast.LENGTH_LONG).show();
        Log.d("url: ", "" + URL);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        Log.i("sizejson", "" + ja.length());
                        CargarListViewAmigos(ja);
                        //Toast.makeText(getApplicationContext(), "respuesta CON datos", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "respuesta SIN datos", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la respuesta enviarRecibirDatos", Toast.LENGTH_LONG).show();
                Log.e("enviarRecibirDatos", "Error en la respuesta");
            }
        });
        queue.add(stringRequest);
    }

    private void EnviarRecibirGeofencesAmigos(int idAmigoPulsado){
        String URL = "http://eduarts.es/geofences/recuperarGeofenceAmigo.php?idUserAmigo=" + idAmigoPulsado;
        recuperarGeofencesAmigo(URL);
    }

    private void recuperarGeofencesAmigo(String URL) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        CargarGeofencesAmigo(ja);
                        //Toast.makeText(getApplicationContext(), "respuesta CON datos", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "respuesta SIN datos", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Amigo sin GeoPoint", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la respuesta enviarRecibirDatos", Toast.LENGTH_LONG).show();
                Log.e("enviarRecibirDatos", "Error en la respuesta");
            }
        });
        queue.add(stringRequest);
    }

    public void borrarGeofence(double lat, double lng, final int position) {

        String registro = "http://eduarts.es/geofences/borrarGeofence.php?idUser=" + user.getId() + "&lat=" + lat + "&lng=" + lng;
        //String registro = "http://10.0.2.2/geofences/mostrarGeofences.php?idUser=" + idUser;
        Log.e("url", "" + registro);


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, registro, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("true")) {
                    // Si los permisos son correctos
                    if (verificarProvedorGPS() && isNetDisponible() && verificarPermisosManifest()) {
                        // Recargamos la activity para que se vuelvan a cargar los geofences y se activen de nuevo ya sin el que se ha borrado
                        Intent intent = new Intent(ActivityUsuario.this, ActivityUsuario.class);
                        startActivity(intent);
                    } else {
                        btnAmigos.setEnabled(false);
                        btnRregMiUbi.setEnabled(true);
                        String mensaje = "LA FUNCION DE INTERNET Y GPS NO PUEDEN ESTAR DESHABILITADAS, POR FAVOR HABILITELAS";
                        mostrarAvisoServicioDeshabilitado(mensaje);
                    }


                    Log.e("xxx", "Se ha podido borrar el geofence");
                } else {
                    Log.e("xxx", "No se ha podido borrar el geofence");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la respuesta enviarRecibirDatos", Toast.LENGTH_LONG).show();
                Log.e("enviarRecibirDatos", "Error en la respuesta");
            }
        });
        queue.add(stringRequest);

    }

    public void CargarMisGeofences(int idUser) {
        // Pedimos los datos por URL segun el idUser

        String registro = "http://eduarts.es/geofences/mostrarGeofences.php?idUser=" + idUser;
        //String registro = "http://10.0.2.2/geofences/mostrarGeofences.php?idUser=" + idUser;
        enviarRecibirDatos(registro);
    }

    private void CargarAmigosYSusGeofences() {
        String URL = "http://eduarts.es/geofences/recuperarAmigos.php?idUser=" + user.getId();
        enviarRecibirDatosAmigos(URL);
    }







    //  ************** INTERFACE USUARIO  ************
    public void CargarListView(final JSONArray ja) {

        final ArrayList<String> listaImprimir = new ArrayList<>();

        // Creamos un contador para ver cuantas filas tiene el JsonArray(ja), porque cada 13 posiciones empieza otro geofence
        int contador = 0;

        for (int i = 0; i < ja.length(); i += 13) {
            contador++;
            try {
                listaImprimir.add(ja.getString(i + 5) + ": " + ja.getString(i + 6));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Añadimos la lista al adapter para mostrara por pantalla
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaImprimir);
        listaResultado.setAdapter(adaptador);

        // Recogemos el contador como variable final
        final int finalContador = contador;

        // Recorremos las filas del ja segun el contador que hemos creado
        for (int j = 0; j <= finalContador; j++) {
            try {
                //myView.setBackgroundColor(0x0000FF00);
                // luego lo multiplicamos x13 para llegar a la parte del String json que necesitamos
                // y recuperamos los valores necesarios
                idGeofencesPulsado = ja.getInt(j * 13);
                String latStringPulsado = ja.getString(j * 13 + 2);
                String lngStringPulsado = ja.getString(j * 13 + 3);
                tituloGeofencePulsado = ja.getString(j * 13 + 5);

                // Recogemos los valores de la lat y lng y los pasamos a double para poder mandarselos a GOOGLE REST
                // y que asi nos los pueda mostrar en el mapa

                latGeofencesPulsado = Double.parseDouble(latStringPulsado);
                lngGeofencesPulsado = Double.parseDouble(lngStringPulsado);

                // Creamos un objeto geofence y lo añadimos a una lista de geofences
                geofence = new Geofence(ja.getInt(j * 13), ja.getInt(j * 13 + 1), ja.getDouble(j * 13 + 2),
                        ja.getDouble(j * 13 + 3), ja.getInt(j * 13 + 4), ja.getString(j * 13 + 5),
                        ja.getString(j * 13 + 6), ja.getString(j * 13 + 7), ja.getString(j * 13 + 8),
                        ja.getString(j * 13 + 9), ja.getString(j * 13 + 10), ja.getString(j * 13 + 11),
                        ja.getString(j * 13 + 12));

                area = new Area1(ja.getDouble(j * 13 + 2), ja.getDouble(j * 13 + 3), ja.getInt(j * 13 + 4), ja.getInt(j * 13), ja.getString(j * 13 + 5));

                AniadirMarcadorGeofence(ja.getDouble(j * 13 + 2), ja.getDouble(j * 13 + 3), ja.getString(j * 13 + 5), ja.getInt(j * 13 + 4));

                listaGeofences.add(geofence);
                listaArea.add(area);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CargarHiloGeofences(listaArea);

        listaResultado.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < listaMarcadores.size(); i++) {
                    if (position == i) {
                        String mensaje = "Desea borrar el geofence, esta accion no puede deshacerse.";

                        // recogemos la lat y lng del marcador
                        double lat = listaMarcadores.get(i).getPosition().latitude;
                        double lng = listaMarcadores.get(i).getPosition().longitude;

                        // sacamos popUp para borrar geofence
                        popUpBorrarGeofence(mensaje, lat, lng, position);

                        //Log.d("yy","id marcador :"+ listaMarcadores.get(i).getId().toString());
                        Log.d("yy", "position marcador :" + listaMarcadores.get(i).getPosition());
                    }
                }

                Log.d("yy", "view.getId(): " + view.getId());
                Log.d("yy", "position :" + position);
                Log.d("yy", "id :" + id);

                return false;
            }
        });

        // Creamos la lista como listener
        listaResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {

                for (int i = 0; i <= listaMarcadores.size() + 1; i++) {

                    if (i == myItemInt) {
                        myView.setBackgroundColor(0x0000FF00);
                        int zoom = 19;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listaMarcadores.get(i).getPosition(), zoom));
                        //onMarkerClick(listaMarcadores.get(i));
                    }
                }
            }
        });
    }

    public void CargarListViewAmigos(final JSONArray ja) {

        final ArrayList<String> listaImprimirAmigos = new ArrayList<>();

        // Creamos un contador para ver cuantas filas tiene el JsonArray(ja), porque cada 13 posiciones empieza otro geofence
        int contador = 0;

        for (int i = 0; i < ja.length(); i += 6) {
            contador++;
            try {
                listaImprimirAmigos.add(ja.getString(i + 3)+": "+ja.getString(i+4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        lvResultadoAmigos.setVisibility(View.INVISIBLE);
        listaResultado.setVisibility(View.INVISIBLE);
        lvAmigos.setVisibility(View.VISIBLE);


        // Añadimos la lista al adapter para mostrara por pantalla
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaImprimirAmigos);
        lvAmigos.setAdapter(adaptador);

        // Recogemos el contador como variable final
        final int finalContador = contador;

/*
        // Recorremos las filas del ja segun el contador que hemos creado
        for (int j = 0; j <= finalContador; j++) {
            try {


                AniadirMarcadorGeofence(ja.getDouble(j * 13 + 2), ja.getDouble(j * 13 + 3), ja.getString(j * 13 + 5), ja.getInt(j * 13 + 4));

                listaGeofences.add(geofence);
                listaArea.add(area);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */

        // Creamos la lista como listener

        lvAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {

                for (int i = 0; i <= finalContador; i++) {

                    if (i == myItemInt) {
                        try {
                            idAmigoPulsado=ja.getInt(i*6+2);
                            nombreAmigo=ja.getString(i*6+3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Enviamos peticion y recuperamos los geofences segun el idAmigoPulsado
                        EnviarRecibirGeofencesAmigos(idAmigoPulsado);
                        Log.d("ddd", String.valueOf(idAmigoPulsado));
                    }
                }
            }
        });
    }

    public void CargarGeofencesAmigo(final JSONArray ja){
        final ArrayList<String> listaImprimirGeofencesAmigo = new ArrayList<>();

        // Creamos un contador para ver cuantas filas tiene el JsonArray(ja), porque cada 13 posiciones empieza otro geofence
        int contador = 0;

        for (int i = 0; i < ja.length(); i += 13) {
            contador++;
            try {
                listaImprimirGeofencesAmigo.add(ja.getString(i + 5) + ": " + ja.getString(i + 6));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Añadimos la lista al adapter para mostrara por pantalla
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaImprimirGeofencesAmigo);
        lvResultadoAmigos.setAdapter(adaptador);

        lvAmigos.setVisibility(View.INVISIBLE);
        listaResultado.setVisibility(View.INVISIBLE);
        lvResultadoAmigos.setVisibility(View.VISIBLE);
        tvCabeceraListas.setText("GeoPoint de "+nombreAmigo);

        final int finalContador = contador;




        // Recorremos las filas del ja segun el contador que hemos creado
        for (int j = 0; j <= finalContador; j++) {
            try {
                //myView.setBackgroundColor(0x0000FF00);
                // luego lo multiplicamos x13 para llegar a la parte del String json que necesitamos
                // y recuperamos los valores necesarios
                //idGeofencesAmigoPulsado=ja.getInt(j*13);
                String latAmigoStringPulsado = ja.getString(j * 13 + 2);
                String lngAmigoStringPulsado = ja.getString(j * 13 + 3);
                tituloAmigoGeofencePulsado = ja.getString(j * 13 + 5);


                // Recogemos los valores de la lat y lng y los pasamos a double para poder mandarselos a GOOGLE REST
                // y que asi nos los pueda mostrar en el mapa

                //latAmigoGeofencesPulsado = Double.parseDouble(latAmigoStringPulsado);
                //lngAmigoGeofencesPulsado = Double.parseDouble(lngAmigoStringPulsado);

                // Creamos un objeto geofence y lo añadimos a una lista de geofences
                geofenceAmigo = new Geofence(ja.getInt(j * 13), ja.getInt(j * 13 + 1), ja.getDouble(j * 13 + 2),
                        ja.getDouble(j * 13 + 3), ja.getInt(j * 13 + 4), ja.getString(j * 13 + 5),
                        ja.getString(j * 13 + 6), ja.getString(j * 13 + 7), ja.getString(j * 13 + 8),
                        ja.getString(j * 13 + 9), ja.getString(j * 13 + 10), ja.getString(j * 13 + 11),
                        ja.getString(j * 13 + 12));

                areaAmigo = new Area1(ja.getDouble(j * 13 + 2), ja.getDouble(j * 13 + 3), ja.getInt(j * 13 + 4), ja.getInt(j * 13), ja.getString(j * 13 + 5));

                AniadirMarcadorGeofenceAmigo(ja.getDouble(j * 13 + 2), ja.getDouble(j * 13 + 3), ja.getString(j * 13 + 5), ja.getInt(j * 13 + 4));

                listaAmigoGeofences.add(geofenceAmigo);
                listaAmigoArea.add(areaAmigo);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        lvResultadoAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {

                for (int i = 0; i <= listaAmigoMarcadores.size(); i++) {
                    if (i == myItemInt) {
                        int zoom = 16;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listaAmigoMarcadores.get(i).getPosition(), zoom));
                    }
                }
            }
        });




        lvResultadoAmigos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {



                // Recorremos las filas del ja segun el contador que hemos creado
                for (int j = 0; j <= finalContador; j++) {
                        //myView.setBackgroundColor(0x0000FF00);
                        // luego lo multiplicamos x13 para llegar a la parte del String json que necesitamos
                        // y recuperamos los valores necesarios
                    if (j == position) {
                        try {
                            idGeofencesAmigoPulsado = ja.getInt(j * 13);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }







                for (int i = 0; i < listaAmigoMarcadores.size(); i++) {
                    if (position == i) {
                        String mensaje = "Desea Guardar el geofence de su amigo "+nombreAmigo+"?";

                        // recogemos la lat y lng del marcador
                        double latAmigo = listaAmigoMarcadores.get(i).getPosition().latitude;
                        double lngAmigo = listaAmigoMarcadores.get(i).getPosition().longitude;

                        // sacamos popUp para borrar geofence
                        popUpGuardarGeofenceAmigo(mensaje, latAmigo, lngAmigo, position);

                        //Log.d("yy","id marcador :"+ listaMarcadores.get(i).getId().toString());
                        Log.d("yy", "position marcador :" + listaAmigoMarcadores.get(i).getPosition());
                    }
                }

                return false;
            }
        });






    }

    private void popUpAniadirAmigo() {

        final String title="";
        TextView tv = new TextView(this);

        tv.setText(title);
        // tv.setPadding(40, 40, 40, 40);
        tv.setHeight(10);
        tv.setWidth(10);
        tv.setTextSize(15);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        et.setWidth(10);


        new AlertDialog.Builder(this)
                .setView(et)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Introduce el Email de tu amigo.")
                .setCustomTitle(tv)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailAmigo = et.getText().toString();
                        AniadirAmigo(emailAmigo);


                        //Toast.makeText(ActivityUsuario.this,"Yaay" + emailAmigo,Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(android.R.string.no,null).show();
    }

    public void AniadirAmigo(String emailAmigo){
        String URL="http://eduarts.es/geofences/aniadirAmigos.php?emailAmigoAniadir="+emailAmigo+"&idUser="+user.getId();
        enviarAniadirAmigo(URL);
    }

    private void enviarAniadirAmigo(String URL) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("true")) {
                    CargarAmigosYSusGeofences();
                    Toast.makeText(getApplicationContext(), "Se ha añadido correctamente", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "no se ha añadido", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error en la respuesta", Toast.LENGTH_LONG).show();
                Log.e("xxx","Error en la respuesta");
            }
        });
        queue.add(stringRequest);
    }

    private void CargarHiloGeofences(final ArrayList<Area1> listaArea) {

        // Llamamos al metodo de addGeofences para pasarle la lista de geofences
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                new UtilityGeofences().addGeofences(listaArea);
                return null;
            }
        }.execute();
    }

    public void popUpBorrarGeofence(String mensaje, final double lat, final double lng, final int position) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(mensaje);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //borrarGeofence(lat, lng, position);
                String mensajeCon="Esta accion no puede deshacerse, esta seguro de borrar el GeoPoint?";

                AlertDialog.Builder dialogo =  new AlertDialog.Builder(ActivityUsuario.this);
                dialogo.setMessage(mensajeCon);
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarGeofence(lat, lng, position);
                    }
                }).setNegativeButton("NO",null).setCancelable(true);
                dialogo.show();
            }
        }).setNegativeButton("Modificar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ActivityUsuario.this, BBDDActivity.class);
                intent.putExtra("Modificar", true);
                intent.putExtra("latModificar", lat);
                intent.putExtra("lngModificar", lng);
                intent.putExtra("idUser", user.getId());
                startActivity(intent);
                finish();
            }
        }).setCancelable(true);
        dialog.show();
    }

    public void popUpGuardarGeofenceAmigo(String mensaje, final double lat, final double lng, final int position) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(mensaje);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviarDatosCopiarGeofenceAmigo();
            }
        }).setCancelable(true);
        dialog.show();
    }

    public void enviarDatosCopiarGeofenceAmigo(){

    String URL="http://eduarts.es/geofences/guardarGeofenceAmigo.php?idUser="+user.getId()+"&idGeofenceAmigo="+idGeofencesAmigoPulsado;
    comprobarCopiaGeofenceAmigo(URL);



}

    public void comprobarCopiaGeofenceAmigo(String URL){
    RequestQueue queue = Volley.newRequestQueue(this);
    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (response.equals("true")) {
               Toast.makeText(getApplicationContext(), "Se ha añadido correctamente", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "no se ha añadido", Toast.LENGTH_LONG).show();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //Toast.makeText(getApplicationContext(), "Error en la respuesta", Toast.LENGTH_LONG).show();
            Log.e("xxx","Error en la respuesta");
        }
    });
    queue.add(stringRequest);
}

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        String mensaje = "¿Quieres salir de la aplicacion?";
        //confirmarSalir(mensaje);

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    // Si confirmamos que si queremos salir...
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aqui estamos cuando acepta que quiere salir

                        // Mandamos a la activity de rellenar los datos del geofending de interes
                        Intent intentBBDD = new Intent(ActivityUsuario.this, MainActivity.class);
                        startActivity(intentBBDD);
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, null).show();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnRegUbi) {
            registrarMiUbicacionComoGeofence();
        } else if (v.getId() == R.id.btnAmigos) {
            //cargarMiLocalizacionActual();
            Intent intentAmigos = new Intent(ActivityUsuario.this, ActivityUsuario.class);
            // pasamos los valores de interes
            intentAmigos.putExtra("Amigos",true);
            // Cambiamos de activity
            startActivity(intentAmigos);
            finish();
        }else if(v.getId()==R.id.btnAniadirAmigo){
            //cargarMiLocalizacionActual();
            Intent intentAmigos = new Intent(ActivityUsuario.this, ActivityUsuario.class);
            // pasamos los valores de interes
            intentAmigos.putExtra("AniadirAmigos",true);
            // Cambiamos de activity
            startActivity(intentAmigos);
            finish();
        }else if(v.getId()==R.id.btnMisGoePoint){
            //cargarMiLocalizacionActual();
            Intent intentAmigos = new Intent(ActivityUsuario.this, ActivityUsuario.class);
            // pasamos los valores de interes
            intentAmigos.putExtra("MisGeoPoint",true);
            // Cambiamos de activity
            startActivity(intentAmigos);
            finish();
        }
    }

    private void registrarMiUbicacionComoGeofence() {
        // 1º recogemos nuestra ubicacion con location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);

        if (location != null) {
            // 2º mandamos a la BBDDActivity y lo guardamos
            Intent intentBBDD = new Intent(ActivityUsuario.this, BBDDActivity.class);

            // pasamos los valores de interes
            intentBBDD.putExtra("Aniadir", true);
            intentBBDD.putExtra("latitud", location.getLatitude());
            intentBBDD.putExtra("longitud", location.getLongitude());

            // Cambiamos de activity
            startActivity(intentBBDD);
        } else {
            String mensaje = "LA FUNCION DE INTERNET Y GPS NO PUEDEN ESTAR DESHABILITADAS, POR FAVOR HABILITELAS";
            mostrarAvisoServicioDeshabilitado(mensaje);
        }
    }






    //  **************** MAPA  ****************
    private void iniciarMapa() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Por defecto tenemos que cargar la posicion del usuario para que si
        // esta en un punto de interes pueda añadir el geofences donde se encuentra

        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


        // Modificar mapas y marcadores
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Cargamos nuestra ubicacion por defecto
        posicionActual();
    }

    @Override
    public void onMapClick(final LatLng latLng) {
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
                    .setMessage("Quieres añadir esta localizacion a un punto de interes?")
                    .setCustomTitle(tv)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        // Si confirmamos que si queremos guardar el punto de interes...
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Aqui estamos cuando acepta que quiere guardar un marcador de interes

                            //setLatLngPulsacion(latLng);

                            // mandamos a la activity de rellenar los datos del geofending de interes
                            Intent intentBBDD = new Intent(ActivityUsuario.this, BBDDActivity.class);

                            // pasamos los valores de interes
                            intentBBDD.putExtra("Aniadir",true);
                            intentBBDD.putExtra("latitud", latLng.latitude);
                            intentBBDD.putExtra("longitud", latLng.longitude);

                            // Cambiamos de activity
                            startActivity(intentBBDD);
                            finish();

                            // Toast.makeText(this, "latitud de la pulsacion " + latLng.latitude, Toast.LENGTH_SHORT).show();
                            // Toast.makeText(this, "longitud de la pulsacion " + latLng.longitude, Toast.LENGTH_SHORT).show();

                            /*
                             * cuando acepta que quiere guardar el marcador le mandamos a la activity de relleno de info
                             * y le tenemos que conectar con los servicios REST de google para cargarle la informacion en
                             * la pantalla de la calle, poblacion ...
                             */

                            // Comprobamos que los datos se han añadido correctamente, si es asi, Añadimos el marcador al mapa
                            //ActualizarMarcador(latLng.latitude,latLng.longitude);

                            //jsonDatosGeofence(latLng);

                        }
                    }).setNegativeButton(android.R.string.no, null).show();
        }
    }

    private void posicionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ActualizarMarcador(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);

        Log.d("location manager", String.valueOf(locationManager));
    }

    public void AniadirMarcador(double lat, double lng) {
        // Recogemos el zoom
        float zoom = 16;

        // recogemos la lat y lng juntas
        LatLng latLngMiUbicacion = new LatLng(lat, lng);

        // Comprobamos si hay un marcador y si esta lo borramos para posicionar el nuevo
        if (markerActuUbi != null) {
            markerActuUbi.remove();
        }

        // Añadimos el marcador de la ubicacion actual
        markerActuUbi = mMap.addMarker(new MarkerOptions().position(latLngMiUbicacion).title("Mi Posicion Actual")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // le decimos a la camara de google que se posicione en la lat, lng y zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMiUbicacion, zoom));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngMiUbicacion));

        Log.d("lat", String.valueOf(lat));
        Log.d("lng", String.valueOf(lng));
        Log.d("coordenadas", String.valueOf(latLngMiUbicacion));
    }

    public void AniadirMarcadorGeofence(double lat, double lng, String titulo, int radio) {

        // Recogemos el zoom
        float zoom = 16;

        // recogemos la lat y lng juntas
        LatLng latLngMiUbicacion = new LatLng(lat, lng);

        // Añadimos el marcador de la ubicacion actual
        marker = mMap.addMarker(new MarkerOptions().position(latLngMiUbicacion).title(titulo)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        listaMarcadores.add(marker);

        double radiusInMeters = 100.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radio)
                .strokeColor(strokeColor).fillColor(shadeColor));

        Log.d("lat", String.valueOf(lat));
        Log.d("lng", String.valueOf(lng));
        Log.d("coordenadas", String.valueOf(latLngMiUbicacion));
    }

    public void AniadirMarcadorGeofenceAmigo(double lat, double lng, String titulo, int radio) {

        // Recogemos el zoom
        float zoom = 16;

        // recogemos la lat y lng juntas
        LatLng latLngAmigo = new LatLng(lat, lng);

        // Añadimos el marcador de la ubicacion actual
        markerAmigo = mMap.addMarker(new MarkerOptions().position(latLngAmigo).title(titulo)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        listaAmigoMarcadores.add(markerAmigo);

        double radiusInMeters = 100.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radio)
                .strokeColor(strokeColor).fillColor(shadeColor));

        Log.d("lat", String.valueOf(lat));
        Log.d("lng", String.valueOf(lng));
        Log.d("coordenadas", String.valueOf(latLngAmigo));
    }

    private void ActualizarMarcador(Location location) {
        if (location != null) {
            latActual = location.getLatitude();
            lngActual = location.getLongitude();
            AniadirMarcador(latActual, lngActual);

            Log.d("lat", String.valueOf(latActual));
            Log.d("lng", String.valueOf(lngActual));
            Log.d("location", String.valueOf(location));
        }
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            LatLng latLngActualizandose = new LatLng(location.getLatitude(), location.getLongitude());

            // Comprobamos si hay un marcador y si esta lo borramos para posicionar el nuevo
            if (markerActuUbi != null) {
                markerActuUbi.remove();
            }

            // Añadimos el marcador de la ubicacion actual
            markerActuUbi = mMap.addMarker(new MarkerOptions().position(latLngActualizandose).title("Mi Posicion Actual")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngActualizandose));
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


    //  ****************  PERMISOS GPS, INTERNET, GOOGLE API  ******************
    private boolean verificarProvedorGPS() {
        // Comprobamos si está habilitado el proveedor GPS.
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //String mensaje="LA FUNCION DEL GPS NO PUEDE ESTAR DESHABILITADA; POR FAVOR HABILITELA";
            //mostrarAvisoServicioDeshabilitado(mensaje);
            return false;
        }
        return true;
    }

    // Con este metodo consultamos si la red esta activa y determinar si hay conexión a Internet.
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    private boolean verificarPermisosManifest() {


        // 1. Comprobamos si tiene concedidos los permisos de red, ubicacion
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        return false;
    }

    // mensaje para que vuelva al MainActivity y conecte los datos de internet antes de acceder al mapa
    public void mostrarAvisoServicioDeshabilitado(String mensaje) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(mensaje);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // mandamos a la activity principal para que acceda con internet
                //Intent intent = new Intent(ActivityUsuario.this, MainActivity.class);
                //startActivity(intent);
            }
        });
        dialog.show();
    }


















    // #######################

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(this, "El titulo de este marcador es: " + marker.getTitle()+ "y el id: "+marker.getId(),Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //Toast.makeText(this, "has pulsado el marcador: " + marker.getPosition(), Toast.LENGTH_LONG).show();

        return false;
    }

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


    // Crear notificaciones
    public void Notificacion(String titulo, String text, String tiker, String info) {

        // Creamos la notificacion
        NotificationCompat.Builder notificacion = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(titulo)
                .setContentText(text)
                .setTicker(tiker)
                .setContentInfo(info);

        // Creamos un intent pendiente para luego pasarselo al servicio de notificaciones
        Intent intentNotificacion = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNotificacion, 0);

        // Creamos la notificacion con el intentPendiente y la lanzamos
        notificacion.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10, notificacion.build());
    }

    public void BorrarTodasNotificaciones() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // para borrar las notificaciones por id
        // notificationManager.cancel("aqui va el id");
    }

    public void confirmarSalir(String mensaje) {


    }

    public void borrarMarcadorGeofenceBorrado(int position) {

        for (int i = 0; i < listaMarcadores.size(); i++) {
            if (position == i) {
                listaMarcadores.get(i).remove();
            }
        }


    }

    private void cargarMiLocalizacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);

        float zoom = 16;

        if (location != null) {
            LatLng miLocalizacion = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miLocalizacion, zoom));
        } else {
            String mensaje = "LA FUNCION DE INTERNET Y GPS NO PUEDEN ESTAR DESHABILITADAS, POR FAVOR HABILITELAS";
            mostrarAvisoServicioDeshabilitado(mensaje);
        }
    }


}

/*
    // ***********  MENU LATERAL  *******************
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mi_cuenta) {
            // Handle the camera action
        } else if (id == R.id.mis_contactos) {

        } else if (id == R.id.mis_geofences) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/





/*  public void ActualizarMarcador(double lat, double lng) {

        LatLng latLngMiUbicacion = new LatLng(lat, lng);

        // Comprobamos si hay un marcador y si esta lo borramos para posicionar el nuevo
       if (marker != null) {
            marker.remove();
        }

        // Añadimos el marcador de la ubicacion actual
       mMap.addMarker(new MarkerOptions().position(latLngMiUbicacion).title("Mi Posicion Actual")
               .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        // le decimos a la camara de google que se posicione en la lat, lng y zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMiUbicacion,18));

            Log.d("lat", String.valueOf(latActual));
            Log.d("lng", String.valueOf(lngActual));
    }*/


//final ArrayList<Object> listaJson = new ArrayList<>();
// recogemos cada fila del json y la añadimos a una lista
       /* for (int i = 0; i < ja.length(); i += 13) {
            try {
                listaJson.add(ja.get(i) + "," + ja.get(i + 1) + "," + ja.get(i + 2) + "," + ja.get(i + 3) + ","
                        + ja.get(i + 4) + "," + ja.get(i + 5) + "," + ja.get(i + 6) + ","
                        + ja.get(i + 7) + "," + ja.get(i + 8) + "," + ja.get(i + 9) + ","
                        + ja.get(i + 10) + "," + ja.get(i + 11) + "," + ja.get(i + 12));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/








   /* public void cargarGeoManual(){

        Area1 rotondaVillarejo=new Area1(40.165451, -3.282900,100);
        Area1 rotondaBelmonte=new Area1(40.133594, -3.332389,100);
        Area1 puertaValencia=new Area1(40.133208, -3.336505,100);
        Area1 viñaLosPaises=new Area1(40.143392, -3.317935,100);
        Area1 casa=new Area1(40.135463, -3.341028,100);
        Area1 juli=new Area1(40.171067, -3.270401,100);

        listaPrueba.add(rotondaBelmonte);
        listaPrueba.add(rotondaVillarejo);
        listaPrueba.add(puertaValencia);
        listaPrueba.add(viñaLosPaises);
        listaPrueba.add(juli);
        listaPrueba.add(casa);

        // Ejecutamos el hilo en segundo plano y le mandamos la lista de geofences
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
               // new UtilityGeofences().addGeofences(new Area1(latitud, longitud, 100));
                new UtilityGeofences().addGeofences(listaPrueba);
                return null;
            }
        }.execute();

        finish();
    }*/






       /*
        @Override
        public void onClick(View v{
        /*    if (v.getId() == R.id.notificacion) {
                // Toast.makeText(MainActivity.this,"notificacion pulsado",Toast.LENGTH_SHORT).show();
                Notificacion("Prueba notificacion titulo", "Prueba notificacion texto", "Prueba notificacion tiker", "8");
                //dialog = new ProgressDialog(this);
                //dialog.setTitle(R.string.esperandoGPS);
                //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //dialog.setCancelable(false);
                //dialog.show();
            } else if (v.getId() == R.id.ubicarme) {
                //cargarGeoManual();
                //AniadirMarcador(37.7750, 122.4183);
                //Intent intent = new Intent(ActivityUsuario.this, MapsActivity.class);
                //startActivity(intent);
            } else if (v.getId() == R.id.bbdd) {
                // Toast.makeText(MainActivity.this,"BBDD pulsado ",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityUsuario.this, BBDDActivity.class);
                startActivity(intent);
            }
        }
    */


//***********           **********          ********
  /* public void comprobarPermisos(){



        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);

        Location location=locationManager.getLastKnownLocation(Context.LOCATION_SERVICE);




        // 1. Comprobamos si tiene concedidos los permisos de red, ubicacion
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            Toast.makeText(this, " permisos de ubicacion correctos", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, "necesitas conceder permisos de ubicacion", Toast.LENGTH_LONG).show();

            // Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            //  startActivity(intent);
        }

        // 2. Consultar la red activa y determinar si hay conexión a Internet.
        if (isNetDisponible()) {
            Toast.makeText(this, "conexion correcta internet", Toast.LENGTH_LONG).show();

        } else {
            String mensaje = "Esta aplicacion no se puede usar sin acceso a la red";

            //popUpConfirmacionConexiones(mensaje);
            Toast.makeText(this, "necesitas tener internet", Toast.LENGTH_LONG).show();
        }

        // Comprobamos que la ubicacion esta establecida ya
        if (ConexionGPSok(location)) {
            Toast.makeText(this, "conexion con GPS correcta", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "conexion con GPS INCORRECTA", Toast.LENGTH_LONG).show();
            String mensaje = "Esta aplicacion no se puede usar sin ubicacion del GPS";

            //popUpConfirmacionConexiones(mensaje);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

    }
*/


/*
    public boolean verificarServGoogle(){

        boolean correcto=true;

        // Verificamos si los servicios de internet estan correctos
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.show();
            correcto=false;
        }

        Log.d("status", String.valueOf(status));

        return correcto;
    }

    private boolean ConexionGPSok(Location location) {

        boolean status;
        double lat,lng;

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            //AniadirMarcador(lat, lng);
            Log.d("lat ConexionGPSok", String.valueOf(lat));
            Log.d("lng ConexionGPSok", String.valueOf(lng));
            Log.d("location", String.valueOf(location));
            status = true;

        } else {
            status = false;
        }

        return status;
    }


    bundle.getBoolean("Amigos")

    new AlertDialog.Builder(this)
                    .setView(et)
                    .setTitle(title)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Seguro que quieres añadir este marcador? Introduce titulo.")
                    .setCustomTitle(tv)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Toast.makeText(MapsActivity.this,"Yaay",Toast.LENGTH_SHORT).show();
                            AniadirMarcadorPulsacion(latitudLongitud,title);
                        }
                    }).setNegativeButton(android.R.string.no,null).show();

*/