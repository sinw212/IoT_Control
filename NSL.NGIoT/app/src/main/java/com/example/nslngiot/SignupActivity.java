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

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private final String pw_regex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$"; // 비밀번호 정규식
    private final String e_maile_regex = "^[a-zA-Z0-9]+\\@[a-zA-Z]+\\.[a-zA-Z]+$"; // 이메일 정규식

    private String email="",
            name="",
            id="",
            pw="",
            encryption_pw="";

    //sql 검증 결과 & default false
    private boolean name_filter = false,
            id_filter = false,
            pw_filter = false,
            mail_filter = false;

    private EditText sign_pw,
            sign_id,
            sign_name,
            sign_mail;

    private Button btn_signup,
            btn_cancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        btn_signup.setOnClickListener(new View.OnClickListener() { //회원 가입 버튼
            @Override
            public void onClick(View v) {
                id =sign_id.getText().toString();
                pw =sign_pw.getText().toString();
                name =sign_name.getText().toString();
                email = sign_mail.getText().toString();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 특수문자 공백처리 및 방어
                name_filter= SQLFilter.sqlFilter(name);
                id_filter = SQLFilter.sqlFilter(id);
                pw_filter = SQLFilter.sqlFilter(pw);
                mail_filter = SQLFilter.sqlFilter(email);
                //////////////////////////////////////////////////////////////////

                if("".equals(name)) { // 이름의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 이름를 입력하세요.", Toast.LENGTH_LONG).show();
                }else if("".equals(id)) { // 아이디(학번)의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 학번을 입력하세요.", Toast.LENGTH_LONG).show();
                }else if("".equals(pw)) { // 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 비밀번호을 입력하세요.", Toast.LENGTH_LONG).show();
                }else if("".equals(email)){ // 이메일의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 이메일을 입력하세요.", Toast.LENGTH_LONG).show();
                }
                else if(name.length()>=20 || id.length()>=20 || pw.length()>=255 || email.length()>=30){ // DB 값 오류 방지
                    Toast.makeText(getApplicationContext(), "Name or ID or Password or Email too Long error.", Toast.LENGTH_LONG).show();
                } else {
                    if(pw.matches(pw_regex)) { // 비밀번호 정책에 올바른 비밀번호 입력 시
                        if(email.matches(e_maile_regex)) { // 이메일을 올바르게 입력 시
                            if(name_filter || id_filter || pw_filter || mail_filter) {// SQL패턴 발견 시
                                Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                encryption_pw = BCrypt.hashpw(pw,BCrypt.gensalt(10)); // 사용할 비밀번호 클라이언트 단에서 해싱10회 진행
                                joinRequest();
                            }
                        } else // 이메일을 올바르게 입력하지 않을 시
                            Toast.makeText(getApplicationContext(), "올바른 형식의 이메일을 입력해주세요.\n"+ "예시) sample23@daum.net", Toast.LENGTH_LONG).show();
                    } else // 비밀번호 정책에 위배된 비밀번호 입력 시
                        Toast.makeText(getApplicationContext(), "비밀번호는 특수문자+숫자+영문자 혼합 8자이상입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    // 회원가입 DB로 전송
    private void joinRequest() {
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "accountAleadyExist":
                                Toast.makeText(getApplicationContext(), "이미 해당 아이디는 사용하고 있습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "notMember":
                                Toast.makeText(getApplicationContext(), "가입 대상자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error":
                                Toast.makeText(getApplicationContext(), "서버 오류입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            case "accountCreated":
                                Toast.makeText(getApplicationContext(), "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
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
                params.put("id", id);
                params.put("pwd",encryption_pw);
                params.put("name", name);
                params.put("mail",email);
                params.put("type","join");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void initView() {
        btn_signup = findViewById(R.id.btn_member_signup);
        btn_cancle = findViewById(R.id.btn_member_cancle);
        sign_pw = (EditText)findViewById(R.id.sign_pw);
        sign_id = (EditText)findViewById(R.id.sign_id);
        sign_name= (EditText)findViewById(R.id.sign_name);
        sign_mail= (EditText)findViewById(R.id.sign_email);
    }
}