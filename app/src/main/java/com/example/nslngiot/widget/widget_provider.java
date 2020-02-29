package com.example.nslngiot.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.MainActivity;
import com.example.nslngiot.MemberFragment.CalendarFragment;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import java.util.HashMap;
import java.util.Map;


public class widget_provider extends AppWidgetProvider {

    private static final String StatusReflash = "com.example.nslngiot.imgbtn_widget_reflash";
    private Intent reflash = new Intent(StatusReflash);
    public boolean person;
    public boolean water;
    public boolean coffe;
    public boolean a4;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        final String action = intent.getAction();

        if (action.equals(StatusReflash)) {//새로고침 버튼 클릭시
            Status_SelectRequest(context);//volley 호출

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

           AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, widget_provider.class), views);//위젯 업데이트
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);//리모트뷰 지정?
        //위젯 버튼 등록?
        PendingIntent pendingIntent_reflash = PendingIntent.getBroadcast(context, 0, reflash, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.imgbtn_widget_reflash, pendingIntent_reflash);

        appWidgetManager.updateAppWidget(appWidgetIds, views);//위젯 업데이트


    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is create
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void Status_SelectRequest(final Context context) {

        //다시 확인
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
                System.out.println("2");
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

    public void setStatus(boolean person, boolean water, boolean coffe, boolean a4, Context context) {

        if (person != this.person || water != this.water || coffe != this.coffe || a4 != this.a4) {//하나라도 상태가 다를 시 실행.
            this.person = person;
            this.water = water;
            this.coffe = coffe;
            this.a4 = a4;
            this.onReceive(context, reflash);//Receive를 다시 호출하여 위젯 이미지를 바꾸게 함.
        }


    }
}