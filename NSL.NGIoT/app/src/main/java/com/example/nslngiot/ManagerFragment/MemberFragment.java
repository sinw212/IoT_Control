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
import com.example.nslngiot.Adapter.ManagerMemberAdapter;

import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

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

    private String url = "http://210.125.212.191:8888/IoT/MemberState.jsp";

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


                if ("".equals(Name) || "".equals(Phone) || "".equals(course) || "".equals(group)) {
                    Toast.makeText(getActivity(), "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
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
        VolleyQueueSingleTon.manager_member_selectSharing = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
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
                params.put("type", "memShow");
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
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "memAleadyExist": //해당 ID가 이미 존재 시
                                Toast.makeText(getActivity(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "memAddSuccess": // 정상적으로 입력되었을시
                                Toast.makeText(getActivity(), "입력되었습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error": // 오류
                                Toast.makeText(getActivity(), "시스템 오류", Toast.LENGTH_SHORT).show();
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
                params.put("name", Name);
                params.put("phone", Phone);
                params.put("dept", course);
                params.put("team", group);
                params.put("type", "memAdd");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}