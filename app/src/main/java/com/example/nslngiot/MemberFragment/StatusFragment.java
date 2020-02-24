package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatusFragment extends Fragment {

    private Button btn_refresh;

    private ImageView imgview_person,
            imgview_water,
            imgview_coffee,
            imgview_a4 ;

    private TextView person_state,
            water_state,
            coffee_state,
            a4_state;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_member_status,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_refresh = getView().findViewById(R.id.btn_refresh);
        imgview_person = getView().findViewById(R.id.imgview_person);
        imgview_water = getView().findViewById(R.id.imgview_water);
        imgview_coffee = getView().findViewById(R.id.imgview_coffee);
        imgview_a4 = getView().findViewById(R.id.imgview_a4);
        person_state = getView().findViewById(R.id.person_state);
        water_state = getView().findViewById(R.id.water_state);
        coffee_state = getView().findViewById(R.id.coffee_state);
        a4_state = getView().findViewById(R.id.a4_state);

        // 재실여부 조회
        member_Person_SelectRequest();

        // 새로고침 리스너
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 재실여부/물/커피/A4 잔여 상태 조회
                member_Person_SelectRequest();
//              member_Water_SelectRequest();
//              member_Coffee_SelectRequest();
//              member_A4_SelectRequest();
            }
        });
    }

    // 현재 랩실 재실여부 상태 조회 통신
    private void member_Person_SelectRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/DoorStatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if("open".equals(response.trim())) {
                            person_state.setText("사람있음");
                            imgview_person.setImageResource(R.drawable.people_exist);
                        } else if("close".equals(response.trim())) {
                            person_state.setText("사람없음");
                            imgview_person.setImageResource(R.drawable.people_nonexist);
                        } else if("error".equals(response)) {
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
                params.put("check","security");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    // 현재 랩실 물 잔여량 상태 조회 통신
    private void member_Water_SelectRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/StatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if("open".equals(response.trim())) {
                            water_state.setText("충분함");
                            imgview_water.setImageResource(R.drawable.water_exist);
                        } else if("close".equals(response.trim())) {
                            water_state.setText("부족함");
                            imgview_water.setImageResource(R.drawable.water_nonexist);
                        } else if("error".equals(response)) {
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
                params.put("check","security");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    // 현재 랩실 커피 잔여량 상태 조회 통신
    private void member_Coffee_SelectRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/StatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if("open".equals(response.trim())) {
                            coffee_state.setText("충분함");
                            imgview_coffee.setImageResource(R.drawable.coffee_exist);
                        } else if("close".equals(response.trim())) {
                            coffee_state.setText("부족함");
                            imgview_coffee.setImageResource(R.drawable.coffee_nonexist);
                        } else if("error".equals(response)) {
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
                params.put("check","security");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    // 현재 랩실 A4 잔여량 상태 조회 통신
    private void member_A4_SelectRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/StatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if("open".equals(response.trim())) {
                            a4_state.setText("충분함");
                            imgview_a4.setImageResource(R.drawable.a4_exist);
                        } else if("close".equals(response.trim())) {
                            a4_state.setText("부족함");
                            imgview_a4.setImageResource(R.drawable.a4_nonexist);
                        } else if("error".equals(response)) {
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