package com.example.signup_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ManagerMypageFragmentTestActivity extends AppCompatActivity {//이 액티비티는 만든 ManagerFramment를 띄우기 위해 사용한다.
    //화면 전환은 fragment에서 할 수 없고 Activity에서 할 수 있음..(?)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_mypage_fragment_test);

        //fragment 초기화
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.Fragment, new ManagerMypageFragment());
        fragmentTransaction.commit();

        //로그아웃버튼 눌렀을 때 첫화면(SelecActivity)로 전환
        //Fragment와 Activity간의 화면전환
        Button logoutButton = (Button) findViewById(R.id.logoutButton1);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
                startActivity(intent);
            }
        });

    }

}
