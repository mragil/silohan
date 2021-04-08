package com.example.silohan.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silohan.Admin.PerumahanActivityAdmin;
import com.example.silohan.Helper.ListPerumahanHelper;
import com.example.silohan.Model.Perumahan;
import com.example.silohan.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterAdminPerumahan extends RecyclerView.Adapter<AdapterAdminPerumahan.ViewHolder> {
    private List<Perumahan> mPerumahanList;
    private ListPerumahanHelper listPerumahanHelper = new ListPerumahanHelper();

    private OnPerumahanListener mOnPerumahanListener;

    public AdapterAdminPerumahan(List<Perumahan> perumahanList, OnPerumahanListener onPerumahanListener){
        mPerumahanList = perumahanList;
        mOnPerumahanListener = onPerumahanListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.perumahan_card, parent, false);
        AdapterAdminPerumahan.ViewHolder viewHolder = new AdapterAdminPerumahan.ViewHolder(mView, mOnPerumahanListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAdminPerumahan.ViewHolder viewHolder, int position) {
        viewHolder.namaPerumahanTV.setText(mPerumahanList.get(position).getPerumahanNama());
        viewHolder.unitPerumahanTV.setText(mPerumahanList.get(position).getPerumahanUnit());
        viewHolder.jarakPerumahanTV.setText(mPerumahanList.get(position).getPerumahanJarak()+" KM");
        Glide.with(viewHolder.itemView.getContext()).load(mPerumahanList.get(position).getPerumahanFoto()).into(viewHolder.fotoPerumahanIV);
    }

    @Override
    public int getItemCount() {
        return mPerumahanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView namaPerumahanTV, unitPerumahanTV, jarakPerumahanTV;
        private ImageView fotoPerumahanIV;
        private MaterialCardView cardPerumahan;

        OnPerumahanListener onPerumahanListener;
        public ViewHolder(@NonNull View itemView, OnPerumahanListener onPerumahanListener) {
            super(itemView);
            namaPerumahanTV = itemView.findViewById(R.id.nama_perumahanTV);
            unitPerumahanTV = itemView.findViewById(R.id.unit_perumahanTV);
            jarakPerumahanTV = itemView.findViewById(R.id.jarak_perumahanTV);
            fotoPerumahanIV = itemView.findViewById(R.id.foto_perumahanIV);

            this.onPerumahanListener = onPerumahanListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPerumahanListener.OnPerumahanClick(mPerumahanList.get(getAdapterPosition()));

        }
    }

    public void clear() {
        mPerumahanList.clear();
        notifyDataSetChanged();
    }


    public void updateList(List<Perumahan> filteredList) {
        mPerumahanList = filteredList;
        notifyDataSetChanged();
    }

    public interface OnPerumahanListener{
        void OnPerumahanClick(Perumahan perumahan);
    }




}
