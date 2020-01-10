package com.example.signup_login;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MypageFragment extends Fragment {//MemberMypageFragment임,,이름 잘못 씀..

    EditText nameEditText = (EditText) getView().findViewById(R.id.nameEditText);
    EditText studentnumEditText = (EditText) getView().findViewById(R.id.studentnumberEditText);
    EditText pwEditText = (EditText) getView().findViewById(R.id.pwEditText);
    EditText pwcEditText = (EditText) getView().findViewById(R.id.pwmodifyEditText);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_member_mypage,container,false);//activity_mypage_fragment와 연결..

    }

    public void onClick(View v){

        switch(v.getId()){
            case R.id.modifyButton: {//수정버튼 누름
                if (pwcEditText.toString() == pwEditText.toString()) {//비밀번호와 비밀번호 확인이 같을 경우

                }
                break;
            }
            case R.id.backButton:{//뒤로가기 버튼

                break;
            }

            case R.id.logoutButton:{//로그아웃 버튼

                Intent intent = new Intent(getActivity().getApplicationContext(), SelectModeActivity.class);
                startActivity(intent);
                //SelectModeActivity로 화면 전환.

                break;
            }

        }

    }
}
