package com.example.levishowwedance.Controlador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.levishowwedance.Custom.DB;
import com.example.levishowwedance.Custom.DataBase;
import com.example.levishowwedance.Modelo.Foto;
import com.example.levishowwedance.Modelo.Usuario;
import com.example.levishowwedance.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public final static String TITLE = "USUARIO";

    private TextView txtUser;
    private TextView txtPublicaciones;
    private Usuario user;
    SharedPreferences sharedPref;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private ArrayList<Foto> fotos;
    ProgressDialog progressDialog ;

    public static ProfileFragment newInstance() {

        return new ProfileFragment();
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getContext());
        String usuarioActual=sharedPref.getString(R.string.userPreferences+"",null);
        if(usuarioActual!=null&&!usuarioActual.equals("")){

            String nombre=sharedPref.getString(R.string.nombrePreferences+"",null);
            String correo=sharedPref.getString(R.string.correoPreferences+"",null);
            String cedula=sharedPref.getString(R.string.cedulaPreferences+"",null);
            String celular=sharedPref.getString(R.string.celularPreferences+"",null);
            String password=sharedPref.getString(R.string.passPreferences+"",null);

            user= new Usuario(nombre,usuarioActual,correo,cedula,celular,password);
            txtUser.setText(usuarioActual);
            //TRAERSE LOS FOTONES DE LA BASE DE DATOS Y GUARDARLOS EN EL ARRAYLIST DEL USER
            mAuth= FirebaseAuth.getInstance();
            final FirebaseUser user=mAuth.getCurrentUser();
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseDatabase=mFirebaseInstance.getReference().child("pictures").child(user.getUid());
            fotos= new ArrayList<Foto>();
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        if(noteSnapshot.child("username").getValue().equals(user.getDisplayName()))
                        {
                           // Foto foto = noteSnapshot.getValue(Foto.class);
                            //fotos.add(foto);
                            //txtPublicaciones.setText(fotos.size()+"");
                            //Toast.makeText(getActivity(), fotos.size()+"",
                              //      Toast.LENGTH_LONG).show();
                           // Recycler_View_Adapter adapter = new Recycler_View_Adapter(fotos, getActivity());
                            //recyclerView.setAdapter(adapter);
                            //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                            //itemAnimator.setAddDuration(1000);
                            //itemAnimator.setRemoveDuration(1000);
                            //recyclerView.setItemAnimator(itemAnimator);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
         txtUser=(TextView)rootView.findViewById(R.id.userText);
        txtPublicaciones=(TextView)rootView.findViewById(R.id.numPhotos);
        progressDialog = new ProgressDialog(rootView.getContext());
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        final String usuarioActual=pref.getString(R.string.userPreferences+"",null);
        if(usuarioActual!=null&&!usuarioActual.equals("")){

            String nombre=pref.getString(R.string.nombrePreferences+"",null);
            String correo=pref.getString(R.string.correoPreferences+"",null);
            String cedula=pref.getString(R.string.cedulaPreferences+"",null);
            String celular=pref.getString(R.string.celularPreferences+"",null);
            String password=pref.getString(R.string.passPreferences+"",null);

            user= new Usuario(nombre,usuarioActual,correo,cedula,celular,password);
            txtUser.setText(usuarioActual);
            mAuth= FirebaseAuth.getInstance();
            final FirebaseUser userf=mAuth.getCurrentUser();
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseDatabase=mFirebaseInstance.getReference().child("pictures");
            // Setting progressDialog Title.
            progressDialog.setTitle("Cargando fotos...");

            // Showing progressDialog.
            progressDialog.show();
            fotos= new ArrayList<Foto>();

            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    fotos.clear();
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Log.d("tag",
                                "RESULTADO: "+noteSnapshot.getKey()+ " "+noteSnapshot.child("username").getValue());
                        Log.d("tag",
                                "RESULTADO: "+noteSnapshot.getKey());
                        Log.d("tag",
                                "RESULTADO: "+noteSnapshot.getKey()+user.getUsername());
                        if(noteSnapshot.child("username").getValue().equals(user.getUsername()))
                        {
                            Log.d("tag",
                                    "RESULTADO: foto del Usuario "+user.getUsername());
                            Foto foto = noteSnapshot.getValue(Foto.class);
                            fotos.add(foto);
                            txtPublicaciones.setText(fotos.size()+"");
                            Recycler_View_Adapter adapter = new Recycler_View_Adapter(fotos, getActivity());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                            itemAnimator.setAddDuration(1000);
                            itemAnimator.setRemoveDuration(1000);
                            recyclerView.setItemAnimator(itemAnimator);
                        }
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });



        }
        return rootView;

    }
}
