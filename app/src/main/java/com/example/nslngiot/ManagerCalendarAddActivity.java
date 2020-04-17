package com.example.nslngiot;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.Security_Utill.XSSFilter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManagerCalendarAddActivity extends AppCompatActivity {

    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");

    private Calendar c;
    private int nYear,nMon,nDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private TextView tv_date;
    private String Date, Title, Detail;
    private EditText EditTitle,EditDetail;
    private ImageButton btn_calendar;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_calendar_add);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Date = "";
        Title = "";
        Detail = "";
        tv_date = findViewById(R.id.tv_date);
        EditTitle = findViewById(R.id.manager_calendar_title);
        EditDetail = findViewById(R.id.manager_calendar_detail);
        btn_calendar = findViewById(R.id.btn_calendar);
        btn_add = findViewById(R.id.btn_add);

        c = Calendar.getInstance();
        nYear = c.get(Calendar.YEAR);
        nMon = c.get(Calendar.MONTH);
        nDay = c.get(Calendar.DAY_OF_MONTH);

        // 오늘 날짜 표현
        tv_date.setText(getTime());

        // Calendar
        // DatePicker Listener
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

                        nYear = year;
                        nMon = monthOfYear;
                        nDay = dayOfMonth;
                    }
                };

        // 달력 아이콘 리스너
        btn_calendar.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog oDialog = new DatePickerDialog(ManagerCalendarAddActivity.this,
                        mDateSetListener, nYear, nMon, nDay);
                oDialog.show();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date = tv_date.getText().toString().trim();
                //////////////////////////////방어 코드////////////////////////////
                //XSS 특수문자 공백처리 및 방어
                Title = XSSFilter.xssFilter(EditTitle.getText().toString().trim());
                Detail = XSSFilter.xssFilter(EditDetail.getText().toString().trim());
                //////////////////////////////////////////////////////////////////

                if (TextUtils.isEmpty(Title) || TextUtils.isEmpty(Detail)) {
                    Toast.makeText(getApplicationContext(), "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    EditTitle.setText("");
                    EditDetail.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                manager_Calendar_SaveRequest(); // 인원 등록 진행
                                Thread.sleep(100);
                                if(VolleyQueueSingleTon.manager_calendar_selectSharing != null){
                                    VolleyQueueSingleTon.manager_calendar_selectSharing.setShouldCache(false);
                                    VolleyQueueSingleTon.getInstance(ManagerCalendarAddActivity.this).addToRequestQueue(VolleyQueueSingleTon.manager_calendar_selectSharing);
                                }
                                Thread.sleep(100);
                                finish(); // 이전 fragment로 이동
                            } catch (InterruptedException e) {
                                System.err.println("ManagerCalendarAddActivity InterruptedException error");
                            }
                        }
                    }).start();


                }
            }
        });
    }

    // 일정 등록 통신
    private void manager_Calendar_SaveRequest(){
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);

                        java.util.Arrays.fill(decryptAESkey,(char)0x20);

                        switch (response.trim()){
                            case "scheduleAddSuccess": // 일정 등록 성공했을 때
                                Toast.makeText(getApplicationContext(), "회의록을 등록하였습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "scheduleAlreadyEist": // 제목이 겹칠 때
                                Toast.makeText(getApplicationContext(), "이미 존재하는 일정입니다.", Toast.LENGTH_SHORT).show();
                            case "error": // 오류
                                Toast.makeText(getApplicationContext(), "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey.toCharArray()));
                params.put("type",AES.aesEncryption("scheduleAdd".toCharArray(),decryptAESkey));
                params.put("date",AES.aesEncryption(Date.toCharArray(),decryptAESkey));
                params.put("title",AES.aesEncryption(Title.toCharArray(),decryptAESkey));
                params.put("text", AES.aesEncryption (Detail.toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}