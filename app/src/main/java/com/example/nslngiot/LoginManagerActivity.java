package com.example.nslngiot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.EditTextCache;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.SQLFilter;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class LoginManagerActivity extends AppCompatActivity {

    private final String pw_regex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$"; // 비밀번호 정규식

    private SharedPreferences login_Preferences;
    private CheckBox auto_login;

    private char[] id;
    private char[] pw;

    //sql 검증 결과 & default false
    private boolean id_filter = false,
            pw_filter = false;

    private EditText login_pw,
            login_id;

    private Button btn_manager_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manager);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        login_Preferences = getSharedPreferences("ManagerLogin", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
        if(login_Preferences.getBoolean("AUTO",false)) {
            // 자동 로그인 체크 시 if 동작, AUTO에 값이 없으면 false 동작으로 if 동작안함
            id = KEYSTORE.keyStore_Decryption(login_Preferences.getString("ID", "default").toCharArray());
            auto_login.setChecked(true);
            Toast toast=Toast.makeText(getApplicationContext(), String.valueOf(id) +" 관리자님 로그인 성공", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), MainManagerActivity.class);
            startActivity(intent);
            finish();
            java.util.Arrays.fill(id, (char) 0x20);
        }

        btn_manager_login.setOnClickListener(new View.OnClickListener() { // 로그인 진행
            @Override
            public void onClick(View v) {
                id = login_id.getText().toString().toCharArray();
                pw = login_pw.getText().toString().toCharArray();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 방어
                id_filter = SQLFilter.sqlFilter(String.valueOf(id));
                pw_filter = SQLFilter.sqlFilter(String.valueOf(pw));
                //////////////////////////////////////////////////////////////////

                if(TextUtils.isEmpty(String.valueOf(id))){ // 아이디(학번)의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 학번을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(String.valueOf(pw))) { // 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 비밀번호을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else if(!"admin915".equals(String.valueOf(id))){ // 관리자 접근 권한 확인
                    Toast.makeText(getApplicationContext(),"관리자ID가 아닙니다.",Toast.LENGTH_SHORT).show();
                } else{
                    // 로그인 진행 시 SQL 인젝션 검증 절차 진행
                    //////////////////////////////////////////방어 코드////////////////////////////
                    if (id_filter || pw_filter) {// SQL패턴 발견 시
                        Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }else if( id.length >=20 || pw.length >=255 ){ // DB 값 오류 방지
                        Toast.makeText(getApplicationContext(), "ID or Password too Long error.", Toast.LENGTH_SHORT).show();
                        /////////////////////////////////////////////////////////////////////////////////////////////////
                    }else {
                        if(String.valueOf(pw).matches(pw_regex)) { // 비밀번호 정책에 올바른 비밀번호 입력 시
                            if(auto_login.isChecked()){ // 자동 로그인 체크 & 로그인 성공
                                login_Preferences = getSharedPreferences("ManagerLogin", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
                                SharedPreferences.Editor editor = login_Preferences.edit();
                                // 프리퍼런스에 암호화 하여 저장
                                editor.putString("ID", String.valueOf(KEYSTORE.keyStore_Encryption(id)));
                                editor.putBoolean("AUTO",true);
                                editor.apply();
                            }
                            // 사용자 로그인 요청
                            login_manager_Request();
                        }else // 비밀번호 정책에 위배된 비밀번호 입력 시
                            Toast.makeText(getApplicationContext(), "비밀번호는 특수문자+숫자+영문자 혼합 8자이상입니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    // 로그인 정보 전송
    private void login_manager_Request() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);

                        java.util.Arrays.fill(decryptAESkey,(char)0x20);

                        if("adminFailed".equals(response.trim())){
                            Toast.makeText(getApplicationContext(),"관리자 ID가 틀립니다.",Toast.LENGTH_SHORT).show();
                        }else if("error".equals(response.trim())){
                            Toast.makeText(getApplicationContext(),"시스템 오류입니다.",Toast.LENGTH_SHORT).show();
                        }else{
                            String[] resPonse_split = response.split(" ");
                            if("adminSuccess".equals(resPonse_split[1])){
                                boolean vaild = BCrypt.checkpw(String.valueOf(pw), resPonse_split[0]); // 암호화된 비밀번호 추출 및 일치 여부 체크
                                if (vaild) { // 비밀번호 적합성 검증 성공 시 true
                                    Toast toast=Toast.makeText(getApplicationContext(), String.valueOf(id) +" 관리자님 로그인 성공", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                    Intent intent = new Intent(getApplicationContext(), MainManagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else   // 비밀번호 불 일치
                                    Toast.makeText(getApplicationContext(),"비밀번호를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        java.util.Arrays.fill(pw, (char) 0x20); // 로그인 진행 과정에서 중요정보 메모리 삭제
                        java.util.Arrays.fill(id, (char) 0x20);
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
                char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey.toCharArray()));
                params.put("id", AES.aesEncryption(id,decryptAESkey));
                params.put("type",AES.aesEncryption("adminLogin".toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void initView() {
        btn_manager_login = findViewById(R.id.btn_manager_login);
        login_pw = findViewById(R.id.login_Manager_pw);
        login_id = findViewById(R.id.login_Manager_id);
        auto_login = findViewById(R.id.cb_login_Manager_autologin);

        id = new char[20];
        pw = new char[255];

        EditTextCache.editTextCacheSecurity(login_pw);
        EditTextCache.editTextCacheSecurity(login_id);
    }
}