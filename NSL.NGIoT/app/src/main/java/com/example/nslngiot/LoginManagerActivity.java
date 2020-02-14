package com.example.nslngiot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.SQLFilter;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class LoginManagerActivity extends AppCompatActivity {

    private final String pw_regex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$"; // 비밀번호 정규식

    private SharedPreferences login_Preferences;
    private CheckBox auto_login;

    private String name="",
            id="",
            pw="";

    //sql 검증 결과 & default false
    private boolean name_filter = false,
            id_filter = false,
            pw_filter = false;

    private EditText login_pw,
            login_id,
            login_name;

    private Button btn_manager_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manager);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        login_Preferences = getSharedPreferences("AUTOLOGIN", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
        if(login_Preferences.getBoolean("AUTO",false)) {
            // 자동 로그인 체크 시 if 동작, AUTO에 값이 없으면 false 동작으로 if 동작안함

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                name = KEYSTORE.keyStore_Decryption(login_Preferences.getString("NAME", "default"));
                id = KEYSTORE.keyStore_Decryption(login_Preferences.getString("ID", "20202020"));
            }
            auto_login.setChecked(true);
            Toast.makeText(getApplicationContext(), id + " " + name + "님 로그인 성공", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainManagerActivity.class);
            startActivity(intent);
            finish();

        }


        btn_manager_login.setOnClickListener(new View.OnClickListener() { // 로그인 진행
            @Override
            public void onClick(View v) {
                name = login_name.getText().toString();
                id = login_id.getText().toString();
                pw = login_pw.getText().toString();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 특수문자 공백처리 및 방어
                name_filter= SQLFilter.sqlFilter(name);
                id_filter = SQLFilter.sqlFilter(id);
                pw_filter = SQLFilter.sqlFilter(pw);
                //////////////////////////////////////////////////////////////////

                if("".equals(name)) { // 이름의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 이름를 입력하세요.", Toast.LENGTH_LONG).show();
                }else if("".equals(id) ){ // 아이디(학번)의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 학번을 입력하세요.", Toast.LENGTH_LONG).show();
                }else if("".equals(pw)) { // 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getApplicationContext(), "사용할 비밀번호을 입력하세요.", Toast.LENGTH_LONG).show();
                }else{
                    // 로그인 진행 시 SQL 인젝션 검증 절차 진행
                    //////////////////////////////////////////방어 코드////////////////////////////
                    if (name_filter || id_filter || pw_filter) {// SQL패턴 발견 시
                        Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }else if(name.length()>=20 || id.length()>=20 || pw.length()>=255 ){ // DB 값 오류 방지
                        Toast.makeText(getApplicationContext(), "Name or ID or Password too Long error.", Toast.LENGTH_LONG).show();
                        /////////////////////////////////////////////////////////////////////////////////////////////////
                    }else {
                        if(pw.matches(pw_regex)) { // 비밀번호 정책에 올바른 비밀번호 입력 시
                            if(auto_login.isChecked()){ // 자동 로그인 체크 & 로그인 성공
                                login_Preferences = getSharedPreferences("AUTOLOGIN", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
                                SharedPreferences.Editor editor = login_Preferences.edit();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    // 프리퍼런스에 암호화 하여 저장
                                    editor.putString("ID", KEYSTORE.keyStore_Encryption(id));
                                    editor.putString("PWD",KEYSTORE.keyStore_Encryption(pw));
                                    editor.putString("NAME",KEYSTORE.keyStore_Encryption(name));
                                    editor.putBoolean("AUTO",true);
                                    editor.apply();
                                }
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

    //데이터베이스로 넘김
    private void login_manager_Request() {
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");

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
                                Toast.makeText(getApplicationContext(), id+" "+name+"님 로그인 성공", Toast.LENGTH_SHORT).show();
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
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void initView() {
        btn_manager_login = findViewById(R.id.btn_manager_login);
        login_pw = (EditText)findViewById(R.id.login_Manager_pw);
        login_id = (EditText)findViewById(R.id.login_Manager_id);
        auto_login = (CheckBox)findViewById(R.id.cb_login_Manager_autologin);
    }
}