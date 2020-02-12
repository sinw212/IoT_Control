package com.example.nslngiot.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class ManagerMemberAdapter extends RecyclerView.Adapter<ManagerMemberAdapter.ViewHolder> {
    private ArrayList<ManagerMemberData> memberdata= null;

    public Context context;
    private SparseBooleanArray SelectedItem = new SparseBooleanArray(0);

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
    public void onBindViewHolder(final ManagerMemberAdapter.ViewHolder holder, final int position){
        ManagerMemberData item = memberdata.get(position);

        holder.numText.setText(item.getNumber());
        holder.nameText.setText(item.getName());
        holder.phoneText.setText(item.getPhone());
        holder.courseText.setText(item.getCourse());
        holder.groupText.setText(item.getGroup());

        holder.itemView.setOnClickListener((new View.OnClickListener() {  //회원정보란 클릭시 수정 이벤트 발생
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_manager_member_editbox, null, false);
                builder.setView(view);
                Button ButtonSubmit = (Button) view.findViewById(R.id.btn_submit_member);//수정 버튼 클릭
                final EditText editTextName = (EditText) view.findViewById(R.id.et_name);
                final EditText editTextPhone = (EditText) view.findViewById(R.id.et_phone);
                EditText editTextCourse = (EditText)view.findViewById(R.id.et_course);
                EditText editTextGroup = (EditText)view.findViewById(R.id.et_group);

                editTextPhone.setText(memberdata.get(position).getPhone());
                editTextName.setText(memberdata.get(position).getName());
                editTextCourse.setText(memberdata.get(position).getCourse());
                editTextGroup.setText(memberdata.get(position).getGroup());


                if (isItemSelected(position)) {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }


                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strPhone = editTextPhone.getText().toString();
                        String strName = editTextName.getText().toString();
                        String strNumber = memberdata.get(position).getNumber();
                        String strCourse = memberdata.get(position).getCourse();
                        String strGroup = memberdata.get(position).getGroup();

                        ManagerMemberData md = new ManagerMemberData();

                        md.setNumber(strNumber);
                        md.setPhone(strPhone);
                        md.setName(strName);
                        md.setGroup(strGroup);
                        md.setCourse(strCourse);


                        memberdata.set(position, md);

                        notifyItemChanged(position);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }));
        if (SelectedItem.get(position, false)) {
            holder.itemView.setBackgroundColor(Color.GRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        holder.itemView.setOnLongClickListener((new View.OnLongClickListener() {  //회원정보란 길게 클릭시 이벤트 발생
            public boolean onLongClick(View v) {

                toggleItemSelected(position);
                return false;
            }
        }));
    }

    @Override
    public int getItemCount(){
        return memberdata.size();
    }

    private void toggleItemSelected(int position) {
        if (SelectedItem.get(position, false) == true) {
            SelectedItem.delete((position));
            notifyItemChanged(position);
        } else {
            SelectedItem.put(position, true);
            notifyItemChanged(position);
        }
    }

    private boolean isItemSelected(int position) {
        return SelectedItem.get(position, false);
    }

    public int clearSelectedItem() {
        int position;
        ManagerMemberData md;

        for (int i = SelectedItem.size() - 1; i >= 0; i--) {
            position = SelectedItem.keyAt(i);
            memberdata.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, memberdata.size());
        }
        SelectedItem.clear();

        if (memberdata.size() > 0) {//삭제 후 아이템이 남아있을 시 실행
            for (int i = 0; i < memberdata.size(); i++) {//리스트 넘버링 갱신
                md = new ManagerMemberData();
                md.setPhone(memberdata.get(i).getPhone());
                md.setName(memberdata.get(i).getName());
                md.setCourse(memberdata.get(i).getCourse());
                md.setGroup(memberdata.get(i).getGroup());
                md.setNumber(Integer.toString((i + 1)));

                memberdata.set(i, md);
                notifyItemChanged(i);

            }
        }
        return memberdata.size();
    }
}