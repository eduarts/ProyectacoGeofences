package com.example.edu.proyectacogeofences;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class BBDDActivity extends AppCompatActivity implements View.OnClickListener {

    User user;

    EditText etLat, etLng, etTitulo, etDescripcion, etProvincia, etRadio, etLocalidad, etCalle, etNumero, etCPostal;
    Button btnGuardar,btnModificar, btnCargar;
    ListView listaResultado;
    MapsActivity mapsActivity;
    ActivityUsuario activityUsuario;

    int idGeofence;
    String titulo;
    String descripcion;
    private String localidad;
    private String calle;
    private String numero;
    private String cPostal;
    private String provincia;
    // private String pais;
    private double latitud;
    private double longitud;
    private int radio;

    private double latModificar;
    private double lngModificar;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbdd);

        mapsActivity = new MapsActivity();
        activityUsuario=new ActivityUsuario();

        user=App.getUser();

        // etLat = (EditText)findViewById(R.id.etLat);// comentado en el html
        // etLng = (EditText)findViewById(R.id.etLng);// comentado en el html
        btnGuardar = (Button) findViewById(R.id.btnSave);
        btnModificar=(Button)findViewById(R.id.btnModificar);
        // btnCargar = (Button)findViewById(R.id.btnLoad);// comentado en el html
        // listaResultado = (ListView)findViewById(R.id.lvLista);// comentado en el html
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
        etProvincia = (EditText) findViewById(R.id.etProvincia);
        etLocalidad = (EditText) findViewById(R.id.etLocalidad);
        etCalle = (EditText) findViewById(R.id.etCalle);
        etNumero = (EditText) findViewById(R.id.etNumero);
        etCPostal = (EditText) findViewById(R.id.etCPostal);
        //etPais = (EditText) findViewById(R.id.etPais);
        etRadio = (EditText) findViewById(R.id.etRadio);

        btnGuardar.setOnClickListener(this);
        btnModificar.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {
            // Comprobar si viene de aniadir
            if (bundle.getBoolean("Aniadir")) {
                latitud = bundle.getDouble("latitud");
                longitud = bundle.getDouble("longitud");
                jsonDatosGeofenceLatLng(latitud, longitud);
            }else if (bundle.getBoolean("Modificar")) {
                // Comprobamos si viene de la opcion de modificar el geofence
                latModificar = bundle.getDouble("latModificar");
                lngModificar = bundle.getDouble("lngModificar");
                idUser = bundle.getInt("idUser");
                btnGuardar.setVisibility(View.INVISIBLE);
                btnModificar.setVisibility(View.VISIBLE);

                // recogemos los datos del geofence segun el idUser y lat y lng y los cargamos los datos en los editext
                cargarEditextModificar(latModificar,lngModificar,idUser);



                /********/


                //Toast.makeText(this, "latModificar: " + latModificar + " lngModificar: " + lngModificar + "y user: " + idUser, Toast.LENGTH_LONG).show();
            }
        }

        // Toast.makeText(this, "latitud: "+latitud+" longitud: "+longitud, Toast.LENGTH_LONG).show();
    }


    public void cargarEditextModificar(double latModif,double lngModif,int idUsu){
        // mandamos la consulta para recoger el geofence a modificar
        String URL="http://eduarts.es/geofences/recuperarGeofence.php?idUser="+idUsu+"&latModif="+latModif+"&lngModif="+lngModif;
        enviarDatosModificarGeofence(URL);
    }

    private void enviarDatosModificarGeofence(String URL) {
        // Enviamos los datos y recogemos el JSONArray
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        // Pasamos el JSONArray para procesarlo y cargarlo en los EditText
                        CargarEditTextModificar(ja);
                        //Toast.makeText(BBDDActivity.this, "Se ha modificado correctamente" , Toast.LENGTH_LONG).show();
                        Log.e("xxx","Se ha podido modificar el geofence");
                    } catch (JSONException e) {
                        Toast.makeText(BBDDActivity.this, "NO se ha podido modificar" , Toast.LENGTH_LONG).show();
                        Log.e("xxx","No se ha podido modificar el geofence");
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error en la respuesta enviarDatosModificarGeofence", Toast.LENGTH_LONG).show();
                Log.e("xxx", "Error en la respuesta enviarDatosModificarGeofence");
            }
        });
        queue.add(stringRequest);
    }

    public void CargarEditTextModificar(JSONArray ja){
    // Cargamos los EditText para que pueda modificar los valores que quiera.
        for (int i = 0; i < ja.length(); i += 13) {
            try {
                etTitulo.setText(ja.getString(i + 5));
                etDescripcion.setText(ja.getString(i + 6));
                etRadio.setText(ja.getString(i + 4));
                etProvincia.setText(ja.getString(i + 7));
                etLocalidad.setText(ja.getString(i + 8));
                etCalle.setText(ja.getString(i + 9));
                etNumero.setText(ja.getString(i + 10));
                etCPostal.setText(ja.getString(i + 11));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /* Una vez que el usuario cambie los valores el siguiente proceso de
        modificacion de geofence entra cuando pulse el boton de MODIFICAR */
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSave) {

            v.setEnabled(false);

            titulo=etTitulo.getText().toString();
            descripcion=etDescripcion.getText().toString();
            if ("".equals(etRadio.getText().toString())) {
                radio=100;
            }else{
                radio = Integer.parseInt(etRadio.getText().toString());
            }
            numero=etNumero.getText().toString();
            cPostal=etCPostal.getText().toString();
            provincia=etProvincia.getText().toString();
            localidad=etLocalidad.getText().toString();
            calle=etCalle.getText().toString();

            // 1º guardamos el geofence con todos los datos en la BBDD
            String string="http://eduarts.es/geofences/insertarGeofence.php?idUser="+user.getId()
                    +"&lat="+latitud+"&lng="+longitud+"&radio="+radio+"&titulo="+titulo
                    +"&desc="+descripcion+"&prov="+provincia+"&loca="+localidad+"&calle="+calle
                    +"&num="+numero+"&cp="+cPostal;

            String URL=string.replace(" ","+");
            Log.d("xxx","url insert Geofence: "+URL);
            EnviarRecibirDatos(URL);

        }else if(v.getId()==R.id.btnModificar){
            // Cargamos en las variables los nuevos valores
            titulo=etTitulo.getText().toString();
            descripcion=etDescripcion.getText().toString();
            if ("".equals(etRadio.getText().toString())) {
                radio=100;
            }else{
                radio = Integer.parseInt(etRadio.getText().toString());
            }
            numero=etNumero.getText().toString();
            cPostal=etCPostal.getText().toString();
            provincia=etProvincia.getText().toString();
            localidad=etLocalidad.getText().toString();
            calle=etCalle.getText().toString();

            // 1º Actualizamos el geofence con todos los datos en la BBDD
            String string="http://eduarts.es/geofences/actualizarGeofence.php?idUser="+user.getId()
                    +"&lat="+latModificar+"&lng="+lngModificar+"&radioModif="+radio+"&tituloModif="+titulo
                    +"&descModif="+descripcion+"&provModif="+provincia+"&locaModif="+localidad+"&calleModif="+calle
                    +"&numModif="+numero+"&cpModif="+cPostal;

            String URL=string.replace(" ","+");
            Log.d("xxx","url insert Geofence: "+URL);
            // Enviamos los nuevos valores
            enviarDatosModificados(URL);
            btnModificar.setEnabled(false);
        }
    }

    public void enviarDatosModificados(String URL){

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Comprobamos que los datos se han actualizado correctamente
                if (response.equals("true")) {
                    // Si la actualizacion es correcta, mandamos al usuario a la activityUsuario
                    Intent intentBBDD = new Intent(BBDDActivity.this, ActivityUsuario.class);
                    startActivity(intentBBDD);
                    btnModificar.setEnabled(true);
                    Log.e("xxx","Se ha podido modificar el geofence");
                    finish();
                }else{
                    Intent intentBBDD = new Intent(BBDDActivity.this, ActivityUsuario.class);
                    startActivity(intentBBDD);
                    Toast.makeText(getApplicationContext(), "No se ha podido actualizar", Toast.LENGTH_LONG).show();
                    Log.e("xxx","No se ha podido modificar el geofence");
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la respuesta enviarDatosModificados", Toast.LENGTH_LONG).show();
                Log.e("enviarDatosModificados", "Error en la respuesta");
            }
        });
        queue.add(stringRequest);
    }

    private void jsonDatosGeofenceLatLng(double lat, double lng) {

        double latPulsacion = lat;
        double lngPulsacion = lng;

        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+ latPulsacion + "," + lngPulsacion;

        HiloRutaWS hiloRutaWS = new HiloRutaWS(this);
        String respuesta = null;
        try {
            respuesta = hiloRutaWS.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            if (respuesta == null) {
                Log.e("xxx","Error en la llamada");
            } else {
                JSONObject jsonObject = new JSONObject(respuesta);
                provincia = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(3).getString("long_name");
                calle = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("long_name");
                numero = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("long_name");
                cPostal = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(6).getString("long_name");
                localidad = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("long_name");

                etCalle.setText(calle);
                etProvincia.setText(provincia);
                etNumero.setText(numero);
                etCPostal.setText(cPostal);
                etLocalidad.setText(localidad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void EnviarRecibirDatos(String URL) {
        //Toast.makeText(getApplicationContext(), "" + URL, Toast.LENGTH_LONG).show();

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
                        Log.e("xxx","respuesta SIN datos");
                        //Toast.makeText(getApplicationContext(), "respuesta SIN datos", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
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

    // METODO PARA CARGAR LOS VALORES
    public void CargarListView(JSONArray ja) {

        // 2º recuperamos el gofence guardado para obtener el id del geofence
        for (int i = 0; i < ja.length(); i += 13) {
            try {
                idGeofence=ja.getInt(i);
                //lista.add(ja.getString(i) + " " + ja.getString(i + 1) + " " + ja.getString(i + 2) + " " + ja.getString(i + 3));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 3º creamos un Area con los valores del geofence
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                //new UtilityGeofences().addGeofences(new Area1(latitud, longitud,user.getId(),radio,num,codPostal,titulo,descripcion,provincia,localidad,calle,System.currentTimeMillis()));
                new UtilityGeofences().addGeofences(new Area1(latitud,longitud,radio,idGeofence,titulo));
                return null;
            }
        }.execute();

        finish();

        // 4º le mandamos a la ActivityUsuario
        Intent intentBBDD = new Intent(BBDDActivity.this, ActivityUsuario.class);
        startActivity(intentBBDD);
        finish();
    }





}// FIN BBDDActivity




    /*private void jsonDatosGeofence(LatLng latLng) {

        double latPulsacion = latLng.latitude;
        double lngPulsacion = latLng.longitude;

        Log.d("latPulsacionBBDD", String.valueOf(latPulsacion));
        Log.d("lngPulsacionBBDD", String.valueOf(lngPulsacion));


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

                String poblacion = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(3).getString("long_name");
                String calle = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("long_name");
                String numero = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("long_name");
                String codPostal = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(6).getString("long_name");
                String localidad = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(3).getString("long_name");
                String pais = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("long_name");

                Toast.makeText(this, "La Direccion Completa de la pulsacion es: " + direccionCompleta, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "La poblacion es " + poblacion, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "La calle " + calle, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "El numero es " + numero, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "El codPostal es " + codPostal, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "La localidad es " + localidad, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "El pais es " + pais, Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/








// ******************* DOCUMENTACION Y METODOS *******************

// BOTON PARA CARGAR LOS VALORES

 /*      btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String consulta = "http://10.0.2.2/geofences/consultas.php";
                EnviarRecibirDatos(consulta);
            }
        });*/


                 /*
                Toast.makeText(this, "La Direccion Completa de la pulsacion es: " + direccionCompleta, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "La poblacion es " + poblacion, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "La calle " + calle, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "El numero es " + numero, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "El codPostal es " + codPostal, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "La localidad es " + localidad, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "El pais es " + pais, Toast.LENGTH_LONG).show();
                */