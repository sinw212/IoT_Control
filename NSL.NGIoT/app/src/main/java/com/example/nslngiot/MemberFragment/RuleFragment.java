package com.example.nslngiot.MemberFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.XSSFilter;

import java.util.HashMap;
import java.util.Map;

public class RuleFragment extends Fragment {

    private TextView member_rule = getView().findViewById(R.id.member_rule);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_member_rule,container,false);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 등록된 랩실 규칙 조회
        member_Rule_SelectRequest();
    }
    // 현재 등록된 규칙 조회 통신
    private void member_Rule_SelectRequest(){
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Rule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] resPonse_split = response.split(" ");
                        if("ruleExist".equals(resPonse_split[1])){
                            member_rule.setText(XSSFilter.xssFilter(resPonse_split[0]));
                        }else if("ruleNotExist".equals(resPonse_split[1])){
                            member_rule.setText("현재 규칙이 등록되어있지 않습니다.");
                        } else if("error".equals(resPonse_split[1])){
                            member_rule.setText("시스템 오류입니다.");
                            Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
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
                // '규칙등록'이라는 신호 정보 push 진행
                params.put("type","ruleShow");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }
}