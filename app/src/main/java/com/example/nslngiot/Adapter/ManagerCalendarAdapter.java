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
        TextView detailText;

        ViewHolder(View itemView){
            super(itemView);

            numText = itemView.findViewById(R.id.manager_calendar_number);
            titleText = itemView.findViewById(R.id.manager_calendar_title);
            detailText = itemView.findViewById(R.id.manager_calendar_detail);
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

        ManagerCalendarData item = calendardata.get(position);

        holder.numText.setText(item.getNumber());
        holder.titleText.setText(item.getTitle());
        holder.detailText.setText(item.getDetail());

        holder.itemView.setOnClickListener((new View.OnClickListener() {  //일정 클릭시 수정 이벤트 발생
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_manager_calendar_editbox, null, false);
                builder.setView(view);
                Button ButtonSubmit = view.findViewById(R.id.btn_submit); //수정 버튼 클릭
                final EditText editTextTitle = view.findViewById(R.id.et_title);
                final EditText editTextDetail = view.findViewById(R.id.et_detail);

                editTextTitle.setText(calendardata.get(position).getTitle());
                editTextDetail.setText(calendardata.get(position).getTitle());

                if (isItemSelected(position)) {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }

                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strTitle = editTextTitle.getText().toString();
                        String strDetail = editTextDetail.getText().toString();
                        String strNumber = calendardata.get(position).getNumber();

                        ManagerCalendarData cd = new ManagerCalendarData();

                        cd.setNumber(strNumber);
                        cd.setTitle(strTitle);
                        cd.setDetail(strDetail);

                        calendardata.set(position, cd);

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

        holder.itemView.setOnLongClickListener((new View.OnLongClickListener() {  //일정 길게 클릭시 이벤트 발생
            public boolean onLongClick(View v) {
                toggleItemSelected(position);
                return false;
            }
        }));

    }

    @Override
    public int getItemCount(){

        return calendardata.size();
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
        ManagerCalendarData cd;

        for (int i = SelectedItem.size() - 1; i >= 0; i--) {
            position = SelectedItem.keyAt(i);
            calendardata.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, calendardata.size());
        }
        SelectedItem.clear();

        if (calendardata.size() > 0) { //삭제 후 아이템이 남아있을 시 실행
            for (int i = 0; i < calendardata.size(); i++) {//리스트 넘버링 갱신
                cd = new ManagerCalendarData();
                cd.setTitle(calendardata.get(i).getTitle());
                cd.setDetail(calendardata.get(i).getDetail());
                cd.setNumber(Integer.toString((i + 1)));
                calendardata.set(i, cd);
                notifyItemChanged(i);
                System.out.println(cd + "\n" + cd.toString());

            }
        }
        return calendardata.size();
    }
}