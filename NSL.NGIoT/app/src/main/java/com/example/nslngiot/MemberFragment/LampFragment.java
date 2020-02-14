package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LampFragment extends Fragment {
    Button btn_refresh, btn_lamp_on, btn_lamp_off;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_member_lamp, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_refresh = getView().findViewById(R.id.btn_refresh);
        btn_lamp_on = getView().findViewById(R.id.btn_lamp_on);
        btn_lamp_off = getView().findViewById(R.id.btn_lamp_off);

        member_Lamp_SelectRequest();

        // 새로고침 리스너
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전등 상태 조회
                member_Lamp_SelectRequest();
            }
        });

        // 불 키는 버튼 리스너
        btn_lamp_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아두이노 통신(불 스위치 모터) 부분
            }
        });

        // 불 끄는 버튼 리스너
        btn_lamp_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아두이노 통신(불 스위치 모터) 부분
            }
        });
    }

    // 현재 랩실 전등 상태 조회 통신
    private void member_Lamp_SelectRequest() {
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/LightStatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("전등",response);
                        if("on".equals(response.trim())) {
                            //불 켜져 있을 때
                            btn_lamp_on.setVisibility(View.VISIBLE);
                            btn_lamp_off.setVisibility(View.INVISIBLE);
//                            btn_lamp_on.setEnabled(true);
//                            btn_lamp_off.setEnabled(false);
                        } else if("off".equals(response.trim())) {
                            //불 꺼져 있을 때
                            btn_lamp_on.setVisibility(View.INVISIBLE);
                            btn_lamp_off.setVisibility(View.VISIBLE);
//                            btn_lamp_on.setEnabled(false);
//                            btn_lamp_off.setEnabled(true);
                        } else if("error".equals(response)) {
                            Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        } //noAndroid도 있다고 했던거 같은데 뭐였드라
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
                params.put("check","security");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }
}