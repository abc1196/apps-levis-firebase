package com.example.levishowwedance.Controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.levishowwedance.Custom.DB;
import com.example.levishowwedance.Custom.DataBase;
import com.example.levishowwedance.Modelo.Usuario;
import com.example.levishowwedance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText input_layout_nameR, input_layout_userR, input_layout_emailR, input_layout_documentoR,
            input_layout_telefonoR, input_layout_passwordR;
    private CheckBox checkBoxRegistro;
    private Button button_loginR;
    private Intent intent;
    private Usuario nuevo;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        input_layout_nameR = (EditText) findViewById(R.id.edit_name);
        input_layout_userR = (EditText) findViewById(R.id.edit_user);
        input_layout_emailR = (EditText) findViewById(R.id.edit_email);
        input_layout_documentoR = (EditText) findViewById(R.id.edit_documento);
        input_layout_telefonoR = (EditText) findViewById(R.id.edit_telefono);
        input_layout_passwordR = (EditText) findViewById(R.id.edit_password);
        button_loginR = (Button) findViewById(R.id.button_loginR);
        checkBoxRegistro = (CheckBox) findViewById(R.id.checkBox);
        button_loginR.setOnClickListener(this);
        intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        nuevo = null;
        progressDialog = new ProgressDialog(RegisterActivity.this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_loginR:
                if(!input_layout_nameR.getText().toString().equals("") && !input_layout_userR.getText().toString().equals("") &&
                        !input_layout_emailR.getText().toString().equals("") && !input_layout_documentoR.getText().toString().equals("") &&
                            !input_layout_telefonoR.getText().toString().equals("") && !input_layout_passwordR.getText().toString().equals("")){

                    if(checkBoxRegistro.isChecked()) {
                        String nombre = input_layout_nameR.getText().toString();
                        String user = input_layout_userR.getText().toString();
                        String email = input_layout_emailR.getText().toString();
                        String documento = input_layout_documentoR.getText().toString();
                        String telefono = input_layout_telefonoR.getText().toString();
                        String password = input_layout_passwordR.getText().toString();

                        nuevo = new Usuario(nombre,user,email,documento,telefono,password);
                        try {
                            // Setting progressDialog Title.
                            progressDialog.setTitle("Registrando Usuario...");

                            // Showing progressDialog.
                            progressDialog.show();
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (!task.isSuccessful()) {

                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Error en el registro. Intente de nuevo.",
                                                        Toast.LENGTH_LONG).show();
                                            }else{

                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Registro exitoso. Bienvenido!",
                                                        Toast.LENGTH_LONG).show();
                                                FirebaseUser firebaseUser= mAuth.getCurrentUser();
                                                mFirebaseDatabase=mFirebaseInstance.getReference("usuarios");
                                                mFirebaseDatabase.child(firebaseUser.getUid()).setValue(nuevo);
                                                getActivity().finish();
                                            }

                                        }
                                    });
                        }catch (Exception e){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Error en el registro. Intente de nuevo.",
                                    Toast.LENGTH_LONG).show();
                        }



                    }else{
                        Toast.makeText(getApplicationContext(),R.string.acuerdos,
                                Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),R.string.validacion,
                            Toast.LENGTH_LONG).show();
                }

                break;
        }

    }

    public void desplegarTerminos(View view){
        checkBoxRegistro.setChecked(false);
        showDialog();

    }
    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.termino_condiciones_dialog, null);
        final TextView title = (TextView)view.findViewById(R.id.title_terminos);
        final TextView text = (TextView)view.findViewById(R.id.text_terminos);
        text.setText("Al presionar a 'Acepto', nos autorizas de manejar la información proporcionada, que es: Nombre, Correo, Celular, Cédula, además" +
                " de cualquier foto subida con fines de ingresar el concurso.");
        builder.setView(view);
        builder.setPositiveButton("Acepto", null);
        builder.setNegativeButton("No acepto",new DialogInterface.OnClickListener() {
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
                        checkBoxRegistro.setChecked(true);
                        dialog.cancel();
                    }
                });
            }
        });
        dialog.show();
    }

    public Activity getActivity(){
        return this;
    }
}
