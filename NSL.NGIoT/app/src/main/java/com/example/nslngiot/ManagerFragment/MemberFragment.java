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

    public RecyclerView m_mrv = null;
    public ManagerMemberAdapter m_ma = null;
    ArrayList<ManagerMemberData> m_memberlist = new ArrayList<ManagerMemberData>();
    private String url = "http://210.125.212.191:8888/IoT/MemberState.jsp";

    public String Name;
    public String Phone;
    public String Dept;
    public String Team;

    public Button input;
    public Button Delete;

    public EditText EditName;
    public EditText Editphone;
    public EditText EditCourse;
    public EditText EditGroup;


    public int menu;// 1. 등록 2. 삭제 3. 데이터 갱신 4. 수정

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_member, container, false);

        EditName = view.findViewById(R.id.member_name);
        Editphone = view.findViewById(R.id.member_phone);
        EditCourse = view.findViewById(R.id.member_course);
        EditGroup = view.findViewById(R.id.member_group);

        input = view.findViewById((R.id.btn_add));
        Delete = view.findViewById(R.id.btn_delete);


        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        member_manager_Request(3);
        super.onActivityCreated(savedInstanceState);
        //등록 버튼
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = EditName.getText().toString();
                Phone = Editphone.getText().toString();
                Dept = EditCourse.getText().toString();
                Team = EditGroup.getText().toString();


                member_manager_Request(1);
                EditName.setText("");
                Editphone.setText("");
                EditCourse.setText("");
                EditGroup.setText("");

//                m_ma.clear();
                member_manager_Request(3);
            }


        });

        Delete.setOnClickListener((new View.OnClickListener() { // 삭제 버튼
            public void onClick(View view) {
//                m_ma.clearSelectedItem();
                member_manager_Request(2);

            }
        }));

    }

    public void additem(String Num, String Name, String phone, String course, String group) {
        ManagerMemberData item = new ManagerMemberData();
        item.setNumber(Num);
        item.setName(Name);
        item.setPhone(phone);
        item.setCourse(course);
        item.setGroup(group);

        m_memberlist.add(item);
    }


    public void member_manager_Request(final int menu) {


        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("리스폰스444 : " + response);

                switch (menu) {
                    case 1:
                        addrespon(response);
                        break;
                    case 2:
                        deleterespon(response);
                        break;
                    case 3:
                        listrespon(response);
                        break;
                    case 4:
                        modifyrespon(response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("에러333 : " + error);
            }
        }
        ) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                switch (menu) {
                    case 1://등록
                        params.put("name", Name);
                        params.put("phone", Phone);
                        params.put("dept", Dept);
                        params.put("team", Team);
                        params.put("type", "memAdd");
                        break;

                    case 2://삭제
//                        params.put("name", m_ma.Name);
//                        params.put("phoe", m_ma.Phone);
//                        params.put("detp", m_ma.Detp);
//                        params.put("team", m_ma.Team);
                        params.put("type", "memDelete");
                        break;

                    case 3://갱신
                        params.put("type", "memShow");
                        break;
                    case 4://데이터 수정
//                        params.put("b_name", m_ma.b_name);
//                        params.put("b_phone", m_ma.b_phone);
//                        params.put("name", m_ma.a_name);
//                        params.put("phone", m_ma.a_phone);
//                        params.put("detp", m_ma.a_detp);
//                        params.put("team", m_ma.a_team);
                        params.put("type", "memModify");
                }

                return params;
            }
        };


        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void addrespon(String response) {//등록 리스폰
        switch (response) {
            case "memAleadyExist"://해당 ID가 이미 존재 시
                Toast.makeText(getActivity(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                break;
            case "memAddSuccess"://정상적으로 입력되었을시
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

    public void deleterespon(String response) {//삭제 리스폰
        switch (response) {
            case "memDeleted"://삭제 성공 시
                Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "memNotExist"://삭제할 내용이 없을 시
                Toast.makeText(getActivity(), "삭제 할 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "error"://오류
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "default Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void listrespon(String response) {//조회 리스폰
        try {
            JSONArray jarray = new JSONArray(response);
            int size = jarray.length();
            for (int i = 0; i < size; i++) {
                JSONObject row = jarray.getJSONObject(i);
                String jname = row.getString("name");
                String jphone = row.getString("phone");
                String jdetp = row.getString("detp");
                String jteam = row.getString("team");
                System.out.println(jname);
                System.out.println(jphone);
                System.out.println(jdetp);
                System.out.println(jteam);

                System.out.println(jname + jphone + jdetp + jteam);
                additem(Integer.toString(i + 1), jname, jphone, jdetp, jteam);
            }
            m_mrv = getActivity().findViewById(R.id.recyclerview_manager_member);
//            m_ma = new ManagerMemberAdapter(m_memberlist);
            m_mrv.setAdapter(m_ma);
            m_mrv.setLayoutManager(new LinearLayoutManager(getActivity()));
            m_ma.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void modifyrespon(String response) {//정보 확인 리스폰
        switch (response) {
            case "memModifySuccess"://수정 성공 시
                Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "memNotExist"://수정할 내용이 없을 시
                Toast.makeText(getActivity(), "삭제 할 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "error"://오류
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "default Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}