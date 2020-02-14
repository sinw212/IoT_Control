package com.example.nslngiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.Security_Utill.SQLFilter;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordReissuanceActivity extends AppCompatActivity {

    private final String e_maile_regex = "^[a-zA-Z0-9]+\\@[a-zA-Z]+\\.[a-zA-Z]+$"; // 이메일 정규식

    private String member_name = "",
            member_id = "",
            member_mail = "";

    //sql 검증 결과 & default false
    private boolean name_filter = false,
            id_filter = false,
            mail_filter = false;

    private EditText re_name,
            re_id,
            re_mail;

    private Button btn_pw_re ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_pw_re);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        btn_pw_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                member_name = re_name.getText().toString().trim();
                member_id = re_id.getText().toString().trim();
                member_mail = re_mail.getText().toString().trim();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 특수문자 공백처리 및 방어
                name_filter = SQLFilter.sqlFilter(member_name);
                id_filter = SQLFilter.sqlFilter(member_id);
                mail_filter = SQLFilter.sqlFilter(member_mail);
                //////////////////////////////////////////////////////////////////

                if ("".equals(member_name)) { // 이름의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if ("".equals(member_id)) { // 아이디(학번)의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "학번을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if ("".equals(member_mail)) { // 이메일의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "발급받을 이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (member_name.length() >= 20 || member_id.length() >= 20 || member_mail.length() >= 30) { // DB 값 오류 방지
                    Toast.makeText(getApplicationContext(), "Name or ID or Email too Long error.", Toast.LENGTH_LONG).show();
                } else {
                    if (member_mail.matches(e_maile_regex)) { // 이메일을 올바르게 입력 시
                        if(name_filter || id_filter || mail_filter) { // SQL패턴 발견 시
                            Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                            finish();
                        }else
                            reissuanceRequest();
                    }
                    else // 이메일을 올바르게 입력하지 않을 시
                        Toast.makeText(getApplicationContext(), "올바른 형식의 이메일을 입력해주세요.\n"+ "예시) sample23@daum.net", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void reissuanceRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "emailSendSuccess":
                                Toast.makeText(getApplicationContext(), member_mail + " 의 이메일\n"+"임시 비밀번호 재발급 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),PasswordReissuanceActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case "userNotExist":
                                Toast.makeText(getApplicationContext(), "학번/이름/메일을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error":
                                Toast.makeText(getApplicationContext(), "서버 오류입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                // 회원가입 정보 push 진행
                params.put("id", member_id);
                params.put("name", member_name);
                params.put("mail", member_mail);
                params.put("type", "find");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void initView() {
        re_name = findViewById(R.id.member_re_name);
        re_id = findViewById(R.id.member_re_id);
        re_mail = findViewById(R.id.member_re_mail);
        btn_pw_re = findViewById(R.id.btn_pw_re);
    }
}