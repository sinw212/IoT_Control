package com.example.signup_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button signupButton = (Button) findViewById(R.id.signupButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //토스트 문자 띄우기
        //후에 데이터베이스와 연결하여 조건 달아야해.
        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText pwEditText = (EditText)findViewById(R.id.pwEditText);
                EditText idEditText = (EditText)findViewById(R.id.nameEditText);
                EditText nameEditText = (EditText)findViewById(R.id.nameEditText);
                String name, id, pw;

                //부원 id목록이랑 일치하는지 확인 후 "등록되어있지 않습니다."ToastMessage띄우기.
                //name,id,pw 통신으로 보내기
                if(pwEditText.getText().toString().length()==0){//비밀번호 비었을때
                    Toast.makeText(MainActivity.this, "비밀번호를 입력하세요.",Toast.LENGTH_LONG).show();
                    return;
                }else if(idEditText.getText().toString().length()==0){//아이디 비었을때
                    Toast.makeText(MainActivity.this, "아이디를 입력하세요.",Toast.LENGTH_LONG).show();
                    return;
                }else if(nameEditText.getText().toString().length()==0){//이름 비었을때
                    Toast.makeText(MainActivity.this, "이름를 입력하세요.",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    name = nameEditText.getText().toString();
                    id = idEditText.getText().toString();
                    pw = pwEditText.getText().toString();

                    Toast.makeText(MainActivity.this, "회원가입 되었습니다.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
                    startActivity(intent);
                    return;
                }
            }
        });


    }

}
