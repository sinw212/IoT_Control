package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.XSSFilter;

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

public class RuleFragment extends Fragment {

    private TextView member_rule;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_member_rule,container,false);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        member_rule = getView().findViewById(R.id.member_rule);
        member_Rule_SelectRequest();
    }

    // 현재 등록된 규칙 조회 통신
    private void member_Rule_SelectRequest(){
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Rule.jsp");

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

                            if("ruleNotExist".equals(response.trim())) // 등록된 규칙이 없을 시
                                member_rule.setText("현재 규칙이 등록되어있지 않습니다.");
                            else if("error".equals(response.trim())){ // 시스템 오류
                                member_rule.setText("시스템 오류입니다.");
                                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                            }else{
                                String[] resPonse_split = response.split("-");
                                if("ruleExist".equals(resPonse_split[1])){ // 등록된 규칙을 받았을 시
                                    member_rule.setText(XSSFilter.xssFilter(resPonse_split[0]));
                                }
                            }
                            decryptAESkey = null; // 객체 재사용 취약 보호
                            response = null;
                        } catch (UnsupportedEncodingException e) {
                            System.err.println("Member RuleFragment Response UnsupportedEncodingException error");
                        } catch (NoSuchPaddingException e) {
                            System.err.println("Member RuleFragment Response NoSuchPaddingException error");
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println("Member RuleFragment Response NoSuchAlgorithmException error");
                        } catch (InvalidAlgorithmParameterException e) {
                            System.err.println("Member RuleFragment Response InvalidAlgorithmParameterException error");
                        } catch (InvalidKeyException e) {
                            System.err.println("Member RuleFragment Response InvalidKeyException error");
                        } catch (BadPaddingException e) {
                            System.err.println("Member RuleFragment Response BadPaddingException error");
                        } catch (IllegalBlockSizeException e) {
                            System.err.println("Member RuleFragment Response IllegalBlockSizeException error");
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
                    params.put("securitykey", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey));
                    params.put("type",AES.aesEncryption("ruleShow",decryptAESkey));
                } catch (BadPaddingException e) {
                    System.err.println("Member RuleFragment Request BadPaddingException error");
                } catch (IllegalBlockSizeException e) {
                    System.err.println("Member RuleFragment Request IllegalBlockSizeException error");
                } catch (InvalidKeySpecException e) {
                    System.err.println("Member RuleFragment Request InvalidKeySpecException error");
                } catch (NoSuchPaddingException e) {
                    System.err.println("Member RuleFragment Request NoSuchPaddingException error");
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("Member RuleFragment Request NoSuchAlgorithmException error");
                } catch (InvalidKeyException e) {
                    System.err.println("Member RuleFragment Request InvalidKeyException error");
                } catch (InvalidAlgorithmParameterException e) {
                    System.err.println("Member RuleFragment Request InvalidAlgorithmParameterException error");
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Member RuleFragment Request UnsupportedEncodingException error");
                }
                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }
}