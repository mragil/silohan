package com.example.silohan.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.silohan.LoginActivity;
import com.example.silohan.MapsActivity;
import com.example.silohan.PerumahanActivity;
import com.example.silohan.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private TextView welcomeTV;
    private MaterialCardView mapsCV, perumahanCV;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    public void lihatMaps(){
        startActivity(new Intent(getActivity(), MapsActivity.class));
    }

    public void daftarPerum(){
        startActivity(new Intent(getActivity(), PerumahanActivity.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        welcomeTV = (TextView) v.findViewById(R.id.welcomeTV);
        mapsCV = (MaterialCardView) v.findViewById(R.id.mapsCardView);
        perumahanCV = (MaterialCardView) v.findViewById(R.id.daftarperumahanCardView);
        welcomeTV.setText("Selamat Datang "+ mAuth.getCurrentUser().getDisplayName());

        mapsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lihatMaps();
            }
        });

        perumahanCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daftarPerum();
            }
        });

        return v;
    }
}