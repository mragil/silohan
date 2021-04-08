package com.example.silohan.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.silohan.Fragments.Admin.HomeAdminFragment;
import com.example.silohan.Fragments.Admin.UserAdminFragment;
import com.example.silohan.LoginActivity;
import com.example.silohan.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivityAdmin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            logout();
        }
    }

    private void logout() {
        startActivity(new Intent(MainActivityAdmin.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        mAuth = FirebaseAuth.getInstance();

        mBottomNavigationView = findViewById(R.id.admin_bottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_layout,new HomeAdminFragment()).commit();

        Dexter.withContext(this).withPermissions(
                READ_EXTERNAL_STORAGE,
                CAMERA,
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                Toast.makeText(MainActivityAdmin.this, "Aplikasi memerlukan permission granted!", Toast.LENGTH_SHORT).show();

            }
        }).check();
    }

    //Listener for Nav Bar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.page_admin_home:
                    selectedFragment = new HomeAdminFragment();

                    break;
                case R.id.page_admin_user:
                    selectedFragment = new UserAdminFragment();
                    break;
            }

            //Begin Transaction
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_layout,selectedFragment).commit();

            return true;
        }
    };
}