package com.example.edu.proyectacogeofences;

/**
 * Created by edu on 28/05/2017.
 */

public class User {

    private int id;
    private String nombre,email,pass;
    private boolean activo;

    public User() {
        this.nombre = "";
        this.email = "";
        this.pass = "";
        this.activo = true;
    }

    public User(int id, String nombre, String email, String pass, boolean activo) {
        this.id=id;
        this.nombre = nombre;
        this.email = email;
        this.pass = pass;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isActivo() {
        return activo;
    }
}
