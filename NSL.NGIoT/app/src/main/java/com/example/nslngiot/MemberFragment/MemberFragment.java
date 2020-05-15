package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Adapter.MemberMemberAdapter;
import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.Network_Utill.NetworkURL;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberFragment extends Fragment {

    private RecyclerView recyclerView = null;
    private LinearLayoutManager layoutManager = null;
    private MemberMemberAdapter memberMemberAdapter = null;
    private ArrayList<ManagerMemberData> arrayList;
    private ManagerMemberData managerMemberData;

    private EditText EditName,
            Editphone,
            EditCourse,
            EditGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_member, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EditName = getView().findViewById(R.id.member_name);
        Editphone = getView().findViewById(R.id.member_phone);
        EditCourse = getView().findViewById(R.id.member_course);
        EditGroup = getView().findViewById(R.id.member_group);
        recyclerView  = getView().findViewById(R.id.recyclerview_member_member);

        member_select_Request();

    }

    // 연구실 인원 정보 조회
    private void member_select_Request() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(NetworkURL.MEMBER_STATE_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // 암호화된 대칭키를 키스토어의 개인키로 복호화
                            char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                            // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                            response = AES.aesDecryption(response.toCharArray(),decryptAESkey);

                            java.util.Arrays.fill(decryptAESkey,(char)0x20);

                            layoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setHasFixedSize(true); // 아이템의 뷰를 일정하게하여 퍼포먼스 향상
                            recyclerView.setLayoutManager(layoutManager); // 앞에 선언한 리사이클러뷰를 매니저에 붙힘
                            // 기존 데이터와 겹치지 않기 위해생성자를 매번 새롭게 생성
                            arrayList = new ArrayList<ManagerMemberData>();

                            JSONArray jarray = new JSONArray(response);
                            int size = jarray.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject row = jarray.getJSONObject(i);
                                managerMemberData= new ManagerMemberData();
                                managerMemberData.setPhone(row.getString("phone"));
                                managerMemberData.setName(row.getString("name"));
                                managerMemberData.setNumber(String.valueOf(i+1));
                                managerMemberData.setCourse(row.getString("dept"));
                                managerMemberData.setGroup(row.getString("team"));
                                arrayList.add(managerMemberData);
                            }
                            // 어댑터에 add한 다량의 데이터 할당
                            memberMemberAdapter = new MemberMemberAdapter(getActivity(),arrayList);
                            // 리사이클러뷰에 어답타 연결
                            recyclerView .setAdapter(memberMemberAdapter);
                        } catch (JSONException e) {
                            System.err.println("Member memberFragment Response JSONException error");
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
                params.put("type",AES.aesEncryption("memShow".toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}