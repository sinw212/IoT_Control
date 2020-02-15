package com.example.nslngiot.ManagerFragment;

import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.Adapter.ManagerAddUserAdapter;
import com.example.nslngiot.Data.ManagerAddUserData;
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
    private ArrayList<ManagerAddUserData> arrayList = new ArrayList<>();

    private Button input, Delete;
    private EditText etName,etId;
    private String url = "http://210.125.212.191:8888/IoT/User.jsp";
    public String ID;
    public String Name;
    // public int menu;// 1. 등록 2. 삭제 3. 데이터 갱신 4. 데이터 확인


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
        Delete = getView().findViewById(R.id.btn_delete);
        recyclerView  = (RecyclerView)getView().findViewById(R.id.recyclerview_manager_adduser);


        // 조회 시작
        add_manager_Request(3);
        // 등록 버튼
        input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ID = etId.getText().toString().trim();
                Name = etName.getText().toString().trim();

                if("".equals(Name) || "".equals(ID)){
                    Toast.makeText(getActivity(), "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    add_manager_Request(1);
                    arrayList.clear(); // 기존에 담아둔 데이터 삭제
                    etId.setText("");
                    etName.setText("");
                    add_manager_Request(3);
                }
            }
        });

        Delete.setOnClickListener((new View.OnClickListener() { // 삭제 버튼
            public void onClick(View view) {
//                managerAddUserAdapter.clearSelectedItem();
                //add_manager_Request(2);
                add_manager_Request(3);
            }
        }));

    }


    public void add_manager_Request(final int menu) {


        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("리스폰스444 : " + response);
                System.out.println("menu : " + menu);
                switch (menu){
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
                        checkrespon(response);
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
                    case 1: // 등록
                        params.put("name", Name);
                        params.put("id", ID);
                        params.put("type", "addUser_Add");
                        break;
                    case 2: // 삭제
                        params.put("name", "");
                        params.put("id", "");
                        params.put("type", "addUser_Delete");
                        break;
                    case 3: // 조회
                        params.put("type", "addUser_List");
                        break;
                    default: // 회원 이름/학번/이메일 띄우기
                        params.put("type", "user_List");
                        break;
                }
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void addrespon(String response) { // 등록 리스폰
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

    public void deleterespon(String response) { // 삭제 리스폰
        switch (response) {
            case "deleteAllSuccess"://삭제 성공 시
                Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "addUserDataNotExist"://삭제할 내용이 없을 시
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

    private void listrespon(String response) { // 조회 리스폰
        try {

            Log.d("진입","ㅇㅇ");
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setHasFixedSize(true); // 아이템의 뷰를 일정하게하여 퍼포먼스 향상
            recyclerView.setLayoutManager(layoutManager); // 앞에 선언한 리사이클러뷰를 매니저에 붙힘

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

    public void checkrespon(String response) { // 정보 확인 리스폰
        try {
            JSONArray jarray = new JSONArray(response);
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jobject = jarray.getJSONObject(i);
                String jname = jobject.getString("name");
                String jid = jobject.getString("id");
                String jeamil = jobject.getString("email");
//                managerAddUserAdapter.setData(jname, jid, jeamil);
                managerAddUserAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}