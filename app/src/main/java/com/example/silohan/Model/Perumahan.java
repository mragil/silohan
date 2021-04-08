package com.example.silohan.Model;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.math.BigDecimal;

public class Perumahan {
    private String perumahanID;
    private String perumahanNama;
    private String perumahanDeskripsi;
    private String perumahanUnit;
    private GeoPoint perumahanLokasi;
    private String perumahanFoto;
    private double perumahanJarak;
    private String perumahanKontak;
    private String perumahanAlamat;
    private double perumahanHarga;

    public Perumahan(String perumahanID, String perumahanNama, String perumahanDeskripsi, String perumahanUnit, GeoPoint perumahanLokasi, String perumahanFoto, double perumahanJarak, String perumahanKontak, String perumahanAlamat, double perumahanHarga) {
        this.perumahanID = perumahanID;
        this.perumahanNama = perumahanNama;
        this.perumahanDeskripsi = perumahanDeskripsi;
        this.perumahanUnit = perumahanUnit;
        this.perumahanLokasi = perumahanLokasi;
        this.perumahanFoto = perumahanFoto;
        this.perumahanJarak = perumahanJarak;
        this.perumahanKontak = perumahanKontak;
        this.perumahanAlamat = perumahanAlamat;
        this.perumahanHarga = perumahanHarga;
    }

    public Perumahan() {
    }

    public String getPerumahanID() {
        return perumahanID;
    }

    public void setPerumahanID(String perumahanID) {
        this.perumahanID = perumahanID;
    }

    public String getPerumahanNama() {
        return perumahanNama;
    }

    public void setPerumahanNama(String perumahanNama) {
        this.perumahanNama = perumahanNama;
    }

    public String getPerumahanDeskripsi() {
        return perumahanDeskripsi;
    }

    public void setPerumahanDeskripsi(String perumahanDeskripsi) {
        this.perumahanDeskripsi = perumahanDeskripsi;
    }

    public String getPerumahanUnit() {
        return perumahanUnit;
    }

    public void setPerumahanUnit(String perumahanUnit) {
        this.perumahanUnit = perumahanUnit;
    }

    public GeoPoint getPerumahanLokasi() {
        return perumahanLokasi;
    }

    public void setPerumahanLokasi(GeoPoint perumahanLokasi) {
        this.perumahanLokasi = perumahanLokasi;
    }

    public String getPerumahanFoto() {
        return perumahanFoto;
    }

    public void setPerumahanFoto(String perumahanFoto) {
        this.perumahanFoto = perumahanFoto;
    }

    public double getPerumahanJarak() {
        return perumahanJarak;
    }

    public void setPerumahanJarak(double perumahanJarak) {
        this.perumahanJarak = perumahanJarak;
    }

    public String getPerumahanKontak() {
        return perumahanKontak;
    }

    public void setPerumahanKontak(String perumahanKontak) {
        this.perumahanKontak = perumahanKontak;
    }

    public String getPerumahanAlamat() {
        return perumahanAlamat;
    }

    public void setPerumahanAlamat(String perumahanAlamat) {
        this.perumahanAlamat = perumahanAlamat;
    }

    public double getPerumahanHarga() {
        return perumahanHarga;
    }

    public void setPerumahanHarga(double perumahanHarga) {
        this.perumahanHarga = perumahanHarga;
    }
}
