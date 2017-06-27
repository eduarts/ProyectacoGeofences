package com.example.edu.proyectacogeofences;

import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by edu on 25/05/2017.
 */

public class HiloRutaWS extends AsyncTask<String, Void, String> {

    private MainActivity activity;
    private MapsActivity mapsActivity;
    private BBDDActivity bbddActivity;

    public HiloRutaWS() {

    }

    public HiloRutaWS(MainActivity activity) {
        this.activity = activity;
    }

    public HiloRutaWS(BBDDActivity activity) {
        this.bbddActivity = activity;
    }

    public HiloRutaWS(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return callRestWSByGet(strings[0]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == null) {
            Toast.makeText(activity, "Error en la llamada", Toast.LENGTH_LONG).show();
        } else {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                JSONArray datos = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("adress_components").getJSONObject(0).getJSONArray("long_name");

                for (int i = 0; i < datos.length(); i++) {
                    JSONObject dato = datos.getJSONObject(i);
                    // TextView tv = new TextView(activity);
                    /*tv.setText(Html.fromHtml(dato.getString("html_instructions") + " ("
                            + dato.getJSONObject("distance").getString("text") + ", "
                            + dato.getJSONObject("duration").getString("text") + ")"));*/

                    // activity.getPanel().addView(tv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private String callRestWSByGet(String url) throws IOException {
        URL urlws = new URL(url);
        URLConnection uc = urlws.openConnection();
        uc.connect();
        //Creamos el objeto con el que vamos a leer
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String inputLine;
        String contenido = "";
        while ((inputLine = in.readLine()) != null) {
            contenido += inputLine + "\n";
        }
        in.close();
        return contenido;
    }
}
