package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment home = new HomeFragment();
    FlagFragment flag = new FlagFragment();
    GroupFragment group = new GroupFragment();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bottomNavigationView = findViewById(R.id.main_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,home).commit();

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getOrder()) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, home).commit();
                return true;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, flag).commit();
                return true;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, group).commit();
                return true;
        }

        return false;
    }
}