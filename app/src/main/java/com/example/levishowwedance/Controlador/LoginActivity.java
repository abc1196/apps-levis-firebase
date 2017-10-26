package com.example.levishowwedance.Controlador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.levishowwedance.Modelo.Usuario;
import com.example.levishowwedance.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {


    private EditText txtEmail;

    private EditText txtPassword;

    private Button btnLogin;

    private Button btnRegister;

    private LoginButton loginButton ;

    private SharedPreferences sharedPref;

    private CallbackManager callbackManager;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

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
            txtEmail = (EditText) findViewById(R.id.edit_username);
            txtPassword = (EditText) findViewById(R.id.edit_password);
            btnLogin = (Button) findViewById(R.id.button_login);
            btnRegister = (Button) findViewById(R.id.button_login);
            loginButton = (LoginButton) findViewById(R.id.login_button);
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            callbackManager = CallbackManager.Factory.create();
            loginButton.setReadPermissions("email", "public_profile");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void login(View v){

        String email=txtEmail.getText().toString();
        final String password=txtPassword.getText().toString();

        if(!email.equals("")&&!password.equals("")) {


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),R.string.error_login,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Bienvenido",
                                        Toast.LENGTH_SHORT).show();

                                // ...

                                FirebaseUser user=mAuth.getCurrentUser();
                                Usuario usuario= new Usuario(user.getDisplayName(),user.getDisplayName(),user.getEmail(),user.getProviderId(),user.getPhoneNumber(),"");
                                if (usuario != null && usuario.getPassword().equals(password)) {
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(R.string.nombrePreferences + "", usuario.getNombreReal());
                                    editor.putString(R.string.userPreferences + "", usuario.getUsername());
                                    editor.putString(R.string.celularPreferences + "", usuario.getCelular());
                                    editor.putString(R.string.cedulaPreferences + "", usuario.getCedula());
                                    editor.putString(R.string.correoPreferences + "", usuario.getCorreo());
                                    editor.putString(R.string.passPreferences + "", usuario.getPassword());
                                    editor.commit();



                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.error_login,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });



        }else{
            Toast.makeText(getApplicationContext(), R.string.validacion,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void register(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Usuario nuevo= new Usuario(user.getDisplayName(),user.getDisplayName(),user.getEmail(),"",user.getPhoneNumber(),"");
                            mFirebaseDatabase=mFirebaseInstance.getReference("usuarios");
                            mFirebaseDatabase.child(user.getUid()).setValue(nuevo);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(R.string.nombrePreferences+"",nuevo.getNombreReal());
                            editor.putString(R.string.userPreferences+"",nuevo.getUsername());
                            editor.putString(R.string.celularPreferences+"",nuevo.getCelular());
                            editor.putString(R.string.cedulaPreferences+"",nuevo.getCedula());
                            editor.putString(R.string.correoPreferences+"",nuevo.getCorreo());
                            editor.putString(R.string.passPreferences+"",nuevo.getPassword());
                            editor.commit();
                            Intent intent = new Intent(getActivity(),HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    public Activity getActivity(){
        return this;
    }


}
