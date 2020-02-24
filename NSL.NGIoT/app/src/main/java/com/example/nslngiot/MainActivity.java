package com.example.nslngiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private long backKeyClickTime = 0;

    private Button btn_manager,
            btn_member,
            btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        btn_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 관리자 모드 눌렀을 시
                Intent intent = new Intent(getApplicationContext(), LoginManagerActivity.class);
                startActivity(intent);
            }
        });

        btn_member.setOnClickListener(new View.OnClickListener() { //랩실 부원 전용 눌렀을 시
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginMemberActivity.class);
                startActivity(intent);
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //회원가입 눌렀을 시
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initView(){
        btn_manager = findViewById(R.id.btn_manager_login);
        btn_member = findViewById(R.id.btn_member_login);
        btn_signup = findViewById(R.id.btn_signup);
    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else{ // 더이상 스택에 프래그먼트가 없을 시 액티비티에서 앱 종료 여부 결정
            if (System.currentTimeMillis() > backKeyClickTime + 2000) { // 1회 누를 시 Toast
                backKeyClickTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyClickTime + 2000) { // 연속 2회 누를 시 activty shutdown
                ActivityCompat.finishAffinity(this);
            }
        }
    }
}