package com.example.nslngiot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.SQLFilter;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private final String pw_regex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$"; // 비밀번호 정규식
    private final String e_maile_regex = "^[a-zA-Z0-9]+\\@[a-zA-Z]+\\.[a-zA-Z]+$"; // 이메일 정규식

    private char[] email;
    private char[] name;
    private char[] id;
    private char[] pw;
    private char[] encryption_pw;

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
                id =sign_id.getText().toString().toCharArray();
                pw =sign_pw.getText().toString().toCharArray();
                name =sign_name.getText().toString().toCharArray();
                email = sign_mail.getText().toString().toCharArray();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 특수문자 공백처리 및 방어
                name_filter= SQLFilter.sqlFilter(String.valueOf(name));
                id_filter = SQLFilter.sqlFilter(String.valueOf(id));
                pw_filter = SQLFilter.sqlFilter(String.valueOf(pw));
                mail_filter = SQLFilter.sqlFilter(String.valueOf(email));
                //////////////////////////////////////////////////////////////////

                if(TextUtils.isEmpty(String.valueOf(name))) { // 이름의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 이름를 입력하세요.", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(String.valueOf(id))) { // 아이디(학번)의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 학번을 입력하세요.", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(String.valueOf(pw))) { // 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 비밀번호을 입력하세요.", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(String.valueOf(email))){ // 이메일의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 이메일을 입력하세요.", Toast.LENGTH_LONG).show();
                }
                else if(name.length>=20 || id.length>=20 || pw.length>=255 || email.length>=30){ // DB 값 오류 방지
                    Toast.makeText(getApplicationContext(), "Name or ID or Password or Email too Long error.", Toast.LENGTH_LONG).show();
                } else {
                    if(String.valueOf(pw).matches(pw_regex)) { // 비밀번호 정책에 올바른 비밀번호 입력 시
                        if(String.valueOf(email).matches(e_maile_regex)) { // 이메일을 올바르게 입력 시
                            if(name_filter || id_filter || pw_filter || mail_filter) {// SQL패턴 발견 시
                                Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                // 사용할 비밀번호 클라이언트 단에서 해싱10회 진행
                                encryption_pw = BCrypt.hashpw(String.valueOf(pw),BCrypt.gensalt(10)).toCharArray();
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
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);
                        decryptAESkey = null;

                        switch (response.trim()) {
                            case "accountAleadyExist":
                                Toast.makeText(getApplicationContext(), "이미 해당 아이디는 사용하고 있습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "notMember":
                                Toast.makeText(getApplicationContext(), "가입 대상자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error":
                                Toast.makeText(getApplicationContext(), "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
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
                // 암호화된 대칭키를 키스토어의 개인키로 복호화
                String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey.toCharArray(),RSA.serverPublicKey.toCharArray()));
                params.put("id", AES.aesEncryption(id,decryptAESkey));
                params.put("pwd",AES.aesEncryption(encryption_pw,decryptAESkey));
                params.put("name",AES.aesEncryption(name,decryptAESkey));
                params.put("mail",AES.aesEncryption(email,decryptAESkey));
                params.put("type",AES.aesEncryption("join".toCharArray(),decryptAESkey));

                decryptAESkey = null;
                java.util.Arrays.fill(encryption_pw, (char) 0x20);  // 회원가입 진행 과정에서 중요정보 메모리 삭제
                java.util.Arrays.fill(pw, (char) 0x20);
                java.util.Arrays.fill(email, (char) 0x20);
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
        sign_pw = findViewById(R.id.sign_pw);
        sign_id = findViewById(R.id.sign_id);
        sign_name= findViewById(R.id.sign_name);
        sign_mail= findViewById(R.id.sign_email);

        email=new char[50];
        name = new char[20];
        id= new char[20];
        pw = new char[255];
        encryption_pw = new char[255];
    }
}