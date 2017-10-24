package com.example.levishowwedance.Controlador;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.levishowwedance.Custom.DB;
import com.example.levishowwedance.Custom.DataBase;
import com.example.levishowwedance.Modelo.Foto;
import com.example.levishowwedance.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PhotoActivity extends AppCompatActivity {


    private SharedPreferences sharedPref;
    private ImageView imageView1;
    private String photoPath;
    private EditText input_title;
    private EditText input_location;
    private EditText input_date;
    private String username;
    private Button button;
    DB baseDatos;
    Foto foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //FALTA HACER ON CLICK, PARA COGER VALORES, GUARDAR EN BASE Y VOLVER A HOME
        sharedPref= this.getSharedPreferences("MyPref", 0);
        String usuarioActual=sharedPref.getString(R.string.userPreferences+"",null);
        if(usuarioActual!=null&&!usuarioActual.equals("")) {

            username = sharedPref.getString(R.string.nombrePreferences + "", null);
            String correo = sharedPref.getString(R.string.correoPreferences + "", null);
            String cedula = sharedPref.getString(R.string.cedulaPreferences + "", null);
            String celular = sharedPref.getString(R.string.celularPreferences + "", null);
            String password = sharedPref.getString(R.string.passPreferences + "", null);
        }

        imageView1 = (ImageView) findViewById(R.id.title);
        input_date=(EditText)findViewById(R.id.edit_date);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        input_date.setText(formattedDate+"");
        input_date.setKeyListener(null);
        input_location=(EditText)findViewById(R.id.edit_title);
        input_title=(EditText)findViewById(R.id.edit_location);
        button=(Button)findViewById(R.id.button_upload);

        baseDatos= new DB(getActivity().getApplicationContext());

        Intent intent = getIntent();
        photoPath=intent.getStringExtra(HomeActivity.PHOTO_PATH);
        Uri photoURI=Uri.fromFile(new File(photoPath));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
            imageView1.setImageBitmap(bitmap);
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void upload(View view){

        if(!input_date.getText().toString().equals("") && !input_location.getText().toString().equals("")
                &&!input_title.getText().toString().equals("")){

            String date = input_date.getText().toString();
            String location = input_location.getText().toString();
            String title = input_title.getText().toString();

            Foto foton = new Foto( username, title,  location,  date, photoPath);
            baseDatos.insertarFoto(foton);
            Intent intent= new Intent(this,HomeActivity.class);
            this.finish();
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(),R.string.validacion,
                    Toast.LENGTH_LONG).show();
        }

    }




    public Activity getActivity(){
        return this;
    }

}
