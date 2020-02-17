package com.example.nslngiot.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.Data.ManagerMemberData;
import com.example.nslngiot.R;
import java.util.ArrayList;

public class MemberCalendarAdapter extends RecyclerView.Adapter<MemberCalendarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ManagerCalendarData> calendarData;

    // ManagerCalendar어댑터에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return calendarData.size();
    }

    public MemberCalendarAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MemberCalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_manager_calendar, viewGroup, false); // 뷰생성
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(MemberCalendarAdapter.ViewHolder holder, final int position) {

        final ManagerCalendarData item = calendarData.get(position); // 위치에 따른 아이템 반환

        holder.numText.setText(item.getNumber());// ManagerCalendarData의 getNumber값을 numtext에 삽입
        holder.dateText.setText(item.getDate());
        holder.titleText.setText(item.getTitle());
        holder.detailText.setText(item.getDetail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle("[공주대학교 네트워크 보안연구실]\n")
                        .setMessage("상세정보\n"+"날짜: "+item.getDate()+"\n"+"제목: "+item.getTitle()+"\n"+
                                "일정: "+item.getDetail()+"\n")
                        .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, item.getDate()+"의 일정 "+item.getTitle()+" 닫기", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView dateText;
        TextView titleText;
        TextView detailText;

        public ViewHolder(View itemView) {
            super(itemView); // 입력 받은 값을 뷰홀더에 삽입
            numText = itemView.findViewById(R.id.manager_calendar_number);
//            dateText = itemView.findViewById(R.id.manager_calendar_date);
            titleText = itemView.findViewById(R.id.manager_calendar_title);
            detailText = itemView.findViewById(R.id.manager_calendar_detail);
        }
    }

    public MemberCalendarAdapter(Activity activity, ArrayList<ManagerCalendarData> list) {
        this.calendarData = list; // 처리하고자하는 아이템 리스트
        this.context = activity; // 보여지는 액티비티
    }
}