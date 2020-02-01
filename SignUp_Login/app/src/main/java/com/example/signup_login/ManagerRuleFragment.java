package com.example.signup_login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class ManagerRuleFragment extends Fragment {

    View view;
    Button backButton, modifyButton, saveButton;
    EditText ruleEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        backButton = (Button) view.findViewById(R.id.backButton);
        modifyButton = (Button) view.findViewById(R.id.modifyButton1);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        ruleEditText = (EditText) view.findViewById(R.id.editText);

        return inflater.inflate(R.layout.fragment_manager_rule,container,false);//activity_mypage_fragment와 연결..
    }

}
