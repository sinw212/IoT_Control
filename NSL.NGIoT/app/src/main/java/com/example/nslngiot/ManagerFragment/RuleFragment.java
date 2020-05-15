package com.example.nslngiot.ManagerFragment;

import android.os.Bundle;
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
import com.example.nslngiot.Network_Utill.NetworkURL;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.XSSFilter;

import java.util.HashMap;
import java.util.Map;

public class RuleFragment extends Fragment {

    private String manager_rule_value="";
    private EditText manager_rule;
    private Button btn_manager_rule_save;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_manager_rule,container,false);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        // 등록된 랩실 규칙 확인
        manager_Rule_SelectRequest();

        btn_manager_rule_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager_rule_value = manager_rule.getText().toString().trim();
                //////////////////////////////방어 코드////////////////////////////
                //XSS 특수문자 공백처리 및 방어
                manager_rule_value = XSSFilter.xssFilter(manager_rule_value);
                //////////////////////////////////////////////////////////////////
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 랩실 규칙 등록
                            manager_Rule_SaveRequest();
                            Thread.sleep(100);
                            // 등록된 랩실 규칙 확인
                            manager_Rule_SelectRequest();
                        } catch (InterruptedException e) {
                            System.err.println("Manager RuleFragment InterruptedException error");
                        }
                    }
                }).start();
            }
        });
    }

    //규칙 등록 통신
    private void manager_Rule_SaveRequest(){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(NetworkURL.RULE_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);

                        java.util.Arrays.fill(decryptAESkey,(char)0x20);
                        switch (response.trim()){
                            case "ruleAdded":
                                Toast.makeText(getActivity(), "규칙을 등록하였습니다.", Toast.LENGTH_SHORT).show();
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
                params.put("type",AES.aesEncryption("ruleAdd".toCharArray(),decryptAESkey));
                params.put("text",AES.aesEncryption(manager_rule_value.toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    // 현재 등록된 규칙 조회
    private void manager_Rule_SelectRequest(){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(NetworkURL.RULE_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);

                        java.util.Arrays.fill(decryptAESkey,(char)0x20);
                        if("ruleNotExist".equals(response.trim())) // 등록된 규칙이 없을 시
                            manager_rule.setText("현재 규칙이 등록되어있지 않습니다.");
                        else if("error".equals(response.trim())){ // 시스템 오류
                            manager_rule.setText("시스템 오류입니다.");
                            Toast.makeText(getActivity(), "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            String[] resPonse_split = response.split("-");
                            if("ruleExist".equals(resPonse_split[resPonse_split.length-1])){ // 등록된 규칙을 받았을 시
                                StringBuilder resPonseBuilder = new StringBuilder(4096);
                                int size = resPonse_split.length-1;
                                for(int i=0 ; i <size ;i++)
                                    resPonseBuilder.append(resPonse_split[i]);
                                manager_rule.setText(XSSFilter.xssFilter(resPonseBuilder.toString().trim()));
                            }else{
                                Toast.makeText(getActivity(), "알 수없는 오류입니다.", Toast.LENGTH_SHORT).show();
                            }
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
                params.put("type",AES.aesEncryption("ruleShow".toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }
    private void initView(){
        manager_rule = getView().findViewById(R.id.manager_rule);
        btn_manager_rule_save = getView().findViewById(R.id.btn_manager_rule_save);
    }
}