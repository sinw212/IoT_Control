package com.example.nslngiot.ManagerFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CalendarFragment extends Fragment {

    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");

    private Calendar c;
    int nYear,nMon,nDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private RecyclerView recyclerView = null;
    private LinearLayoutManager layoutManager = null;
    private ManagerCalendarAdapter managerCalendarAdapter = null;
    private ArrayList<ManagerCalendarData> arrayList;
    private ManagerCalendarData managerCalendarData;

    private TextView tv_date;
    private ImageButton btn_calendar;
    private Button btn_add;
    public static String Date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manager_calendar, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Date = "";
        tv_date = getView().findViewById(R.id.tv_date);
        btn_calendar = getView().findViewById(R.id.btn_calendar);
        btn_add = getView().findViewById(R.id.btn_add);
        recyclerView  = getView().findViewById(R.id.recyclerview_manager_calendar);
        // 오늘 날짜 표현
        tv_date.setText(getTime());

        // 등록된 일정 '제목조회' 조회
        manager_Calendar_Title_SelectRequest();

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

                        // 등록된 일정 '제목조회' 조회
                        manager_Calendar_Title_SelectRequest();
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
                DatePickerDialog oDialog = new DatePickerDialog(getActivity(),
                        mDateSetListener, nYear, nMon, nDay);
                oDialog.show();
            }
        });

        // 추가 버튼 리스너
        btn_add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManagerCalendarAddActivity.class);
                startActivity(intent);
            }
        });
    }

    // 현재 등록된 일정 제목조회 통신
    private void manager_Calendar_Title_SelectRequest() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        VolleyQueueSingleTon.manager_calendar_selectSharing = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // 암호화된 대칭키를 키스토어의 개인키로 복호화
                            String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                            // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                            response = AES.aesDecryption(response,decryptAESkey);

                            //******* 일정이 없으면,response값으로 scheduleNotExist 던져야 하나, []값이 넘어와 if실행안됨  *******//
                            if("scheduleNotExist".equals(response.trim())) // 등록된 일정이 없을 시
                                Toast.makeText(getActivity(), "현재 일정이 등록되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                            else if("error".equals(response.trim())){ // 시스템 오류
                                Toast.makeText(getActivity(), "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                            }else{
                                layoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setHasFixedSize(true); // 아이템의 뷰를 일정하게하여 퍼포먼스 향상
                                recyclerView.setLayoutManager(layoutManager); // 앞에 선언한 리사이클러뷰를 매니저에 붙힘
                                // 기존 데이터와 겹치지 않기 위해 생성자를 매번 새롭게 생성
                                arrayList = new ArrayList<ManagerCalendarData>();

                                JSONArray jarray = new JSONArray(response);
                                int size = jarray.length();
                                for (int i = 0; i < size; i++) {
                                    JSONObject row = jarray.getJSONObject(i);
                                    managerCalendarData = new ManagerCalendarData();
                                    managerCalendarData.setTitle(row.getString("save_title"));
                                    managerCalendarData.setNumber(String.valueOf(i+1));
                                    arrayList.add(managerCalendarData);
                                }
                                // 어댑터에 add한 다량의 데이터 할당
                                managerCalendarAdapter = new ManagerCalendarAdapter(getActivity(),arrayList);
                                // 리사이클러뷰에 어답타 연결
                                recyclerView .setAdapter(managerCalendarAdapter);
                            }
                            decryptAESkey = null; // 객체 재사용 취약 보호
                            response = null;
                        } catch (UnsupportedEncodingException e) {
                            System.err.println("Manager CalendarFragment Response UnsupportedEncodingException error");
                        } catch (NoSuchPaddingException e) {
                            System.err.println("Manager CalendarFragment Response NoSuchPaddingException error");
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println("Manager CalendarFragment Response NoSuchAlgorithmException error");
                        } catch (InvalidAlgorithmParameterException e) {
                            System.err.println("Manager CalendarFragment Response InvalidAlgorithmParameterException error");
                        } catch (InvalidKeyException e) {
                            System.err.println("Manager CalendarFragment Response InvalidKeyException error");
                        } catch (BadPaddingException e) {
                            System.err.println("Manager CalendarFragment Response BadPaddingException error");
                        } catch (IllegalBlockSizeException e) {
                            System.err.println("Manager CalendarFragment Response IllegalBlockSizeException error");
                        } catch (JSONException e) {
                            System.err.println("Manager CalendarFragment Response JSONException error");
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

                try {
                    params.put("securitykey", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey));
                    params.put("type",AES.aesEncryption("scheduleList",decryptAESkey));
                    params.put("date",AES.aesEncryption(Date = tv_date.getText().toString().trim(),decryptAESkey));
                } catch (BadPaddingException e) {
                    System.err.println("Manager CalendarFragment Request BadPaddingException error");
                } catch (IllegalBlockSizeException e) {
                    System.err.println("Manager CalendarFragment Request IllegalBlockSizeException error");
                } catch (InvalidKeySpecException e) {
                    System.err.println("Manager CalendarFragment Request InvalidKeySpecException error");
                } catch (NoSuchPaddingException e) {
                    System.err.println("Manager CalendarFragment Request NoSuchPaddingException error");
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("Manager CalendarFragment Request NoSuchAlgorithmException error");
                } catch (InvalidKeyException e) {
                    System.err.println("Manager CalendarFragment Request InvalidKeyException error");
                } catch (InvalidAlgorithmParameterException e) {
                    System.err.println("Manager CalendarFragment Request InvalidAlgorithmParameterException error");
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Manager CalendarFragment Request UnsupportedEncodingException error");
                }
                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        VolleyQueueSingleTon.manager_calendar_selectSharing.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(VolleyQueueSingleTon.manager_calendar_selectSharing);
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}