package com.example.silohan.Fragments.Admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.silohan.LoginActivity;
import com.example.silohan.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserAdminFragment extends Fragment {
    private FirebaseAuth mAuth;
    Button logoutBTN;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserAdminFragment newInstance(String param1, String param2) {
        UserAdminFragment fragment = new UserAdminFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_admin, container, false);
        logoutBTN = (Button) v.findViewById(R.id.logout_admin_BTN);
        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return v;
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

}