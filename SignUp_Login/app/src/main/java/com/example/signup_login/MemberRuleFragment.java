package com.example.signup_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class MemberRuleFragment extends Fragment {

    EditText editText = (EditText) getView().findViewById(R.id.ruleEditText);
    //editText.setHorizontallyScrolling(false);//여러줄로 쓰기 가능하게 하려고
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();//manager가 intent로 보낸 규칙을 받아온다.
        String rule = intent.getStringExtra("rule");
        editText.setText(rule);

        return inflater.inflate(R.layout.fragment_member_rule,container,false);//activity_mypage_fragment와 연결..
    }



}