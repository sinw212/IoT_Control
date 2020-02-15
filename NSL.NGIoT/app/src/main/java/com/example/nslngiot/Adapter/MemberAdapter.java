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

import com.example.nslngiot.Data.MemberData;

import com.example.nslngiot.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private ArrayList<MemberData> memberdata = null;
    public String Name;
    public String Phone;
    public String Detp;
    public String Team;
    public String b_name;
    public String b_phone;
    public String a_name;
    public String a_phone;
    public String a_detp;
    public String a_team;

    public Context context;
    private SparseBooleanArray SelectedItem = new SparseBooleanArray(0);

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView nameText;
        TextView phoneText;
        TextView courseText;
        TextView groupText;

        ViewHolder(View itemView) {
            super(itemView);

            numText = itemView.findViewById(R.id.manager_member_number);
            nameText = itemView.findViewById(R.id.manager_member_number);
            phoneText = itemView.findViewById(R.id.manager_member_phone);
            courseText = itemView.findViewById(R.id.manager_member_course);
            groupText = itemView.findViewById(R.id.manager_member_group);
        }
    }


    public MemberAdapter(ArrayList<MemberData> list) {
        memberdata = list;
    }


    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_manager_member, parent, false);
        MemberAdapter.ViewHolder vh = new MemberAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MemberAdapter.ViewHolder holder, final int position) {
        MemberData item = memberdata.get(position);

        holder.numText.setText(item.getNumber());
        holder.nameText.setText(item.getName());
        holder.phoneText.setText(item.getPhone());
        holder.courseText.setText(item.getCourse());
        holder.groupText.setText(item.getGroup());


        holder.itemView.setOnClickListener((new View.OnClickListener() {  //회원정보란 클릭시 수정 이벤트 발생
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_member, null, false);
                builder.setView(view);
                Button ButtonSubmit = view.findViewById(R.id.btn_submit_member);//수정 버튼 클릭

                final EditText editTextName = view.findViewById(R.id.member_name);
                final EditText editTextPhone = view.findViewById(R.id.member_phone);
                EditText editTextCourse = view.findViewById(R.id.member_course);
                EditText editTextGroup = view.findViewById(R.id.member_group);
                b_name = editTextName.getText().toString();
                b_phone = editTextPhone.getText().toString();

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
                        String strCourse = memberdata.get(position).getCourse();
                        String strGroup = memberdata.get(position).getGroup();

                        a_name = strName;
                        a_phone = strPhone;
                        a_detp = strCourse;
                        a_team = strGroup;
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
    public int getItemCount() {
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
        MemberData md;

        for (int i = SelectedItem.size() - 1; i >= 0; i--) {
            position = SelectedItem.keyAt(i);

            Name = (memberdata.get(position).getName());
            Phone = (memberdata.get(position).getPhone());
            Detp = (memberdata.get(position).getCourse());
            Team = (memberdata.get(position).getGroup());

            memberdata.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, memberdata.size());
        }
        SelectedItem.clear();

        if (memberdata.size() > 0) {//삭제 후 아이템이 남아있을 시 실행
            for (int i = 0; i < memberdata.size(); i++) {//리스트 넘버링 갱신
                md = new MemberData();
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

    public void setData(String name, String phone, String detp, String team) {
        this.Name = name;
        this.Phone = phone;
        this.Detp = detp;
        this.Team = team;
    }

    public void clear() {
        int size =memberdata.size()-1;
        for (int i = size; i >= 0; i--) {
            memberdata.remove(i);
        }
        notifyDataSetChanged();
    }

}