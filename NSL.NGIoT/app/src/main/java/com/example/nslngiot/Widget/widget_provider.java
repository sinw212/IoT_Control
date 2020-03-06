package com.example.nslngiot.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.NetworkCheck;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;
import com.example.nslngiot.SplashActivity;

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


public class widget_provider extends AppWidgetProvider {

    private final String StatusReflash = "com.example.nslngiot.imgbtn_widget_refresh";
    private final String tv_Calendar = "com.example.nslngiot.tv_widget_calendar";
    private Intent reflash = new Intent(StatusReflash);
    //private Intent Calendar = new Intent(tv_Calendar);
    public boolean person;//재실 여부
    public boolean water;//물 잔여량
    public boolean coffe;//커피 잔여량
    public boolean a4;//a4 잔여량
    long mNow;//getTime 에서 쓸 시간 변수
    public Date mDate;//getTime 에서 쓸 시간 변수
    public SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");
    public String calTitle1 = "";//리스폰에서 받아온 일정 이름
    public String calTitle2 = "";//리스폰에서 받아온 일정 이름

    public String check = "";//리스폰 반복 방지를 위한 체크 (일정)
    private int checkSecurity = 0;//securitycheck 에서 쓰는 변수..

    @Override
    public void onReceive(Context context, Intent intent) {

        //네트워크 연결 상태 확인 및 보안 점검?
        if (NetworkCheck.networkCheck(context)) {
            securitycheck(context);
        } else {
            Toast.makeText(context, "네트워크연결이 되지 않습니다.\n" + "네트워크 수신상태를 확인하세요.", Toast.LENGTH_SHORT).show();
            // finish();
        }

        //리모트뷰 지정 및 버튼 연결/?
        super.onReceive(context, intent);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_member_status);//리모트뷰 지정?

        //위젯 버튼 등록?
        PendingIntent pendingIntent_reflash = PendingIntent.getBroadcast(context, 0, reflash, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.imgbtn_widget_refresh, pendingIntent_reflash);

        //일정 텍스트 클릭 시 앱 실행
        Intent intentCalendar = new Intent(Intent.ACTION_MAIN);
        intentCalendar.addCategory(Intent.CATEGORY_LAUNCHER);
        intentCalendar.setComponent(new ComponentName(context, SplashActivity.class));
        PendingIntent pi = PendingIntent.getActivity(context, 0, intentCalendar, 0);
        views.setOnClickPendingIntent(R.id.tv_widget_calendar, pi);

        //앱에서 입력 받기(새로고침 버튼)
        final String action = intent.getAction();

