package com.example.silohan.Helper;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.silohan.Model.Perumahan;
import com.google.firebase.firestore.GeoPoint;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListPerumahanHelper {


    public double hitungJarak(GeoPoint perumahanLokasi, Location lokasiSekarang){
        Location lokasiPerum = new Location("");
        lokasiPerum.setLatitude(perumahanLokasi.getLatitude());
        lokasiPerum.setLongitude(perumahanLokasi.getLongitude());

        double initialLat = lokasiSekarang.getLatitude();
        double initialLong = lokasiSekarang.getLongitude();
        double finalLat = lokasiPerum.getLatitude();
        double finalLong = lokasiPerum.getLongitude();

        int R = 6371; // km (Earth radius)
        double dLat = toRadians(finalLat-initialLat);
        double dLon = toRadians(finalLong-initialLong);
        initialLat = toRadians(initialLat);
        finalLat = toRadians(finalLat);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return BigDecimal
                .valueOf(R * c)
                .setScale(2,BigDecimal.ROUND_HALF_EVEN)
                .doubleValue();
    }

    public double toRadians(double deg) {
        return deg * (Math.PI/180);
    }

    //Mengurutkan Data Perumahan Berdasarkan Jarak (Ascending)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Perumahan> urutkanPerumahan(List<Perumahan> perumahanList, Location lokasiSekarang){

        for (Perumahan perumahan : perumahanList){
            perumahan.setPerumahanJarak(hitungJarak(perumahan.getPerumahanLokasi(), lokasiSekarang));
        }
        List<Perumahan> sortedPerumahan = perumahanList.stream()
                .sorted(Comparator.comparing(Perumahan::getPerumahanJarak))
                .collect(Collectors.toList());
        return sortedPerumahan;
    }

    //Format Rupiah dari Integer
    public String formatRupiah(double harga){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(harga);
    }


}
