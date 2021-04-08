package com.example.silohan.Adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.silohan.Fragments.LocationCardFragment;
import com.example.silohan.Model.Perumahan;

import java.util.ArrayList;
import java.util.List;

public class AdapterViewPagerLokasi extends FragmentPagerAdapter {
    List<Perumahan> perumahanList;
    List<LocationCardFragment> fragmentList = new ArrayList<>();

    public AdapterViewPagerLokasi(FragmentManager fm, List<Perumahan> perumahanList) {
        super(fm);
        this.perumahanList = perumahanList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        LocationCardFragment fragment = LocationCardFragment.newInstance(perumahanList.get(position));
        fragmentList.add(fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return perumahanList.size();
    }

}
