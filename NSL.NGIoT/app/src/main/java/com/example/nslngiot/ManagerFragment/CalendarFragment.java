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
    TextView tv_date;
    private String calendar_date="";

    Calendar c;
    int nYear,nMon,nDay;
    DatePickerDialog.OnDateSetListener mDateSetListener;


    private RecyclerView recyclerView = null;
    private LinearLayoutManager layoutManager = null;
    private ManagerCalendarAdapter managerAddUserAdapter = null;
    private ArrayList<ManagerCalendarData> arrayList = new ArrayList<>();

    private ImageButton btn_calendar;
    private Button btn_add, btn_delete;
    private EditText manager_title;
    private String url = "http://210.125.212.191:8888/IoT/Schedule.jsp";
    public String Title;
    public String Detail;

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

        manager_title = getView().findViewById(R.id.manager_calendar_title);
        btn_calendar = getView().findViewById(R.id.btn_calendar);
        btn_add = getView().findViewById(R.id.btn_add);
        btn_delete = getView().findViewById(R.id.btn_delete);
        recyclerView  = (RecyclerView)getView().findViewById(R.id.recyclerview_manager_adduser);

        // 등록된 일정 '제목조회' 조회
        manager_Calendar_Title_SelectRequest();

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

                // 등록된 일정 '제목조회' 조회
                manager_Calendar_Title_SelectRequest();

                oDialog.show();
            }
        });

        // 추가 버튼 리스너
        btn_add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManagerCalendarAddActivity.class);
                getActivity().startActivity(intent);

                // 등록된 일정 '제목조회' 조회
                manager_Calendar_Title_SelectRequest();
            }
        });

        // 삭제 버튼 리스너
        btn_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar_date = tv_date.getText().toString();
                // 등록된 일정 삭제
                manager_Calendar_Delete_SaveRequest();
                // 등록된 일정 '제목조회' 조회
                manager_Calendar_Title_SelectRequest();
            }
        });
    }

    // 현재 등록된 일정 제목조회 통신
    private void manager_Calendar_Title_SelectRequest() {

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //json
                        try {
                            layoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setHasFixedSize(true); // 아이템의 뷰를 일정하게하여 퍼포먼스 향상
                            recyclerView.setLayoutManager(layoutManager); // 앞에 선언한 리사이클러뷰를 매니저에 붙힘

                            JSONArray jarray = new JSONArray(response);
                            int size = jarray.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject row = jarray.getJSONObject(i);
                                ManagerCalendarData managerAddUserData = new ManagerCalendarData();
                                managerAddUserData.setTitle(row.getString("title"));
                                managerAddUserData.setDetail(row.getString("detail"));
                                managerAddUserData.setNumber(String.valueOf(i+1));
                                arrayList.add(managerAddUserData);
                            }
                            // 어댑터에 add한 다량의 데이터 할당
                            managerAddUserAdapter = new ManagerCalendarAdapter(getActivity(),arrayList);
                            // 리사이클러뷰에 어답타 연결
                            recyclerView .setAdapter(managerAddUserAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //scheduleNotExist
                        //error
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
                // '일정등록'이라는 신호 정보 push 진행
                params.put("date", String.valueOf(tv_date));
                params.put("type","scheduleList");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    //일정 삭제 등록 통신
    private void manager_Calendar_Delete_SaveRequest(){
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()){
                            case "scheduleDelete": // 일정 삭제 성공했을 때
                                Toast.makeText(getActivity(), "일정 삭제를 등록하였습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error": // 오류
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getActivity(), "default Error", Toast.LENGTH_SHORT).show();
                                break;
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
                // '일정등록'이라는 신호 정보 push 진행
                params.put("date", calendar_date);
                params.put("title", String.valueOf(manager_title));
                params.put("type","scheduleDelete");

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