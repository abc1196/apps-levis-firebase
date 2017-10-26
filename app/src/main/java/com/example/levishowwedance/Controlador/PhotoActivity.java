package com.example.levishowwedance.Controlador;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.levishowwedance.Custom.DB;
import com.example.levishowwedance.Modelo.Foto;
import com.example.levishowwedance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.widget.Toast.LENGTH_LONG;

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

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

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

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mProgressDialog=new ProgressDialog(PhotoActivity.this);

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
        try {
            photoPath = intent.getStringExtra(HomeActivity.PHOTO_PATH);
            Uri photoURI = Uri.fromFile(new File(photoPath));

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
            imageView1.setImageBitmap(bitmap);
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void upload(View view){
        try {
            if(!input_date.getText().toString().equals("") && !input_location.getText().toString().equals("")
                    &&!input_title.getText().toString().equals("")){

                final String date = input_date.getText().toString();
                final String location = input_location.getText().toString();
                final String title = input_title.getText().toString();

                // Setting progressDialog Title.
                mProgressDialog.setTitle("Subiendo foto...");

                // Showing progressDialog.
                mProgressDialog.show();


                final FirebaseUser user= mAuth.getCurrentUser();
                String userID= user.getUid();
                if(!userID.equals("")||!userID.equals(null)) {

                    Uri photoURI = Uri.fromFile(new File(photoPath));
                    StorageReference sref= mStorageRef.child("images/pictures"+"/"+userID+"/"+title+".jpg");
                    sref.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl= taskSnapshot.getDownloadUrl();

                            final Foto foton = new Foto( username, title,  location,  date, downloadUrl.toString());
                            mFirebaseDatabase=mFirebaseInstance.getReference("pictures");
                            String uid=mFirebaseDatabase.push().getKey();
                            mFirebaseDatabase.child(uid).setValue(foton);


                            Toast.makeText(getApplicationContext(), "Foto subida" , Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "La foto no se pudo subir" , Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    })
                    ;
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    imageView1.setImageBitmap(bitmap);
                    imageView1.setScaleType(ImageView.ScaleType.FIT_XY);



                   // baseDatos.insertarFoto(foton);
                    Intent intent= new Intent(this,HomeActivity.class);
                    this.finish();
                    mProgressDialog.dismiss();
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),R.string.validacion,
                            LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "La foto no se pudo subir" , Toast.LENGTH_LONG).show();
            mProgressDialog.dismiss();
        }
    }




    public Activity getActivity(){
        return this;
    }

}