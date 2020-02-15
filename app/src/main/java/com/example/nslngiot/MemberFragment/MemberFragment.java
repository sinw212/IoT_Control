package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.Data.ManagerMemberData;
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

    private String url = "http://210.125.212.191:8888/IoT/MemberState.jsp";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_member, container, false);
//        m_mrv = getActivity().findViewById(R.id.recyclerview_member_member);
//        //m_ma = new MemberAdapter(m_memberlist);
//        m_mrv.setAdapter(m_ma);
//        m_mrv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void add_manager_Request(final int menu) {


        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("리스폰스444 : " + response);
                System.out.println("menu : " + menu);

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
                params.put("type", "addUser_List");
                return params;
            }
        };
    }

    public void listrespon(String response) {//조회 리스폰
        try {

//            m_mrv = getActivity().findViewById(R.id.recyclerview_member_member);
//            //m_ma = new MemberAdapter(m_mlist);
//            m_mrv.setAdapter(m_ma);
//            m_mrv.setLayoutManager(new LinearLayoutManager(getActivity()));

            JSONArray jarray = new JSONArray(response);
            int size = jarray.length();
            for (int i = 0; i < size; i++) {
                JSONObject row = jarray.getJSONObject(i);
                String jname = row.getString("name");
                String jid = row.getString("id");

                //additem(Integer.toString(i + 1), jname, jid);

                //m_ua.notifyDataSetChanged();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}