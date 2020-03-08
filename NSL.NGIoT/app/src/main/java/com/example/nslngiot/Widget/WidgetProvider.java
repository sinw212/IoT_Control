package com.example.nslngiot.Widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class WidgetProvider extends AppWidgetProvider {

    private final static String reflash_Flag = "com.example.nslngiot.imgbtn_widget_refresh";
    private SharedPreferences Preferences; // 앱 XML 저장 및 읽기 전용
    private String calenderTitle; // 연구실 일정 정보
    private boolean lab_Lamp; // 전등
    private boolean lab_Person; // 재실 여부
    private boolean lab_Coffe; // 커피 잔여량
    private boolean lab_A4; // a4 잔여량
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat;

    /*브로드캐스트를 수신할때, Override된 콜백 메소드가 호출되기 직전에 호출됨*/
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction(); // 새로고침 시, 액션 이벤트 리시브

        // 클릭 이벤트 셋팅
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_member_status);
        Intent setup_intent = new Intent(context, WidgetProvider.class);
        setup_intent.setAction(reflash_Flag); // 새로고침 등록
        PendingIntent pendingIntent_reflash = PendingIntent.getBroadcast(context, 0, setup_intent, 0);
        views.setOnClickPendingIntent(R.id.imgbtn_widget_refresh, pendingIntent_reflash);

        // 새로 고침 이벤트 체크 진행
        if (action!= null && action.equals(reflash_Flag.trim())) {
            // 날짜 셋팅
            mNow = System.currentTimeMillis();
            mDate = new Date(mNow);
            mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");

            // 일정 정보 누를 시, Activity 실행
            Intent intentCalendar = new Intent(Intent.ACTION_MAIN);
            intentCalendar.addCategory(Intent.CATEGORY_LAUNCHER);
            intentCalendar.setComponent(new ComponentName(context, LoginMemberActivity.class));
            PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, intentCalendar, 0);
            views.setOnClickPendingIntent(R.id.tv_widget_calendar, mainActivityPendingIntent);

            try {
                AES.aesKeyGen(); // 연결된 액티비티 데이터 리드를 위해 키를 한번 더 생성
                AES.secretKEY = KEYSTORE.keyStore_Encryption(AES.secretKEY);

                status_Request(context); // 연구실 상태정보 호출
                Thread.sleep(100);
                member_calendar_Request(context); // 연구실 일정정보 호출
            } catch (InterruptedException e) {
                System.err.println("WidgetProvider InterruptedException error");
            } catch (NoSuchAlgorithmException e) {
                System.err.println("WidgetProvider NoSuchAlgorithmException error");
            }

            // 네트워크 진행 완료 시, 받아온 데이터가 지워지는 문제 발생, XML에 저장.
            // 사용 시, 다시 XML 데이터 불러오는 안전한 형태로 진행
            Preferences = context.getSharedPreferences("LAB_CALENDER", Activity.MODE_PRIVATE);
            calenderTitle = Preferences.getString("CALENDER", "NOTHING");
            Preferences = context.getSharedPreferences("LAB_STATUS", Activity.MODE_PRIVATE);
            lab_Lamp = Preferences.getBoolean("LAMP", false);
            lab_Person = Preferences.getBoolean("PERSON", false);
            lab_Coffe = Preferences.getBoolean("COFFE", false);
            lab_A4 = Preferences.getBoolean("A4", false);

            views.setTextViewText(R.id.tv_widget_calendar, " 연구실 대표 일정: " +calenderTitle+"\n 상세 정보는 일정을 눌러 확인하세요.");
            if (lab_Lamp)
                views.setImageViewResource(R.id.img_lamp, R.drawable.lamp_on);
            else
                views.setImageViewResource(R.id.img_lamp, R.drawable.lamp_off);

            if (lab_Person) {
                views.setImageViewResource(R.id.img_person, R.drawable.people_exist);
            }
            else {
                views.setImageViewResource(R.id.img_person, R.drawable.people_nonexist);
            }

            if (lab_Coffe) {
                views.setImageViewResource(R.id.img_coffee, R.drawable.coffee_exist);
            }
            else
                views.setImageViewResource(R.id.img_coffee, R.drawable.coffee_nonexist);

            if (lab_A4)
                views.setImageViewResource(R.id.img_a4, R.drawable.a4_exist);
            else
                views.setImageViewResource(R.id.img_a4, R.drawable.a4_nonexist);
            Toast.makeText(context,"연구실 정보 조회 성공",Toast.LENGTH_SHORT).show();
        }
        else
            views.setTextViewText(R.id.tv_widget_calendar, " 새로 고침을 눌러주세요.");

        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, WidgetProvider.class), views); // 위젯 업데이트
        super.onReceive(context,intent);
    }

    @Override
    public void onEnabled(Context context) {
        /*
         * 위젯이 처음 생성될때 호출됨
         * 동일한 위젯이 생성되도 최초 생성때만 호출됨
         */
        int checkSecurity = 0;
        try {
            for (int i = 0; i < 10; i++) {
                switch (i * 10) {
                    case 10:
                        KEYSTORE keystore = new KEYSTORE();
                        keystore.keyStore_init(context); // 최초 1회 KeyStore에 저장할 AES 대칭키 생성
                        checkSecurity += 1;
                        break;
                    case 20:
                        AES.aesKeyGen();
                        AES.secretKEY = KEYSTORE.keyStore_Encryption(AES.secretKEY);
                        // 생성된 개인키/대칭키 keystore의 비대칭암호로 암호화하여 static 메모리 적재
                        checkSecurity += 1;
                        break;
                    default:
                        break;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("WidgetProvider NoSuchAlgorithmException error ");
        }
        if(checkSecurity == 2)
            Toast.makeText(context,"위젯을 설치하셨다면 우측 상단\n새로고침을 눌러주세요.",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context,"위젯설정실패 다시 설치해주세요.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        /*
         * 위젯을 갱신할때 호출됨
         * 주의 : Configure Activity를 정의했을때는 위젯 등록시 처음 한번은 호출이 되지 않습니다
         */
    }

    @Override
    public void onDisabled(Context context) {
        /*
         * 위젯의 마지막 인스턴스가 제거될때 호출됨
         * onEnabled()에서 정의한 리소스 정리할때
         */
    }

    private void status_Request(final Context context) {
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
                                Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        // 위젯에 등록할 '상태' 정보 XML에 저장
                        Preferences = context.getSharedPreferences("LAB_STATUS", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = Preferences.edit();
                        editor.putBoolean("LAMP", lab_Lamp);
                        editor.putBoolean("PERSON", lab_Person);
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
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }

    // 연구실 일정 정보 조회
    private void member_calendar_Request(final Context context) {
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
                                Toast.makeText(context, "현재 일정이 등록되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                            else if ("error".equals(response.trim())) { // 시스템 오류
                                Toast.makeText(context, "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray jarray = new JSONArray(response);
                                JSONObject row = jarray.getJSONObject(0);

                                // 위젯에 등록할 '일정' 정보 XML에 저장
                                Preferences = context.getSharedPreferences("LAB_CALENDER", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = Preferences.edit();
                                editor.putString("CALENDER", row.getString("save_title"));
                                editor.apply();
                            }
                            decryptAESkey = null; // 객체 재사용 취약 보호
                            response = null;
                        } catch (UnsupportedEncodingException e) {
                            System.err.println("WidgetProvider Response UnsupportedEncodingException error");
                        } catch (NoSuchPaddingException e) {
                            System.err.println("WidgetProvider Response NoSuchPaddingException error");
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println("WidgetProvider Response NoSuchAlgorithmException error");
                        } catch (InvalidAlgorithmParameterException e) {
                            System.err.println("WidgetProvider Response InvalidAlgorithmParameterException error");
                        } catch (InvalidKeyException e) {
                            System.err.println("WidgetProvider Response InvalidKeyException error");
                        } catch (BadPaddingException e) {
                            System.err.println("WidgetProvider Response BadPaddingException error");
                        } catch (IllegalBlockSizeException e) {
                            System.err.println("WidgetProvider Response IllegalBlockSizeException error");
                        } catch (JSONException e) {
                            System.err.println("WidgetProvider Response JSONException error");
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
                    System.err.println("WidgetProvider Request BadPaddingException error");
                } catch (IllegalBlockSizeException e) {
                    System.err.println("WidgetProvider Request IllegalBlockSizeException error");
                } catch (InvalidKeySpecException e) {
                    System.err.println("WidgetProvider Request InvalidKeySpecException error");
                } catch (NoSuchPaddingException e) {
                    System.err.println("WidgetProvider Request NoSuchPaddingException error");
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("WidgetProvider Request NoSuchAlgorithmException error");
                } catch (InvalidKeyException e) {
                    System.err.println("WidgetProvider Request InvalidKeyException error");
                } catch (InvalidAlgorithmParameterException e) {
                    System.err.println("WidgetProvider Request InvalidAlgorithmParameterException error");
                } catch (UnsupportedEncodingException e) {
                    System.err.println("WidgetProvider Request UnsupportedEncodingException error");
                }
                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }
}