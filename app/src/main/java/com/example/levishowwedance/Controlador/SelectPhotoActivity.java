package com.example.levishowwedance.Controlador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.levishowwedance.R;

import java.io.File;
import java.io.IOException;

public class SelectPhotoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView imageView;
    ProgressDialog progressDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView=(ImageView)findViewById(R.id.image_selected);
        progressDialog= new ProgressDialog(SelectPhotoActivity.this);
        progressDialog.setTitle("Cargando Foto...");
        progressDialog.show();
        Intent intent= getIntent();
        String ruta=intent.getStringExtra("RUTA");
        Glide.with(SelectPhotoActivity.this).load(ruta).centerCrop().into(imageView);
        progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
