package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    ImageButton btn_refresh;

    ImageView personE = null;
    ImageView personNE = null;
    ImageView waterE = null;
    ImageView waterNE = null;
    ImageView coffeeE = null;
    ImageView coffeeNE = null;
    ImageView a4E = null;
    ImageView a4NE = null;

    TextView person_state;
    TextView water_state;
    TextView coffee_state;
    TextView a4_state;

    // 테스트로 해보려고 현재 시간 받아오기
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("mm");
    String formatDate = sdfNow.format(date);

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

        personE = getView().findViewById(R.id.btn_personE);
        personNE = getView().findViewById(R.id.btn_personNE);
        waterE = getView().findViewById(R.id.btn_waterE);
        waterNE = getView().findViewById(R.id.btn_waterNE);
        coffeeE = getView().findViewById(R.id.btn_coffeeE);
        coffeeNE = getView().findViewById(R.id.btn_coffeeNE);
        a4E = getView().findViewById(R.id.btn_a4E);
        a4NE = getView().findViewById(R.id.btn_a4NE);

        personE.setVisibility(View.VISIBLE);
        personNE.setVisibility(View.INVISIBLE);
        waterE.setVisibility(View.VISIBLE);
        waterNE.setVisibility(View.INVISIBLE);
        coffeeE.setVisibility(View.VISIBLE);
        coffeeNE.setVisibility(View.INVISIBLE);
        a4E.setVisibility(View.VISIBLE);
        a4NE.setVisibility(View.INVISIBLE);

        person_state = getView().findViewById(R.id.person_state);
        water_state = getView().findViewById(R.id.water_state);
        coffee_state = getView().findViewById(R.id.coffee_state);
        a4_state = getView().findViewById(R.id.a4_state);

        // 새로고침 리스너
        btn_refresh.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              // 재실여부/물/커피/A4 잔여 상태 조회
              member_Status_SelectRequest();
          }
        });
    }

    // 현재 랩실 전등 상태 조회 통신
    private void member_Status_SelectRequest() {
        //다시 확인
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/StatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 재실여부 상태 확인
                        if("open".equals(response.trim())) {
                            // 랩실에 사람 있을 때
                            person_state.setText("사람 있음");
                            personE.setVisibility(View.VISIBLE);
                            personNE.setVisibility(View.INVISIBLE);
                        } else if("close".equals(response.trim())) {
                            // 랩실에 사람 없을
                            person_state.setText("사람 없음");
                            personE.setVisibility(View.INVISIBLE);
                            personNE.setVisibility(View.VISIBLE);

                        // 물 잔여량 상태 확인
                        } else if("open".equals(response.trim())) {
                            // 물 충분히 있을 때
                            water_state.setText("충분함");
                            waterE.setVisibility(View.VISIBLE);
                            waterNE.setVisibility(View.INVISIBLE);
                        } else if("close".equals(response.trim())) {
                            // 물 부족할 때
                            water_state.setText("부족함");
                            waterE.setVisibility(View.INVISIBLE);
                            waterNE.setVisibility(View.VISIBLE);

                        // 커피 잔여량 상태 확인
                        } else if("open".equals(response.trim())) {
                            // 커피 충분히 있을 때
                            coffee_state.setText("충분함");
                            coffeeE.setVisibility(View.VISIBLE);
                            coffeeNE.setVisibility(View.INVISIBLE);
                        } else if("close".equals(response.trim())) {
                            // 커피 부족할 때
                            coffee_state.setText("부족함");
                            coffeeE.setVisibility(View.INVISIBLE);
                            coffeeNE.setVisibility(View.VISIBLE);

                        // A4 잔여량 상태 확인
                        } else if("open".equals(response.trim())) {
                            // A4 충분히 있을 때
                            a4_state.setText("충분함");
                            a4E.setVisibility(View.VISIBLE);
                            a4NE.setVisibility(View.INVISIBLE);
                        } else if("close".equals(response.trim())) {
                            // A4 부족할 때
                            a4_state.setText("부족함");
                            a4E.setVisibility(View.INVISIBLE);
                            a4NE.setVisibility(View.VISIBLE);

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