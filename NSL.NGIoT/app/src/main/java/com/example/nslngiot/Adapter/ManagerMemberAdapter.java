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
import com.example.nslngiot.Data.ManagerMemberData;

import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerMemberAdapter extends RecyclerView.Adapter<ManagerMemberAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerMemberData> memberData;

    // ManagerMember어댑터에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return memberData.size();
    }

    public ManagerMemberAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ManagerMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view =LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_manager_member,viewGroup,false); // 뷰생성
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public  void onBindViewHolder(ManagerMemberAdapter.ViewHolder holder , final int position) {

        final ManagerMemberData item = memberData.get(position); // 위치에 따른 아이템 반환

        holder.numText.setText(item.getNumber());// ManagerMemberData의 getNumber값을 numtext에 삽입
        holder.nameText.setText(item.getName());
        holder.phoneText.setText(item.getPhone());
        holder.courseText.setText(item.getCourse());
        holder.groupText.setText(item.getGroup());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle("[공주대학교 네트워크 보안연구실]\n"+item.getName()+"님")
                        .setMessage("상세정보\n\n"+"이름: "+item.getName()+"\n"+"전화번호: "+item.getPhone()+"\n"+
                                "교육과정: "+item.getCourse()+"\n"+"현 소속: "+item.getGroup()+"\n\n"
                                +item.getCourse()+"과정의 "+item.getName()+"님\n")
                        .setPositiveButton("정보 삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 삭제 진행
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Manager_member_delete_Request(item.getName(),item.getPhone(),item.getCourse(),item.getGroup());
                                            Thread.sleep(100); // 0.1 초 슬립
                                            if(VolleyQueueSingleTon.manager_member_selectSharing != null){
                                                // 연구실 인원 정보 조회
                                                VolleyQueueSingleTon.manager_member_selectSharing.setShouldCache(false);
                                                VolleyQueueSingleTon.getInstance(context).addToRequestQueue(VolleyQueueSingleTon.manager_member_selectSharing);
                                            }
                                        } catch (InterruptedException e) {
                                            System.err.println("ManagerMemberAdapter InterruptedException error");
                                        }
                                    }
                                }).start();
                                dialog.dismiss();
                            }
                        }).setNeutralButton("정보 수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "수정아직구현안함"+" "+item.getCourse()+"과정의 "+item.getName(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, item.getCourse()+"과정의 "+item.getName()+"님 닫기", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView nameText;
        TextView phoneText;
        TextView courseText;
        TextView groupText;

        public ViewHolder(View itemView) {
            super(itemView); // 입력 받은 값을 뷰홀더에 삽입
            numText = itemView.findViewById(R.id.manager_member_number);
            nameText = itemView.findViewById(R.id.manager_member_name);
            phoneText = itemView.findViewById(R.id.manager_member_phone);
            courseText =itemView.findViewById(R.id.manager_member_course);
            groupText=itemView.findViewById(R.id.manager_member_group);

        }
    }

    public ManagerMemberAdapter (Activity activity, ArrayList<ManagerMemberData> list) {
        this.memberData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }

    // 회원정보 삭제
    private void Manager_member_delete_Request(final String name, final String phone, final String course, final  String group) {
        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/User.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("진입",response);
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
                params.put("phone", phone);
                params.put("dept", course);
                params.put("team", group);
                params.put("type", "memDelete"); // error/nonTypeRequest

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
    }

//    // 회원정보 수정
//    private void Manager_member_update_Request(final String name, final String phone, final String course, final  String group) {
//
//        final StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/User.jsp");
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST, String.valueOf(url),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        switch (response) {
//        case "memModifySuccess": // 수정 성공 시
//            Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
//            break;
//        case "memNotExist": // 수정할 내용이 없을 시
//            Toast.makeText(getActivity(), "삭제 할 내용이 없습니다.", Toast.LENGTH_LONG).show();
//            break;
//        case "error"://오류
//            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
//            break;
//        default:
//            Toast.makeText(getActivity(), "default Error", Toast.LENGTH_LONG).show();
//            break;
//    }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("name", name);
//                params.put("phone", phone);
//                params.put("dept", course);
//                params.put("team", group);
//                params.put("type", "memDelete");
//

//      params.put("b_name", m_ma.b_name);
//                        params.put("b_phone", m_ma.b_phone);
//                        params.put("name", m_ma.a_name);
//                        params.put("phone", m_ma.a_phone);
//                        params.put("detp", m_ma.a_detp);
//                        params.put("team", m_ma.a_team);
//                        params.put("type", "memModify");
//                return params;
//            }
//        };
//
//        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
//        // 항상 새로운 데이터를 위해 false
//        stringRequest.setShouldCache(false);
//        VolleyQueueSingleTon.getInstance(context).addToRequestQueue(stringRequest);
//    }
}