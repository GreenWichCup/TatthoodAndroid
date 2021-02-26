package com.example.tatthood.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tatthood.ModelData.User;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HoodMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int FINE_LOCATION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient locationClient;
    private List<User> mHoods;
    Query reference;
 //   private ClusterManager mClusterManager;
 //   private MyClusterManagerRenderer mClusterManagerRenderer;
 //   private ArrayList<ClusterMarker>mClusterMarkers = new ArrayList<>();

    Button buttonOpenDialog, btnAction, btnHoods, btnArtist, btnSeller;
    LinearLayout linearLayout ;
    BottomSheetBehavior bottomSheetBehavior;
    TextView hoodTitle, hoodAddress;
    ImageView hoodPicture;

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
        //Bottom sheet
        buttonOpenDialog =  view.findViewById(R.id.btnBottomSheet);
        btnHoods= view.findViewById(R.id.btn_hoods);
        btnArtist=view.findViewById(R.id.btn_artist);
        btnSeller= view.findViewById(R.id.btn_seller);
        linearLayout = view.findViewById(R.id.lnBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        btnAction = view.findViewById(R.id.btnAction);

        hoodTitle = view.findViewById(R.id.hoodTitle);
        hoodPicture = view.findViewById(R.id.hoodPicture);
        hoodAddress = view.findViewById(R.id.hoodAddress);

        reference = FirebaseDatabase.getInstance().getReference("App_users");

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                    default:
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
        btnHoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHoods();
            }
        });


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        // Position the map's camera near Sydney, Australia.
        showMeTheUserCurrentLocation();
        //list Hoods
        searchHoods();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                hoodTitle.getText().toString();
                Log.i(TAG, "onMarkerClick: "+ title);
                bottomSheetInfos(title);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14.0f);
                mMap.moveCamera(cameraUpdate);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return false;
            }
        });


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
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0f);
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
         final int index = 0;
        Query query = FirebaseDatabase.getInstance().getReference("App_users")
                .orderByChild("status")
                .equalTo("Hood");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    Double lat = Double.parseDouble(user.getLat());
                    Double lng = Double.parseDouble(user.getLng());
                            LatLng hoodCoord = new LatLng(lat, lng);
                            MarkerOptions hoodMarker = new MarkerOptions()
                                    .position(hoodCoord)
                                    .title(user.getUsername());
                    Log.i(TAG, "snapshot: "+ user.getUsername());
                    mMap.addMarker(hoodMarker);
                    mHoods.add(user);
                        }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

  /*  private void addMapMarkers(){
        if (mMap != null) {
            if (mClusterManager ==null){
                mClusterManager = new ClusterManager<ClusterMarker>(getContext().getApplicationContext(),mMap);
            } if (mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(getActivity(),mMap,mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
        }
    }*/

    private void bottomSheetInfos(String mTitle){
      for (User user : mHoods) {
          Log.i(TAG, "bottonSheetInfos: "+user.getEmail());
          if (user.getUsername().equals(mTitle)){
               hoodTitle.setText(user.getUsername());
              GlideApp.with(getActivity()).load(user.getimageUrl()).into(hoodPicture);
              hoodAddress.setText(user.getAddress());

          }
      }

    }

}