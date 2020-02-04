package com.example.nslngiot.MemberFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.XSSFilter;

public class RuleFragment extends Fragment {

    private String member_rule_value="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_member_rule,container,false);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //////////////////////////////방어 코드////////////////////////////
        //XSS 특수문자 공백처리 및 방어
        member_rule_value = XSSFilter.xssFilter(member_rule_value);
        //////////////////////////////////////////////////////////////////
    }
}
