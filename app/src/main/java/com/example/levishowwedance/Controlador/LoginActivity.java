package com.example.levishowwedance.Controlador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.levishowwedance.Custom.DB;
import com.example.levishowwedance.Custom.DataBase;
import com.example.levishowwedance.Modelo.Usuario;
import com.example.levishowwedance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private EditText txtUser;

    private EditText txtPassword;

    private Button btnLogin;

    private Button btnRegister;

    private DB db;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref= this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String usuarioActual=sharedPref.getString(R.string.userPreferences+"",null);
        if(usuarioActual!=null&&!usuarioActual.equals("")) {
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
            finish();
        }else {
            txtUser = (EditText) findViewById(R.id.edit_username);
            txtPassword = (EditText) findViewById(R.id.edit_password);
            btnLogin = (Button) findViewById(R.id.button_login);
            btnRegister = (Button) findViewById(R.id.button_login);
            db = new DB(getActivity().getApplicationContext());
        }
    }
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    public void login(View v){

        String user=txtUser.getText().toString();
        String password=txtPassword.getText().toString();

        if(!user.equals("")&&!password.equals("")) {
            Usuario usuario = db.buscarUsuario(user);
            if (usuario != null&&usuario.getPassword().equals(password)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(R.string.nombrePreferences+"",usuario.getNombreReal());
                editor.putString(R.string.userPreferences+"",usuario.getUsername());
                editor.putString(R.string.celularPreferences+"",usuario.getCelular());
                editor.putString(R.string.cedulaPreferences+"",usuario.getCedula());
                editor.putString(R.string.correoPreferences+"",usuario.getCorreo());
                editor.putString(R.string.passPreferences+"",usuario.getPassword());
                editor.commit();

                mAuth.signInWithEmailAndPassword(usuario.getCorreo(), password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.error_login,
                                            Toast.LENGTH_LONG).show();
                                }

                                // ...
                            }
                        });

                Intent intent = new Intent(this,HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_login,
                        Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), R.string.validacion,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void register(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public Activity getActivity(){
        return this;
    }
}
