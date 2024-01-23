package com.example.riegoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    private TextInputEditText time;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        time = findViewById(R.id.time_lectura);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("actualizacion").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int tiempo = snapshot.getValue(Integer.class);
                        if (tiempo != 0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    time.setText(Integer.toString(tiempo));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    public void signOut(View view)
    {
        mAuth.signOut();
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeTime(View view){
        // Obtener el valor del tiempo del TextInputEditText
        String tiempoText = time.getText().toString();

        if (!tiempoText.isEmpty()) {
            databaseReference.child("actualizacion").setValue(Integer.valueOf(tiempoText));
        }
    }
}