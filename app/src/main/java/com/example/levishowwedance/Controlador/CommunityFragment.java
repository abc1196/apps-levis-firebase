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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.levishowwedance.Custom.DB;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommunityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public final static String TITLE = "USUARIO";

    private RecyclerView recyclerView;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private ArrayList<Foto> fotos;
    ProgressDialog progressDialog ;

    public static CommunityFragment newInstance() {

        return new CommunityFragment();
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase=mFirebaseInstance.getReference().child("pictures");
        fotos= new ArrayList<Foto>();
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                  //  Foto foto = noteSnapshot.getValue(Foto.class);
                    //fotos.add(foto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       // Recicler_View_Adapter_Community adapter = new Recicler_View_Adapter_Community(fotos, getActivity());
       // recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        //itemAnimator.setAddDuration(1000);
        //itemAnimator.setRemoveDuration(1000);
        //recyclerView.setItemAnimator(itemAnimator);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        // Setting progressDialog Title.

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase=mFirebaseInstance.getReference().child("pictures");
        fotos= new ArrayList<Foto>();
        progressDialog = new ProgressDialog(rootView.getContext());
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fotos.clear();
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    Foto foto = noteSnapshot.getValue(Foto.class);
                    fotos.add(foto);
                    Recicler_View_Adapter_Community adapter = new Recicler_View_Adapter_Community(fotos, getActivity());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                    itemAnimator.setAddDuration(1000);
                    itemAnimator.setRemoveDuration(1000);
                    recyclerView.setItemAnimator(itemAnimator);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;

    }
}
