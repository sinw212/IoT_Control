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

import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class ManagerAddUserAdapter extends RecyclerView.Adapter<ManagerAddUserAdapter.ViewHolder> {

    private ArrayList<ManagerAddUserData> userdata;
    public Context context;
    private SparseBooleanArray SelectedItem = new SparseBooleanArray(0);


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView nameText;
        TextView idText;

        ViewHolder(View itemView) {
            super(itemView);

            numText = itemView.findViewById(R.id.manager_adduser_number);
            nameText = itemView.findViewById(R.id.manager_adduser_name);
            idText = itemView.findViewById(R.id.manager_adduser_id);

        }
    }


    public ManagerAddUserAdapter(ArrayList<ManagerAddUserData> list) {
        userdata = list;
    }


    @Override
    public ManagerAddUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_manager_useradd, parent, false);
        ManagerAddUserAdapter.ViewHolder vh = new ManagerAddUserAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ManagerAddUserAdapter.ViewHolder holder, final int position) {

        ManagerAddUserData item = userdata.get(position);

        holder.numText.setText(item.getNumber());
        holder.nameText.setText(item.getName());
        holder.idText.setText(item.getID());

        holder.itemView.setOnClickListener((new View.OnClickListener() {  //회원정보란 클릭시 수정 이벤트 발생
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_manager_adduser_editbox, null, false);
                builder.setView(view);
                Button ButtonSubmit = (Button) view.findViewById(R.id.btn_submit);//수정 버튼 클릭
                final EditText editTextName = (EditText) view.findViewById(R.id.et_name);
                final EditText editTextID = (EditText) view.findViewById(R.id.et_ID);

                editTextID.setText(userdata.get(position).getID());
                editTextName.setText(userdata.get(position).getName());

                if (isItemSelected(position)) {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }


                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strID = editTextID.getText().toString();
                        String strName = editTextName.getText().toString();
                        String strNumber = userdata.get(position).getNumber();

                        ManagerAddUserData ud = new ManagerAddUserData();

                        ud.setNumber(strNumber);
                        ud.setID(strID);
                        ud.setName(strName);

                        userdata.set(position, ud);

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
    public int getItemCount() {

        return userdata.size();
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
        ManagerAddUserData ud;

        for (int i = SelectedItem.size() - 1; i >= 0; i--) {
            position = SelectedItem.keyAt(i);
            userdata.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, userdata.size());
        }
        SelectedItem.clear();

        if (userdata.size() > 0) {//삭제 후 아이템이 남아있을 시 실행
            for (int i = 0; i < userdata.size(); i++) {//리스트 넘버링 갱신
                ud = new ManagerAddUserData();
                ud.setID(userdata.get(i).getID());
                ud.setName(userdata.get(i).getName());
                ud.setNumber(Integer.toString((i + 1)));
                userdata.set(i, ud);
                notifyItemChanged(i);
                System.out.println(ud + "\n" + ud.toString());

            }
        }
        return userdata.size();
    }
}