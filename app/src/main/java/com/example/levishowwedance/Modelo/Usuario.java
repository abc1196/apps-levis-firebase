package com.example.levishowwedance.Modelo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Juan K on 14/09/2017.
 */
@IgnoreExtraProperties
public class Usuario {

    private String nombreReal;
    private String username;
    private String correo;
    private String cedula;
    private String celular;
    private String password;

    private ArrayList<Foto> fotos;

    public Usuario(){

    }

    public Usuario(String nombreReal, String username, String correo, String cedula, String celular, String password){
            this.nombreReal = nombreReal;
            this.username = username;
            this.correo = correo;
            this.cedula = cedula;
            this.celular = celular;
            this.password = password;
            fotos = new ArrayList<Foto>();

    }

    public String getNombreReal() {
        return nombreReal;
    }

    public void setNombreReal(String nombre) {
        this.nombreReal = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsuario(String usuario) {
        this.username = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        password=pass;
    }

    public ArrayList<Foto> getFotos(){
        return fotos;
    }


    public Foto buscarFoto(String titulo){
        Foto buscada = null;
        for (int i = 0; i<fotos.size(); i++){
            if(titulo.equals(fotos.get(i).getTitulo())) {
                buscada = fotos.get(i);
            }
        }
        return  buscada;
    }

}
