package com.example.levishowwedance.Custom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.levishowwedance.Modelo.Usuario;
import com.example.levishowwedance.Modelo.Foto;

import java.util.ArrayList;

/**
 * Created by Juan K on 14/09/2017.
 */

public class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String BASE_LEVIS = "baseDatos";
    private static final String TABLA_USUARIOS = "usuario";
    private static final String TABLA_FOTOS = "fotos";


    private static final String NOMBRE_USUARIO = "nombre";
    private static final String USERNAME = "username";
    private static final String CORREO_USUARIO = "correo";
    private static final String CEDULA_USUARIO = "cedula";
    private static final String CELULAR_USUARIO = "celular";
    private static final String PASSWORD_USUARIO = "password";

    private static final String TITULO_FOTO = "titulo";
    private static final String UBICACION_FOTO = "ubicacion";
    private static final String FECHA_FOTO = "fecha";
    private static final String DIRECCION_FOTO = "direccion";
    //private static final String ID_FOTO = "id";


    public static final String SQL_CREATE_TABLA_USUARIOS = "CREATE TABLE "
            + DB.TABLA_USUARIOS + " ( "
            + NOMBRE_USUARIO + " TEXT, "
            + USERNAME + " TEXT PRIMARY KEY, "
            + CORREO_USUARIO + " TEXT, "
            + CEDULA_USUARIO + " TEXT, "
            + CELULAR_USUARIO + " TEXT, "
            + PASSWORD_USUARIO + " TEXT )"
            ;

    public static final String SQL_CREATE_TABLA_FOTOS = "CREATE TABLE "
            + DB.TABLA_FOTOS + " ( "
            + USERNAME + " TEXT, "
            + TITULO_FOTO + " TEXT, "
            + UBICACION_FOTO + " TEXT, "
            + FECHA_FOTO + " TEXT, "
            + DIRECCION_FOTO + " TEXT PRIMARY KEY )"
            ;

    public DB(Context context){
        super(context,BASE_LEVIS,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLA_USUARIOS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLA_FOTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertarUsuario(Usuario user){
        SQLiteDatabase db = getWritableDatabase();
        if(db!=null){
            ContentValues valores = new ContentValues();
            valores.put(NOMBRE_USUARIO, user.getNombreReal());
            valores.put(USERNAME, user.getUsername());
            valores.put(CORREO_USUARIO, user.getCorreo());
            valores.put(CEDULA_USUARIO, user.getCedula());
            valores.put(CELULAR_USUARIO, user.getCelular());
            valores.put(PASSWORD_USUARIO, user.getPassword());
            db.insertOrThrow(TABLA_USUARIOS,null,valores);
        }

    }

    public Usuario buscarUsuario(String username){
        SQLiteDatabase db = getReadableDatabase();
        Usuario valido=null;
        if(db!=null){
            Cursor cursor=db.rawQuery("SELECT * FROM "+TABLA_USUARIOS+" WHERE "+USERNAME+" = ? ",new String[]{username});
            if(cursor.moveToFirst()){
                do{
                    String nombre = cursor.getString(0);
                    String usuario = cursor.getString(1);
                    String correo = cursor.getString(2);
                    String cedula = cursor.getString(3);
                    String celular = cursor.getString(4);
                    String pass = cursor.getString(5);

                    valido= new Usuario(nombre,usuario,correo,cedula,celular,pass);


                }while(cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return valido;
    }

    public void insertarFoto(Foto nueva){
        SQLiteDatabase db = this.getWritableDatabase();
        if(db!=null){
            ContentValues valores = new ContentValues();
            valores.put(USERNAME, nueva.getUsername());
            valores.put(TITULO_FOTO, nueva.getTitulo());
            valores.put(UBICACION_FOTO, nueva.getUbicacion() );
            valores.put(FECHA_FOTO, nueva.getFecha()+"");
            valores.put(DIRECCION_FOTO, nueva.getImagen());
            db.insertOrThrow(TABLA_FOTOS, null, valores);
        }
    }

    public ArrayList<Foto> buscarFotosUsuario(String username){
        String[] foto = new String[6];
        ArrayList<Foto> busquedaFotos = new ArrayList<Foto>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+TABLA_FOTOS+" WHERE "+USERNAME+" = ? ",new String[]{username});
        if(cursor.moveToFirst()) {
            foto = new String[5];
            for (int i = 0; i < 5; i++){
                foto[i]=cursor.getString(i);
            }
            Foto nueva = new Foto(foto[0], foto[2], foto[1],foto[3],foto[4]);

            busquedaFotos.add(nueva);
            while (cursor.moveToNext()){
                foto = new String[5];
                for (int i = 0; i < 5; i++){
                    foto[i]=cursor.getString(i);
                }
                nueva = new Foto(foto[0], foto[2], foto[1],foto[3],foto[4]);
                busquedaFotos.add(nueva);
            }
        }
        return busquedaFotos;
    }

    public ArrayList<Foto> buscarFotos(){
        String[] foto = new String[5];
        ArrayList<Foto> fotones = new ArrayList<Foto>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM " + TABLA_FOTOS;
        Cursor busqueda = db.rawQuery(q,null);
        if(busqueda.moveToFirst()) {
            foto = new String[5];
            for (int i = 0; i < 5; i++){
                foto[i]=busqueda.getString(i);
            }
            Foto nueva = new Foto(foto[0], foto[2], foto[1],foto[3],foto[4]);
            fotones.add(nueva);
            while (busqueda.moveToNext()){
                foto = new String[5];
                for (int i = 0; i < 5; i++){
                    foto[i]=busqueda.getString(i);
                }
                nueva = new Foto(foto[0], foto[2], foto[1],foto[3],foto[4]);
                fotones.add(nueva);
            }
        }
        return fotones;
    }

    public void actualizarPassword(Usuario user, String pass){
        SQLiteDatabase db = getWritableDatabase();
        if(db!=null){
            ContentValues valores = new ContentValues();
            valores.put(NOMBRE_USUARIO, user.getNombreReal());
            valores.put(USERNAME, user.getUsername());
            valores.put(CORREO_USUARIO, user.getCorreo());
            valores.put(CEDULA_USUARIO, user.getCedula());
            valores.put(CELULAR_USUARIO, user.getCelular());
            valores.put(PASSWORD_USUARIO, pass);
            db.update(TABLA_USUARIOS, valores, USERNAME+"=?",new String[]{user.getUsername()});
        }

    }
}
