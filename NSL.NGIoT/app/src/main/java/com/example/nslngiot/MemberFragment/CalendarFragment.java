package com.example.nslngiot.MemberFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.example.nslngiot.Adapter.MemberCalendarAdapter;
import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private String Date;
    private ImageButton btn_calendar;
    private TextView tv_date;
    private RecyclerView recyclerView = null;
    private LinearLayoutManager layoutManager = null;
    private MemberCalendarAdapter memberCalendarAdapter = null;
    private ArrayList<ManagerCalendarData> arrayList;
    private ManagerCalendarData managerCalendarData;

    private String url = "http://210.125.212.191:8888/IoT/Schedule.jsp";

//    TextView TextTitle, TextDetail;
    Calendar c;
    int nYear,nMon,nDay;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_member_calendar,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Date = "";
        tv_date = getView().findViewById(R.id.tv_date);
        btn_calendar = getView().findViewById(R.id.btn_calendar);
        recyclerView  = getView().findViewById(R.id.recyclerview_member_calendar);

        // 오늘 날짜 표현
        tv_date.setText(getTime());

        // 등록된 일정 조회
        member_select_Request();

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

        btn_calendar.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog oDialog = new DatePickerDialog(getContext(),
                        mDateSetListener, nYear, nMon, nDay);

                // 등록된 일정 조회
                member_select_Request();

                oDialog.show();
            }
        });
    }

    // 일정 '제목조회' 조회
    private void member_select_Request() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Date = tv_date.getText().toString().trim();

                        try {
                            layoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setHasFixedSize(true); // 아이템의 뷰를 일정하게하여 퍼포먼스 향상
                            recyclerView.setLayoutManager(layoutManager); // 앞에 선언한 리사이클러뷰를 매니저에 붙힘
                            // 기존 데이터와 겹치지 않기 위해생성자를 매번 새롭게 생성
                            arrayList = new ArrayList<ManagerCalendarData>();

                            JSONArray jarray = new JSONArray(response);
                            int size = jarray.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject row = jarray.getJSONObject(i);
                                managerCalendarData= new ManagerCalendarData();
                                managerCalendarData.setDate(row.getString("date"));
                                managerCalendarData.setTitle(row.getString("title"));
                                managerCalendarData.setDetail(row.getString("detail"));
                                managerCalendarData.setNumber(String.valueOf(i+1));
                                arrayList.add(managerCalendarData);
                            }
                            // 어댑터에 add한 다량의 데이터 할당
                            memberCalendarAdapter = new MemberCalendarAdapter(getActivity(),arrayList);
                            // 리사이클러뷰에 어답타 연결
                            recyclerView .setAdapter(memberCalendarAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("date", Date);
                params.put("type", "scheduleList");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}