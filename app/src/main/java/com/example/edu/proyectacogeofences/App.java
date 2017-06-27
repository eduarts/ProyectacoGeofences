package com.example.edu.proyectacogeofences;

import android.app.Application;
import android.content.Context;

/**
 * Created by edu on 29/05/2017.
 */

public class App extends Application {

    private static User user=null;
    private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User usuario) {
        user = usuario;
    }

    public static Context getAppContext() {
        return App.context;
    }
}
