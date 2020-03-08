package com.example.nslngiot.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.nslngiot.LoginMemberActivity;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;

import java.security.NoSuchAlgorithmException;

public class WidgetProvider extends AppWidgetProvider {
    private final String reflash_Flag = "com.example.nslngiot.imgbtn_widget_refresh";

    /*
     * 브로드캐스트를 수신할때, Override된 콜백 메소드가 호출되기 직전에 호출됨
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction(); // 새로고침 시, 액션 이벤트 리시브

        // 새로 고침 이벤트 체크 진행
        if (action.equals(reflash_Flag.trim())) {
            Intent widgetBackgroud = new Intent(context,WidgetProviderService.class);
            context.startService(widgetBackgroud );
        }
        else{
            // 클릭 이벤트 셋팅
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_member_status);
            Intent setup_intent = new Intent(context, WidgetProvider.class);
            setup_intent.setAction(reflash_Flag); // 새로고침 등록
            PendingIntent pendingIntent_reflash = PendingIntent.getBroadcast(context, 0, setup_intent, 0);
            views.setOnClickPendingIntent(R.id.imgbtn_widget_refresh, pendingIntent_reflash);

            // 일정 정보 누를 시 MainActivity 실행
            Intent intentCalendar = new Intent(Intent.ACTION_MAIN);
            intentCalendar.addCategory(Intent.CATEGORY_LAUNCHER);
            intentCalendar.setComponent(new ComponentName(context, LoginMemberActivity.class));
            PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, intentCalendar, 0);
            views.setOnClickPendingIntent(R.id.tv_widget_calendar, mainActivityPendingIntent);

            views.setTextViewText(R.id.tv_widget_calendar, " 일정 조회를 위해 우측 상단의 새로고침\n 버튼을 눌러주세요.");
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, WidgetProvider.class), views); // 위젯 업데이트
        }
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
            System.err.println("SplashActivity NoSuchAlgorithmException error ");
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
}