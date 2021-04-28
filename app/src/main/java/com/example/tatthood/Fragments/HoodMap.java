package com.example.tatthood.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tatthood.ModelData.MyClusterItem;
import com.example.tatthood.ModelData.User;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class HoodMap extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private static final int FINE_LOCATION_REQUEST_CODE = 1000;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    private boolean permissionDenied = false;
    private Location currentLocation;
    private FusedLocationProviderClient locationClient;
    private List<User> mHoods;
    private String userMarkerUid;
    Query reference;
    private ClusterManager<MyClusterItem> clusterManager;

    //   private MyClusterManagerRenderer mClusterManagerRenderer;
    //   private ArrayList<ClusterMarker>mClusterMarkers = new ArrayList<>();

    Button buttonOpenDialog, btnAction, btnHoods, btnArtist, btnSeller;
    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;
    TextView hoodTitle, hoodAddress;
    ImageView hoodPicture;
    private static final String TAG = HoodMap.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hood_map, container, false);
        //initialize map fragment

        //Async Map
        mHoods = new ArrayList<>();
        prepareLocationServices();

       /* SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(HoodMap.this);*/


        //Bottom sheet
        buttonOpenDialog = view.findViewById(R.id.btnBottomSheet);
        btnHoods = view.findViewById(R.id.btn_hoods);
        btnArtist = view.findViewById(R.id.btn_artist);
        btnSeller = view.findViewById(R.id.btn_seller);
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
                        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("id", userMarkerUid);
                        editor.apply();
                        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Profile()).commit();
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
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
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
                Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fetchLocation();

        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Styling Map
        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        // Location and clustering
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        // Add a marker in Sydney and mov
        // e the camera
        enableMyLocation();
        clusterManager = new ClusterManager<>(requireActivity(), mMap);
        mMap.setOnCameraIdleListener(clusterManager);

        clusterManager.getClusterMarkerCollection().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                final LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View view = inflater.inflate(R.layout.custom_info_window, null);
                final TextView textView = view.findViewById(R.id.textViewTitle);
                String text = (marker.getTitle() != null) ? marker.getTitle() : "Cluster Item";
                textView.setText(text);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        clusterManager.getMarkerCollection().setOnInfoWindowClickListener(marker ->
                Toast.makeText(getContext(), "Info window clicked.", Toast.LENGTH_SHORT).show());

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            this.requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //Here previous Map function

    /*  private void giveMePermissionToAccessLocation() {
          ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
      }
  */
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getActivity(), "The user denied to give us access the location", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/
  /*  private void showMeTheUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            giveMePermissionToAccessLocation();
        } else {
            locationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions userMarkerLocation =new MarkerOptions().position(latLng).title("Your current location is here");
                        mMap.addMarker(userMarkerLocation);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0f);
                        mMap.moveCamera(cameraUpdate);
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    } */
    private void prepareLocationServices() {
        locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    private void fetchLocation() {

        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
            return;
        }
        Task<Location> task = locationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getActivity(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(HoodMap.this);
                }
            }
        });
    }

    private void searchHoods() {
        Query query = FirebaseDatabase.getInstance().getReference("App_users")
                .orderByChild("status")
                .equalTo("Hood");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    double lat = Double.parseDouble(user.getLat());
                    double lng = Double.parseDouble(user.getLng());
                    MyClusterItem itemHood = new MyClusterItem(lat, lng, user.getUsername(), user.getStatus());
                    clusterManager.addItem(itemHood);
                    clusterManager.cluster();
                    // mHoods.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void bottomSheetInfos(String mTitle) {
        for (User user : mHoods) {
            Log.i(TAG, "bottonSheetInfos: " + user.getEmail());
            if (user.getUsername().equals(mTitle)) {
                hoodTitle.setText(user.getUsername());
                GlideApp.with(getActivity()).load(user.getimageUrl()).into(hoodPicture);
                hoodAddress.setText(user.getAddress());
                userMarkerUid = user.getId();
            }
        }
    }

    private void loadMarkerIcon(final Marker marker, String url) {
        GlideApp.with(getContext()).asBitmap().load(url).override(100, 100)
                .fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).dontTransform()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resource);
                        marker.setIcon(icon);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FINE_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }
}