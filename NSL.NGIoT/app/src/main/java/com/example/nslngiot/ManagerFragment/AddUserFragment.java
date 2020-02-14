package com.example.nslngiot.ManagerFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.nslngiot.Adapter.ManagerAddUserAdapter;
import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddUserFragment extends Fragment {

    public RecyclerView m_urv = null;
    public ManagerAddUserAdapter m_ua = null;
    public int Count = 0;
    ArrayList<ManagerAddUserData> m_userlist = new ArrayList<ManagerAddUserData>();

    private EditText etName;
    private EditText etId;
    private String url = "http://210.125.212.191:8888/IoT/User.jsp";
    public String ID;
    public String Name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_adduser, container, false);


        m_urv = view.findViewById(R.id.recyclerview_manager_adduser);
        m_ua = new ManagerAddUserAdapter(m_userlist);
        m_urv.setAdapter(m_ua);
        m_urv.setLayoutManager(new LinearLayoutManager(getActivity()));


        Button input = (Button) view.findViewById((R.id.btn_add));
        Button Delete = (Button) view.findViewById(R.id.btn_delete);

        // 등록 버튼
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataInput();
            }
        });

        // 삭제 버튼
        Delete.setOnClickListener((new View.OnClickListener() {
            public void onClick(View view) {
                Count = m_ua.clearSelectedItem();
            }
        }));
        return view;
    }

    public void additem(String Num, String Name, String ID) {
        ManagerAddUserData item = new ManagerAddUserData();

        item.setNumber(Num);
        item.setName(Name);
        item.setID(ID);
        m_userlist.add(item);
    }

    public void DataInput() {

        etId = getView().findViewById(R.id.edit_manager_ID);
        etName = getView().findViewById(R.id.edit_manager_name);

        ID = etId.getText().toString();
        Name = etName.getText().toString();

        Count++;// Json 데이터 길이로 넘버링
        additem(Integer.toString(Count), String.valueOf(etName.getText()), String.valueOf(etId.getText()));
        m_ua.notifyDataSetChanged();
        add_manager_Request();
        etId.setText(" ");
        etName.setText(" ");
    }


    public void add_manager_Request() {


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("리스폰스444 : " + response);
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

                params.put("name", Name);
                params.put("id", ID);
                params.put("type", "addUser_Add");


                return params;
            }
        };


        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}