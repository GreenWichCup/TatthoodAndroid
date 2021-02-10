package com.example.tatthood.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tatthood.ModelData.User;
import com.example.tatthood.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HoodMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private static final int FINE_LOCATION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient locationClient;

    private List<String> mHoods;
    Query reference;
    Button buttonOpenDialog, btnAction;
    LinearLayout linearLayout ;
    BottomSheetBehavior bottomSheetBehavior;
    private static final String TAG =  HoodMap.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hood_map, container, false);
        //initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        //Async Map
        mHoods = new ArrayList<>();
        prepareLocationServices();

        supportMapFragment.getMapAsync(HoodMap.this);
        geocoder = new Geocoder(getContext());

        //Bottom sheet
        buttonOpenDialog =  view.findViewById(R.id.btnBottomSheet);
        linearLayout = view.findViewById(R.id.lnBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        btnAction = view.findViewById(R.id.btnAction);

        reference = FirebaseDatabase.getInstance().getReference("App_users");

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        buttonOpenDialog.setText("Expand");
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        buttonOpenDialog.setText("Close");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        buttonOpenDialog.setText("Other case ");
                        break;
                }
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Action is triggered",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        buttonOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    buttonOpenDialog.setText("Close ");
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    buttonOpenDialog.setText("Expand ");

                }

            }
        });



        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Position the map's camera near Sydney, Australia.
        showMeTheUserCurrentLocation();

        //list Hoods
        searchHoods();

    }

    private void giveMePermissionToAccessLocation() {
        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                showMeTheUserCurrentLocation();

            } else {

                Toast.makeText(getActivity(), "The user denied to give us access the location", Toast.LENGTH_SHORT).show();

            }

        }


    }

    private void showMeTheUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            giveMePermissionToAccessLocation();
        } else {
            locationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your current location is here"));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
                        mMap.moveCamera(cameraUpdate);

                    } else {
                        Toast.makeText(getActivity(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
    }

    private void prepareLocationServices() {
        locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }


    private void searchHoods(){
        Query query = FirebaseDatabase.getInstance().getReference("App_users")
              .orderByChild("status")
              .equalTo("Hood");
       query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    String hoodAddress  = user.getAddress();
                    try {
                       List<Address> addresses = geocoder.getFromLocationName(hoodAddress,1);
                       if (addresses.size()>0) {
                           Address value = addresses.get(0);
                           LatLng hoodCoord = new LatLng(value.getLatitude(), value.getLongitude());
                           Log.d(TAG, "hood infos" + value.toString());
                           MarkerOptions hoodMarker = new MarkerOptions()
                                   .position(hoodCoord)
                                   .title(value.getLocality());
                           mMap.addMarker(hoodMarker);
                       }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}