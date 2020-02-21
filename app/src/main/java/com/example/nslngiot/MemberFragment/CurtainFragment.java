package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nslngiot.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurtainFragment extends Fragment {

    ImageButton btn_refresh;
    ImageButton btn_curtain_up;
    ImageButton btn_curtain_down;

    // 테스트로 해보려고 현재 시간 받아오기
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("mm");
    String formatDate = sdfNow.format(date);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_curtain,container,false);

        btn_refresh = view.findViewById(R.id.btn_refresh);
        btn_curtain_up = view.findViewById(R.id.btn_curtain_up);
        btn_curtain_down = view.findViewById(R.id.btn_curtain_down);

        btn_curtain_down.setEnabled(false);

        // 새로고침 리스너
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 테스트 용으로 한 것. 아두이노 통신 수정 해야함

                // 랩실 버티칼 상태 확인
                if(Integer.valueOf(String.valueOf(sdfNow)) >= 30 && Integer.valueOf(String.valueOf(sdfNow)) < 60) {
                    // 버티칼 올리고 싶을 때
                    btn_curtain_up.setEnabled(true);
                    btn_curtain_down.setEnabled(false);
                }
                else {
                    // 버티칼 내리고 싶을 때
                    btn_curtain_up.setEnabled(false);
                    btn_curtain_down.setEnabled(true);
                }
            }
        });

        // 버티칼 올리기 버튼 리스너
        btn_curtain_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아두이노 통신 부분
            }
        });

        // 버티칼 내리기 버튼 리스너
        btn_curtain_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아두이노 통신 부분
            }
        });

        return view;
    }
}