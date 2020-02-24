package com.example.nslngiot.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.ManagerCalendarAddActivity;
import com.example.nslngiot.ManagerFragment.CalendarFragment;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerCalendarAdapter extends RecyclerView.Adapter<ManagerCalendarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerCalendarData> calendarData;
    private String Date = CalendarFragment.Date;

    // ManagerCalendar어댑터에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return calendarData.size();
    }

    public ManagerCalendarAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ManagerCalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view =LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_manager_calendar,viewGroup,false); // 뷰생성
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(ManagerCalendarAdapter.ViewHolder holder, int position) {

        final ManagerCalendarData item = calendarData.get(position); // 위치에 따른 아이템 반환

        holder.numText.setText(item.getNumber()); // ManagerCalendarData의 getNumber값을 numtext에 삽입
        holder.titleText.setText(item.getTitle()); // -

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 '상세조회' 조회
                calendar_select_Request(item.getTitle(), Date);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView titleText;

        public ViewHolder(View itemView) {
            super(itemView); // 입력 받은 값을 뷰홀더에 삽입
            numText = itemView.findViewById(R.id.manager_calendar_number);
            titleText = itemView.findViewById(R.id.manager_calendar_title);
        }
    }

    public ManagerCalendarAdapter (Activity activity, ArrayList<ManagerCalendarData> list) {
        this.calendarData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }

    // 일정 삭제
    private void Manager_calendar_delete_Request(final String Date, final String Title) {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "scheduleDelete":// 삭제했을 시
                                Toast.makeText(context, "일정을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "error": // 오류
                                Toast.makeText(context, "시스템 에러", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                params.put("date", Date);
                params.put("title", Title);
                params.put("type", "scheduleDelete"); // error/nonTypeRequest

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }

    // 회원정보 상세 조회
    private void calendar_select_Request(final String Title , final String Date){
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if("scheduleNotExist".equals(response.trim())) // 등록된 일정이 없을 시정
                            Toast.makeText(context, "현재 일정이 등록되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                        else if("error".equals(response.trim())){ // 시스템 오류
                            Toast.makeText(context, "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                        }else {
                            final String[] resPonse_split = response.split("-");
                            switch (resPonse_split[3]) {
                                case "scheduleExist":// 조회 성공 시
                                    new AlertDialog.Builder(context).setCancelable(false)
                                        .setTitle("[공주대학교 네트워크 보안연구실]\n")
                                        .setMessage("상세정보\n"+"날짜: "+resPonse_split[0]+"\n"+"제목: "+resPonse_split[1]+"\n"+
                                                "일정: "+resPonse_split[2]+"\n")
                                        .setPositiveButton("정보 삭제", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 삭제 진행
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Manager_calendar_delete_Request(resPonse_split[0],resPonse_split[1]);
                                                            Thread.sleep(100); // 0.1 초 슬립
                                                            if(VolleyQueueSingleTon.manager_calendar_selectSharing != null){
                                                                // 일정 조회
                                                                VolleyQueueSingleTon.manager_calendar_selectSharing.setShouldCache(false);
                                                                VolleyQueueSingleTon.getInstance(context).addToRequestQueue(VolleyQueueSingleTon.manager_calendar_selectSharing);
                                                            }
                                                        } catch (InterruptedException e) {
                                                            System.err.println("ManagerCalendarAdapter InterruptedException error");
                                                        }
                                                    }
                                                }).start();
                                                dialog.dismiss();
                                            }
                                        }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, resPonse_split[0] + "의 일정", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }).show();
                                    break;
                                default:
                                    Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
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
                params.put("title", Title);
                params.put("date", Date);
                params.put("type", "scheduleShow");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }
}