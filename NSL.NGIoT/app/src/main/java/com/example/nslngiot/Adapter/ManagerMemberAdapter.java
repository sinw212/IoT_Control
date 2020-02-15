package com.example.nslngiot.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.Data.ManagerMemberData;

import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class ManagerMemberAdapter extends RecyclerView.Adapter<ManagerMemberAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerMemberData> memberData;



    // ManagerMember어댑터에서 관리하는 아이템의 개수를 반환
    // ManagerAddUser어댑터에서 관리하는 아이템의 개수를 반환
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
        ManagerMemberAdapter.ViewHolder viewHolder = new ManagerMemberAdapter.ViewHolder(view);
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
                        .setTitle(item.getName()+" "+item.getName()+"님")
                        .setMessage(item.getCourse()+"과정의"+item.getName()+"님을 삭제/수정 하시겠습니까?\n")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 삭제 진행
//                                addUser_delete_Request(item.getName(),item.getID());

//                                if(VolleyQueueSingleTon.addUserselectSingleTon != null){
//                                    // 인원 현황 정보 조회 진행
//                                    VolleyQueueSingleTon.addUserselectSingleTon.setShouldCache(false);
//                                    VolleyQueueSingleTon.getInstance(context).addToRequestQueue(VolleyQueueSingleTon.addUserselectSingleTon);
//                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소"+" "+item.getCourse()+"과정의 "+item.getName(), Toast.LENGTH_SHORT).show();
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
}