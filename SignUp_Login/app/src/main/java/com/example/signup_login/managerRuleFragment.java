package com.example.signup_login;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class managerRuleFragment extends Fragment {

    private Context context;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_manager_rule,container,false);//activity_mypage_fragment와 연결..

    }

    public void onCLick(View v){

        EditText editText = (EditText) getView().findViewById(R.id.editText);

        switch(v.getId()){
            case R.id.saveButton:{

                String memo = editText.getText().toString();
                editText.setText(memo);
                editText.setEnabled(false);
                Toast.makeText(context,"등록 완료", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.modifyButton:{

                editText.setEnabled(true);
                Toast.makeText(context,"수정 가능", Toast.LENGTH_LONG).show();
                break;

            }


        }

    }


}
