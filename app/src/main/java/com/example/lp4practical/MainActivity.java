package com.example.lp4practical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class MainActivity extends AppCompatActivity {
    EditText etLat, etLng;
    Button btnShow,btnSave;
    private GoogleMap map;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLat = findViewById(R.id.etLat);
        etLng = findViewById(R.id.etLng);
        btnSave = findViewById(R.id.btnSave);
        btnShow = findViewById(R.id.btnShow);
        mLocationRequest = new  LocationRequest();
        mLocationCallback = new LocationCallback();
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);
        folderLocation = getFilesDir().getAbsolutePath() + "/LP4";



        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if(result == true){
                Log.d("File Read/Write", "Folder Created");
            }
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                checkPermission();

                Task<Location> task = client.getLastLocation();
                task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                            LatLng poi_RP = new LatLng(1.449448844877881, 103.78384596996054);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_RP,
                                    15));




                        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION);

                        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                            map.setMyLocationEnabled(true);
                        } else {
                            Log.e("GMap - Permission", "GPS access has not been granted");
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                        }
                        UiSettings ui = map.getUiSettings();
                        ui.setCompassEnabled(true);
                        ui.setZoomControlsEnabled(true);
                        ui.setMyLocationButtonEnabled(true);

                    }

                });

                btnShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkPermission();
                        File targetFile_I = new File(folderLocation,"19008424_Ahmed.txt");
                        if (!targetFile_I.exists()){
                            Toast.makeText(MainActivity.this, "File doesnt exist", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        LatLng showLocation = new LatLng(Double.parseDouble(String.valueOf(etLat.getText().toString())), Double.parseDouble(String.valueOf(etLng.getText().toString())));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(showLocation, 10));
                        map.addMarker(new MarkerOptions()
                                .position(showLocation)
                                .title("Latitude: " + etLat.getText() + ", Longitude: " + etLng.getText())
                                .icon(BitmapDescriptorFactory.defaultMarker()));
                    }
                });


            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                try {

                    File targetFile_I = new File(folderLocation,"19008424_Ahmed.txt");
                    targetFile_I.delete();
                    FileWriter writer_I = new FileWriter(targetFile_I,true);
                    writer_I.write( etLat.getText().toString().trim() + "\n" + etLng.getText().toString().trim() + "\n");
                    writer_I.flush();
                    writer_I.close();


                } catch (Exception e){
                    Toast.makeText(MainActivity.this,"Failed to write",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return false;
        }
    }
}