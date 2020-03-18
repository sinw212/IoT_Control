package com.example.nslngiot.ManagerFragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.example.nslngiot.Adapter.ManagerMemberAdapter;

import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.SQLFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberFragment extends Fragment {

    private RecyclerView recyclerView = null;
    private LinearLayoutManager layoutManager = null;
    private ManagerMemberAdapter managerMemberAdapter = null;
    private ArrayList<ManagerMemberData> arrayList;
    private ManagerMemberData managerMemberData;

    //sql 검증 결과 & default false
    private boolean name_filter = false,
            phone_filter = false,
            group_filter = false,
            course_filter = false;

    private String Name,
            Phone,
            course,
            group;

    private Button input;

    private EditText EditName,
            Editphone,
            EditCourse,
            EditGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_member, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Name = "";
        Phone = "";
        course = "";
        group = "";
        EditName = getView().findViewById(R.id.member_name);
        Editphone = getView().findViewById(R.id.member_phone);
        EditCourse = getView().findViewById(R.id.member_course);
        EditGroup = getView().findViewById(R.id.member_group);
        input = getView().findViewById((R.id.btn_add));
        recyclerView  = (RecyclerView)getView().findViewById(R.id.recyclerview_manager_member);

        // 조회 시작
        Manager_member_select_Request();

        // 인원 등록
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = EditName.getText().toString().trim();
                Phone = Editphone.getText().toString().trim();
                course = EditCourse.getText().toString().trim();
                group = EditGroup.getText().toString().trim();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 방어
                name_filter= SQLFilter.sqlFilter(Name);
                phone_filter= SQLFilter.sqlFilter(Phone);
                course_filter=SQLFilter.sqlFilter(course);
                group_filter=SQLFilter.sqlFilter(group);
                //////////////////////////////////////////////////////////////////

                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(Phone) || TextUtils.isEmpty(course) || TextUtils.isEmpty(group)) {
                    Toast.makeText(getActivity(), "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(name_filter || phone_filter || course_filter || group_filter){ // SQL패턴 발견 시
                    Toast.makeText(getActivity(), "공격시도가 발견되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    EditName.setText("");
                    Editphone.setText("");
                    EditCourse.setText("");
                    EditGroup.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Manager_memeber_Added_Request(); // 인원 등록 진행
                                Thread.sleep(100); // 0.1초 슬립
                                Manager_member_select_Request(); // 변경된 인원 현황 조회
                            } catch (InterruptedException e) {
                                System.err.println("ManagerMemberFragment InterruptedException error");
                            }
                        }
                    }).start();
                }
            }
        });
    }

    // 연구실 인원 정보 조회
    private void Manager_member_select_Request() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/MemberState.jsp");

       VolleyQueueSingleTon.manager_member_selectSharing = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            // 암호화된 대칭키를 키스토어의 개인키로 복호화
                            String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                            // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                            response = AES.aesDecryption(response.toCharArray(),decryptAESkey);
                            decryptAESkey = null; // 객체 재사용 취약 보호

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
                            managerMemberAdapter = new ManagerMemberAdapter(getActivity(),arrayList);
                            // 리사이클러뷰에 어답타 연결
                            recyclerView .setAdapter(managerMemberAdapter);
                        } catch (JSONException e) {
                            System.err.println("Manager MemberFragment SelectRequest Response JSONException error");
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

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey.toCharArray(),RSA.serverPublicKey.toCharArray()));
                params.put("type",AES.aesEncryption("memShow".toCharArray(),decryptAESkey));

                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        VolleyQueueSingleTon.manager_member_selectSharing.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(VolleyQueueSingleTon.manager_member_selectSharing);
    }

    // 연구실 인원 등록
    private void Manager_memeber_Added_Request() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/MemberState.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);
                        decryptAESkey = null; // 객체 재사용 취약 보호

                        switch (response.trim()) {
                            case "memAleadyExist": //해당 ID가 이미 존재 시
                                Toast.makeText(getActivity(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "memAddSuccess": // 정상적으로 입력되었을시
                                Toast.makeText(getActivity(), "입력되었습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error": // 오류
                                Toast.makeText(getActivity(), "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey.toCharArray(),RSA.serverPublicKey.toCharArray()));
                params.put("type",AES.aesEncryption("memAdd".toCharArray(),decryptAESkey));
                params.put("name", AES.aesEncryption(Name.toCharArray(),decryptAESkey));
                params.put("phone", AES.aesEncryption(Phone.toCharArray(),decryptAESkey));
                params.put("dept", AES.aesEncryption(course.toCharArray(),decryptAESkey));
                params.put("team", AES.aesEncryption(group.toCharArray(),decryptAESkey));

                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}