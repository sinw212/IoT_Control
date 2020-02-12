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

import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.R;

import java.util.ArrayList;


public class ManagerCalendarAdapter extends RecyclerView.Adapter<ManagerCalendarAdapter.ViewHolder> {

    private ArrayList<ManagerCalendarData> calendardata;
    public Context context;
    private SparseBooleanArray SelectedItem = new SparseBooleanArray(0);

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView numText;
        TextView titleText;

        ViewHolder(View itemView){
            super(itemView);

            numText = itemView.findViewById(R.id.manager_calendar_number);
            titleText = itemView.findViewById(R.id.manager_calendar_title);
        }
    }


    public ManagerCalendarAdapter(ArrayList<ManagerCalendarData> list){
        calendardata = list;
    }


    @Override
    public ManagerCalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_manager_calendar, parent, false);
        ManagerCalendarAdapter.ViewHolder vh = new ManagerCalendarAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ManagerCalendarAdapter.ViewHolder holder, final int position) {

//        ManagerCalendarData item = calendardata.get(position);

//        holder.numText.setText(item.getNumber());
//        holder.titleText.setText(item.getTitle());

//        holder.itemView.setOnClickListener((new View.OnClickListener(){  //일정 클릭시 이벤트 발생
//            public void onClick(View v){
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                View view = LayoutInflater.from(context).inflate(R.layout.editbox_manager_adduser,null,false);
//                builder.setView(view);
//                Button ButtonSubmit = (Button)view.findViewById(R.id.btn_submit);
//                final EditText editTextName = (EditText)view.findViewById(R.id.et_name);
//                final EditText editTextID = (EditText)view.findViewById(R.id.et_ID);

//                editTextID.setText(userdata.get(position).getID());
//                editTextName.setText(userdata.get(position).getName());

//                final AlertDialog dialog =builder.create();
//                ButtonSubmit.setOnClickListener(new View.OnClickListener(){
//                    public void onClick(View v){
//                        String strID = editTextID.getText().toString();
//                        String strName = editTextName.getText().toString();
//                        String strNumber = userdata.get(position).getNumber().toString();
//
//                        ManagerAddUserData ud = new ManagerAddUserData();
//
//                        ud.setNumber(strNumber);
//                        ud.setID(strID);
//                        ud.setName(strName);
//
//                        userdata.set(position, ud);

//                        notifyItemChanged(position);
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//            }
//        }));

//        holder.itemView.setOnLongClickListener((new View.OnLongClickListener(){  //일정 길게 클릭시 이벤트 발생
//            public boolean onLongClick(View v){
//                if(isItemSelected(position)){
//                    holder.itemView.setBackgroundColor(Color.GRAY);
//                }else{
//                    holder.itemView.setBackgroundColor(Color.WHITE);
//                }
//                toggleItemSelected(position);
//                /*if(SelectedItem.get(position,false)){
//                SelectedItem.put(position, false);
//                v.setBackgroundColor(Color.WHITE);
//              }else{
//                  SelectedItem.put(position, true);
//                  v.setBackgroundColor(Color.GRAY);
//              }*/
//               /* userdata.remove(position); // 삭제
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position, userdata.size());*/
//                return false;
//            }
//        }));

    }

    @Override
    public int getItemCount(){
//
        return calendardata.size();
    }

//    private void toggleItemSelected(int position){
//        if(SelectedItem.get(position, false)==true){
//            SelectedItem.delete((position));
//            notifyItemChanged(position);
//        }else{
//            SelectedItem.put(position,true);
//            notifyItemChanged(position);
//        }
//    }
//
//    private boolean isItemSelected(int position){
//        return SelectedItem.get(position, false);
//    }
//
//    public void clearSelectedItem(){
//        int position;
//
//        for(int i =0; i<SelectedItem.size();i++){
//            position = SelectedItem.keyAt(i);
//            userdata.remove(position);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, userdata.size());
//        }
//        SelectedItem.clear();
//    }
}