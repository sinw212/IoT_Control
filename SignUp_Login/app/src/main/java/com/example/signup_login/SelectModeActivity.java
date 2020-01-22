package com.example.signup_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class SelectModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        Button managerButton = (Button) findViewById(R.id.managerButton);
        Button memberButton = (Button) findViewById(R.id.memberButton);
        Button signupButton = (Button) findViewById(R.id.signupButton1);

        managerButton.setOnClickListener(new View.OnClickListener(){//관리자 모드 버튼 클릭시 관리자 회원가입 화면으로 이동

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManagerLoginActivity.class);
                startActivity(intent);
            }
        });

        memberButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemberLoginActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    public void managermodeClick(){
        Intent intent = new Intent(getApplicationContext(), ManagerLoginActivity.class);
        startActivity(intent);
    }
}
