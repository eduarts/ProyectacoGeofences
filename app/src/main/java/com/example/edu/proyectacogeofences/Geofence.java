package com.example.edu.proyectacogeofences;

/**
 * Created by edu on 08/06/2017.
 */

public class Geofence {

    public int idGeofence,idUser,radio;
    public double lat,lng;
    public String titulo,descripcion,provincia,localidad,calle,num,codPostal,fechaRegistro;


    public Geofence(int idGeofence, int idUser, double lat, double lng, int radio, String titulo,
                    String descripcion, String provincia, String localidad, String calle, String num,
                    String codPostal, String fechaRegistro) {

        this.idGeofence = idGeofence;
        this.idUser = idUser;
        this.radio = radio;
        this.num = num;
        this.codPostal = codPostal;
        this.lat = lat;
        this.lng = lng;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.provincia = provincia;
        this.localidad = localidad;
        this.calle = calle;
        this.fechaRegistro = fechaRegistro;
    }


    public int getIdGeofence() {
        return idGeofence;
    }

    public void setIdGeofence(int idGeofence) {
        this.idGeofence = idGeofence;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
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

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
