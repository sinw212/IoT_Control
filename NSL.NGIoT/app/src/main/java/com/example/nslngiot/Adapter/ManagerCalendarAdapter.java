package com.example.nslngiot.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;

import java.util.ArrayList;

public class ManagerCalendarAdapter extends RecyclerView.Adapter<ManagerCalendarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerCalendarData> calendarData;

    // ManagerAddUser어댑터에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return calendarData.size();
    }

    public ManagerCalendarAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ManagerCalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view =LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_manager_calendar,viewGroup,false); // 뷰생성
        ManagerCalendarAdapter.ViewHolder viewHolder = new ManagerCalendarAdapter.ViewHolder(view);
        return  viewHolder;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(ManagerCalendarAdapter.ViewHolder holder, int position) {

        ManagerCalendarData item = calendarData.get(position); // 위치에 따른 아이템 반환

        holder.numText.setText(item.getNumber()); // ManagerCalendarData의 getNumber값을 numtext에 삽입
        holder.titleText.setText(item.getTitle()); // -
        holder.detailText.setText(item.getDetail()); // -

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView titleText;
        TextView detailText;

        public ViewHolder(View itemView) {
            super(itemView); // 입력 받은 값을 뷰홀더에 삽입
            numText = itemView.findViewById(R.id.manager_calendar_number);
            titleText = itemView.findViewById(R.id.manager_calendar_title);
            detailText = itemView.findViewById(R.id.manager_calendar_detail);

            // 아이템에 대한 이벤트 리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "c",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public ManagerCalendarAdapter (Activity activity, ArrayList<ManagerCalendarData> list) {
        this.calendarData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }
}