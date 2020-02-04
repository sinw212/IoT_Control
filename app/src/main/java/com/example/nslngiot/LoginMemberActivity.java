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

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class LoginMemberActivity extends AppCompatActivity {

    //SQL 방어 Patter&String
    final private Pattern SpecialCharsList = Pattern.compile("['\"\\-#()@;=*/+]");
    final private String defendList = "(union|select|from|where)";
    final private Pattern sql_pattern = Pattern.compile(defendList,Pattern.CASE_INSENSITIVE);

    private String name="";
    private String id="";
    private String pw="";

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
        name = SpecialCharsList.matcher(name).replaceAll("");
        id = SpecialCharsList.matcher(id).replaceAll("");
        pw = SpecialCharsList.matcher(pw).replaceAll("");
        //공백 처리 후 남은 select / union / from / where 검증
        final Matcher name_matcher = sql_pattern.matcher(name);
        final Matcher id_matcher = sql_pattern.matcher(id);
        final Matcher pw_matcher = sql_pattern.matcher(pw);
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
                    if(name_matcher.find()) {// SQL패턴 발견 시
                        name = "";
                        Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }else if(id_matcher.find()) { // SQL패턴 발견 시
                        id = "";
                        Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }else if(pw_matcher.find()) { // SQL패턴 발견 시
                        pw = "";
                        Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }else if(name.length()>=20){ // DB 값 오류 방지
                        Toast.makeText(getApplicationContext(), "이름 Long error.", Toast.LENGTH_LONG).show();
                    }else if(id.length()>=20){ // DB 값 오류 방지
                        Toast.makeText(getApplicationContext(), "학번 Long error.", Toast.LENGTH_LONG).show();
                    }else if(pw.length()>=255){ // DB 값 오류 방지
                        Toast.makeText(getApplicationContext(), "비밀번호 Long error.", Toast.LENGTH_LONG).show();
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

        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/User.jsp추후협의");
        RequestQueue queue = Volley.newRequestQueue(LoginMemberActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "로그인성공시 flag":
                                Toast.makeText(getApplicationContext(), "로그인 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainMemberActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case "로그인실패시 flag":
                                Toast.makeText(getApplicationContext(), "가입 대상자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                // 로그인 정보 push 진행
                params.put("id", id);
                params.put("name", name);
                params.put("type","백엔드와협의");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
}