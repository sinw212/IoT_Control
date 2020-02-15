package com.example.nslngiot;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nslngiot.ManagerFragment.AddUserFragment;
import com.example.nslngiot.ManagerFragment.CalendarFragment;
import com.example.nslngiot.ManagerFragment.IpFragment;
import com.example.nslngiot.ManagerFragment.MeetLogFragment;
import com.example.nslngiot.ManagerFragment.MemberFragment;
import com.example.nslngiot.ManagerFragment.MypageFragment;
import com.example.nslngiot.ManagerFragment.OrganizationFragment;
import com.example.nslngiot.ManagerFragment.RuleFragment;
import com.example.nslngiot.ManagerFragment.StructureFragment;
import com.google.android.material.navigation.NavigationView;


public class MainManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                    new AddUserFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_add_user);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_add_user:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new AddUserFragment()).commit();
                break;
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new CalendarFragment()).commit();
                break;
            case R.id.nav_meet_log:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new MeetLogFragment()).commit();
                break;
            case R.id.nav_member:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new MemberFragment()).commit();
                break;
            case R.id.nav_organization:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new OrganizationFragment()).commit();
                break;
            case R.id.nav_structure:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new StructureFragment()).commit();
                break;
            case R.id.nav_ip:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new IpFragment()).commit();
                break;
            case R.id.nav_rule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new RuleFragment()).commit();
                break;
            case R.id.nav_mypage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new MypageFragment()).commit();
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

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
}