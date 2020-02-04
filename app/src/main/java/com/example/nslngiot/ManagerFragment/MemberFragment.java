package com.example.nslngiot.ManagerFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    ArrayList<MemberData> m_memberlist = new ArrayList<MemberData>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_member,container,false);

        m_mrv = view.findViewById(R.id.manager_memberlist);
        m_ma = new MemberAdapter(m_memberlist);
        m_mrv.setAdapter(m_ma);
        m_mrv.setLayoutManager(new LinearLayoutManager(getActivity()));

        additem("1", "이민규", "010-4783-2038", "3학년", "공주대");
        m_ma.notifyDataSetChanged();

        return  view;
    }

    public void additem(String Num, String Name, String phone, String course, String group){
        MemberData item = new MemberData();

        item.setNumber(Num);
        item.setName(Name);
        item.setPhone(phone);
        item.setCourse(course);
        item.setGroup(group);

        m_memberlist.add(item);
    }
}