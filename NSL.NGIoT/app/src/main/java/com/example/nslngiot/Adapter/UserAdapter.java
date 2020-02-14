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

import com.example.nslngiot.Data.UserData;
import com.example.nslngiot.ManagerFragment.AddUserFragment;
import com.example.nslngiot.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<UserData> userdata;
    public Context context;
    private SparseBooleanArray SelectedItem = new SparseBooleanArray(0);
    public String Name;
    public String Id;
    public String Email;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView nameText;
        TextView idText;

        ViewHolder(View itemView) {
            super(itemView);

            numText = itemView.findViewById(R.id.tv_Number_addusert);
            nameText = itemView.findViewById(R.id.tv_Name_adduser);
            idText = itemView.findViewById(R.id.tv_ID_adduser);

        }
    }


    public UserAdapter(ArrayList<UserData> list) {
        userdata = list;
    }


    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_manager_useradd, parent, false);
        UserAdapter.ViewHolder vh = new UserAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, final int position) {

        UserData item = userdata.get(position);

        holder.numText.setText(item.getNumber());
        holder.nameText.setText(item.getName());
        holder.idText.setText(item.getID());

        holder.itemView.setOnClickListener((new View.OnClickListener() {  //회원정보란 클릭시 수정 이벤트 발생
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_adduser, null, false);
                builder.setView(view);

                final TextView TextName = (TextView) view.findViewById(R.id.et_name);
                final TextView TextID = (TextView) view.findViewById(R.id.et_ID);
                TextView TextEmail = (TextView) view.findViewById(R.id.et_email);

                TextName.setText(Name);
                TextID.setText(Id);
                TextEmail.setText(Email);
                /*TextID.setText(userdata.get(position).getID());
                TextName.setText(userdata.get(position).getName());
                */

                if (isItemSelected(position)) {//색상 변경
                    holder.itemView.setBackgroundColor(Color.GRAY);
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }


                final AlertDialog dialog = builder.create();
                /*ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strID = editTextID.getText().toString();
                        String strName = editTextName.getText().toString();
                        String strNumber = userdata.get(position).getNumber();

                        UserData ud = new UserData();

                        ud.setNumber(strNumber);
                        ud.setID(strID);
                        ud.setName(strName);

                        userdata.set(position, ud);

                        notifyItemChanged(position);
                        dialog.dismiss();
                    }
                });*/
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

    public void clearSelectedItem() {
        int position;
        UserData ud;

        for (int i = SelectedItem.size() - 1; i >= 0; i--) {
            position = SelectedItem.keyAt(i);
            Name = (userdata.get(position).getName());
            Id = (userdata.get(position).getID());
            userdata.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, userdata.size());
        }
        SelectedItem.clear();

        if (userdata.size() > 0) {//삭제 후 아이템이 남아있을 시 실행
            for (int i = 0; i < userdata.size(); i++) {//리스트 넘버링 갱신
                ud = new UserData();
                ud.setID(userdata.get(i).getID());
                ud.setName(userdata.get(i).getName());
                ud.setNumber(Integer.toString((i + 1)));
                userdata.set(i, ud);
                notifyItemChanged(i);
                System.out.println(ud + "\n" + ud.toString());

            }
        }

    }

    public void setData(String name, String id, String email) {
        this.Name = name;
        this.Id = id;
        this.Email = email;
    }

    public void clear() {
        int size =userdata.size()-1;
        for (int i = size; i >= 0; i--) {
            userdata.remove(i);
        }
        notifyDataSetChanged();
    }

}

