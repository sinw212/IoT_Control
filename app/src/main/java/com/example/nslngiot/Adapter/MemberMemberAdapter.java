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
import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.R;
import java.util.ArrayList;

public class MemberMemberAdapter extends RecyclerView.Adapter<MemberMemberAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerMemberData> memberData;

    // ManagerMember어댑터에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return memberData.size();
    }

    public MemberMemberAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MemberMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_manager_member, viewGroup, false); // 뷰생성
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(MemberMemberAdapter.ViewHolder holder, final int position) {

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
                        .setTitle("[공주대학교 네트워크 보안연구실]\n" + item.getName()+ "님")
                        .setMessage("상세정보\n\n" + "이름: " + item.getName() + "\n" + "전화번호: " + item.getPhone()+ "\n" +
                                "교육과정: " + item.getCourse() + "\n" + "현 소속: " + item.getGroup() + "\n\n"
                                + item.getCourse() + "과정의 " + item.getName() + "님\n")
                        .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, item.getCourse() + "과정의 " + item.getName() + "님 닫기", Toast.LENGTH_SHORT).show();
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
            courseText = itemView.findViewById(R.id.manager_member_course);
            groupText = itemView.findViewById(R.id.manager_member_group);

        }
    }

    public MemberMemberAdapter(Activity activity, ArrayList<ManagerMemberData> list) {
        this.memberData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }
}