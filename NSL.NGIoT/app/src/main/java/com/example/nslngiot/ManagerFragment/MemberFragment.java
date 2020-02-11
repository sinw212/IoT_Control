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

import com.example.nslngiot.Adapter.MemberAdapter;
import com.example.nslngiot.Data.MemberData;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class MemberFragment extends Fragment {

    public RecyclerView m_mrv = null;
    public MemberAdapter m_ma = null;
    public int Count = 0;
    ArrayList<MemberData> m_memberlist = new ArrayList<MemberData>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_member, container, false);

        m_mrv = view.findViewById(R.id.manager_memberlist);
        m_ma = new MemberAdapter(m_memberlist);
        m_mrv.setAdapter(m_ma);
        m_mrv.setLayoutManager(new LinearLayoutManager(getActivity()));
        Button input = (Button) view.findViewById((R.id.btn_add_member));
        Button Delete = (Button) view.findViewById(R.id.btn_delete_member);

        input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DataInput();
            }


        });//등록 버튼

        Delete.setOnClickListener((new View.OnClickListener() { // 삭제 버튼
            public void onClick(View view) {
                Count = m_ma.clearSelectedItem();

            }
        }));

        return view;
    }

    public void additem(String Num, String Name, String phone, String course, String group) {
        MemberData item = new MemberData();

        item.setNumber(Num);
        item.setName(Name);
        item.setPhone(phone);
        item.setCourse(course);
        item.setGroup(group);

        m_memberlist.add(item);
    }

    public void DataInput() {
        Count++;// Json 데이터 길이로 넘버링
        EditText EditName = getActivity().findViewById(R.id.edit_manaager_name);
        EditText Editphone = getActivity().findViewById(R.id.edit_member_phone);
        EditText EditCourse = getActivity().findViewById(R.id.edit_member_course);
        EditText EditGroup = getActivity().findViewById(R.id.edit_member_group);
        additem(Integer.toString(Count), String.valueOf(EditName.getText()), String.valueOf(Editphone.getText()),String.valueOf(EditCourse.getText()),String.valueOf(EditGroup.getText()));
        m_ma.notifyDataSetChanged();

        EditName.setText(" ");
        Editphone.setText(" ");
        EditCourse.setText(" ");
        EditGroup.setText(" ");

    }
}