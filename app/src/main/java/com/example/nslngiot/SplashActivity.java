package com.example.nslngiot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.Security_Utill.RSA;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rsaKeyRequest();
    }
    private void rsaKeyRequest(){ // RSA암호화에 사용할 공개키를 서버에게 요청
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Login.jsp");
        RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] resPonse_split = response.split(" ");
                        if("키받음".equals(resPonse_split[1])) {

                            RSA.publicKEY=resPonse_split[0]; //String으로 변환된 공개키 메모리에 정적로딩
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();

                        }else if("키 못받음".equals(resPonse_split[1])) {
                            Toast.makeText(getApplicationContext(), "암호화 셋팅 실패. 다시 실행해주세요.", Toast.LENGTH_SHORT).show();
                            finish();
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
                params.put("type","키주세요");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
}
