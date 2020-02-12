package com.example.nslngiot.ManagerFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.Adapter.ManagerAddUserAdapter;
import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUserFragment extends Fragment {

    public TextView TitleText;
    public TextView inputText;
    public Button backButton;
    public Button revisionButton;
    public Button addButton;
    public RecyclerView m_urv = null;
    public ManagerAddUserAdapter m_ua = null;

    public int Count= 0;
    public EditText IDtext;
    ArrayList<ManagerAddUserData> m_userlist = new ArrayList<ManagerAddUserData>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_manager_adduser,container, false);

        IDtext = getActivity().findViewById(R.id.add_input_ID);


        m_urv = view.findViewById(R.id.recyclerview_manager_adduser);
        m_ua = new ManagerAddUserAdapter(m_userlist);
        m_urv.setAdapter(m_ua);
        m_urv.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button input = (Button)view.findViewById((R.id.btn_add));
        Button Delete = (Button)view.findViewById(R.id.btn_delete);

        input.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DataInput();
            }
        });

        Delete.setOnClickListener((new View.OnClickListener(){
            public void onClick(View view){
                m_ua.clearSelectedItem();
            }
        }));

        return  view;
    }

    public void additem(String Num, String Name, String ID){
        ManagerAddUserData item = new ManagerAddUserData();

        item.setNumber(Num);
        item.setName(Name);
        item.setID(ID);

        m_userlist.add(item);
    }

    public void DataInput(){
        Count++;// Json 데이터 길이로 넘버링
        EditText EditID = getActivity().findViewById(R.id.add_input_ID);
        EditText EditName = getActivity().findViewById(R.id.edit_manager_name);
        additem(Integer.toString(Count),  String.valueOf(EditName.getText()), String.valueOf(EditID.getText()));
        m_ua.notifyDataSetChanged();
        EditID.setText(" ");
        EditName.setText(" ");
    }
}