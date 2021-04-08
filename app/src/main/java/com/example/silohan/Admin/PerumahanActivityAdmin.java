package com.example.silohan.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.silohan.Adapter.AdapterAdminPerumahan;
import com.example.silohan.Adapter.AdapterPerumahan;
import com.example.silohan.Helper.ListPerumahanHelper;
import com.example.silohan.Model.Perumahan;
import com.example.silohan.PerumahanActivity;
import com.example.silohan.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PerumahanActivityAdmin extends AppCompatActivity implements AdapterAdminPerumahan.OnPerumahanListener {
    private RecyclerView adminperumahanRV;
    private AdapterAdminPerumahan adminperumahanRVAdapter;
    private RecyclerView.LayoutManager adminperumahanRVLayoutManager;
    private SwipeRefreshLayout swipeContainer;
    private TextInputLayout adminsearchTF;
    private FloatingActionButton addFAB;
    private Dialog addPerumahanDialog;
    public Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

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
    private ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perumahan_admin);
        getSupportActionBar().setTitle("Daftar Perumahan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
        adminsearchTF = findViewById(R.id.admin_searchPerumahanTF);
        adminperumahanRV = (RecyclerView) findViewById(R.id.admin_listperumahanRV);
        adminperumahanRVLayoutManager = new LinearLayoutManager(this);
        adminperumahanRV.setLayoutManager(adminperumahanRVLayoutManager);

        mPerumahanList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        perumahanRef = db.collection("perumahan");

        startGettingLocation();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPerumahanList.clear();
                //Get Data & Set The Adapter With Updated Data
                readData(perumahanList -> {
                    adminperumahanRVAdapter = new AdapterAdminPerumahan(listPerumahanHelper.urutkanPerumahan(mPerumahanList,currentLocation),PerumahanActivityAdmin.this::OnPerumahanClick);
                    adminperumahanRV.setAdapter(adminperumahanRVAdapter);
                });
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        adminsearchTF.getEditText().addTextChangedListener(new TextWatcher() {
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

        //Dialog Add
        addPerumahanDialog = new Dialog(PerumahanActivityAdmin.this);
        addPerumahanDialog.setContentView(R.layout.dialog_add_perumahan);

        foto = addPerumahanDialog.findViewById(R.id.add_foto_perumahan_detail);
        TextInputLayout namaPerumahanText = addPerumahanDialog.findViewById(R.id.add_nama_perumahan);
        TextInputLayout ukuranPerumahanText = addPerumahanDialog.findViewById(R.id.add_ukuran_perumahan);
        TextInputLayout hargaPerumahanText = addPerumahanDialog.findViewById(R.id.add_harga_perumahan);
        TextInputLayout alamatPerumahanText = addPerumahanDialog.findViewById(R.id.add_alamat_perumahan);
        TextInputLayout deskPerumahanText = addPerumahanDialog.findViewById(R.id.add_deskripsi_perumahan);
        TextInputLayout kontakPerumahanText = addPerumahanDialog.findViewById(R.id.add_kontak_perumahan);
        TextView latPerumahan = addPerumahanDialog.findViewById(R.id.add_lat_perumahan);
        TextView longPerumahan = addPerumahanDialog.findViewById(R.id.add_long_perumahan);

        Button pickLocation = addPerumahanDialog.findViewById(R.id.geopoint_perumahan_btn);
        Button pickFoto = addPerumahanDialog.findViewById(R.id.upload_foto_btn);
        Button cancelDialog = addPerumahanDialog.findViewById(R.id.cancel_add_perumahan_btn);
        Button addPerumahan = addPerumahanDialog.findViewById(R.id.add_perumahan_btn);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPerumahanDialog.dismiss();
            }
        });

        addFAB = findViewById(R.id.add_fab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Upload Foto trus ke storage trus ambil access url aksesnya dan masukin ke variabel foto;

                pickFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pilihFotoGallery();

                    }
                });

                addPerumahanDialog.show();



                pickLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Dialog Maps
                        final Dialog dialog = new Dialog(PerumahanActivityAdmin.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.dialog_pick_location);
                        Window w = dialog.getWindow();
                        w.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        dialog.show();

                        GoogleMap googleMap;
                        MapView mapView = dialog.findViewById(R.id.mapview_pick_location);
                        Button pickLocationBTN = dialog.findViewById(R.id.button_pick_location);

                        MapsInitializer.initialize(PerumahanActivityAdmin.this);
                        mapView.onCreate(dialog.onSaveInstanceState());
                        mapView.onResume();
                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng posisiabsen = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()); ////your lat lng
                                googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Lokasi Sekarang"));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                                googleMap.getUiSettings().setZoomControlsEnabled(true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posisiabsen,15.0f));

                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng latLng) {
                                        MarkerOptions marker = new MarkerOptions()
                                                .position(new LatLng(latLng.latitude, latLng.longitude))
                                                .title("Perumahan Baru");
                                        googleMap.addMarker(marker);
                                        latPerumahan.setText(String.valueOf(latLng.latitude));
                                        longPerumahan.setText(String.valueOf(latLng.longitude));
                                    }
                                });
                            }
                        });


                        pickLocationBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                    }
                });

                addPerumahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference DBref = db.collection("perumahan").document();
                        String idPerumahan = DBref.getId();

                        if (filePath != null) {
                            // Code for showing progressDialog while uploading
                            ProgressDialog progressDialog
                                    = new ProgressDialog(PerumahanActivityAdmin.this);
                            progressDialog.setTitle("Uploading...");
                            progressDialog.show();

                            // Defining the child of storageReference
                            StorageReference StorageRef
                                    = storageReference
                                    .child(
                                            "foto_perumahan/"
                                                    + idPerumahan);

                            // adding listeners on upload
                            // or failure of image
                            StorageRef.putFile(filePath)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Image uploaded successfully
                                            // Dismiss dialog
                                            progressDialog.dismiss();
                                            //Ambil download URL
                                            StorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if(task.isSuccessful()) {
                                                        Uri downloadUri = task.getResult();
                                                        String fotoPerum = downloadUri.toString();
                                                        String namaPerumahan = namaPerumahanText.getEditText().getText().toString();
                                                        String ukuranPerumahan = ukuranPerumahanText.getEditText().getText().toString();
                                                        double hargaPerumahan = Double.valueOf(hargaPerumahanText.getEditText().getText().toString());
                                                        String alamatPerumahan = alamatPerumahanText.getEditText().getText().toString();
                                                        String deskPerumahan = deskPerumahanText.getEditText().getText().toString();
                                                        String kontakPerumahan = kontakPerumahanText.getEditText().getText().toString();
                                                        Double latitude = Double.valueOf(latPerumahan.getText().toString());
                                                        Double longitude = Double.valueOf(longPerumahan.getText().toString());
                                                        GeoPoint lokasiperumahan = new GeoPoint(latitude,longitude);

                                                        Perumahan perumahanBaru = new Perumahan(idPerumahan,namaPerumahan,deskPerumahan,ukuranPerumahan,lokasiperumahan,fotoPerum,0,kontakPerumahan,alamatPerumahan,hargaPerumahan);
                                                        saveToDB("perumahan",perumahanBaru);

                                                        addPerumahanDialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast
                                                    .makeText(PerumahanActivityAdmin.this,
                                                            "Failed " + e.getMessage(),
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            double progress
                                                    = (100.0
                                                    * snapshot.getBytesTransferred()
                                                    / snapshot.getTotalByteCount());
                                            progressDialog.setMessage(
                                                    "Uploaded "
                                                            + (int)progress + "%");
                                        }
                                    });
                        }

                    }
                });
            }
        });

    }

    private void startGettingLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationProviderClient.requestLocationUpdates(locationRequest,locationCallback, PerumahanActivityAdmin.this.getMainLooper());
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    //Get Data & Set The Adapter With Updated Data
                    readData(perumahanList -> {
                        adminperumahanRVAdapter = new AdapterAdminPerumahan(listPerumahanHelper.urutkanPerumahan(mPerumahanList,currentLocation),PerumahanActivityAdmin.this::OnPerumahanClick);
                        adminperumahanRV.setAdapter(adminperumahanRVAdapter);
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
                Toast.makeText(PerumahanActivityAdmin.this, "Permission needed", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(PerumahanActivityAdmin.this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
            }
        }
    }

    private void pilihFotoGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null){
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        filePath = data.getData();
                        foto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        cursor.close();
                    }
                }
            }
        }
    }

    @Override
    public void OnPerumahanClick(Perumahan perumahan) {
        Intent intent = new Intent(this, EditPerumahanActivity.class);
        Bundle infoPerum = new Bundle();
        infoPerum.putString("id",perumahan.getPerumahanID());
        infoPerum.putString("nama",perumahan.getPerumahanNama());
        infoPerum.putString("desk",perumahan.getPerumahanDeskripsi());
        infoPerum.putString("ukuran",perumahan.getPerumahanUnit());
        infoPerum.putDouble("lat",perumahan.getPerumahanLokasi().getLatitude());
        infoPerum.putDouble("long",perumahan.getPerumahanLokasi().getLongitude());
        infoPerum.putString("foto",perumahan.getPerumahanFoto());
        infoPerum.putString("kontak",perumahan.getPerumahanKontak());
        infoPerum.putString("alamat",perumahan.getPerumahanAlamat());
        infoPerum.putDouble("harga",perumahan.getPerumahanHarga());
        intent.putExtras(infoPerum);
        startActivity(intent);
    }

    public interface GetDataCallBack {
        void onGetDataCallBack(List<Perumahan> perumahanList);
    }

    public void readData(PerumahanActivityAdmin.GetDataCallBack getDataCallBack) {
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
        adminperumahanRVAdapter.updateList(filteredList);
    }

    private void saveToDB(String path, Perumahan newPerumahan) {
        mPerumahanList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection(path);
        collectionRef.document(newPerumahan.getPerumahanID()).set(newPerumahan).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Get Data & Set The Adapter With Updated Data
                readData(perumahanList -> {
                    adminperumahanRVAdapter = new AdapterAdminPerumahan(listPerumahanHelper.urutkanPerumahan(mPerumahanList,currentLocation),PerumahanActivityAdmin.this::OnPerumahanClick);
                    adminperumahanRV.setAdapter(adminperumahanRVAdapter);
                });
            }
        });

    }
}