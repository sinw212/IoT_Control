package com.example.nslngiot.Widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.LoginMemberActivity;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import androidx.annotation.Nullable;

public class WidgetProviderService extends Service {

    private SharedPreferences Preferences;
    private String calenderTitle; // 일정 제목
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat;

    private boolean lab_Person; // 재실 여부
    private boolean lab_Water; // 물 잔여량
    private boolean lab_Coffe; // 커피 잔여량
    private boolean lab_A4; // a4 잔여량

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate() {
        super.onCreate();
        lab_Person=false;
        lab_Water=false;
        lab_Coffe=false;
        lab_A4=false;
        calenderTitle="";
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String reflash_Flag = "com.example.nslngiot.imgbtn_widget_refresh";
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_member_status);
        Intent setup_intent = new Intent(getApplicationContext(), WidgetProvider.class);

        setup_intent.setAction(reflash_Flag); // 새로고침 등록
        PendingIntent pendingIntent_reflash = PendingIntent.getBroadcast(getApplicationContext(), 0, setup_intent, 0);
        views.setOnClickPendingIntent(R.id.imgbtn_widget_refresh, pendingIntent_reflash);

        // 일정 정보 누를 시 MainActivity 실행
        Intent intentCalendar = new Intent(Intent.ACTION_MAIN);
        intentCalendar.addCategory(Intent.CATEGORY_LAUNCHER);
        intentCalendar.setComponent(new ComponentName(getApplicationContext(), LoginMemberActivity.class));
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentCalendar, 0);
        views.setOnClickPendingIntent(R.id.tv_widget_calendar, mainActivityPendingIntent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    status_Request(); // 연구실 상태정보 호출
                    Thread.sleep(500);
                    member_calendar_Request(); // 연구실 일정정보 호출
                } catch (InterruptedException e) {
                    System.err.println("WidgetProviderService InterruptedException error");
                }
            }
        }).start();

        /*====================================================================================================================================**/
        // 네트워크 진행 완료 시, 받아온 데이터가 지워지는 문제 발생, XML에 저장.
        // 사용 시, 다시 XML 데이터 불러오는 안전한 형태로 진행
        Preferences = getSharedPreferences("LAB_CALENDER", Activity.MODE_PRIVATE);
        calenderTitle=Preferences.getString("CALENDER","NOTHING");
        Preferences = getSharedPreferences("LAB_STATUS", Activity.MODE_PRIVATE);
        lab_Person = Preferences.getBoolean("PERSON",false);
        lab_Water =  Preferences.getBoolean("WATER",false);
        lab_Coffe =  Preferences.getBoolean("COFFE",false);
        lab_A4 =  Preferences.getBoolean("A4",false);

        // 연구실 일정 정보 위젯에 View 띄우기
        views.setTextViewText(R.id.tv_widget_calendar, " 연구실 대표 일정: " +calenderTitle+"\n 상세 정보는 일정을 눌러 확인하세요.");
        if (lab_Person)
            views.setImageViewResource(R.id.img_person, R.drawable.people_exist);
        else
            views.setImageViewResource(R.id.img_person, R.drawable.people_nonexist);

        if (lab_Water)
            views.setImageViewResource(R.id.img_water, R.drawable.water_exist);
        else
            views.setImageViewResource(R.id.img_water, R.drawable.water_nonexist);

        if (lab_Coffe)
            views.setImageViewResource(R.id.img_coffee, R.drawable.coffee_exist);
        else
            views.setImageViewResource(R.id.img_coffee, R.drawable.coffee_nonexist);

        if (lab_A4)
            views.setImageViewResource(R.id.img_a4, R.drawable.a4_exist);
        else
            views.setImageViewResource(R.id.img_a4, R.drawable.a4_nonexist);

        /*====================================================================================================================================**/

        AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(
                new ComponentName(getApplicationContext(), WidgetProvider.class), views); // 위젯 업데이트
        Toast.makeText(getApplicationContext(), "연구실 상태 확인 완료", Toast.LENGTH_SHORT).show();

        stopSelf(); // 서비스 종료
        return super.onStartCommand(intent,flags,startId);
    }

    private void status_Request() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/DoorStatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "open":
                                lab_Person = true;
                                break;
                            case "close":
                                lab_Person = false;
                                break;
                            case "error":
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        // 위젯에 등록할 '상태' 정보 XML에 저장
                        Preferences = getSharedPreferences("LAB_STATUS", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = Preferences.edit();
                        editor.putBoolean("PERSON", lab_Person);
                        editor.putBoolean("WATER", lab_Water);
                        editor.putBoolean("COFFE", lab_Coffe);
                        editor.putBoolean("A4", lab_A4);
                        editor.apply();
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
                params.put("check", "security");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    // 연구실 일정 정보 조회
    private void member_calendar_Request() {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // 암호화된 대칭키를 키스토어의 개인키로 복호화
                            String decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);
                            // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                            response = AES.aesDecryption(response, decryptAESkey);

                            //******* 일정이 없으면,response값으로 scheduleNotExist 던져야 하나, []값이 넘어와 if실행안됨  *******//
                            if ("scheduleNotExist".equals(response.trim())) // 등록된 일정이 없을 시
                                Toast.makeText(getApplicationContext(), "현재 일정이 등록되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                            else if ("error".equals(response.trim())) { // 시스템 오류
                                Toast.makeText(getApplicationContext(), "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray jarray = new JSONArray(response);
                                JSONObject row = jarray.getJSONObject(0);

                                // 위젯에 등록할 '일정' 정보 XML에 저장
                                Preferences = getSharedPreferences("LAB_CALENDER", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = Preferences.edit();
                                editor.putString("CALENDER", row.getString("save_title"));
                                editor.apply();
                            }
                            decryptAESkey = null; // 객체 재사용 취약 보호
                            response = null;
                        } catch (UnsupportedEncodingException e) {
                            System.err.println("WidgetProviderService Response UnsupportedEncodingException error");
                        } catch (NoSuchPaddingException e) {
                            System.err.println("WidgetProviderService Response NoSuchPaddingException error");
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println("WidgetProviderService Response NoSuchAlgorithmException error");
                        } catch (InvalidAlgorithmParameterException e) {
                            System.err.println("WidgetProviderService Response InvalidAlgorithmParameterException error");
                        } catch (InvalidKeyException e) {
                            System.err.println("WidgetProviderService Response InvalidKeyException error");
                        } catch (BadPaddingException e) {
                            System.err.println("WidgetProviderService Response BadPaddingException error");
                        } catch (IllegalBlockSizeException e) {
                            System.err.println("WidgetProviderService Response IllegalBlockSizeException error");
                        } catch (JSONException e) {
                            System.err.println("WidgetProviderService Response JSONException error");
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
                // 오늘 날짜 확인

                try {
                    params.put("securitykey", RSA.rsaEncryption(decryptAESkey, RSA.serverPublicKey));
                    params.put("type", AES.aesEncryption("scheduleList", decryptAESkey));
                    params.put("date", AES.aesEncryption(mFormat.format(mDate), decryptAESkey));
                } catch (BadPaddingException e) {
                    System.err.println("WidgetProviderService Request BadPaddingException error");
                } catch (IllegalBlockSizeException e) {
                    System.err.println("WidgetProviderService Request IllegalBlockSizeException error");
                } catch (InvalidKeySpecException e) {
                    System.err.println("WidgetProviderService Request InvalidKeySpecException error");
                } catch (NoSuchPaddingException e) {
                    System.err.println("WidgetProviderService Request NoSuchPaddingException error");
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("WidgetProviderService Request NoSuchAlgorithmException error");
                } catch (InvalidKeyException e) {
                    System.err.println("WidgetProviderService Request InvalidKeyException error");
                } catch (InvalidAlgorithmParameterException e) {
                    System.err.println("WidgetProviderService Request InvalidAlgorithmParameterException error");
                } catch (UnsupportedEncodingException e) {
                    System.err.println("WidgetProviderService Request UnsupportedEncodingException error");
                }
                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}