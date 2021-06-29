package com.crewmates.autolibodb.init;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.crewmates.autolibodb.MainActivity;
import com.crewmates.autolibodb.R;

public class WelcomAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        EditText initialDistance = findViewById(R.id.initialDistance);
        EditText fuellevel = findViewById(R.id.fuelLevel);
        EditText temperature = findViewById(R.id.temperature);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dist = initialDistance.getText().toString();
                Intent i = new Intent(WelcomAct.this, MainActivity.class);
                i.putExtra("distance",Double.valueOf(dist));
                i.putExtra("fuel",Double.valueOf(fuellevel.getText().toString()));
                i.putExtra("temperature",Double.valueOf(temperature.getText().toString()));
                startActivity(i);
            }
        });
    }

}