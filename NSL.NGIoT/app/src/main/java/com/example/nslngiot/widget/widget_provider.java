package com.example.nslngiot.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.nslngiot.R;

public class widget_provider extends AppWidgetProvider {
    private static final String ACTION_BUTTON = "android.appwidget.provider";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);


        Intent calendar = new Intent(ACTION_BUTTON);
        PendingIntent pendingcalendar = PendingIntent.getBroadcast(context, 0, calendar, PendingIntent.FLAG_CANCEL_CURRENT );
        views.setOnClickPendingIntent(R.id.tv_calendar_widget, pendingcalendar);


        /*//버튼1 클릭 : 클릭 성공 메세지 출력!
        Intent intent1 = new Intent(ACTION_BUTTON1);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.button1, pendingIntent1);

        //버튼2 클릭 : 클릭하면 웹브라우저를 열어서 지정된 사이트를 보내 준다.
        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"));
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
        views.setOnClickPendingIntent(R.id.button2, pendingIntent2);

        //버튼3 클릭 : 이미지뷰에 비트맵 이미지를 교체해준다.
        Intent intent3 = new Intent(ACTION_BUTTON3);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button3, pendingIntent3);
*/
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

   /* @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), NewAppWidget.class.getName());
        int[] appWidgets = appWidgetManager.getAppWidgetIds(thisAppWidget);

        final String action = intent.getAction();
        if(action.equals(ACTION_BUTTON1)){
            //your code here
            Toast.makeText(context, "버튼을 클릭했어요.", Toast.LENGTH_LONG).show();
            onUpdate(context, appWidgetManager, appWidgets);
        } else if (action.equals(ACTION_BUTTON3)){
            Toast.makeText(context, "이미지를 교체 할께요.", Toast.LENGTH_SHORT).show();
            //AsyncTask를 이용해서 이미지를 가져와서 교체해 보자.
            String imgUrl = "http:///교체할이미지주소.png";
            new DownloadBitmap(views, appWidgets[0], appWidgetManager).execute(imgUrl); //AsyncTask 실행
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, NewAppWidget.class), views);
        }
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
    }*/
}


