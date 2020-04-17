package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
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
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;

import java.util.HashMap;
import java.util.Map;

public class StatusFragment extends Fragment {

    private ImageButton btn_refresh;

    private ImageView imgview_personE,
            imgview_personNE,
            imgview_coffeeE,
            imgview_coffeeNE,
            imgview_a4E,
            imgview_a4NE;

    private TextView person_state,
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

        initView();

        lab_All_SelectRequest(); // 재실여부 조회

        // 새로고침 리스너
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 재실여부/커피잔여/A4잔여 상태조회
                lab_All_SelectRequest();
                Toast.makeText(getActivity(),"연구실 정보 조회완료",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 현재 랩실 재실여부 상태 조회 통신
    private void lab_All_SelectRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/IoTStatusCheck.jsp");

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

                        String[] resPonse_split = response.split("-");
                        switch (resPonse_split[0].trim()){ // 0번지는 재실여부
                            case "open":
                                person_state.setText("사람있음");
                                imgview_personE.setVisibility(View.VISIBLE);
                                imgview_personNE.setVisibility(View.INVISIBLE);
                                break;
                            case "close":
                                person_state.setText("사람없음");
                                imgview_personE.setVisibility(View.INVISIBLE);
                                imgview_personNE.setVisibility(View.VISIBLE);
                                break;
                            default:
                                person_state.setText("재실오류");
                                imgview_personE.setVisibility(View.INVISIBLE);
                                imgview_personNE.setVisibility(View.VISIBLE);
                                break;
                        }

                        switch (resPonse_split[1].trim()){ // 1번지는 커피 여부
                            case "coffeeenough":
                                coffee_state.setText("충분함");
                                imgview_coffeeE.setVisibility(View.VISIBLE);
                                imgview_coffeeNE.setVisibility(View.INVISIBLE);
                                break;
                            case "coffeelack":
                                coffee_state.setText("부족함");
                                imgview_coffeeE.setVisibility(View.INVISIBLE);
                                imgview_coffeeNE.setVisibility(View.VISIBLE);
                                break;
                            default:
                                coffee_state.setText("커피오류");
                                imgview_coffeeE.setVisibility(View.INVISIBLE);
                                imgview_coffeeNE.setVisibility(View.VISIBLE);
                                break;
                        }

                        switch (resPonse_split[2].trim()){ // 2번지는 A4 여부
                            case "A4enough":
                                a4_state.setText("충분함");
                                imgview_a4E.setVisibility(View.VISIBLE);
                                imgview_a4NE.setVisibility(View.INVISIBLE);
                                break;
                            case "A4lack":
                                a4_state.setText("부족함");
                                imgview_a4E.setVisibility(View.INVISIBLE);
                                imgview_a4NE.setVisibility(View.VISIBLE);
                                break;
                            default:
                                a4_state.setText("종이오류");
                                imgview_a4E.setVisibility(View.INVISIBLE);
                                imgview_a4NE.setVisibility(View.VISIBLE);
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

                params.put("key", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey.toCharArray()));
                params.put("check",AES.aesEncryption("security".toCharArray(),decryptAESkey));

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
        btn_refresh = getView().findViewById(R.id.btn_refresh);
        imgview_personE = getView().findViewById(R.id.imgview_personE);
        imgview_personNE = getView().findViewById(R.id.imgview_personNE);
        imgview_coffeeE = getView().findViewById(R.id.imgview_coffeeE);
        imgview_coffeeNE = getView().findViewById(R.id.imgview_coffeeNE);
        imgview_a4E = getView().findViewById(R.id.imgview_a4E);
        imgview_a4NE = getView().findViewById(R.id.imgview_a4NE);
        person_state = getView().findViewById(R.id.person_state);
        coffee_state = getView().findViewById(R.id.coffee_state);
        a4_state = getView().findViewById(R.id.a4_state);

        imgview_personE.setVisibility(View.INVISIBLE);
        imgview_personNE.setVisibility(View.VISIBLE);
        imgview_coffeeE.setVisibility(View.INVISIBLE);
        imgview_coffeeNE.setVisibility(View.VISIBLE);
        imgview_a4E.setVisibility(View.INVISIBLE);
        imgview_a4NE.setVisibility(View.VISIBLE);
    }
}