package com.example.nslngiot.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberCalendarAdapter extends RecyclerView.Adapter<MemberCalendarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerCalendarData> calendarData;

    // ManagerCalendar어댑터에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return calendarData.size();
    }

    public MemberCalendarAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MemberCalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_manager_calendar, viewGroup, false); // 뷰생성
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(MemberCalendarAdapter.ViewHolder holder, final int position) {

        final ManagerCalendarData item = calendarData.get(position); // 위치에 따른 아이템 반환

        holder.numText.setText(item.getNumber());// ManagerCalendarData의 getNumber값을 numtext에 삽입
        holder.titleText.setText(item.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            String date = VolleyQueueSingleTon.member_DATE;

            @Override
            public void onClick(View v) {
                calendar_select_Request(date, item.getTitle());
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

    public MemberCalendarAdapter(Activity activity, ArrayList<ManagerCalendarData> list) {
        this.calendarData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }

    // 연구실 일정 정보 상세 조회
    private void calendar_select_Request(final String Date , final String Title){
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 암호화된 대칭키를 키스토어의 개인키로 복호화
                        char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                        // 복호화된 대칭키를 이용하여 암호화된 데이터를 복호화 하여 진행
                        response = AES.aesDecryption(response.toCharArray(),decryptAESkey);

                        java.util.Arrays.fill(decryptAESkey,(char)0x20);
                        if("error".equals(response.trim())) // 시스템 오류
                            Toast.makeText(context, "시스템 오류입니다.", Toast.LENGTH_SHORT).show();
                        else {
                            final String[] resPonse_split = response.split("-");
                            if ("scheduleExist".equals(resPonse_split[3])) { // 조회 성공 시
                                new AlertDialog.Builder(context)
                                        .setCancelable(false)
                                        .setTitle("[공주대학교 네트워크 보안연구실]\n")
                                        .setMessage("상세정보\n\n" + "날짜: " + resPonse_split[0] + "\n" + "제목: " + resPonse_split[1] + "\n" +
                                                "일정: " + resPonse_split[2] + "\n")
                                        .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                            else
                                Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                char[] decryptAESkey = KEYSTORE.keyStore_Decryption(AES.secretKEY);

                params.put("securitykey", RSA.rsaEncryption(decryptAESkey,RSA.serverPublicKey.toCharArray()));
                params.put("type",AES.aesEncryption( "scheduleShow".toCharArray(),decryptAESkey));
                params.put("date",AES.aesEncryption(Date.toCharArray(),decryptAESkey));
                params.put("title",AES.aesEncryption(Title.toCharArray(),decryptAESkey));

                java.util.Arrays.fill(decryptAESkey,(char)0x20);
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }
}