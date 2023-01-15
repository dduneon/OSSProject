package com.daihansci.drunk_calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    CalendarFragment calendarFragment;
    StatFragment statFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarFragment = new CalendarFragment();
        statFragment = new StatFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, statFragment).commit();

        NavigationBarView navigationView = findViewById(R.id.bottom_navigationview);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, calendarFragment).commit();
                        return true;
                    case R.id.stat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, statFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}