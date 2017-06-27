package com.example.edu.proyectacogeofences;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class RegistrarseActivity extends AppCompatActivity implements View.OnClickListener{

    EditText userReg,emailReg,passReg,passConfirmReg;
    Button btnAceptarRegistro;
    User user;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        userReg=(EditText)findViewById(R.id.etUserReg);
        emailReg=(EditText)findViewById(R.id.etEmailReg);
        passReg=(EditText)findViewById(R.id.etPassReg);
        passConfirmReg=(EditText)findViewById(R.id.etPassConfirReg);
        btnAceptarRegistro=(Button)findViewById(R.id.btnAceptarRegistro);

        btnAceptarRegistro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Pulsando el bton aceptar
        if(v.getId()==R.id.btnAceptarRegistro){
            //Toast.makeText(getApplicationContext(), "pulsado", Toast.LENGTH_LONG).show();
            // Comprobamos que las 2 contraseñas son iguales
            if(passReg.getText().toString().equals(passConfirmReg.getText().toString())){
                // Comprobamos que el email de usuario no esta ya registrado
                //String registro = "http://10.0.2.2/geofences/comprobarUser.php?emailReg="+emailReg.getText().toString();
                String registro = "http://eduarts.es/geofences/comprobarUser.php?emailReg="+emailReg.getText().toString();

                comprobarUser(registro);
                // Toast.makeText(getApplicationContext(), "Enviado", Toast.LENGTH_LONG).show();
                Log.i("registro",registro);
            }else{
                Log.e("error de contraseñas","#Contraseña: "+passReg.toString()+" #Contraseña confirmacion: "+passConfirmReg.toString());
            }
        }
    }

    public void comprobarUser(String URL){

        //Toast.makeText(getApplicationContext(), ""+URL, Toast.LENGTH_LONG).show();

        // Rescatamos la respuesta
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            // Si la respuesta es true, podemos añadir al usuario
            if (response.toString().equals("true")){
                crearUsuario();
            }else{
                // no podemos añadir al usuario, este user ya existe
                Log.e("error de usuario","Usuario "+userReg.getText().toString()+ "ya existe");
            }
            // Toast.makeText(getApplicationContext(), "Response: "+response.toString(), Toast.LENGTH_LONG).show();
            }
            // Si la respuesta da error...
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la respuesta", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    public void crearUsuario(){

        // Pasamos los parametros para guardarlos en la base de datos
        //String URL = "http://10.0.2.2/geofences/registrarse.php?nameUserReg="+userReg.getText().toString()+"&emailReg="+emailReg.getText().toString()+"&passReg="+passReg.getText().toString();
        String URL = "http://eduarts.es/geofences/registrarse.php?nameUserReg="+userReg.getText().toString()+"&emailReg="+emailReg.getText().toString()+"&passReg="+passReg.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            response = response.replace("][", ",");
            if (response.length() > 0) {
                try {
                    JSONArray ja = new JSONArray(response);
                    Log.i("sizejson", "" + ja.length());
                    RecogerUsuarioRegistrado(ja);
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

    public void RecogerUsuarioRegistrado(JSONArray ja) throws JSONException {
        // Recorremos el json para crear el usuario que acabamos de registrar
        for (int i = 0; i < 1; i++) {
            // Creamos el usuario
            user = new User(ja.getInt(0),ja.getString(1), ja.getString(3), ja.getString(2), true);
            // le pasamos el usuario a la App
            App.setUser(user);

            // Mandamos al usuario a la ActivityUsuario
            Intent intent = new Intent(RegistrarseActivity.this, ActivityUsuario.class);
            startActivity(intent);
        }
    }
}