        if (action.equals(StatusReflash)) {//새로고침 버튼 클릭시

            member_select_Request(context);//일정 volley 호츌
            Status_SelectRequest(context);//아두이노 volley 호출
            // System.out.println("리시브에서 받은 일정" + calTitle);
            views.setTextViewText(R.id.tv_widget_calendar, " " + calTitle1 +"\n"+ " "+calTitle2);

            //이미지 변경 if else문들
            if (person == true) {
                views.setImageViewResource(R.id.img_person, R.drawable.people_exist);
            } else if (person == false) {
                views.setImageViewResource(R.id.img_person, R.drawable.people_nonexist);
            }
            if (water == true) {
                views.setImageViewResource(R.id.img_water, R.drawable.water_exist);
            } else if (water == false) {
                views.setImageViewResource(R.id.img_water, R.drawable.water_nonexist);
            }
            if (coffe == true) {
                views.setImageViewResource(R.id.img_coffee, R.drawable.coffee_exist);
            } else if (coffe == false) {
                views.setImageViewResource(R.id.img_coffee, R.drawable.coffee_nonexist);
            }
            if (a4 == true) {
                views.setImageViewResource(R.id.img_a4, R.drawable.a4_exist);
            } else if (a4 == false) {
                views.setImageViewResource(R.id.img_a4, R.drawable.a4_nonexist);
            }
        }
        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, widget_provider.class), views);//위젯 업데이트

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is create
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public synchronized void Status_SelectRequest(final Context context) {
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/DoorStatusCheck.jsp");

        StringRequest stringRequest = new StringRequest(

                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    boolean person = false;
                    boolean water = false;
                    boolean a4 = false;
                    boolean coffe = false;

                    @Override
                    public void onResponse(String response) {

                        // 재실여부 상태 확인
                        Log.d("진입", response);
                        switch (response) {
                            case "open":
                                // Toast.makeText(context, "ㅇㅇㅇㅇㅇ", Toast.LENGTH_SHORT).show();
                                person = true;
                                break;
                            case "close":
                                // Toast.makeText(context, "ㄴㄴㄴㄴ", Toast.LENGTH_SHORT).show();
                                person = false;
                                break;
                            case "error":
                                Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        setStatus(person, water, coffe, a4, context);
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

    //이미지 상태 세팅(ex 재실 시 person =true..)
    public void setStatus(boolean person, boolean water, boolean coffe, boolean a4, Context context) {
        System.out.println("위젯 이미지 설정");
        if (person != this.person || water != this.water || coffe != this.coffe || a4 != this.a4) {//하나라도 상태가 다를 시 실행.
            this.person = person;
            this.water = water;
            this.coffe = coffe;
            this.a4 = a4;
            this.onReceive(context, reflash);//Receive를 다시 호출하여 위젯 이미지를 바꾸게 함.
        }
    }


    //일정 volley
    private void member_select_Request(final Context context) {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");
        //    System.out.println("진입");
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
                                System.out.println("일정 진입");
                                JSONObject row = jarray.getJSONObject(0);
                                calTitle1 = row.getString("save_title");
                                row = jarray.getJSONObject(1);
                                calTitle2 = row.getString("save_title");
                                System.out.println("일정 : " + row.getString("save_title"));
                                refreshCalendar(context);
                            }
                            decryptAESkey = null; // 객체 재사용 취약 보호
                            response = null;
                        } catch (UnsupportedEncodingException e) {
                            System.err.println("Member CalendarFragment Response UnsupportedEncodingException error");
                        } catch (NoSuchPaddingException e) {
                            System.err.println("Member CalendarFragment Response NoSuchPaddingException error");
                        } catch (NoSuchAlgorithmException e) {
                            System.err.println("Member CalendarFragment Response NoSuchAlgorithmException error");
                        } catch (InvalidAlgorithmParameterException e) {
                            System.err.println("Member CalendarFragment Response InvalidAlgorithmParameterException error");
                        } catch (InvalidKeyException e) {
                            System.err.println("Member CalendarFragment Response InvalidKeyException error");
                        } catch (BadPaddingException e) {
                            System.err.println("Member CalendarFragment Response BadPaddingException error");
                        } catch (IllegalBlockSizeException e) {
                            System.err.println("Member CalendarFragment Response IllegalBlockSizeException error");
                        } catch (JSONException e) {
                            System.err.println("Member CalendarFragment Response JSONException error");
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
                    params.put("securitykey", RSA.rsaEncryption(decryptAESkey, RSA.serverPublicKey));
                    params.put("type", AES.aesEncryption("scheduleList", decryptAESkey));
                    params.put("date", AES.aesEncryption(getTime(), decryptAESkey));
                } catch (BadPaddingException e) {
                    System.err.println("Member CalendarFragment Request BadPaddingException error");
                } catch (IllegalBlockSizeException e) {
                    System.err.println("Member CalendarFragment Request IllegalBlockSizeException error");
                } catch (InvalidKeySpecException e) {
                    System.err.println("Member CalendarFragment Request InvalidKeySpecException error");
                } catch (NoSuchPaddingException e) {
                    System.err.println("Member CalendarFragment Request NoSuchPaddingException error");
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("Member CalendarFragment Request NoSuchAlgorithmException error");
                } catch (InvalidKeyException e) {
                    System.err.println("Member CalendarFragment Request InvalidKeyException error");
                } catch (InvalidAlgorithmParameterException e) {
                    System.err.println("Member CalendarFragment Request InvalidAlgorithmParameterException error");
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Member CalendarFragment Request UnsupportedEncodingException error");
                }
                decryptAESkey = null;
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    //현재 시간 체크
    public String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    //App에서 일정 갱신
    public void refreshCalendar(Context context) {
        System.out.println("리스폰에서 받은 일정" + calTitle1);
        if (!calTitle1.equals(check)) {
            check = calTitle1;
            this.onReceive(context, reflash);
        }
    }


    //보안 점검
    public void securitycheck(Context context) {
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
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.err.println("SplashActivity InterruptedException error ");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SplashActivity NoSuchAlgorithmException error ");
        }
    }
}