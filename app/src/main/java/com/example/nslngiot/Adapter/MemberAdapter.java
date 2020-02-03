package com.example.nslngiot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.MemberData;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private ArrayList<MemberData> memberdata= null;


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView numText;
        TextView nameText;
        TextView phoneText;
        TextView courseText;
        TextView groupText;

        ViewHolder(View itemView){
            super(itemView);

            numText = itemView.findViewById(R.id.member_num_list);
            nameText = itemView.findViewById(R.id.member_name_list);
            phoneText = itemView.findViewById(R.id.member_phone_list);
            courseText = itemView.findViewById(R.id.member_course_list);
            groupText = itemView.findViewById(R.id.member_group_list);
        }
    }


    public MemberAdapter(ArrayList<MemberData> list){
        memberdata = list;
    }


    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_manager_member, parent, false);
        MemberAdapter.ViewHolder vh = new MemberAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MemberAdapter.ViewHolder holder, int position){
        MemberData item = memberdata.get(position);

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