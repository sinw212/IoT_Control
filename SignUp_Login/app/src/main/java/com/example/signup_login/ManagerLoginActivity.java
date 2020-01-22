package com.example.signup_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManagerLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                EditText idEditText = (EditText)  findViewById(R.id.idEditText);
                EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
                EditText pwEditText = (EditText) findViewById(R.id.pwEditText);
                String id, name, pw;

                id = idEditText.getText().toString();
                name = nameEditText.getText().toString();
                pw = pwEditText.getText().toString();

                //비밀번호 받아와서 일치하면 로그인 시켜야해
                if(id.equals("admin915") && pw.equals("abc")){//id가 admin915이고 비밀번호가abc인 경우에만 로그인가능
                    if(name.equals("김소현")){                                                                                //비밀번호 임의 설정한 것
                        id = idEditText.getText().toString();
                        name = nameEditText.getText().toString();
                        pw = nameEditText.getText().toString();
                        Toast.makeText(getApplicationContext(), "로그인 완료되었습니다.",Toast.LENGTH_LONG ).show();

                        Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);//selectmode로 화면전환.
                        startActivity(intent);
                        return;
                    }else{
                        Toast.makeText(getApplicationContext(), "관리자가 아닙니다.",Toast.LENGTH_LONG ).show();
                        return;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "관리자가 아닙니다.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }


}