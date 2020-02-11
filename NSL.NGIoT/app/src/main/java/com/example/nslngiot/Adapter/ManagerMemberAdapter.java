package com.example.nslngiot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class ManagerMemberAdapter extends RecyclerView.Adapter<ManagerMemberAdapter.ViewHolder> {
    private ArrayList<ManagerMemberData> memberdata= null;


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView numText;
        TextView nameText;
        TextView phoneText;
        TextView courseText;
        TextView groupText;

        ViewHolder(View itemView){
            super(itemView);

            numText = itemView.findViewById(R.id.manager_member_number);
            nameText = itemView.findViewById(R.id.manager_member_name);
            phoneText = itemView.findViewById(R.id.manager_member_phone);
            courseText = itemView.findViewById(R.id.manager_member_course);
            groupText = itemView.findViewById(R.id.manager_member_group);
        }
    }


    public ManagerMemberAdapter(ArrayList<ManagerMemberData> list){
        memberdata = list;
    }


    @Override
    public ManagerMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_manager_member, parent, false);
        ManagerMemberAdapter.ViewHolder vh = new ManagerMemberAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ManagerMemberAdapter.ViewHolder holder, int position){
        ManagerMemberData item = memberdata.get(position);

        holder.numText.setText(item.getNumber());
        holder.nameText.setText(item.getName());
        holder.phoneText.setText(item.getPhone());
        holder.courseText.setText(item.getCourse());
        holder.groupText.setText(item.getGroup());
    }

    @Override
    public int getItemCount(){
        return memberdata.size();
    }
}