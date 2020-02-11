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

import com.example.nslngiot.MemberFragment.CalendarFragment;
import com.example.nslngiot.MemberFragment.IpFragment;
import com.example.nslngiot.MemberFragment.MeetLogFragment;
import com.example.nslngiot.MemberFragment.MemberFragment;
import com.example.nslngiot.MemberFragment.MypageFragment;
import com.example.nslngiot.MemberFragment.OrganizationFragment;
import com.example.nslngiot.MemberFragment.RuleFragment;
import com.example.nslngiot.MemberFragment.StructureFragment;
import com.example.nslngiot.MemberFragment.StatusFragment;
import com.example.nslngiot.MemberFragment.LampFragment;
import com.example.nslngiot.MemberFragment.CurtainFragment;

import com.google.android.material.navigation.NavigationView;


public class MainMemberActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);
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
                    new CalendarFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_calendar);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
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
            case R.id.nav_status:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new StatusFragment()).commit();
                break;
            case R.id.nav_lamp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new LampFragment()).commit();
                break;
            case R.id.nav_curtain:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new CurtainFragment()).commit();
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