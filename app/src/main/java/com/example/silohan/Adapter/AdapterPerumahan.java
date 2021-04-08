package com.example.silohan.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silohan.Model.Perumahan;
import com.example.silohan.R;
import com.example.silohan.Helper.ListPerumahanHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AdapterPerumahan extends RecyclerView.Adapter<AdapterPerumahan.ViewHolder> {
    private List<Perumahan> mPerumahanList;
    private ListPerumahanHelper listPerumahanHelper = new ListPerumahanHelper();
    private AdapterPerumahan.OnPerumahanListener mOnPerumahanListener;

    public AdapterPerumahan(List <Perumahan> perumahanList, AdapterPerumahan.OnPerumahanListener onPerumahanListener){
        mPerumahanList = perumahanList;
        mOnPerumahanListener = onPerumahanListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.perumahan_card, parent, false);
        AdapterPerumahan.ViewHolder viewHolder = new AdapterPerumahan.ViewHolder(mView, mOnPerumahanListener);

        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.namaPerumahanTV.setText(mPerumahanList.get(position).getPerumahanNama());
        holder.unitPerumahanTV.setText(mPerumahanList.get(position).getPerumahanUnit());
        holder.jarakPerumahanTV.setText(mPerumahanList.get(position).getPerumahanJarak()+" KM");
        Glide.with(holder.itemView.getContext()).load(mPerumahanList.get(position).getPerumahanFoto()).into(holder.fotoPerumahanIV);
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
            cardPerumahan = itemView.findViewById(R.id.card_daftar_perumahan);

            this.onPerumahanListener = onPerumahanListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPerumahanListener.OnPerumahanClick(mPerumahanList.get(getAdapterPosition()));
        }
    }

    public void filterList(List<Perumahan> filteredList) {
        mPerumahanList = filteredList;
        notifyDataSetChanged();
    }

    public interface OnPerumahanListener{
        void OnPerumahanClick(Perumahan perumahan);
    }
}
