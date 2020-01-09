package com.example.signup_login;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MypageFragment extends Fragment {//MemberMypageFragment임,,이름 잘못 씀..

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_member_mypage,container,false);//activity_mypage_fragment와 연결..

    }
}
