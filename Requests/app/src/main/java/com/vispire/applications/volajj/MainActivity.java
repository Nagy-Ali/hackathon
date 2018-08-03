package com.vispire.applications.volajj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    SurfaceView cameraView;
    BarcodeDetector barcodeDetector ;
    CameraSource cameraSource;
    SurfaceHolder holder;
    DatabaseReference mDatabase;
    android.support.design.widget.TextInputEditText currentStatusText;

    private FusedLocationProviderClient mFusedLocationClient;

    ToggleButton MedicalTab;
    ToggleButton LostTab;
    ToggleButton OtherTab;

    RelativeLayout cameraLayout;

    String QRCode;
    double mlatitude;
    double mlongitude;
    int chosenCase = 0; //0 is medical, 1 is lost, 2 is Other

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MedicalTab = (ToggleButton) findViewById(R.id.medical_tab);
        LostTab = (ToggleButton) findViewById(R.id.lost_tab);
        OtherTab = (ToggleButton) findViewById(R.id.other_tab);
        MedicalTab.setButtonDrawable(getDrawable(R.drawable.health));
        LostTab.setButtonDrawable(getDrawable(R.drawable.lost_));
        OtherTab.setButtonDrawable(getDrawable(R.drawable.other_));
        LostTab.setChecked(false);
        OtherTab.setChecked(false);

        currentStatusText = (android.support.design.widget.TextInputEditText) findViewById(R.id.status_layout);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mDatabase = FirebaseDatabase.getInstance().getReference("Request");

        QRCode = "";

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
        }

        cameraLayout = (RelativeLayout) findViewById(R.id.cameraLayout);
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if(!barcodeDetector.isOperational())
            Toast.makeText(getApplicationContext(), "Barcode Detector is not supported", Toast.LENGTH_LONG).show();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1024,768)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        cameraSource.start(cameraView.getHolder());
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error With Camera Permission", Toast.LENGTH_LONG).show();
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes =  detections.getDetectedItems();
                if(barcodes.size() > 0){
                    Barcode barcode = barcodes.valueAt(0);
                    QRCode = barcode.displayValue;
                    cameraLayout.setPadding(50,50,50,0);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cameraLayout.setPadding(0,0,0,0);
                }
            }
        });
    }

    public void MedicalChosen(android.view.View view){
        chosenCase = 0;
        MedicalTab.setButtonDrawable(getDrawable(R.drawable.health));
        LostTab.setButtonDrawable(getDrawable(R.drawable.lost_));
        OtherTab.setButtonDrawable(getDrawable(R.drawable.other_));
        LostTab.setChecked(false);
        OtherTab.setChecked(false);
        //MedicalTab.setTextColor(0x27AAE1);
        //LostTab.setTextColor(0x000010);
        //OtherTab.setTextColor(0x000010);
    }
    public void LostChosen(android.view.View view){
        chosenCase = 1;
        MedicalTab.setButtonDrawable(getDrawable(R.drawable.health_));
        LostTab.setButtonDrawable(getDrawable(R.drawable.lost));
        OtherTab.setButtonDrawable(getDrawable(R.drawable.other_));
        MedicalTab.setChecked(false);
        OtherTab.setChecked(false);
    }
    public void OtherChosen(android.view.View view){
        chosenCase = 2;
        MedicalTab.setButtonDrawable(getDrawable(R.drawable.health_));
        LostTab.setButtonDrawable(getDrawable(R.drawable.lost_));
        OtherTab.setButtonDrawable(getDrawable(R.drawable.other));
        MedicalTab.setChecked(false);
        LostTab.setChecked(false);
    }

    public void LoginScreen(android.view.View view){
        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
    }

    public void SubmittedScreen(android.view.View view) {

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mlatitude = location.getLatitude();
                                mlongitude = location.getLongitude();
                                //String msg = String.format("Latitude: %f, Longitude: %f", mlatitude, mlongitude);
                                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                                String requestType = "";
                                if (chosenCase == 0)
                                    requestType = "Medical";
                                else if (chosenCase == 1)
                                    requestType = "Lost";
                                else if (chosenCase == 2)
                                    requestType = "Other";

                                String currentStatus = currentStatusText.getText().toString();

                                Request request = new Request(mlongitude,mlatitude,requestType,QRCode, currentStatus);
                                mDatabase.child("requests").push().setValue(request.toMap());

                                Toast.makeText(getApplicationContext(), "Submitted Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Turn Location On", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;
    private static final int MY_PERMISSION_CAMERA = 11;
}


