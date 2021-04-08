package com.example.silohan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.silohan.Adapter.AdapterPerumahan;
import com.example.silohan.Helper.ListPerumahanHelper;
import com.example.silohan.Model.Perumahan;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PerumahanActivity extends AppCompatActivity implements AdapterPerumahan.OnPerumahanListener {
    private RecyclerView perumahanRV;
    private AdapterPerumahan perumahanRVAdapter;
    private RecyclerView.LayoutManager perumahanRVLayoutManager;
    private TextInputLayout searchTF;

    FirebaseFirestore db;
    CollectionReference perumahanRef;

    List<Perumahan> mPerumahanList;
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
        setContentView(R.layout.activity_perumahan);
        getSupportActionBar().setTitle("Daftar Perumahan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        //End


        searchTF = findViewById(R.id.searchPerumahanTF);
        perumahanRV = (RecyclerView) findViewById(R.id.listperumahanRV);
        perumahanRVLayoutManager = new LinearLayoutManager(this);
        perumahanRV.setLayoutManager(perumahanRVLayoutManager);

        mPerumahanList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        perumahanRef = db.collection("perumahan");

        startGettingLocation();

        searchTF.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cariPerumahan(s.toString());
            }
        });

    }

    private void startGettingLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationProviderClient.requestLocationUpdates(locationRequest,locationCallback, PerumahanActivity.this.getMainLooper());
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    //Get Data & Set The Adapter With Updated Data
                    readData(perumahanList -> {
                        perumahanRVAdapter = new AdapterPerumahan(listPerumahanHelper.urutkanPerumahan(mPerumahanList,currentLocation),PerumahanActivity.this::OnPerumahanClick);
                        perumahanRV.setAdapter(perumahanRVAdapter);
                    });
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
                Toast.makeText(PerumahanActivity.this, "Permission needed", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(PerumahanActivity.this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
            }
        }

    }

    @Override
    public void OnPerumahanClick(Perumahan perumahan) {
        //Dialog Detail
        Dialog detailPerumahanDialog = new Dialog(this);
        detailPerumahanDialog.setContentView(R.layout.dialog_detail_perumahan);
//        detailPerumahanDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton closeDialog = detailPerumahanDialog.findViewById(R.id.close_dialog_button);
        ImageView fotoPerumahan = detailPerumahanDialog.findViewById(R.id.foto_perumahan_detail);
        TextView namaPerumahan = detailPerumahanDialog.findViewById(R.id.nama_perumahan_detail);
        TextView ukuranPerumahan = detailPerumahanDialog.findViewById(R.id.ukuran_perumahan_detail);
        TextView hargaPerumahan = detailPerumahanDialog.findViewById(R.id.harga_perumahan_detail);
        TextView alamatPerumahan = detailPerumahanDialog.findViewById(R.id.alamat_perumahan_detail);
        TextView deskripsiperumahan = detailPerumahanDialog.findViewById(R.id.deskripsi_perumahan_detail);
        Button hubungiKontak = detailPerumahanDialog.findViewById(R.id.hubungi_kontak_button);

        Glide.with(this).load(perumahan.getPerumahanFoto()).into(fotoPerumahan);
        namaPerumahan.setText(perumahan.getPerumahanNama());
        ukuranPerumahan.setText("Ukuran \t\t: "+perumahan.getPerumahanUnit());
        deskripsiperumahan.setText("Deskripsi \n"+perumahan.getPerumahanDeskripsi());
        hargaPerumahan.setText("Harga \t\t\t: "+listPerumahanHelper.formatRupiah(perumahan.getPerumahanHarga()));
        alamatPerumahan.setText(perumahan.getPerumahanAlamat());
        hubungiKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+perumahan.getPerumahanKontak()));
                startActivity(intent);
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailPerumahanDialog.dismiss();
            }
        });

        detailPerumahanDialog.show();

    }

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

    }

    private void cariPerumahan(String text) {
        List<Perumahan> filteredList = new ArrayList<>();

        for (Perumahan perumahan : mPerumahanList ) {
            if (perumahan.getPerumahanNama().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(perumahan);
            }
        }
        perumahanRVAdapter.filterList(filteredList);
    }



}