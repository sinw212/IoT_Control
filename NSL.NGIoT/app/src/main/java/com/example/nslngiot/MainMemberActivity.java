package com.example.nslngiot;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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

import com.google.android.material.navigation.NavigationView;

public class MainMemberActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private long backKeyClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

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
                        new CalendarFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_meet_log:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new MeetLogFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_member:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new MemberFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_organization:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new OrganizationFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_structure:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new StructureFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_ip:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new IpFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_rule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new RuleFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_status:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new StatusFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_mypage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new MypageFragment()).addToBackStack(null).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else { // 더이상 스택에 프래그먼트가 없을 시 액티비티에서 앱 종료 여부 결정
            if (System.currentTimeMillis() > backKeyClickTime + 2000) { // 1회 누를 시 Toast
                backKeyClickTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyClickTime + 2000) { // 연속 2회 누를 시 activty shutdown
                ActivityCompat.finishAffinity(this);
            }
        }
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
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