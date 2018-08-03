package com.vispire.applications.volajj;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

public class RequestDetailsActivity extends AppCompatActivity {

    MapView mapView;
    GoogleMap map;

    DatabaseReference db;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Toolbar toolbar = findViewById(R.id.toolbar);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Double longitude = Double.parseDouble(getIntent().getStringExtra("EXTRA_LONGITUDE"));
        final Double latitude = Double.parseDouble(getIntent().getStringExtra("EXTRA_LATITUDE"));
        String user = getIntent().getStringExtra("EXTRA_USER");
        String title = getIntent().getStringExtra("EXTRA_TYPE");
        String status = getIntent().getStringExtra("EXTRA_STATUS");

        ((TextView) findViewById(R.id.details_title)).setText(title);
        ((TextView) findViewById(R.id.details_status)).setText(status);

        db = FirebaseDatabase.getInstance().getReference();

        db.child("Users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String name = "";
                    String country = "";
                    String language = "";
                    String age = "";
                    String gender = "";
                    String image = "";
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.getKey() != null){
                            if (ds.getKey().equals("Birthdate")) {
                                age = ds.getValue(String.class);
                            }
                            if (ds.getKey().equals("Name")) {
                                name = ds.getValue(String.class);
                            }
                            if (ds.getKey().equals("Language")) {
                                language = ds.getValue(String.class);
                            }
                            if (ds.getKey().equals("Country")) {
                                country = ds.getValue(String.class);
                            }
                            if (ds.getKey().equals("gender")) {
                                gender = ds.getValue(String.class);
                            }
                            if (ds.getKey().equals("image")) {
                                image = ds.getValue(String.class);
                            }
                        }
                    }
                    ((TextView) findViewById(R.id.details_name)).setText(name);
                    ((TextView) findViewById(R.id.details_age)).setText(age + " years");
                    ((TextView) findViewById(R.id.details_gender)).setText(gender);
                    ((TextView) findViewById(R.id.details_nationality)).setText(country);
                    ((TextView) findViewById(R.id.details_language)).setText(language);

                    final ImageView imgView = findViewById(R.id.details_image);
                    String ref = "requests/" + image;

                    storageRef.child(ref).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            new DownloadImageTask(imgView).execute(uri.toString());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            imgView.setImageURI(null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        mapView =

                findViewById(R.id.details_map);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(new

                                    OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            map = googleMap;

                                            if (ContextCompat.checkSelfPermission(RequestDetailsActivity.this,
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                                                    == PackageManager.PERMISSION_GRANTED) {
                                                map.setMyLocationEnabled(true);
                                            }

                                            LatLng lotLang = new LatLng(longitude, latitude);
                                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lotLang, 10);
                                            map.animateCamera(cameraUpdate);
                                            map.addMarker(new MarkerOptions().position(lotLang).title("Me"));
                                        }
                                    });

        Button accept = findViewById(R.id.details_accept);
        accept.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestDetailsActivity.this, MapsActivity2.class);
                intent.putExtra("EXTRA_LATITUDE", getIntent().getStringExtra("EXTRA_LATITUDE"));
                intent.putExtra("EXTRA_LONGITUDE", getIntent().getStringExtra("EXTRA_LONGITUDE"));
                startActivity(intent);
                Toast.makeText(RequestDetailsActivity.this,
                        "You have accepted the request successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
