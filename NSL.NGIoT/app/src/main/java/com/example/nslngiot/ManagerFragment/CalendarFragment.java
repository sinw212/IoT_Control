package com.example.nslngiot.ManagerFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nslngiot.R;

public class CalendarFragment extends Fragment {
    View view;
//    long mNow;
//    Date mDate;
//
//    SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");
//
//    TextView date;
//    ImageButton btn_calendar;
//    ListView listview;
//    Button btn_add;
//
//    String strDate;
//
//    String date_for_data = "";
//
//    Calendar c;
//    int nYear, nMon, nDay;
//    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manager_calendar,container,false);

        // 오늘 날짜 표현
//        date = (TextView) view.findViewById(R.id.textview_date);
//        date.setText(getTime(mFormat));

//        btn_calendar = view.findViewById(R.id.btn_calender);
//        btn_add = view.findViewById(R.id.btn_add);

        // Calendar
        //DatePicker Listener
//        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                strDate = String.valueOf(year) + "년 ";
//
//                if (monthOfYear + 1 > 0 && monthOfYear + 1 < 10)
//                    strDate += "0" + String.valueOf(monthOfYear + 1) + "월 ";
//                else
//                    strDate += String.valueOf(monthOfYear + 1) + "월 ";
//
//                if (dayOfMonth > 0 && dayOfMonth < 10)
//                    strDate += "0" + String.valueOf(dayOfMonth) + "일";
//                else
//                    strDate += String.valueOf(dayOfMonth) + "일";
//
//                date.setText(strDate);
//
//                date_for_data = "";
//                date_for_data += String.valueOf(year);
//
//                if(monthOfYear+1 > 0 && monthOfYear+1 < 10)
//                    date_for_data += "0"+String.valueOf(monthOfYear+1);
//                else
//                    date_for_data += String.valueOf(monthOfYear+1);
//
//                if(dayOfMonth > 0 && dayOfMonth < 10)
//                    date_for_data += "0"+String.valueOf(dayOfMonth);
//                else
//                    date_for_data += String.valueOf(dayOfMonth);
//            }
//        };

//        c = Calendar.getInstance();
//        nYear = c.get(Calendar.YEAR);
//        nMon = c.get(Calendar.MONTH);
//        nDay = c.get(Calendar.DAY_OF_MONTH);

//        btn_calendar.setOnClickListener(new ImageButton.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                DatePickerDialog oDialog = new DatePickerDialog(getContext(),
//                        mDateSetListener, nYear, nMon, nDay);
//
//                oDialog.show();
//            }
//        });

//        btn_add.setOnClickListener(new Button.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               getActivity()로 MainAvtivity의 replaceFragment를 불러옴
//               ((MainManagerActivity)getActivity()).replaceFragment(CalendarAddFragment.newInstance());
//           }
//        });

        return view;
    }

//    public String getTime(SimpleDateFormat Format) {
//        mNow = System.currentTimeMillis();
//        mDate = new Date(mNow);
//        return Format.format(mDate);
//    }
}
