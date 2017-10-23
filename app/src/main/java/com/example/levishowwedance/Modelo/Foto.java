package com.example.levishowwedance.Modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Juan K on 14/09/2017.
 */

public class Foto {
    private String titulo;
    private String fecha;
    private String imagen;
    private String username;
    private String ubicacion;


    public Foto(String username, String titulo, String ubicacion, String fecha, String direccion) {

        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.username = username;
        this.imagen=direccion;
        this.fecha=fecha;

    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTitulo() {

        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}