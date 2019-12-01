package com.example.nslngiot.Manager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nslngiot.R;
import com.google.android.material.navigation.NavigationView;


public class main_manager extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                    new add_userFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_add_user);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_add_user:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new add_userFragment()).commit();
                break;
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new calendarFragment()).commit();
                break;
            case R.id.nav_meet_log:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new meet_logFragment()).commit();
                break;
            case R.id.nav_member:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new memberFragment()).commit();
                break;
            case R.id.nav_organization:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new organizationFragment()).commit();
                break;
            case R.id.nav_structure:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new structureFragment()).commit();
                break;
            case R.id.nav_ip:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new ipFragment()).commit();
                break;
            case R.id.nav_rule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new ruleFragment()).commit();
                break;
            case R.id.nav_mypage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new mypageFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}