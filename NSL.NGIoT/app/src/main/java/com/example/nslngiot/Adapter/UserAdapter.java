package com.example.nslngiot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.UserData;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<UserData> userdata = null;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView numText;
        TextView nameText;
        TextView idText;

        ViewHolder(View itemView){
            super(itemView);

            numText = itemView.findViewById(R.id.NumberText);
            nameText = itemView.findViewById(R.id.NameText);
            idText = itemView.findViewById(R.id.IDText);
        }
    }

    public UserAdapter(ArrayList<UserData> list){
        userdata = list;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_manager_useradd, parent, false);
        UserAdapter.ViewHolder vh = new UserAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position){
        UserData item = userdata.get(position);

        holder.numText.setText(item.getNumber());
        holder.nameText.setText(item.getName());
        holder.idText.setText(item.getID());
    }

    @Override
    public int getItemCount(){
        return userdata.size();
    }
}