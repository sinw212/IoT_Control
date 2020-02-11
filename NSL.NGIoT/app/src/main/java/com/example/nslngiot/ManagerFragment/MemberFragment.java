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

import com.example.nslngiot.Adapter.ManagerMemberAdapter;
import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class MemberFragment extends Fragment {

    public RecyclerView m_mrv = null;
    public ManagerMemberAdapter m_ma = null;
    ArrayList<ManagerMemberData> m_memberlist = new ArrayList<ManagerMemberData>();
    public int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_member,container,false);

        m_mrv = view.findViewById(R.id.recyclerview_manager_member);
        m_ma = new ManagerMemberAdapter(m_memberlist);
        m_mrv.setAdapter(m_ma);
        m_mrv.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button input = (Button)view.findViewById((R.id.btn_add));
        input.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DataInput();
            }
        });

        return  view;
    }

    public void additem(String Num, String Name, String phone, String course, String group){
        ManagerMemberData item = new ManagerMemberData();

        item.setNumber(Num);
        item.setName(Name);
        item.setPhone(phone);
        item.setCourse(course);
        item.setGroup(group);

        m_memberlist.add(item);
    }

    public void DataInput(){

        count++;
        EditText EditName = getActivity().findViewById(R.id.member_name);
        EditText EditPhone = getActivity().findViewById(R.id.member_phone);
        EditText Editcourse = getActivity().findViewById(R.id.member_course);
        EditText EditGroup = getActivity().findViewById(R.id.member_group);

        additem(Integer.toString(count),String.valueOf(EditName.getText()), String.valueOf(EditPhone.getText()),String.valueOf(Editcourse.getText()),String.valueOf(EditGroup.getText()));

        m_ma.notifyDataSetChanged();
        EditName.setText(" ");
        EditPhone.setText(" ");
        Editcourse.setText(" ");
        EditGroup.setText(" ");
    }
}