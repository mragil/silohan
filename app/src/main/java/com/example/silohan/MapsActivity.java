package com.example.silohan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.silohan.Adapter.AdapterViewPagerLokasi;
import com.example.silohan.Helper.ListPerumahanHelper;
import com.example.silohan.Model.Perumahan;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseFirestore db;
    CollectionReference perumahanRef;
    private GoogleMap mMap;
    List<Perumahan> mPerumahanList = new ArrayList<>();;
    List<Marker> markerList = new ArrayList<>();
    ViewPager viewPager;
    AdapterViewPagerLokasi adapterViewPagerLokasi;
    int oldPosition = 0;
    private ListPerumahanHelper listPerumahanHelper = new ListPerumahanHelper();

    //Tambahan
    private static final int UPDATE_INTERVAL = 5000; // 5 seconds
    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private Location currentLocation;
    private int LOCATION_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Tambahan
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i("TAG","Location result is available");
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if(locationAvailability.isLocationAvailable()){
                    Log.i("TAG","Location is available");
                }else {
                    Log.i("TAG","Location is unavailable");
                }
            }
        };
        startGettingLocation();
        //End

        getSupportActionBar().setTitle("Lihat Maps");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setPageMargin(15);
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
        LatLng currentPlace = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentPlace).title("Lokasi Elu"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPlace, 15.0f));

        db = FirebaseFirestore.getInstance();
        perumahanRef = db.collection("perumahan");

        //Get Data
        readData(perumahanList -> {
            List<Perumahan> listPerumUrut = listPerumahanHelper.urutkanPerumahan(perumahanList,currentLocation);
            initMarkers(listPerumUrut);
            adapterViewPagerLokasi = new AdapterViewPagerLokasi(getSupportFragmentManager(),listPerumUrut);
            viewPager.setAdapter(adapterViewPagerLokasi);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    updateView(listPerumUrut,position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        });
        //End Get Data
    }

    //Tambahan
    private void startGettingLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationProviderClient.requestLocationUpdates(locationRequest,locationCallback, MapsActivity.this.getMainLooper());
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            });

            locationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("TAG", "Exception while getting the location: "+e.getMessage());
                }
            });


        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(MapsActivity.this, "Permission needed", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
            }
        }
    }

    private void stopLocationRequests() {
        locationProviderClient.removeLocationUpdates(locationCallback);
    }
    //End

    public interface GetDataCallBack {
        void onGetDataCallBack(List<Perumahan> perumahanList);
    }

    public void readData(GetDataCallBack getDataCallBack) {
        perumahanRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Perumahan perumahan = document.toObject(Perumahan.class);
                        mPerumahanList.add(perumahan);
                        Log.d("TAG", document.getId() + " => " + document.getData());
                    }
                    getDataCallBack.onGetDataCallBack(mPerumahanList);
                } else {
                    Log.w("TAG", "Error getting documents.", task.getException());
                    System.out.println("GAGAL");
                }
            }
        });
        //End Get Data Perumahan From Firestore

    }


    private void updateView(List<Perumahan> perumahanList, int position) {
        markerList.get(position).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_perumahan_selected));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(perumahanList.get(position).getPerumahanLokasi().getLatitude(),perumahanList.get(position).getPerumahanLokasi().getLongitude()), 15));

        markerList.get(oldPosition).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_perumahan_unselected));
        oldPosition = position;
    }

    private void initMarkers(List<Perumahan> perumahanList) {

        for (int i = 0; i < perumahanList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(perumahanList.get(i).getPerumahanLokasi().getLatitude(),perumahanList.get(i).getPerumahanLokasi().getLongitude()));
            markerOptions.title(perumahanList.get(i).getPerumahanNama());
            if (i == 0)
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_perumahan_selected));
            else
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_perumahan_unselected));
            markerList.add(mMap.addMarker(markerOptions));
        }
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(perumahanList.get(0).getPerumahanLokasi().getLatitude(),perumahanList.get(0).getPerumahanLokasi().getLongitude()), 15));

    }
}