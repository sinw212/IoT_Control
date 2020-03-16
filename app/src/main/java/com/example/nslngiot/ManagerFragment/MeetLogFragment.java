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
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.XSSFilter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MeetLogFragment extends Fragment {

    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");

    private TextView tv_date;
    private ImageButton btn_calendar;
    private String Meetlog;
    private EditText manager_meetlog;
    private Button btn_manager_meetlog_add;

    private Calendar c;
    private int nYear,nMon,nDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manager_meetlog,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Meetlog = "";
        tv_date = getView().findViewById(R.id.tv_date);
        btn_calendar = getView().findViewById(R.id.btn_calendar);
        manager_meetlog = getView().findViewById(R.id.manager_meetlog);
        btn_manager_meetlog_add = getView().findViewById(R.id.btn_manager_meetlog_add);

        // 오늘 날짜 표현
        tv_date.setText(getTime());

        // 등록된 회의록 조회
        manager_Meetlog_SelectRequest();

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

                        // 등록된 회의록 조회
                        manager_Meetlog_SelectRequest();
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
                oDialog.show();
            }
        });

        btn_manager_meetlog_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Meetlog = manager_meetlog.getText().toString().trim();
                //////////////////////////////방어 코드////////////////////////////
                //XSS 특수문자 공백처리 및 방어
                Meetlog = XSSFilter.xssFilter(Meetlog);
                //////////////////////////////////////////////////////////////////
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            manager_Meetlog_SaveRequest();
                            Thread.sleep(100);
                            manager_Meetlog_SelectRequest();
                        } catch (InterruptedException e) {
                           System.err.println("Manager MeetLogFrament InterruptedException error");
                        }
                    }
                }).start();
            }
        });
    }
    //회의록 등록 통신
    private void manager_Meetlog_SaveRequest(){
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Conference.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);
                        decryptAESkey = null; // 객체 재사용 취약 보호

                        switch (response.trim()){
                            case "cfAdded":
                                Toast.makeText(getActivity(), "회의록을 등록하였습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error":
                                Toast.makeText(getActivity(), "서버오류입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                // 암호화된 대칭키를 키스토어의 개인키로 복호화
                String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey.toCharArray(),RSA.serverPublicKey.toCharArray()));
                params.put("type", AES.aesEncryption("cfAdd".toCharArray(),decryptAESkey));
                params.put("date", AES.aesEncryption(tv_date.getText().toString().toCharArray(),decryptAESkey));
                params.put("text", AES.aesEncryption(Meetlog.toCharArray(),decryptAESkey));

                decryptAESkey = null;
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
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Conference.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);
                        decryptAESkey = null; // 객체 재사용 취약 보호

                        if("error".equals(response.trim())){ //시스템 에러일때
                            manager_meetlog.setText("시스템 오류입니다.");
                            Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }else if("cfNotExist".equals(response.trim())){ // 등록된 회의록이 없을때
                            manager_meetlog.setText("현재 회의록이 등록되어있지 않습니다.");
                        }else{
                            String[] resPonse_split = response.split("-");
                            if("cfExist".equals(resPonse_split[1])){
                                manager_meetlog.setText(XSSFilter.xssFilter(resPonse_split[0]));
                            }else
                                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

                // 암호화된 대칭키를 키스토어의 개인키로 복호화
                String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey.toCharArray(),RSA.serverPublicKey.toCharArray()));
                params.put("type", AES.aesEncryption("cfShow".toCharArray(),decryptAESkey));
                params.put("date", AES.aesEncryption(tv_date.getText().toString().toCharArray(),decryptAESkey));

                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getActivity()).addToRequestQueue(stringRequest);
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}