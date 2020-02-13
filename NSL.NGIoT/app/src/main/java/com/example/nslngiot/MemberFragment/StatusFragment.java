package com.example.nslngiot.MemberFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nslngiot.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusFragment extends Fragment {

    ImageButton btn_refresh;

    ImageView personE = null;
    ImageView personNE = null;
    ImageView waterE = null;
    ImageView waterNE = null;
    ImageView coffeeE = null;
    ImageView coffeeNE = null;
    ImageView a4E = null;
    ImageView a4NE = null;

    TextView person_state;
    TextView water_state;
    TextView coffee_state;
    TextView a4_state;

    // 테스트로 해보려고 현재 시간 받아오기
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("mm");
    String formatDate = sdfNow.format(date);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_status,container,false);

        btn_refresh = view.findViewById(R.id.btn_refresh);

        personE = view.findViewById(R.id.btn_personE);
        personNE = view.findViewById(R.id.btn_personNE);
        waterE = view.findViewById(R.id.btn_waterE);
        waterNE = view.findViewById(R.id.btn_waterNE);
        coffeeE = view.findViewById(R.id.btn_coffeeE);
        coffeeNE = view.findViewById(R.id.btn_coffeeNE);
        a4E = view.findViewById(R.id.btn_a4E);
        a4NE = view.findViewById(R.id.btn_a4NE);

        personE.setVisibility(View.VISIBLE);
        personNE.setVisibility(View.INVISIBLE);
        waterE.setVisibility(View.VISIBLE);
        waterNE.setVisibility(View.INVISIBLE);
        coffeeE.setVisibility(View.VISIBLE);
        coffeeNE.setVisibility(View.INVISIBLE);
        a4E.setVisibility(View.VISIBLE);
        a4NE.setVisibility(View.INVISIBLE);

        person_state = view.findViewById(R.id.person_state);
        water_state = view.findViewById(R.id.water_state);
        coffee_state = view.findViewById(R.id.coffee_state);
        a4_state = view.findViewById(R.id.a4_state);

        // 새로고침 리스너
        btn_refresh.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              // 테스트 용으로 한 것. 아두이노 통신 수정 해야함

              // 재실여부 상태 확인
              if(Integer.valueOf(String.valueOf(sdfNow)) >= 30 && Integer.valueOf(String.valueOf(sdfNow)) < 60) {
                  // 랩실에 사람 있을 때
                  person_state.setText("사람 있음");
                  personE.setVisibility(View.VISIBLE);
                  personNE.setVisibility(View.INVISIBLE);
              }
              else {
                  // 랩실에 사람 없을
                  person_state.setText("사람 없음");
                  personE.setVisibility(View.INVISIBLE);
                  personNE.setVisibility(View.VISIBLE);
              }

              // 물 잔여량 상태 확인
              if(Integer.valueOf(String.valueOf(sdfNow)) >= 30 && Integer.valueOf(String.valueOf(sdfNow)) < 60) {
                  // 물 충분히 있을 때
                  water_state.setText("충분함");
                  waterE.setVisibility(View.VISIBLE);
                  waterNE.setVisibility(View.INVISIBLE);
              }
              else {
                  // 물 부족할 때
                  water_state.setText("부족함");
                  waterE.setVisibility(View.INVISIBLE);
                  waterNE.setVisibility(View.VISIBLE);
              }

              // 커피 잔여량 상태 확인
              if(Integer.valueOf(String.valueOf(sdfNow)) >= 30 && Integer.valueOf(String.valueOf(sdfNow)) < 60) {
                  // 커피 충분히 있을 때
                  coffee_state.setText("충분함");
                  coffeeE.setVisibility(View.VISIBLE);
                  coffeeNE.setVisibility(View.INVISIBLE);
              }
              else {
                  // 커피 부족할 때
                  coffee_state.setText("부족함");
                  coffeeE.setVisibility(View.INVISIBLE);
                  coffeeNE.setVisibility(View.VISIBLE);
              }

              // A4 잔여량 상태 확인
              if(Integer.valueOf(String.valueOf(sdfNow)) >= 30 && Integer.valueOf(String.valueOf(sdfNow)) < 60) {
                  // A4 충분히 있을 때
                  a4_state.setText("충분함");
                  a4E.setVisibility(View.VISIBLE);
                  a4NE.setVisibility(View.INVISIBLE);
              }
              else {
                  // A4 부족할 때
                  a4_state.setText("부족함");
                  a4E.setVisibility(View.INVISIBLE);
                  a4NE.setVisibility(View.VISIBLE);
              }
          }
        });

        return view;
    }
}