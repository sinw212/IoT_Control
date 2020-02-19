package com.example.nslngiot.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import java.util.HashMap;
import java.util.Map;

public class widget_provider extends AppWidgetProvider {

    private static final String  StatusReflash= "com.example.nslngiot.imgbtn_widget_reflash";
    public ImageView imgview_person = null;
    public boolean person;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_member_status);
        Intent reflash = new Intent(StatusReflash);
        PendingIntent pendingIntent_reflash = PendingIntent.getBroadcast(context, 0, reflash, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.imgbtn_widget_refresh, pendingIntent_reflash);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_member_status);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), widget_provider.class.getName());
        int[] appWidgets = appWidgetManager.getAppWidgetIds(thisAppWidget);
        //imgview_person = views.findViewById(R.id.img_person);
        final String action = intent.getAction();
        if(action.equals(StatusReflash)){//새로고침 버튼 클릭시

            member_Person_SelectRequest(context);
            if(person ==true){
                views.setImageViewResource(R.id.img_person, R.drawable.people_exist);
            }else if(person ==false){
                views.setImageViewResource(R.id.img_person,R.drawable.people_nonexist);
            }
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, widget_provider.class), views);
        }

        /* else if (action.equals(ACTION_BUTTON3)){
            Toast.makeText(context, "이미지를 교체 할께요.", Toast.LENGTH_SHORT).show();
            //AsyncTask를 이용해서 이미지를 가져와서 교체해 보자.
            String imgUrl = "http:///교체할이미지주소.png";
            new DownloadBitmap(views, appWidgets[0], appWidgetManager).execute(imgUrl); //AsyncTask 실행
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, NewAppWidget.class), views);
        }*/
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private synchronized void member_Person_SelectRequest(final Context context) {
        // views = new RemoteViews(context.getPackageName(), R.layout.widget);

        //다시 확인
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/DoorStatusCheck.jsp");



        StringRequest stringRequest = new StringRequest(

                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 재실여부 상태 확인
                        Log.d("진입", response);
                        switch (response){
                            case "open":
                                Toast.makeText(context, "ㅇㅇㅇㅇㅇ", Toast.LENGTH_LONG).show();

                                break;
                            case "close":
                                Toast.makeText(context, "ㄴㄴㄴㄴ", Toast.LENGTH_LONG).show();

                                break;
                            case "error":
                                Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                break;
                        }
                   /*     if("open".equals(response.trim())) {
                            // 랩실에 사람 있을 때
                            Toast.makeText(context, "ㅇㅇㅇㅇㅇ", Toast.LENGTH_LONG).show();
                            views.setImageViewResource(R.id.img_person, R.drawable.people_exist);
                        } else if("close".equals(response.trim())) {
                            // 랩실에 사람 없을
                            Toast.makeText(context, "ㄴㄴㄴㄴ", Toast.LENGTH_LONG).show();
                            views.setImageViewResource(R.id.img_person,R.drawable.people_nonexist);
                        } else if("error".equals(response)) {
                            Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        }*/ //noAndroid도 있다고 했던거 같은데 뭐였드라
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
                params.put("check","security");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }
}