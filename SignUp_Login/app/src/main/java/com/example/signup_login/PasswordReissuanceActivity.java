package com.example.signup_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordReissuanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reissuance);

        final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        final EditText idEditText = (EditText) findViewById(R.id.idEditText);
        final EditText securitynumEditText = (EditText) findViewById(R.id.securitynumEditText);

        Button reissuanceButton = (Button) findViewById(R.id.reissuanceButton);

        reissuanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext() , "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(idEditText.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext() , "id를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(securitynumEditText.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext() , "주민번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else{//재발급 완료시 처리.

                }
            }
        });
    }
}
