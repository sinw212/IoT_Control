package com.example.nslngiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginManagerActivity extends AppCompatActivity {

    String name, id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manager);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btn_manager_login = findViewById(R.id.btn_manager_login);

        btn_manager_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et_id = findViewById(R.id.et_id);
                EditText et_name = findViewById(R.id.et_name);
                EditText et_pw = findViewById(R.id.et_pw);


                id = et_id.getText().toString();
                name = et_name.getText().toString();
                pw = et_pw.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MainManagerActivity.class);//관리자가 맞으면 관리자모드로 전환
                startActivity(intent);
                //비밀번호 받아와서 일치하면 로그인 시켜야해
               /* if(id.equals("admin915") && pw.equals("abc")){//id가 admin915이고 비밀번호가abc인 경우에만 로그인가능
                    if(name.equals("김소현")){                                                                                //비밀번호 임의 설정한 것
                        id = et_id.getText().toString();
                        name = et_name.getText().toString();
                        pw = et_pw.getText().toString();
                        Toast.makeText(getApplicationContext(), "로그인 완료되었습니다.",Toast.LENGTH_LONG ).show();

                        Intent intent = new Intent(getApplicationContext(), MainManagerActivity.class);//관리자가 맞으면 관리자모드로 전환
                        startActivity(intent);
                        return;
                    }else{
                        Toast.makeText(getApplicationContext(), "관리자가 아닙니다.",Toast.LENGTH_LONG ).show();
                        return;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "관리자가 아닙니다.", Toast.LENGTH_LONG).show();
                    return;
                }*/
            }
        });
    }
}