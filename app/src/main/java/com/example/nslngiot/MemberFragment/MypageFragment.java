package com.example.nslngiot.MemberFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.MainActivity;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.EditTextCache;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.SQLFilter;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment {

    private final String pw_regex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$"; // 비밀번호 정규식
    private SharedPreferences login_Preferences;

    private char[] member_name;
    private char[] member_id;
    private char[] member_corrently_pw;
    private char[] member_new_pw;
    private char[] member_modify_pw;

    //sql 검증 결과 & default false
    private boolean member_name_filter = false,
            member_id_filter = false,
            member_corrently_pw_filter = false,
            member_new_pw_filter = false,
            member_modify_pw_filter = false;

    private EditText name,
            id,
            corr_pw,
            new_pw,
            modify_pw;

    private Button
            btn_member_Modifiy,
            btn_member_logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_member_mypage,container,false);
        return  v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        initView();

        btn_member_Modifiy.setOnClickListener(new View.OnClickListener() { // 비밀번호 변경
            @Override
            public void onClick(View view) {
                member_name = name.getText().toString().toCharArray();
                member_id = id.getText().toString().trim().toCharArray();
                member_corrently_pw= corr_pw.getText().toString().toCharArray();
                member_new_pw = new_pw.getText().toString().toCharArray();
                member_modify_pw = modify_pw.getText().toString().toCharArray();

                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 특수문자 공백처리 및 방어
                member_name_filter = SQLFilter.sqlFilter(String.valueOf(member_name));
                member_id_filter =  SQLFilter.sqlFilter(String.valueOf(member_id));
                member_corrently_pw_filter = SQLFilter.sqlFilter(String.valueOf(member_corrently_pw));
                member_new_pw_filter = SQLFilter.sqlFilter(String.valueOf(member_new_pw));
                member_modify_pw_filter = SQLFilter.sqlFilter(String.valueOf(member_modify_pw));
                //////////////////////////////////////////////////////////////////
                if (TextUtils.isEmpty(String.valueOf(member_name))) { // 현재 이름의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getActivity(), "이름를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(String.valueOf(member_id))) { // 현재 아이디(학번)의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getActivity(), "학번을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(String.valueOf(member_corrently_pw))) { // 현재 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getActivity(), "현재 비밀번호을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(String.valueOf(member_new_pw))) { // 변경할 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getActivity(), "변경할 비밀번호을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(String.valueOf(member_modify_pw))) { // 변경을 확인할 비밀번호의 공백 입력 및 널문자 입력 시
                    Toast.makeText(getActivity(), "변경 비밀번호를 한번 더 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 비밀번호 진행 시 SQL 인젝션 검증 절차 진행
                    //////////////////////////////////////////방어 코드////////////////////////////
                    if (member_name_filter || member_id_filter || member_corrently_pw_filter ) {// SQL패턴 발견 시
                        Toast.makeText(getActivity(), "공격시도가 발견되었습니다.", Toast.LENGTH_SHORT).show();
                    } else if(member_new_pw_filter || member_modify_pw_filter){
                        Toast.makeText(getActivity(), "공격시도가 발견되었습니다.", Toast.LENGTH_SHORT).show();
                    } else if (member_name.length >= 20 || member_id.length >= 20 ||member_corrently_pw.length >= 255) { // DB 값 오류 방지
                        Toast.makeText(getActivity(), "Name or ID or Password too Long error.", Toast.LENGTH_SHORT).show();
                    } else if(member_new_pw.length>=255 || member_modify_pw.length>=255){ // DB 값 오류 방지
                        Toast.makeText(getActivity(), "New Password or Modify Password too Long error.", Toast.LENGTH_SHORT).show();
                    }else {
                        if(String.valueOf(member_corrently_pw).matches(pw_regex)){ // 비밀번호 정책에 올바른 비밀번호 입력 시
                            if(Arrays.equals(member_new_pw, member_modify_pw)) { // 새로운 비밀번호 비교 검증
                                if(String.valueOf(member_new_pw).matches(pw_regex)){ // 비밀번호 정책에 올바른 비밀번호 입력 시
                                    // 변경할 비밀번호 클라이언트 단에서 해싱10회 진행
                                    member_new_pw = BCrypt.hashpw(String.valueOf(member_new_pw),BCrypt.gensalt(10)).toCharArray();
                                    member_ModifyRequest(); // DB로 개인정보 전송
                                } else// 비밀번호 정책에 위배된 비밀번호 입력 시
                                    Toast.makeText(getActivity(), "수정할 비밀번호는 특수문자+숫자+영문자 혼합 8자이상입니다.", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getActivity(), "변경 할 비밀번호를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(getActivity(), "현재 비밀번호는 특수문자+숫자+영문자 혼합 8자이상입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 로그아웃 진행
        btn_member_logout.setOnClickListener(new View.OnClickListener() { // 로그아웃
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setCancelable(false)
                        .setTitle("[공주대학교 네트워크 보안연구실]")
                        .setMessage("정말 로그아웃 하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                login_Preferences = getActivity().getSharedPreferences("MemberLogin", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
                                SharedPreferences.Editor editor = login_Preferences.edit();
                                editor.clear(); // 자동 로그인 정보 삭제
                                editor.apply();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    //데이터베이스로 넘김
    private void member_ModifyRequest() {
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

                        switch (response.trim()) {
                            case "pwdChangeSuccess":
                                Toast.makeText(getActivity(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                // 비밀번호 변경 후, 재 로그인을 위해 자동 로그인&현재 로그인상태 해제
                                login_Preferences = getActivity().getSharedPreferences("MemberLogin", Activity.MODE_PRIVATE); // 해당 앱 말고는 접근 불가
                                SharedPreferences.Editor editor = login_Preferences.edit();
                                editor.clear(); // 자동 로그인 정보 삭제
                                editor.apply();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                                break;
                            case "pwdChangeFailed":
                                Toast.makeText(getActivity(), "현재 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "idNotExist":
                                Toast.makeText(getActivity(), "ID(학번)가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error":
                                Toast.makeText(getActivity(), "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey.toCharArray()));
                params.put("id", AES.aesEncryption(member_id,decryptAESkey));
                params.put("pwd", AES.aesEncryption(member_new_pw,decryptAESkey));
                params.put("b_pwd", AES.aesEncryption(member_corrently_pw,decryptAESkey));
                params.put("type", AES.aesEncryption("change".toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                java.util.Arrays.fill(member_name, (char) 0x20);  // 비밀번호 변경 진행 과정에서 중요정보 메모리 삭제
                java.util.Arrays.fill(member_id, (char) 0x20);
                java.util.Arrays.fill(member_corrently_pw, (char) 0x20);
                java.util.Arrays.fill(member_new_pw, (char) 0x20);
                java.util.Arrays.fill(member_modify_pw, (char) 0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    private void initView(){
        btn_member_Modifiy = getView().findViewById(R.id.btn_member_mypage_modify);
        btn_member_logout = getView().findViewById(R.id.btn_member_mypage_logout);
        name = getView().findViewById(R.id.member_name);
        id = getView().findViewById(R.id.member_id);
        corr_pw = getView().findViewById(R.id.member_corrently_pw);
        new_pw = getView().findViewById(R.id.member_new_pw);
        modify_pw = getView().findViewById(R.id.member_modifypw);

        member_name = new char[20];
        member_id = new char[20];
        member_corrently_pw = new char[255];
        member_new_pw = new char[255];
        member_modify_pw = new char[255];

        EditTextCache.editTextCacheSecurity(name);
        EditTextCache.editTextCacheSecurity(id);
        EditTextCache.editTextCacheSecurity(corr_pw);
        EditTextCache.editTextCacheSecurity(new_pw);
        EditTextCache.editTextCacheSecurity(modify_pw);
    }
}