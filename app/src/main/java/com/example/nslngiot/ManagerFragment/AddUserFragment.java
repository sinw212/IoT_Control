package com.example.nslngiot.ManagerFragment;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Adapter.UserAdapter;
import com.example.nslngiot.Data.UserData;
import com.example.nslngiot.MainActivity;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class AddUserFragment extends Fragment {
    public TextView tv_title;
    public TextView tv_input;
    public Button btn_back;
    public Button btn_modify;
    public Button btn_add;
    public RecyclerView m_urv = null;
    public UserAdapter m_ua = null;
    public EditText IDtext;

    ArrayList<UserData> m_userlist= new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_adduser,container,false);

        IDtext = (EditText)getActivity().findViewById(R.id.input);

        m_urv = view.findViewById(R.id.list_manger_adduser);
        m_ua = new UserAdapter(m_userlist);
        m_urv.setAdapter(m_ua);
        m_urv.setLayoutManager(new LinearLayoutManager(getActivity()));

        additem("1", "제발", "되라12");
        additem("2", "오예","된다");
        m_ua.notifyDataSetChanged();

        return  view;
    }
    public void additem(String Num, String Name, String ID){
        UserData item = new UserData();

        item.setNumber(Num);
        item.setName(Name);
        item.setID(ID);

        m_userlist.add(item);
    }
    public void DataInput(){

    }
}
