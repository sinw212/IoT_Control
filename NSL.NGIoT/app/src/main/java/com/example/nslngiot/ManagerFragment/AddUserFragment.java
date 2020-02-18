package com.example.nslngiot.ManagerFragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Adapter.ManagerAddUserAdapter;
import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.MainManagerActivity;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

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

    private Button input;
    private EditText etName,etId;
    private String url = "http://210.125.212.191:8888/IoT/User.jsp";
    public String ID,Name;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_adduser, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        etId = getView().findViewById(R.id.edit_manager_ID);
        etName = getView().findViewById(R.id.edit_manager_name);
        input = getView().findViewById((R.id.btn_add));
        recyclerView  = (RecyclerView)getView().findViewById(R.id.recyclerview_manager_adduser);


        // 조회 시작
        addUser_select_Request();

        // 등록 버튼
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ID = etId.getText().toString().trim();
                Name = etName.getText().toString().trim();

                if("".equals(Name) || "".equals(ID)){
                    Toast.makeText(getActivity(), "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    addUser_Added_Request(); // 회원 정보 등록

                    etId.setText("");
                    etName.setText("");
                    addUser_select_Request(); // 변경된 회원 정보 조회
                }
            }
        });


    }

    // 회원정보 조회
    private void addUser_select_Request() {
        VolleyQueueSingleTon.addUserselectSingleTon = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            layoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setHasFixedSize(true); // 아이템의 뷰를 일정하게하여 퍼포먼스 향상
                            recyclerView.setLayoutManager(layoutManager); // 앞에 선언한 리사이클러뷰를 매니저에 붙힘
                            // 기존 데이터와 겹치지 않기 위해생성자를 매번 새롭게 생성
                            arrayList = new ArrayList<ManagerAddUserData>();

                            JSONArray jarray = new JSONArray(response);
                            int size = jarray.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject row = jarray.getJSONObject(i);
                                ManagerAddUserData managerAddUserData = new ManagerAddUserData();
                                managerAddUserData.setID(row.getString("id"));
                                managerAddUserData.setName(row.getString("name"));
                                managerAddUserData.setNumber(String.valueOf(i+1));
                                arrayList.add(managerAddUserData);
                            }
                            // 어댑터에 add한 다량의 데이터 할당
                            managerAddUserAdapter = new ManagerAddUserAdapter(getActivity(),arrayList);
                            // 리사이클러뷰에 어답타 연결
                            recyclerView .setAdapter(managerAddUserAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("type", "addUser_List");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        VolleyQueueSingleTon.addUserselectSingleTon.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(VolleyQueueSingleTon.addUserselectSingleTon);
    }


    // 회원 정보 삽입
    private void addUser_Added_Request() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "IDAleadyExist"://해당 ID가 이미 존재 시
                                Toast.makeText(getActivity(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "addUser_addSuccess"://정상적으로 입력되었을시
                                Toast.makeText(getActivity(), "입력되었습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error"://오류
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), "default Error", Toast.LENGTH_SHORT).show();
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
                params.put("name", Name);
                params.put("id", ID);
                params.put("type", "addUser_Add");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}