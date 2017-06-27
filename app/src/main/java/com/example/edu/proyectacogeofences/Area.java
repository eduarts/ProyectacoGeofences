package com.example.edu.proyectacogeofences;

/**
 * Created by edu on 29/05/2017.
 */

public class Area {

    String titulo,descripcion;
    double lat,lng,radio;

    public Area(String titulo, String descripcion, double lat, double lng, float radio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lat = lat;
        this.lng = lng;
        this.radio = radio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }
}
