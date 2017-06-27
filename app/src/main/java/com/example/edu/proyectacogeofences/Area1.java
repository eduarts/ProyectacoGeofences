package com.example.edu.proyectacogeofences;


import android.widget.EditText;

public class Area1 {



    public double lat,lng;
    //en metros
    public int idUser,num,codPostal;
    public String titulo,descripcion,provincia,localidad,calle;
    public int idGeofence,radio;

    public Area1(double latitud, double longitud, int radio,int idGeofence,String titulo) {
        this.lat = latitud;
        this.lng = longitud;
        this.radio = radio;
        this.idGeofence=idGeofence;
        this.titulo=titulo;
    }



}
