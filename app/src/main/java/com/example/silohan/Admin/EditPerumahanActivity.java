package com.example.silohan.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.silohan.Model.Perumahan;
import com.example.silohan.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditPerumahanActivity extends AppCompatActivity {

    //UI Component
    TextInputLayout namaPerumahan, ukuranPerumahan, hargaPerumahan, alamatPerumahan, deskPerumahan, kontakPerumahan;
    Button editFoto, editPerumahan, cancelPerumahan, editLokasi, deletePerumahan;
    ImageView fotoPerumahan;
    TextView latPerum,longPerum;


    //Variabel
    Perumahan perumahan;
    Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perumahan);

        getSupportActionBar().setTitle("Edit Perumahan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fotoPerumahan = findViewById(R.id.edit_foto_perumahan_detail);
        namaPerumahan = findViewById(R.id.edit_nama_perumahan);
        ukuranPerumahan = findViewById(R.id.edit_ukuran_perumahan);
        hargaPerumahan = findViewById(R.id.edit_harga_perumahan);
        alamatPerumahan = findViewById(R.id.edit_alamat_perumahan);
        deskPerumahan = findViewById(R.id.edit_deskripsi_perumahan);
        kontakPerumahan = findViewById(R.id.edit_kontak_perumahan);
        latPerum = findViewById(R.id.edit_lat_perumahan);
        longPerum = findViewById(R.id.edit_long_perumahan);
        editLokasi = findViewById(R.id.edit_geopoint_perumahan_btn);
        editFoto = findViewById(R.id.edit_foto_btn);
        editPerumahan = findViewById(R.id.edit_perumahan_btn);
        cancelPerumahan = findViewById(R.id.cancel_edit_perumahan_btn);
        deletePerumahan = findViewById(R.id.delete_perumahan_btn);

        if (getIntent().getExtras() != null){
            perumahan = new Perumahan(
                    getIntent().getExtras().getString("id"),
                    getIntent().getExtras().getString("nama"),
                    getIntent().getExtras().getString("desk"),
                    getIntent().getExtras().getString("ukuran"),
                    new GeoPoint(getIntent().getExtras().getDouble("lat"),getIntent().getExtras().getDouble("long")),
                    getIntent().getExtras().getString("foto"),
                    0,
                    getIntent().getExtras().getString("kontak"),
                    getIntent().getExtras().getString("alamat"),
                    getIntent().getExtras().getInt("harga")
            );
            initializeView(perumahan);
        }

        editLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialog Maps
                final Dialog dialog = new Dialog(EditPerumahanActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_edit_location);
                Window w = dialog.getWindow();
                w.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.show();

                GoogleMap googleMap;
                MapView mapView = dialog.findViewById(R.id.mapview_edit_location);
                Button pickLocationBTN = dialog.findViewById(R.id.button_edit_location);

                MapsInitializer.initialize(EditPerumahanActivity.this);
                mapView.onCreate(dialog.onSaveInstanceState());
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng posisiabsen = new LatLng(perumahan.getPerumahanLokasi().getLatitude(),perumahan.getPerumahanLokasi().getLongitude()); ////your lat lng
                        googleMap.addMarker(new MarkerOptions().position(posisiabsen).title(perumahan.getPerumahanNama()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posisiabsen,15.0f));

                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                MarkerOptions marker = new MarkerOptions()
                                        .position(new LatLng(latLng.latitude, latLng.longitude))
                                        .title(perumahan.getPerumahanNama());
                                googleMap.addMarker(marker);
                                latPerum.setText(String.valueOf(latLng.latitude));
                                longPerum.setText(String.valueOf(latLng.longitude));
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

        editFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihFotoGallery();
            }
        });

        editPerumahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filePath != null){
                    // Code for showing progressDialog while uploading
                    ProgressDialog progressDialog
                            = new ProgressDialog(EditPerumahanActivity.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    // Defining the child of storageReference
                    StorageReference StorageRef
                            = storageReference
                            .child(
                                    "foto_perumahan/"
                                            + perumahan.getPerumahanID());
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
                                                Perumahan editPerumahan = new Perumahan(
                                                        perumahan.getPerumahanID(),
                                                        namaPerumahan.getEditText().getText().toString(),
                                                        deskPerumahan.getEditText().getText().toString(),
                                                        ukuranPerumahan.getEditText().getText().toString(),
                                                        new GeoPoint(Double.valueOf(latPerum.getText().toString()),Double.valueOf(longPerum.getText().toString())),
                                                        downloadUri.toString(),
                                                        0,
                                                        kontakPerumahan.getEditText().getText().toString(),
                                                        alamatPerumahan.getEditText().getText().toString(),
                                                        Integer.valueOf(hargaPerumahan.getEditText().getText().toString())
                                                );
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                CollectionReference collectionRef = db.collection("perumahan");
                                                collectionRef.document(editPerumahan.getPerumahanID()).set(editPerumahan).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(EditPerumahanActivity.this, "Edit Data Berhasil", Toast.LENGTH_SHORT).show();
                                                            //Get Data & Set The Adapter With Updated Data
                                                            finish();
                                                        } else {
                                                            Log.w("TAG", "Error edit documents.", task.getException());
                                                            System.out.println("GAGAL");
                                                        }
                                                    }
                                                });
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
                                            .makeText(EditPerumahanActivity.this,
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
                } else { //Gak Ngerubah Foto
                    Perumahan editPerumahan = new Perumahan(
                            perumahan.getPerumahanID(),
                            namaPerumahan.getEditText().getText().toString(),
                            deskPerumahan.getEditText().getText().toString(),
                            ukuranPerumahan.getEditText().getText().toString(),
                            new GeoPoint(Double.valueOf(latPerum.getText().toString()),Double.valueOf(longPerum.getText().toString())),
                            perumahan.getPerumahanFoto(),
                            0,
                            kontakPerumahan.getEditText().getText().toString(),
                            alamatPerumahan.getEditText().getText().toString(),
                            Integer.valueOf(hargaPerumahan.getEditText().getText().toString())
                    );
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference collectionRef = db.collection("perumahan");
                    collectionRef.document(editPerumahan.getPerumahanID()).set(editPerumahan).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditPerumahanActivity.this, "Edit Data Berhasil", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.w("TAG", "Error edit documents.", task.getException());
                                System.out.println("GAGAL");
                            }
                        }
                    });
                }

            }
        });

        cancelPerumahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deletePerumahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionRef = db.collection("perumahan");
                collectionRef.document(perumahan.getPerumahanID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(EditPerumahanActivity.this, "Edit Data Berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            System.out.println("Gagal Hapus Data!");
                        }
                    }
                });
            }
        });

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
                        fotoPerumahan.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        cursor.close();
                    }
                }
            }
        }
    }

    private void initializeView(Perumahan perumahan) {
        Glide.with(this).load(perumahan.getPerumahanFoto()).into(fotoPerumahan);
        namaPerumahan.getEditText().setText(perumahan.getPerumahanNama());
        ukuranPerumahan.getEditText().setText(perumahan.getPerumahanUnit());
        hargaPerumahan.getEditText().setText(String.valueOf(perumahan.getPerumahanHarga()));
        alamatPerumahan.getEditText().setText(perumahan.getPerumahanAlamat());
        deskPerumahan.getEditText().setText(perumahan.getPerumahanDeskripsi());
        kontakPerumahan.getEditText().setText(perumahan.getPerumahanKontak());
        latPerum.setText(String.valueOf(perumahan.getPerumahanLokasi().getLatitude()));
        longPerum.setText(String.valueOf(perumahan.getPerumahanLokasi().getLongitude()));
    }

}