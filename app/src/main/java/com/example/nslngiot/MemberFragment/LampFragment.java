package com.example.nslngiot.MemberFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

public class LampFragment extends Fragment {

    private Button btn_refresh, btn_lamp_on, btn_lamp_off;
    private TextView lamp_state;
    private LinearLayout lamp_on_state, lamp_off_state;

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
        lamp_state = getView().findViewById(R.id.lamp_state);
        lamp_on_state = getView().findViewById(R.id.lamp_on_state);
        lamp_off_state = getView().findViewById(R.id.lamp_off_state);

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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            member_Lamp_OnRequest();
                            Thread.sleep(100);
                            member_Lamp_SelectRequest();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        // 불 끄는 버튼 리스너
        btn_lamp_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            member_Lamp_OffRequest();
                            Thread.sleep(100);
                            member_Lamp_SelectRequest();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    // 현재 랩실 전등 상태 조회 통신
    private void member_Lamp_SelectRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/LightStatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "on":
                                //불 켜져 있을 때
                                lamp_state.setText("현재 상태 : ON");
                                btn_lamp_on.setEnabled(false); // 불 on 비활성화
                                btn_lamp_off.setEnabled(true); // 불 off 활성화
                                lamp_on_state.setBackgroundColor(Color.GRAY);
                                lamp_off_state.setBackgroundColor(Color.WHITE);
                                break;
                            case "off":
                                //불 꺼져 있을 때
                                lamp_state.setText("현재상태 : OFF");
                                btn_lamp_on.setEnabled(true); // 불 on 활성화
                                btn_lamp_off.setEnabled(false); // 불 off 비활성화
                                lamp_off_state.setBackgroundColor(Color.GRAY);
                                lamp_on_state.setBackgroundColor(Color.WHITE);
                                break;
                            case "error":
                                Toast.makeText(getActivity(), "시스템 오류입니다.", Toast.LENGTH_LONG).show();
                                break;
                            default:
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
                params.put("check","security");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    // 불을 on을 위한 통신
    private void member_Lamp_OnRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/LightAndroidControl.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()){
                            case "ok":
                                Toast.makeText(getActivity(), "연구실 불을 ON 하였습니다.", Toast.LENGTH_LONG).show();
                                break;
                            case "noAndroid":
                                Toast.makeText(getActivity(), "체크 값이 알맞지 않습니다.", Toast.LENGTH_LONG).show();
                                break;
                            default:
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
                params.put("check","security");
                params.put("light","1"); // 불 on 신호
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    // 불을 off 하기 위한 통신
    private void member_Lamp_OffRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/LightAndroidControl.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()){
                            case "ok":
                                Toast.makeText(getActivity(), "연구실 불을 OFF 하였습니다.", Toast.LENGTH_LONG).show();
                                break;
                            case "noAndroid":
                                Toast.makeText(getActivity(), "체크 값이 알맞지 않습니다.", Toast.LENGTH_LONG).show();
                                break;
                            default:
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
                params.put("check","security");
                params.put("light","0"); // 불 off 신호
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }
}