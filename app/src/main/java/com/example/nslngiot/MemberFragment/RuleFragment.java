package com.example.nslngiot.MemberFragment;

import android.content.Intent;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.XSSFilter;

import java.util.HashMap;
import java.util.Map;

public class RuleFragment extends Fragment {

    private EditText member_rule = getView().findViewById(R.id.member_rule);
    private Button btn_member_rule_save = getView().findViewById(R.id.btn_member_back);

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
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/User.jsp추후협의");
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()){
                            case "성공":
                                // XSS 방지
                                member_rule.setText(XSSFilter.xssFilter(response));
                                break;
                            case "실패":
                                Toast.makeText(getActivity(), "규칙을 등록하지 못했습니다.", Toast.LENGTH_LONG).show();
                                break;
                            case "디비오류?":
                                Toast.makeText(getActivity(), "서버오류입니다.", Toast.LENGTH_LONG).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
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
                // '규칙등록'이라는 신호 정보 push 진행
                params.put("?","시그널뭘로하지");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
}
