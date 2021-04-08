package com.example.silohan;
import com.example.silohan.Model.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextInputLayout emailTF,passwordTF,namaTF;
    private Button registerBTN;

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
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Pendaftaran");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        emailTF = findViewById(R.id.emailTFRegister);
        passwordTF = findViewById(R.id.passwordTFRegister);
        namaTF = findViewById(R.id.namaTFRegister);
        registerBTN = findViewById(R.id.registerButton);

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        String email = emailTF.getEditText().getText().toString();
        String password = passwordTF.getEditText().getText().toString();
        String nama = namaTF.getEditText().getText().toString();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nama).build();
                            user.updateProfile(profileUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    User newUser = new User(user.getUid(),nama,email,password,1);
                                    savetoDB("users",newUser);
                                    loggedIn(user);
                                }
                            });
                            Log.d("MUACH", "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MUACH", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loggedIn(null);
                        }
                    }
                });
    }

    private void savetoDB(String path, User newUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection(path);
        usersRef.document(newUser.getUserID()).set(newUser);

    }


    private void loggedIn(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "GAGAL DAFTAR!", Toast.LENGTH_SHORT).show();
        }

    }


}