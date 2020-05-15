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
import com.example.nslngiot.Adapter.ManagerAddUserAdapter;
import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.Network_Utill.NetworkURL;
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

public class AddUserFragment extends Fragment {

    private RecyclerView recyclerView = null;
    private LinearLayoutManager layoutManager = null;
    private ManagerAddUserAdapter managerAddUserAdapter = null;
    private ArrayList<ManagerAddUserData> arrayList;
    private ManagerAddUserData managerAddUserData;

    private Button input;
    private EditText etName,etId;
    private String ID,Name;

    //sql 검증 결과 & default false
    private boolean name_filter = false,
            id_filter = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_adduser, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ID="";
        Name = "";
        etId = getView().findViewById(R.id.edit_manager_ID);
        etName = getView().findViewById(R.id.edit_manager_name);
        input = getView().findViewById((R.id.btn_add));
        recyclerView  = (RecyclerView)getView().findViewById(R.id.recyclerview_manager_adduser);


        // 조회 시작
        addUser_select_Request();

        // 신규 인원 등록 (등록을 해야 회원 가입이 가능)
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ID = etId.getText().toString().trim();
                Name = etName.getText().toString().trim();
                //////////////////////////////방어 코드////////////////////////////
                //SQL 인젝션 방어
                name_filter = SQLFilter.sqlFilter(Name);
                id_filter = SQLFilter.sqlFilter(ID);
                ////////////////////////////////////////////////////////////////
                if(TextUtils.isEmpty(Name) || TextUtils.isEmpty(ID)){
                    Toast.makeText(getActivity(), "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(id_filter || name_filter){
                    Toast.makeText(getActivity(), "공격시도가 발견되었습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    etId.setText("");
                    etName.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                addUser_Added_Request(); // 회원 정보 등록
                                Thread.sleep(100); // 0.1 초 슬립
                                addUser_select_Request(); // 변경된 회원 정보 조회
                            } catch (InterruptedException e) {
                                System.err.println("Manager AddUserFragment InterruptedException error");
                            }
                        }
                    }).start();
                }
            }
        });
    }

    // 회원정보 조회
    private void addUser_select_Request() {
        VolleyQueueSingleTon.addUser_selectSharing = new StringRequest(
                Request.Method.POST, String.valueOf(NetworkURL.USER_URL),
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
                            arrayList = new ArrayList<ManagerAddUserData>();

                            JSONArray jarray = new JSONArray(response);
                            int size = jarray.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject row = jarray.getJSONObject(i);
                                managerAddUserData = new ManagerAddUserData();
                                managerAddUserData.setID(row.getString("id"));
                                managerAddUserData.setName(row.getString("name"));
                                managerAddUserData.setNumber(String.valueOf(i+1));
                                arrayList.add(managerAddUserData);
                            }
                            // 어댑터에 add한 다량의 데이터 할당
                            managerAddUserAdapter = new ManagerAddUserAdapter(getActivity(),arrayList);
                            // 리사이클러뷰에 어답타 연결
                            recyclerView .setAdapter(managerAddUserAdapter);
                        }  catch (JSONException e) {
                            System.err.println("Manager AddUserFragment SelectRequest Response JSONException error");
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
                params.put("type",AES.aesEncryption("addUser_List".toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        VolleyQueueSingleTon.addUser_selectSharing.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(VolleyQueueSingleTon.addUser_selectSharing);
    }


    // 회원 정보 삽입
    private void addUser_Added_Request() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(NetworkURL.USER_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);

                        java.util.Arrays.fill(decryptAESkey,(char)0x20);
                        switch (response) {
                            case "IDAleadyExist": // 해당 ID가 이미 존재 시
                                Toast.makeText(getActivity(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "addUser_addSuccess": // 정상적으로 입력되었을시
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
                char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey.toCharArray()));
                params.put("type",AES.aesEncryption("addUser_Add".toCharArray(),decryptAESkey));
                params.put("name", AES.aesEncryption(Name.toCharArray(),decryptAESkey));
                params.put("id", AES.aesEncryption(ID.toCharArray(),decryptAESkey));

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