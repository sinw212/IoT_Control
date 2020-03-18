package com.example.nslngiot.Network_Utill;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyQueueSingleTon {

    // (관리자 전용) ManagerAddUserAdapter& AdduesrFragment에서 신규인원 등록 조회 공유를 위한 변수
    public static StringRequest addUser_selectSharing;

    // (관리자 & 일반 사용자) ManagerCalendarAdapter & CalendarFragment (manager&member)에서 일정 조회 공유를 위한 변수
    public static StringRequest manager_calendar_selectSharing;
    public static String manager_DATE = "";

    // (관리자 & 일반 사용자) ManagerMemberAdapter & MemberFragment (manager&member)에서 연구실 인원 조회 공유를 위한 변수
    public static StringRequest manager_member_selectSharing;
    public static String member_DATE = "";

    private static VolleyQueueSingleTon volleyQueueSingleTonInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleyQueueSingleTon(Context getContext){
        context = getContext;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyQueueSingleTon getInstance(Context getContext){
        if(volleyQueueSingleTonInstance == null){
            volleyQueueSingleTonInstance = new VolleyQueueSingleTon(getContext);
        }
        return volleyQueueSingleTonInstance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }
}