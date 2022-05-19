package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment home = new HomeFragment();
    FlagFragment flag = new FlagFragment();
    GroupFragment group = new GroupFragment();

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
        switch (item.getItemId()) {
            case R.id.home_btn:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, home).commit();
                return true;
            case R.id.flag_btn:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, flag).commit();
                return true;
            case R.id.group_btn:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, group).commit();
                return true;
        }

        return false;
    }
}