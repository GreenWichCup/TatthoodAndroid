package com.example.tatthood;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//import com.google.android.libraries.places.api.model.Place;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int FINE_LOCATION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        prepareLocationServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        showUserCurrentLocation();
    }
    private void accessLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == FINE_LOCATION_REQUEST_CODE){
            //length is equal to 1 because we do one  permission request
            // PaPackageManager.PERMISSION_GRANTED means the user have accepted the permission
            if (grantResults.length==1 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                showUserCurrentLocation();
            } else {
                Toast.makeText(this, "the user denied the permission request", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // able to be know if the permission was given by the user
    private void showUserCurrentLocation(){
 


        //context is this because the method is called from the MapActivity
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            accessLocationPermission();
        } else {

            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
               Location location = task.getResult();
                if(location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Your current location is here"));
                    CameraUpdate cameraLocation = CameraUpdateFactory.newLatLngZoom(latLng,16.0f);
                    mMap.moveCamera(cameraLocation);



                } else {
                   Toast.makeText(MapsActivity.this, "Something went wrong. Please try again",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    private void prepareLocationServices(){
 fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

}