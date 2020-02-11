package com.example.nslngiot.ManagerFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.XSSFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MeetLogFragment extends Fragment {
    View view;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");
    ArrayList<String> items;
    ListView listview;
    DatePickerDialog.OnDateSetListener myDatePicker;
    TextView tv_date;

    private String manager_meetlog_value="";
    private EditText manager_meetlog = getView().findViewById(R.id.et_manager_meetlog);
    private Button btn_manager_meetlog_add = getView().findViewById(R.id.btn_manager_meetlog_add);


    Calendar c;
    int nYear,nMon,nDay;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manager_meetlog,container,false);

        // 오늘 날짜 표현
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_date.setText(getTime());

        ImageButton button_calendar = (ImageButton) view.findViewById(R.id.btn_calendar);

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

        button_calendar.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog oDialog = new DatePickerDialog(getContext(),
                        mDateSetListener, nYear, nMon, nDay);

                oDialog.show();
            }
        });

        return view;
    }

    public String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 등록된 회의록 확인 진행
        manager_Meetlog_SelectRequest();

        btn_manager_meetlog_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager_meetlog_value = manager_meetlog.getText().toString();
                //////////////////////////////방어 코드////////////////////////////
                //XSS 특수문자 공백처리 및 방어
                manager_meetlog_value = XSSFilter.xssFilter(manager_meetlog_value);
                //////////////////////////////////////////////////////////////////

                manager_Meetlog_SaveRequest();
                manager_Meetlog_SelectRequest();

            }
        });
    }
    //회의록 등록 통신
    private void manager_Meetlog_SaveRequest(){
        // 날짜랑 회의록 내용이랑 같이 서버로.
        // Meetlog 맞는지 다시 확인
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Meetlog.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()){
                            // 다시 확인
                            case "meetlogAdded":
                                Toast.makeText(getActivity(), "회의록을 등록하였습니다.", Toast.LENGTH_LONG).show();
                                break;
                            case "error":
                                Toast.makeText(getActivity(), "서버오류입니다.", Toast.LENGTH_LONG).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
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
                // '회의록등록'이라는 신호 정보 push 진행
                params.put("text",manager_meetlog_value);
                params.put("type","meetlogUpload");
                // 다시 확인

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    // 현재 등록된 회의록 조회 통신
    private void manager_Meetlog_SelectRequest(){
        // 다시 확인
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Meetlog.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] resPonse_split = response.split(" ");
                        if("meetlogExist".equals(resPonse_split[1])){
                            manager_meetlog.setText(XSSFilter.xssFilter(resPonse_split[0]));
                        }else if("meetlogNotExist".equals(resPonse_split[1])){
                            manager_meetlog.setText("현재 회의록이 등록되어있지 않습니다.");
                        } else if("error".equals(resPonse_split[1])){
                            manager_meetlog.setText("시스템 오류입니다.");
                            Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
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
                // '회의록등록'이라는 신호 정보 push 진행
                params.put("type","meetlogShow");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }
}