package com.example.signup_login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {

    private String name = "";
    private String id = "";
    private String pw = "";
    private String encryption_pw = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button signupButton = (Button) findViewById(R.id.signupButton);

        //토스트 문자 띄우기
        //후에 데이터베이스와 연결하여 조건 달아야해.
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pwEditText = (EditText) findViewById(R.id.pwEditText);
                EditText idEditText = (EditText) findViewById(R.id.nameEditText);
                EditText nameEditText = (EditText) findViewById(R.id.nameEditText);

                pw = pwEditText.getText().toString();
                id = idEditText.getText().toString();
                name = nameEditText.getText().toString();



                if (pw.length() == 0) {//비밀번호 비었을때
                    Toast.makeText(SignupActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                } else if (id.length() == 0) {//아이디 비었을때
                    Toast.makeText(SignupActivity.this, "아이디를 입력하세요.", Toast.LENGTH_LONG).show();
                } else if (name.length() == 0) {//이름 비었을때
                    Toast.makeText(SignupActivity.this, "이름를 입력하세요.", Toast.LENGTH_LONG).show();
                } else {
                    //비밀번호 암호화
                    encryption_pw = BCrypt.hashpw(pw, BCrypt.gensalt(10));
                    Log.i("진입1",encryption_pw);
                    //name,id,pw 통신으로 보내기
                    Database();
                }
            }
        });
    }

    //데이터베이스로 넘김
    private void Database() {

        //url 구하기
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/User.jsp");
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("진입2",response);
                        switch (response.trim()) {
                            case "userAleadyExist":
                                Toast.makeText(getApplicationContext(), "이미 해당 아이디는 사용하고 있습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "notMember":
                                Toast.makeText(getApplicationContext(), "가입 대상자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error":
                                Toast.makeText(getApplicationContext(), "서버 오류입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            case "userCreated":
                                Log.i("진입3",encryption_pw);
                                Toast.makeText(getApplicationContext(), "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // 회원가입 정보 push 진행
                params.put("id", id);
                params.put("pwd",encryption_pw);
                params.put("name", name);
                params.put("type","user_Add");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false

        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
}