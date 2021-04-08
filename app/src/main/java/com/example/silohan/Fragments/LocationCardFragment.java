package com.example.silohan.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.silohan.Adapter.AdapterViewPagerLokasi;
import com.example.silohan.Helper.ListPerumahanHelper;
import com.example.silohan.Model.Perumahan;
import com.example.silohan.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationCardFragment extends Fragment {

    Perumahan perumahan;
    ImageView fotoPerumahanLokasi;
    TextView namaPerumahanLokasi, jarakPerumahanLokasi;
    Button detailPerumLokasi;
    private ListPerumahanHelper listPerumahanHelper = new ListPerumahanHelper();

    public LocationCardFragment() {
        // Required empty public constructor
    }

    public static LocationCardFragment newInstance(Perumahan perumahan) {
        LocationCardFragment fragment = new LocationCardFragment();
        fragment.perumahan = perumahan;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fotoPerumahanLokasi = view.findViewById(R.id.foto_perumahanLokasi);
        namaPerumahanLokasi = view.findViewById(R.id.nama_perumahanLokasi);
        jarakPerumahanLokasi = view.findViewById(R.id.jarak_perumahanLokasi);
        detailPerumLokasi = view.findViewById(R.id.detail_location_card);

        Glide.with(view.getContext()).load(perumahan.getPerumahanFoto()).into(fotoPerumahanLokasi);
        namaPerumahanLokasi.setText(perumahan.getPerumahanNama());
        jarakPerumahanLokasi.setText(perumahan.getPerumahanJarak() + " KM");
        detailPerumLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailPerumahan(perumahan);
            }
        });
    }

    public void detailPerumahan(Perumahan perumahan){
        //Dialog Detail
        Dialog detailPerumahanDialog = new Dialog(getContext());
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

}