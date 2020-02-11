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

public class RuleFragment extends Fragment {

//    EditText et_member_rule = getView().findViewById(R.id.et_member_rule);
    //editText.setHorizontallyScrolling(false);//여러줄로 쓰기 가능하게 하려고

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Intent intent = getActivity().getIntent();//manager가 intent로 보낸 규칙을 받아온다.
//        String rule = intent.getStringExtra("rule");
//        et_member_rule.setText(rule);

        return inflater.inflate(R.layout.fragment_member_rule,container,false);
    }
}
