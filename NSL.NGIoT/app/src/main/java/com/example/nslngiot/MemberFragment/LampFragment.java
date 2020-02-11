package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nslngiot.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LampFragment extends Fragment {

    ImageButton btn_refresh;
    ImageButton btn_lamp_on;
    ImageButton btn_lamp_off;

    // 테스트로 해보려고 현재 시간 받아오기
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("mm");
    String formatDate = sdfNow.format(date);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_lamp,container,false);

        btn_refresh = view.findViewById(R.id.btn_refresh);
        btn_lamp_on = view.findViewById(R.id.btn_lamp_on);
        btn_lamp_off = view.findViewById(R.id.btn_lamp_off);

        btn_lamp_off.setEnabled(false);

        // 새로고침 리스너
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 테스트 용으로 한 것. 아두이노 통신 수정 해야함

                // 랩실 전등 상태 확인
                if(Integer.valueOf(String.valueOf(sdfNow)) >= 30 && Integer.valueOf(String.valueOf(sdfNow)) < 60) {
                    //불 켜져 있을 때
                    btn_lamp_on.setEnabled(true);
                    btn_lamp_off.setEnabled(false);
                }
                else {
                    //불 꺼져 있을 때
                    btn_lamp_on.setEnabled(false);
                    btn_lamp_off.setEnabled(true);
                }
            }
        });

        // 불 키는 버튼 리스너
        btn_lamp_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아두이노 통신 부분
            }
        });

        // 불 끄는 버튼 리스너
        btn_lamp_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아두이노 통신 부분
            }
        });

        return view;
    }
}