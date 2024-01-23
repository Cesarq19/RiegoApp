package com.example.riegoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MeasureActivity extends AppCompatActivity {
    private TextView humedadReal;
    private TextView temperaturaReal;
    private ImageView imgFoco;
    private TextInputEditText time;
    private DatabaseReference databaseReference;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        humedadReal = findViewById(R.id.valorRealH);
        temperaturaReal = findViewById(R.id.valorRealT);
        imgFoco = findViewById(R.id.luz);
        time = findViewById(R.id.time_on);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("foco").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Foco foco = snapshot.getValue(Foco.class);
                        if (foco != null) {
                            Log.d("foco",foco.getEstado());
                            time.setText(Integer.toString(foco.getTiempo()));
                            if ("ON".equals(foco.getEstado())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("foco", "FOCO ON");
                                        focoOn();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("foco", "FOCO OFF");
                                        focoOff();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar errores si es necesario
                    }
                }
        );
        databaseReference.child("lectura").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Lectura lectura = snapshot.getValue(Lectura.class);

                        if (lectura  != null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    humedadReal.setText(Float.toString(lectura.getHumedad()));
                                    temperaturaReal.setText(Float.toString(lectura.getTemperatura()));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
        handler = new Handler();

    }

    public void focoOn()
    {
        imgFoco.setImageResource(R.drawable.foco_on);
    }

    public void focoOff(){
        imgFoco.setImageResource(R.drawable.foco_off);
    }

    public void encender(View view){
        // Obtener el valor del tiempo del TextInputEditText
        String tiempoText = time.getText().toString();

        if (!tiempoText.isEmpty()) {
            // Convertir el tiempo a un valor numérico (en segundos)
            int tiempoSegundos = Integer.parseInt(tiempoText);

            // Cambiar el estado a "ON" en la base de datos
            databaseReference.child("foco").child("estado").setValue("ON");
            databaseReference.child("foco").child("tiempo").setValue(tiempoSegundos);
            // Programar una tarea después del tiempo especificado para cambiar el estado a "OFF"
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseReference.child("foco").child("estado").setValue("OFF");
                }
            }, tiempoSegundos * 1000); // Convertir segundos a milisegundos
        }
    }

}