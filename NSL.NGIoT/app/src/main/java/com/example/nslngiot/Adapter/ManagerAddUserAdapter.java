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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerAddUserAdapter extends RecyclerView.Adapter<ManagerAddUserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerAddUserData> addUserData;

    // ManagerAddUser어댑터에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return addUserData.size();
    }

    public ManagerAddUserAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ManagerAddUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view =LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_manager_useradd,viewGroup,false); // 뷰생성
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public  void onBindViewHolder(ManagerAddUserAdapter.ViewHolder holder , final int position) {

        final ManagerAddUserData item = addUserData.get(position); // 위치에 따른 아이템 반환

        holder.numText.setText(item.getNumber()); // ManagerAddUserData의 getNumber값을 numtext에 삽입
        holder.nameText.setText(item.getName()); // -
        holder.idText.setText(item.getID()); // -

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle(item.getID()+" "+item.getName()+"님")
                        .setMessage(item.getID()+" "+item.getName()+"님을 삭제/수정 하시겠습니까?\n")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 삭제 진행
                                addUser_delete_Request(item.getName(),item.getID());

                                if(VolleyQueueSingleTon.addUserselectSingleTon != null){
                                    // 인원 현황 정보 조회 진행
                                    VolleyQueueSingleTon.addUserselectSingleTon.setShouldCache(false);
                                    VolleyQueueSingleTon.getInstance(context).addToRequestQueue(VolleyQueueSingleTon.addUserselectSingleTon);
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소"+" "+item.getID()+" "+item.getName(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
            } //수정? user테이블에 수정하고자하는 데이터 전송 / 삭제 누르면 삭제  취소 수정 삭제
        });
        //인원현황 취소 수정 삭제
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView nameText;
        TextView idText;
        public ViewHolder(View itemView) {
            super(itemView); // 입력 받은 값을 뷰홀더에 삽입
            numText = itemView.findViewById(R.id.manager_adduser_number);
            nameText = itemView.findViewById(R.id.manager_adduser_name);
            idText = itemView.findViewById(R.id.manager_adduser_id);
        }
    }

    public ManagerAddUserAdapter (Activity activity, ArrayList<ManagerAddUserData> list) {
        this.addUserData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }


    // 회원정보 삭제
    public void addUser_delete_Request(final String name, final String id) {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/User.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "deleteAllSuccess":// 삭제했을 시
                                Toast.makeText(context, "회원 정보 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "addUserDataNotExist":// 삭제 실패했을 시
                                Toast.makeText(context, "회원 정보 삭제를 실패했습니다.", Toast.LENGTH_SHORT).show();
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
                params.put("name", name);
                params.put("id", id);
                params.put("type", "addUser_Delete");
                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }

}