package com.example.signup_login;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class managerMypageFragment extends Fragment {//fragment_manager_mypage와 연결

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manager_mypage,container,false);//activity_mypage_fragment와 연결..

    }
}
