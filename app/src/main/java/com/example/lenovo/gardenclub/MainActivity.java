package com.example.lenovo.gardenclub;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Intent i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnHome = findViewById(R.id.homeButton0);
        Button btnTour = findViewById(R.id.tourButtonOnHomePage);
        Button btnEvents = findViewById(R.id.eventsButtonOnHomePage);
        Button btnImpact = findViewById(R.id.impactButtonOnHomePage);
        Button btnMember = findViewById(R.id.membershipButtonOnHomePage);

        Typeface arial = Typeface.createFromAsset(getAssets(), "arial.ttf");

        btnTour.setTypeface(arial);
        btnEvents.setTypeface(arial);
        btnImpact.setTypeface(arial);
        btnMember.setTypeface(arial);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MainActivity.this, TourActivity.class);
                startActivity(i);
            }
        });

        btnEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(i);
            }
        });

        btnImpact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MainActivity.this, ImpactActivity.class);
                startActivity(i);
            }
        });

        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MainActivity.this, MembershipActivity.class);
                startActivity(i);
            }
        });
    }
}
