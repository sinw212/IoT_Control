package com.example.nslngiot.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.R;


import java.util.ArrayList;

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
    public void onBindViewHolder(ManagerAddUserAdapter.ViewHolder holder, int position) {

        ManagerAddUserData item = addUserData.get(position); // 위치에 따른 아이템 반환

        holder.numText.setText(item.getNumber()); // ManagerAddUserData의 getNumber값을 numtext에 삽입
        holder.nameText.setText(item.getName()); // -
        holder.idText.setText(item.getID()); // -

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

            // 아이템에 대한 이벤트 리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "c",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public ManagerAddUserAdapter (Activity activity, ArrayList<ManagerAddUserData> list) {
        this.addUserData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }
}