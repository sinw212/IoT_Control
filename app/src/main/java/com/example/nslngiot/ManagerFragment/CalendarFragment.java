package com.example.nslngiot.ManagerFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Adapter.ManagerCalendarAdapter;
import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.ManagerCalendarAddActivity;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.XSSFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarFragment extends Fragment {
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");
    TextView tv_date;

    Calendar c;
    int nYear,nMon,nDay;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    public RecyclerView m_crv = null;
    public ManagerCalendarAdapter m_ca = null;
    public int Count = 0;
    ArrayList<ManagerCalendarData> m_calendarlist = new ArrayList<ManagerCalendarData>();

    private EditText manager_title, manager_detail;
    private ImageButton btn_calendar;
    private Button btn_add, btn_delete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manager_calendar, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 오늘 날짜 표현
        tv_date = getView().findViewById(R.id.tv_date);
        tv_date.setText(getTime());

        btn_calendar = getView().findViewById(R.id.btn_calendar);
        btn_add = getView().findViewById(R.id.btn_add);
        btn_delete = getView().findViewById(R.id.btn_delete);

        m_crv = getView().findViewById(R.id.recyclerview_manager_calendar);
        m_ca = new ManagerCalendarAdapter(m_calendarlist);
        m_crv.setAdapter(m_ca);
        m_crv.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 등록된 일정 조회
//        manager_Calendar_SelectRequest();

        // Calendar
        //DatePicker Listener
        mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        String strDate = String.valueOf(year) + "년 ";

                        if (monthOfYear + 1 > 0 && monthOfYear + 1 < 10)
                            strDate += "0" + String.valueOf(monthOfYear + 1) + "월 ";
                        else
                            strDate += String.valueOf(monthOfYear + 1) + "월 ";

                        if (dayOfMonth > 0 && dayOfMonth < 10)
                            strDate += "0" + String.valueOf(dayOfMonth) + "일";
                        else
                            strDate += String.valueOf(dayOfMonth) + "일";

                        tv_date.setText(strDate);
                    }
                };

        c = Calendar.getInstance();
        nYear = c.get(Calendar.YEAR);
        nMon = c.get(Calendar.MONTH);
        nDay = c.get(Calendar.DAY_OF_MONTH);

        // 달력 아이콘 리스너
        btn_calendar.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog oDialog = new DatePickerDialog(getContext(),
                        mDateSetListener, nYear, nMon, nDay);

                // 등록된 일정 조회
//                manager_Calendar_SelectRequest();

                oDialog.show();
            }
        });

        // 추가 버튼 리스너
        btn_add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManagerCalendarAddActivity.class);
                getActivity().startActivity(intent);

                // 등록된 일정 조회
//                manager_Calendar_SelectRequest();
            }
        });

        // 삭제 버튼 리스너
        btn_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Count = m_ca.clearSelectedItem();
            }
        });
    }

    // 현재 등록된 일정 제목조회 통신
    private void manager_Calendar_Title_SelectRequest() {
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //오늘 날짜에 대한 json값 가져오기 -> 풀어주기


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // '규칙등록'이라는 신호 정보 push 진행
                params.put("date", String.valueOf(tv_date));
                params.put("title", String.valueOf(manager_title));
                params.put("type","scheduleShow");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    public String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}