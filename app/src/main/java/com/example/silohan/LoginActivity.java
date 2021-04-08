package com.example.silohan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.silohan.Admin.MainActivityAdmin;
import com.example.silohan.Model.Perumahan;
import com.example.silohan.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout emailTF,passwordTF;
    private Button loginBTN, daftarBTN;
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loggedIn(currentUser);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        emailTF = findViewById(R.id.emailTFLogin);
        passwordTF = findViewById(R.id.passwordTFLogin);
        loginBTN = findViewById(R.id.loginButton);
        daftarBTN = findViewById(R.id.daftarButton);

        daftarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = emailTF.getEditText().getText().toString();
        String password = passwordTF.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MUACH", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loggedIn(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MUACH", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loggedIn(null);
                        }
                    }
                });
    }

    private void loggedIn(FirebaseUser user) {
        if(user != null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference userRef = db.collection("users");
            userRef.document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if(user.getLevel() == 1){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivityAdmin.class));
                            finish();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "GAGAL LOGIN!", Toast.LENGTH_SHORT).show();
        }

    }
}