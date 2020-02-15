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
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUserFragment extends Fragment {

    public RecyclerView m_urv = null;
    public ManagerAddUserAdapter m_ua = null;

    ArrayList<ManagerAddUserData> m_userlist = new ArrayList<>();


    private Button input, Delete;

    private EditText etName;
    private EditText etId;
    private String url = "http://210.125.212.191:8888/IoT/User.jsp";
    public String ID;
    public String Name;//
    // public int menu;// 1. 등록 2. 삭제 3. 데이터 갱신 4. 데이터 확인


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_adduser, container, false);

        etId = view.findViewById(R.id.edit_manager_ID);
        etName = view.findViewById(R.id.edit_manager_name);
        input = view.findViewById((R.id.btn_add));
        Delete = view.findViewById(R.id.btn_delete);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        add_manager_Request(3);

        super.onActivityCreated(savedInstanceState);
        //등록 버튼
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ID = etId.getText().toString().trim();
                Name = etName.getText().toString().trim();

                add_manager_Request(1);
                etId.setText("");
                etName.setText("");
                m_ua.clear();
                add_manager_Request(3);
            }
        });

        Delete.setOnClickListener((new View.OnClickListener() { // 삭제 버튼
            public void onClick(View view) {

                m_ua.clearSelectedItem();
                add_manager_Request(2);


            }
        }));

    }

    public void additem(String Num, String Name, String ID) {//리사이클러뷰에 리스트 추가
        ManagerAddUserData item = new ManagerAddUserData();

        item.setNumber(Num);
        item.setName(Name);
        item.setID(ID);

        m_userlist.add(item);
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
                    case 1://등록
                        params.put("name", Name);
                        params.put("id", ID);
                        params.put("type", "addUser_Add");

                        break;
                    case 2://삭제
                        params.put("name", m_ua.Name);
                        params.put("id", m_ua.Id);
                        params.put("type", "addUser_Delete");

                        break;
                    case 3://갱신
                        params.put("type", "addUser_List");
                        break;
                    case 4://데이터 확인
                        params.put("type", "user_List");
                }

                return params;
            }
        };


        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void addrespon(String response) {//등록 리스폰
        switch (response) {
            case "IDAleadyExist"://해당 ID가 이미 존재 시
                Toast.makeText(getActivity(), "이미 존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
                break;
            case "addUser_addSuccess"://정상적으로 입력되었을시
                Toast.makeText(getActivity(), "입력되었습니다.", Toast.LENGTH_LONG).show();
                break;
            case "error"://오류
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getActivity(), "default Error", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void deleterespon(String response) {//삭제 리스폰
        switch (response) {
            case "deleteAllSuccess"://삭제 성공 시
                Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
                break;
            case "addUserDataNotExist"://삭제할 내용이 없을 시
                Toast.makeText(getActivity(), "삭제 할 내용이 없습니다.", Toast.LENGTH_LONG).show();
                break;
            case "error"://오류
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getActivity(), "default Error", Toast.LENGTH_LONG).show();
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
                String jid = row.getString("id");
                additem(Integer.toString(i + 1), jname, jid);

            }
            m_urv = getActivity().findViewById(R.id.recyclerview_manager_adduser);
            m_ua = new ManagerAddUserAdapter(m_userlist);
            m_urv.setAdapter(m_ua);
            m_urv.setLayoutManager(new LinearLayoutManager(getActivity()));
            m_ua.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void checkrespon(String response) {//정보 확인 리스폰
        try {
            JSONArray jarray = new JSONArray(response);
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jobject = jarray.getJSONObject(i);
                String jname = jobject.getString("name");
                String jid = jobject.getString("id");
                String jeamil = jobject.getString("email");
                m_ua.setData(jname, jid, jeamil);
                m_ua.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}