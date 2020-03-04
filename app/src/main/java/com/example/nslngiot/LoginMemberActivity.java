package com.example.nslngiot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.SQLFilter;

import org.mindrot.jbcrypt.BCrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import androidx.appcompat.app.AppCompatActivity;

public class LoginMemberActivity extends AppCompatActivity {

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

    private Button btn_re_pw,
            btn_member_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_member);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        login_Preferences = getSharedPreferences("MemberLogin", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
        if(login_Preferences.getBoolean("AUTO",false)) {
            // 자동 로그인 체크 시 if 동작, AUTO에 값이 없으면 false 동작으로 if 동작안함
            name = KEYSTORE.keyStore_Decryption(login_Preferences.getString("NAME", "default"));
            id = KEYSTORE.keyStore_Decryption(login_Preferences.getString("ID", "default"));
            auto_login.setChecked(true);

            Toast.makeText(getApplicationContext(), id + " " + name + "님 로그인 성공", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainMemberActivity.class);
            startActivity(intent);
            finish();
        }

        btn_re_pw.setOnClickListener(new View.OnClickListener() { //비밀번호 재발급 진행
            @Override
            public void onClick(View v) { // 비밀번호 재 발급
                Intent intent = new Intent(getApplicationContext(), PasswordReissuanceActivity.class);
                startActivity(intent);
            }
        });

        btn_member_login.setOnClickListener(new View.OnClickListener() { // 로그인 진행
            @Override
            public void onClick(View v) {
                name = login_name.getText().toString().trim();
                id = login_id.getText().toString().trim();
                pw = login_pw.getText().toString().trim();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 방어
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
                    if (name_filter || id_filter || pw_filter) { // SQL패턴 발견 시
                        Toast.makeText(getApplicationContext(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }else if(name.length()>=20 || id.length()>=20 || pw.length()>=255 ){ // DB 값 오류 방지
                        Toast.makeText(getApplicationContext(), "Name or ID or Password too Long error.", Toast.LENGTH_LONG).show();
                        /////////////////////////////////////////////////////////////////////////////////////////////////
                    }else {
                        if(pw.matches(pw_regex)) { // 비밀번호 정책에 올바른 비밀번호 입력 시
                            if(auto_login.isChecked()){ // 자동 로그인 체크 & 로그인 성공
                                login_Preferences = getSharedPreferences("MemberLogin", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
                                SharedPreferences.Editor editor = login_Preferences.edit();
                                // 프리퍼런스에 암호화 하여 저장
                                editor.putString("ID", KEYSTORE.keyStore_Encryption(id));
                                editor.putString("NAME",KEYSTORE.keyStore_Encryption(name));
                                editor.putBoolean("AUTO",true);
                                editor.apply();

                            }
                            // 사용자 로그인 요청
                            login_member_Request();
                        }else // 비밀번호 정책에 위배된 비밀번호 입력 시
                            Toast.makeText(getApplicationContext(), "비밀번호는 특수문자+숫자+영문자 혼합 8자이상입니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    // 로그인 정보 전송
    private void login_member_Request() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // 암호화된 대칭키를 키스토어의 개인키로 복호화
                            String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                            // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                            response = AES.aesDecryption(response,decryptAESkey);

                            if("loginFailed".equals(response.trim()))
                                Toast.makeText(getApplicationContext(),"아이디를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                            else if("error".equals(response.trim()))
                                Toast.makeText(getApplicationContext(),"시스템 오류입니다.",Toast.LENGTH_SHORT).show();
                            else {
                                String[] resPonse_split = response.split(" ");
                                if ("loginSuccess".equals(resPonse_split[1])) {
                                    boolean vaild = BCrypt.checkpw(pw, resPonse_split[0]); // 암호화된 비밀번호 추출 및 일치 여부 체크
                                    if (vaild) { // 비밀번호 적합성 검증 성공 시 true
                                        Toast.makeText(getApplicationContext(), id + " " + name + "님 로그인 성공", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainMemberActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else // 비밀번호 불 일치
                                        Toast.makeText(getApplicationContext(), "비밀번호를 잘못 입력하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            decryptAESkey = null; // 객체 재사용 취약 보호
                            response = null;
                        } catch (UnsupportedEncodingException e) {
                           System.err.println("LoginMemberActivity Response UnsupportedEncodingException error");
                        } catch (NoSuchPaddingException e) {
                            System.err.println("LoginMemberActivity Response NoSuchPaddingException error");
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println("LoginMemberActivity Response NoSuchAlgorithmException error");
                        } catch (InvalidAlgorithmParameterException e) {
                            System.err.println("LoginMemberActivity Response InvalidAlgorithmParameterException error");
                        } catch (InvalidKeyException e) {
                            System.err.println("LoginMemberActivity Response InvalidKeyException error");
                        } catch (BadPaddingException e) {
                            System.err.println("LoginMemberActivity Response BadPaddingException error");
                        } catch (IllegalBlockSizeException e) {
                            System.err.println("LoginMemberActivity Response IllegalBlockSizeException error");
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

                try {
                    params.put("securitykey",RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey));
                    params.put("id", AES.aesEncryption(id,decryptAESkey));
                    params.put("name",AES.aesEncryption(name,decryptAESkey));
                    params.put("type",AES.aesEncryption("login",decryptAESkey));
                } catch (UnsupportedEncodingException e) {
                    System.err.println("LoginMemberActivity Request UnsupportedEncodingException error");
                } catch (NoSuchPaddingException e) {
                    System.err.println("LoginMemberActivity Request NoSuchPaddingException error");
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("LoginMemberActivity Request NoSuchAlgorithmException error");
                } catch (InvalidAlgorithmParameterException e) {
                    System.err.println("LoginMemberActivity Request InvalidAlgorithmParameterException error");
                } catch (InvalidKeyException e) {
                    System.err.println("LoginMemberActivity Request InvalidKeyException error");
                } catch (BadPaddingException e) {
                    System.err.println("LoginMemberActivity Request BadPaddingException error");
                } catch (IllegalBlockSizeException e) {
                    System.err.println("LoginMemberActivity Request IllegalBlockSizeException error");
                } catch (InvalidKeySpecException e) {
                    System.err.println("LoginMemberActivity Request InvalidKeySpecException error");
                }
                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void initView() {
        btn_re_pw = findViewById(R.id.btn_re_pw);
        btn_member_login = findViewById(R.id.btn_member_login);
        login_pw = findViewById(R.id.loginMember_pw);
        login_id = findViewById(R.id.loginMember_id);
        login_name = findViewById(R.id.loginMember_name);
        auto_login = findViewById(R.id.cb_login_Member_autologin);
    }
}