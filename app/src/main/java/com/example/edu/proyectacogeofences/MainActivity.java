package com.example.edu.proyectacogeofences;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private Button aceptar, registrarse;
    private EditText emailUser, pass;
    LocationManager lm;
    ProgressDialog dialog;
    BBDDActivity bbddActivity;
    ActivityUsuario activityUsuario;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user= new User();

        // creamos el location Manager para comprobar despues si tenemos los servicio de geoposicionamiento del dispositivo
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        activityUsuario = new ActivityUsuario();




        // bbddActivity=new BBDDActivity();

        // MENU LATERAL //
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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

        // NOMBRE USUARIO Y PASS
        emailUser = (EditText) findViewById(R.id.etEmail);
        pass = (EditText) findViewById(R.id.etPass);

        emailUser.setText("sergio@gmail.com");
        pass.setText("1234");


        // ACEPTAR
        aceptar = (Button) findViewById(R.id.btnAceptar);
        aceptar.setOnClickListener(this);

        // REGISTRARSE
        registrarse = (Button) findViewById(R.id.btnRegistrar);
        registrarse.setOnClickListener(this);

        // verificamos los servicios del proovedor GPS y la conexion a internet


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnAceptar) {
            // MANDAMOS LOS VALORES POR GET AL PHP
            // Le pasamos los valores del punto del mapa pulsado si acepta guardarlos
            String registro = "http://eduarts.es/geofences/login.php?emailUser="+emailUser.getText().toString()+"&pass="+pass.getText().toString();
            //String registro = "http://10.0.2.2/geofences/login.php?emailUser=" + emailUser.getText().toString() + "&pass=" + pass.getText().toString();

            if (verificarProvedorGPS() && isNetDisponible() && verificarPermisosManifest()){
                enviarDatosLogin(registro);
                //Toast.makeText(getApplicationContext(), "Enviado", Toast.LENGTH_LONG).show();
                Log.i("registro", registro);
            }else{
               // aceptar.setEnabled(false);
               // registrarse.setEnabled(true);
                String mensaje="LA FUNCION DE INTERNET Y GPS NO PUEDEN ESTAR DESHABILITADAS, POR FAVOR HABILITELAS";
                mostrarAvisoServicioDeshabilitado(mensaje);
            }
        } else if (v.getId() == R.id.btnRegistrar) {
            Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void enviarDatosLogin(String URL) {

        //Toast.makeText(getApplicationContext(),URL, Toast.LENGTH_LONG).show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        Log.i("sizejson", "" + ja.length());
                        comprobarUser(ja);
                        //Toast.makeText(getApplicationContext(), "respuesta CON datos", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "respuesta SIN datos", Toast.LENGTH_LONG).show();
                        Log.e("enviarDatosLogin","respuesta SIN datos");
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la respuesta", Toast.LENGTH_LONG).show();
                Log.e("enviarDatosLogin","Error en la respuesta");
            }
        });

        queue.add(stringRequest);
    }

    // METODO PARA COMPROBAR SI EL USUARIO ESTA EN LA BBDD O NO
    public void comprobarUser(JSONArray ja) throws JSONException {

        String passBBDD;

        for (int i = 0; i < 1; i++) {
            passBBDD = ja.getString(2);
            if (passBBDD.equals(pass.getText().toString())) {
                // Toast.makeText(getApplicationContext(), "pass iguales", Toast.LENGTH_LONG).show();

                // Recogemos el usuario con sus valores
                user = new User(ja.getInt(0),ja.getString(1), ja.getString(3), ja.getString(2), true);
                // si es correcto le pasamos el usuario
                App.setUser(user);
                // y mandamos a la ActivityUsuario
                Intent intent = new Intent(MainActivity.this, ActivityUsuario.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Contraseña incorrecta, NO estas registrado?", Toast.LENGTH_LONG).show();
                Log.e("comprobarUser","Contraseña incorrecta, NO estas registrado?");
            }
        }
        emailUser.setText("");
        pass.setText("");
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

    // mensaje para que vuelva al MainActivity y conecte los datos de internet antes de accederal mapa
    public void mostrarAvisoServicioDeshabilitado(String mensaje) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(mensaje);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dejamos que confirme y le mantenemos en la activity principal para que
                // no pueda acceder a la activiti del usuario
            }
        });
        dialog.show();
    }

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }*/

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}







/*Para obtener la posición del usuario deberemos llamar al método requestLocationUpdates() que recibirá 4 parámetros con la siguiente información:

–	Nombre del proveedor de localización del que queremos recibir actualizaciones de posición.
–	Tiempo en milisegundos entre actualizaciones.
–	Distancia en metros entre actualizaciones.
–	Instancia de un objeto LocationListener.*/




  /*  @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/


                //MENU
/*
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

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_camera) {
                // Handle the camera action
            } else if (id == R.id.nav_gallery) {

            } else if (id == R.id.nav_slideshow) {

            } else if (id == R.id.nav_manage) {

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_send) {

            }

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
*/