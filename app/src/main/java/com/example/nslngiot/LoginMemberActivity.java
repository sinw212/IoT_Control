package com.example.nslngiot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.Security_Utill.SQLFilter;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class LoginMemberActivity extends AppCompatActivity {

    private String name="";
    private String id="";
    private String pw="";

    //sql 검증 결과 & default false
    private boolean name_filter = false;
    private boolean id_filter = false;
    private boolean pw_filter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_member);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btn_re_pw = findViewById(R.id.btn_re_pw);
        Button btn_member_login = findViewById(R.id.btn_member_login);

        EditText login_pw = (EditText)findViewById(R.id.loginMember_pw);
        EditText login_id = (EditText)findViewById(R.id.loginMember_id);
        EditText login_name= (EditText)findViewById(R.id.loginMember_name);

        name = login_name.getText().toString();
        id = login_id.getText().toString();
        pw = login_pw.getText().toString();

        //////////////////////////////방어 코드////////////////////////////
        //SQL 인젝션 특수문자 공백처리 및 방어
        name_filter= SQLFilter.sqlFilter(name);
        id_filter = SQLFilter.sqlFilter(id);
        pw_filter = SQLFilter.sqlFilter(pw);
        //////////////////////////////////////////////////////////////////

        btn_re_pw.setOnClickListener(new View.OnClickListener() { //비밀번호 재발급 진행
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PasswordReissuanceActivity.class);
                startActivity(intent);
            }
        });

        btn_member_login.setOnClickListener(new View.OnClickListener() { // 로그인 진행
            @Override
            public void onClick(View v) {
                if("".equals(name) || name.length() == 0) { // 이름의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 이름를 입력하세요.", Toast.LENGTH_LONG).show();
                }else if("".equals(id) || id.length() == 0) { // 아이디(학번)의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 학번을 입력하세요.", Toast.LENGTH_LONG).show();
                }else if("".equals(pw) || pw.length() == 0) { // 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 비밀번호을 입력하세요.", Toast.LENGTH_LONG).show();
                }else{
                    // 로그인 진행 시 SQL 인젝션 검증 절차 진행
                    //////////////////////////////////////////방어 코드////////////////////////////
                    if (name_filter || id_filter || pw_filter) {// SQL패턴 발견 시
                        name = "";
                        id = "";
                        pw = "";
                        Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }else if(name.length()>=20 || id.length()>=20 ||pw.length()>=255 ){ // DB 값 오류 방지
                        Toast.makeText(getApplicationContext(), "Name or ID or Password too Long error.", Toast.LENGTH_LONG).show();
                        /////////////////////////////////////////////////////////////////////////////////////////////////
                    }else {
                        // 검증완료
                        login_member_Request();
                    }
                }
            }
        });
    }
    //데이터베이스로 넘김
    private void login_member_Request() {

        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");
        RequestQueue queue = Volley.newRequestQueue(LoginMemberActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] resPonse_split = response.split(" ");
                        if("loginFailed".equals(resPonse_split[1])){
                            Toast.makeText(getApplicationContext(),"아이디를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else if("loginSuccess".equals(resPonse_split[1])){
                            boolean vaild = BCrypt.checkpw(pw, resPonse_split[0]); // 암호화된 비밀번호 추출 및 일치 여부 체크
                            if (vaild) { // 비밀번호 적합성 검증 성공 시 true
                                Toast.makeText(getApplicationContext(), "로그인 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainManagerActivity.class);
                                startActivity(intent);
                                finish();
                            }else   // 비밀번호 불 일치
                                Toast.makeText(getApplicationContext(),"비밀번호를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else if("error".equals(resPonse_split[1]))
                            Toast.makeText(getApplicationContext(),"시스템 오류입니다..",Toast.LENGTH_SHORT).show();

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
                // 로그인 정보 push 진행
                params.put("id", id);
                params.put("name", name);
                params.put("type","login");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
}