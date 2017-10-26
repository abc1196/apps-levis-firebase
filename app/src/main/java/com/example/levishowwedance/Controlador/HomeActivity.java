package com.example.levishowwedance.Controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.levishowwedance.Custom.DB;
import com.example.levishowwedance.Custom.DataBase;
import com.example.levishowwedance.Modelo.Usuario;
import com.example.levishowwedance.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 1;
    public final static String PHOTO_PATH = "com.example.levishowwedance.PHOTO_PATH";
    private final static int REQUEST_IMAGE_CAPTURE=1;
    public final static String Photo_Bitmap="Photo_Bitmap";

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private ViewPagerAdapter mViewPagerAdapter;
    private FloatingActionButton fab;
    private TabLayout mTabLayout;
    private DB baseDatos;
    private Usuario user;
    private int[] tabIcons = {
            R.drawable.usericon,
            R.drawable.usergroup
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
         sharedPref= this.getSharedPreferences("MyPref", 0);
        String usuarioActual=sharedPref.getString(R.string.userPreferences+"",null);
        if(usuarioActual!=null&&!usuarioActual.equals("")){

            String nombre=sharedPref.getString(R.string.nombrePreferences+"",null);
            String correo=sharedPref.getString(R.string.correoPreferences+"",null);
            String cedula=sharedPref.getString(R.string.cedulaPreferences+"",null);
            String celular=sharedPref.getString(R.string.celularPreferences+"",null);
            String password=sharedPref.getString(R.string.passPreferences+"",null);
            mAuth = FirebaseAuth.getInstance();
            user= new Usuario(nombre,usuarioActual,correo,cedula,celular,password);

        }
        fab=(FloatingActionButton)findViewById(R.id.fab);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        baseDatos= new DB(getActivity().getApplicationContext());
        setViewPager();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:

                cerrarSesion();

                return true;

            case R.id.action_password:
                showDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void cerrarSesion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle("Cerrar Sesión");
        builder.setMessage("¿Seguro que quiere cerrar sesión?");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences().edit();
                        editor.remove(R.string.userPreferences+"");
                        editor.remove(R.string.passPreferences+"");
                        editor.remove(R.string.correoPreferences+"");
                        editor.remove(R.string.cedulaPreferences+"");
                        editor.remove(R.string.celularPreferences+"");
                        editor.remove(R.string.nombrePreferences+"");
                        user = null;

                        editor.commit();
                        mAuth.signOut();
                        LoginManager.getInstance().logOut();
                        finish();
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_password_dialog, null);
        final EditText et_old_password = (EditText)view.findViewById(R.id.et_old_password);
        final EditText et_new_password = (EditText)view.findViewById(R.id.et_new_password);
        final EditText et_confirm_password = (EditText)view.findViewById(R.id.et_confirm_password);
        TextView tv_message = (TextView)view.findViewById(R.id.tv_message);
        ProgressBar progress = (ProgressBar)view.findViewById(R.id.progress);
        builder.setView(view);
        builder.setTitle("Cambiar Contraseña");
        builder.setPositiveButton("Aceptar", null);
        builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener(){

            @Override
            public void onShow(final DialogInterface dialog){
                Button button=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        String passViejo = et_old_password.getText().toString();
                        final String passNuevo = et_new_password.getText().toString();
                        String passConfirm=et_confirm_password.getText().toString();
                        /**  if(!passViejo.equals("")&&!passNuevo.equals("")&&!passConfirm.equals("")) {
                         if (user.getPassword().equals(passViejo)) {
                         if(passNuevo.equals(passConfirm)) {
                         baseDatos.actualizarPassword(user, passNuevo);
                         user.setPassword(passNuevo);
                         Toast.makeText(getApplicationContext(), "Contraseña cambiada con éxito",
                         Toast.LENGTH_LONG).show();
                         dialog.dismiss();
                         }else{
                         Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden.",
                         Toast.LENGTH_LONG).show();
                         }
                         } else {
                         Toast.makeText(getApplicationContext(), "La contraseña anterior no coincide.",
                         Toast.LENGTH_LONG).show();
                         }
                         }else{
                         Toast.makeText(getApplicationContext(), R.string.validacion,
                         Toast.LENGTH_LONG).show();
                         }
                         */
                        if(!passViejo.equals("")&&!passNuevo.equals("")&&!passConfirm.equals("")) {

                            if(passNuevo.equals(passConfirm)) {
                                final FirebaseUser user = mAuth.getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(mAuth.getCurrentUser().getEmail(), passViejo);

// Prompt the user to re-provide their sign-in credentials
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    user.updatePassword(passNuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Contraseña actualizada.",
                                                                        Toast.LENGTH_LONG).show();
                                                            } else {
                                                                task.getException().printStackTrace();
                                                                Toast.makeText(getApplicationContext(), "No se pudo actualizar la contraseña.",
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Contraseña antigua incorrecta.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden.",
                                        Toast.LENGTH_LONG).show();
                            }

                        }else{
                            Toast.makeText(getApplicationContext(), R.string.validacion,
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                });
            }
        });
        dialog.show();
    }


    private void setViewPager() {

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        mTabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    public Activity getActivity(){
        return this;
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPref;
    }

    public void tomarPic(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE );
            }
        }
    }

    private Uri photoURI;
    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
           //mandamos el imageBitmap al PhotoActivity
            Intent intent= new Intent(this,PhotoActivity.class);
            intent.putExtra(Photo_Bitmap,photoURI.getPath());
            intent.putExtra(PHOTO_PATH,mCurrentPhotoPath);
            startActivity(intent);
        }
    }
}

