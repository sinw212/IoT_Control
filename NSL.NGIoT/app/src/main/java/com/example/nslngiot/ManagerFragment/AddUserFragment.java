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

    public int Count =0;
    ArrayList<UserData> m_userlist= new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_adduser,container,false);



        m_urv = view.findViewById(R.id.list_manger_adduser);
        m_ua = new UserAdapter(m_userlist);
        m_urv.setAdapter(m_ua);
        m_urv.setLayoutManager(new LinearLayoutManager(getActivity()));
        Button input = (Button) view.findViewById((R.id.btn_add));
        Button Delete = (Button) view.findViewById(R.id.btn_delete);

        input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DataInput();
            }


        });//등록 버튼

        Delete.setOnClickListener((new View.OnClickListener() { // 삭제 버튼
            public void onClick(View view) {
                Count = m_ua.clearSelectedItem();

            }
        }));


        return  view;
    }
    public void additem(String Num, String Name, String ID){
        UserData item = new UserData();

        item.setNumber(Num);
        item.setName(Name);
        item.setID(ID);

        m_userlist.add(item);
    }
    public void DataInput() {
        Count++;// Json 데이터 길이로 넘버링
        EditText EditID = getActivity().findViewById(R.id.edit_manaager_ID);
        EditText EditName = getActivity().findViewById(R.id.edit_manaager_name);
        additem(Integer.toString(Count), String.valueOf(EditName.getText()), String.valueOf(EditID.getText()));
        m_ua.notifyDataSetChanged();
        EditID.setText(" ");
        EditName.setText(" ");
    }
}
