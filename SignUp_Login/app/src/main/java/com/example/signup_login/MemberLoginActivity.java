package com.example.signup_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MemberLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_login);
    }
    public void onsignupButtonClicked(View view){
        Toast.makeText(getApplicationContext(), "로그인 대상이 아닙니다.",Toast.LENGTH_SHORT).show();
    }

}
