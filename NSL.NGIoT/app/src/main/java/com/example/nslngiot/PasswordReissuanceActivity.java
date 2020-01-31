package com.example.nslngiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordReissuanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_pw_re);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final EditText et_name = findViewById(R.id.et_name);
        final EditText et_id = findViewById(R.id.et_id);
        final EditText et_pw = findViewById(R.id.et_pw);

        Button btn_pw_re = findViewById(R.id.btn_pw_re);

        btn_pw_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_name.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext() , "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(et_id.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext() , "id를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(et_pw.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext() , "주민번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else{//재발급 완료시 처리.
                    Toast.makeText(getApplicationContext(), "비밀번호가 재발급 되었습니다.",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), LoginMemberActivity.class);//랩실 부원 전용 로그인창으로 화면전환
                    startActivity(intent);
                    return;
                }
            }
        });
    }
}