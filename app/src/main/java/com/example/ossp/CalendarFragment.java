package com.example.ossp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class CalendarFragment extends Fragment {

    final String TAG = "calendar test";
    CompactCalendarView compactCalendarView;
    private Date selectedDate = null;
    private AlertDialog alertDialog;
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy년 MM월", Locale.KOREA);
    private SimpleDateFormat dateFormatForKor = new SimpleDateFormat("M월 d일", Locale.KOREA);
    TextView startTime, endTime, count, countTextView, statCountTextView, hourTextView, statHourTextView;
    ImageView soju1, soju2, soju3, soju4, soju5, sojuH;
    ImageView[] sojus;

    DBHelper helper;
    SQLiteDatabase db;

    float pickCount = 0;

    // 시간, 량을 선택했는지 확인 -> 오류 발생 방지
    private boolean checkselst = false, checkseled = false, checkselct = false;

    private DrunkEvent drunkEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        compactCalendarView = (CompactCalendarView) v.findViewById(R.id.compactcalendar_view);

        // 데이터베이스 생성 및 초기화
        helper = new DBHelper(getContext(), "mydb.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        String sql = "select * from mytable;";
        Cursor c = db.rawQuery(sql, null);
        StringTokenizer st;

        while(c.moveToNext()){
            String tmpDate = c.getString(c.getColumnIndex("drunkDate"));
            String tmpStart = c.getString(c.getColumnIndex("drunkStart"));
            String tmpEnd = c.getString(c.getColumnIndex("drunkEnd"));
            String tmpCount = c.getString(c.getColumnIndex("drunkCount"));

            st = new StringTokenizer(tmpStart, ":");
            int startHour = Integer.parseInt(st.nextToken());
            int startMin = Integer.parseInt(st.nextToken());

            st = new StringTokenizer(tmpEnd, ":");
            int endHour = Integer.parseInt(st.nextToken());
            int endMin = Integer.parseInt(st.nextToken());

            float count = Float.parseFloat(tmpCount);

            DrunkEvent tmpDrunk = new DrunkEvent(startHour, startMin, endHour, endMin, count);

            // 이벤트 등록하는 부분
            Date trans_date = null;
            try {
                trans_date = dateFormatForDisplaying.parse(tmpDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long time = trans_date.getTime();

            Event ev = new Event(Color.GREEN, time, tmpDrunk);
            compactCalendarView.addEvent(ev);
        }

        // TextView
        TextView textView_month = (TextView) v.findViewById(R.id.textView_month);
        TextView clickedDateTextView = (TextView) v.findViewById(R.id.clickedDateTextView);
        countTextView = (TextView) v.findViewById(R.id.countTextView);
        statCountTextView = (TextView) v.findViewById(R.id.statCountTextView);
        hourTextView = (TextView) v.findViewById(R.id.hourTextView);
        statHourTextView = (TextView) v.findViewById(R.id.statHourTextView);

        // ImageView
        soju1 = v.findViewById(R.id.soju1);
        soju2 = v.findViewById(R.id.soju2);
        soju3 = v.findViewById(R.id.soju3);
        soju4 = v.findViewById(R.id.soju4);
        soju5 = v.findViewById(R.id.soju5);
        sojuH = v.findViewById(R.id.sojuHalf);

        sojus = new ImageView[]{soju1, soju2, soju3, soju4, soju5, sojuH};

        textView_month.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        if(selectedDate == null)    selectedDate = new Date();

        // NumberPicker 초기화하기
        initDialog();

        // 초기 오늘 날짜로 지정하기
        String tmpDate = dateFormatForKor.format(new Date());
        clickedDateTextView.setText(tmpDate);
        loadEvent(new Date());

        // 이벤트를 추가하는 버튼
        Button event_addBtn = (Button) v.findViewById(R.id.okBtn) ;
        event_addBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택하지 않았다면 오늘 날짜를 선택하는 것으로 설정

                drunkEvent = new DrunkEvent();

                alertDialog.show();
            }
        });

        Button button_remove_events = (Button) v.findViewById(R.id.cancleBtn) ;
        button_remove_events.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                compactCalendarView.removeEvents(selectedDate);
                String delDate = transFormat(selectedDate);
                String sql3 = "DELETE FROM mytable WHERE drunkDate='" + delDate + "';";
                db.execSQL(sql3);
            }
        });


        /*
        이벤트 가져오는 부분 삭제했다고 기술하기

        Button button_get_event = (Button) v.findViewById(R.id.button_get_event) ;
        button_get_event.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date dateyymm = compactCalendarView.getFirstDayOfCurrentMonth();
                String yyymm = dateFormatForMonth2.format(dateyymm);

                String date = yyymm + "-01"; //"2021-11-01";

                Date trans_date = null;
                try {
                    trans_date = dateFormatForDisplaying.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long time = trans_date.getTime();
                List<Event> events = compactCalendarView.getEvents(time);

                String info = "";
                if (events.size() > 0)
                {
                    info = events.get(0).getData().toString();
                }

                textView_result2.setText("이벤트 이름 : " + info);
            }
        });
        */

        // 캘린더뷰 데이 클릭 이벤트
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                resetImages();
                Log.d(TAG, "onDayClick: ");
                loadEvent(dateClicked);

                SimpleDateFormat transFormat = new SimpleDateFormat("M월 d일");
                String date1 = transFormat.format(selectedDate);
                clickedDateTextView.setText(date1);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView_month.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });
        return v;
    }


    void loadEvent(Date d) {
        selectedDate = d;
        List<Event> events = compactCalendarView.getEvents(selectedDate);
        int totalH, totalM;

        if (events.size() > 0) {
            DrunkEvent getDrunk = (DrunkEvent) events.get(0).getData();
            float showCount = getDrunk.getCount();
            countTextView.setText("이 날은 음주를 " + showCount + "병을 하셨네요");
            for(int i=1; i<=showCount; i++) {
                sojus[i].setVisibility(View.VISIBLE);
            }
            if(showCount%1 != 0)    sojus[5].setVisibility(View.VISIBLE);

            int stHour = getDrunk.getStHour();
            int stMin = getDrunk.getStMin();
            int edHour = getDrunk.getEdHour();
            int edMin = getDrunk.getEdMin();

            if(edHour < stHour) {
                int tmpMin = stHour * 60 + stMin;
                int todMin = 1440 - tmpMin;

                todMin += edHour * 60 + edMin;
                totalH = todMin / 60;
                totalM = todMin % 60;
            } else {
                int tmpMin = (edHour * 60 + edMin) - (stHour * 60 + stMin);
                totalH = tmpMin / 60;
                totalM = tmpMin % 60;
            }
            hourTextView.setText("이 날 총 " + totalH + "시간 " + totalM + "분 음주 하셨네요");
        } else {
            countTextView.setText("이 날은 음주를 안하셨습니다!");
            hourTextView.setText("이 날 총 0시간 음주 하셨네요");
        }
    }
    void resetImages() {
        for(int i=0; i<6; i++) {
            sojus[i].setVisibility(View.GONE);
        }
    }
    private String transFormat(Date d) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = transFormat.format(d);

        return date;
    }

    private void initDialog() {
        final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add, null);

        // 요소들 객체화 시키는 부분
        Button okBtn = dialogView.findViewById(R.id.okBtn);
        Button cancelBtn = dialogView.findViewById(R.id.cancleBtn);
        startTime = dialogView.findViewById(R.id.starttimeText);
        endTime = dialogView.findViewById(R.id.endtimeText);
        count = dialogView.findViewById(R.id.countText);
        Button starttimeBtn = dialogView.findViewById(R.id.starttimeBtn);
        Button endtimeBtn = dialogView.findViewById(R.id.endtimeBtn);
        Button countBtn = dialogView.findViewById(R.id.countBtn);

        d.setView(dialogView);
        alertDialog = d.create();

        /*

        */
        starttimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder d2 = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_timepicker, null);
                final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
                d2.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour = 0, min = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            hour = timePicker.getHour();
                            min = timePicker.getMinute();
                        }

                        String showText = "";
                        int showHour = hour;

                        if(hour >= 12) {
                            showText += "오후";
                            showHour -= 12;
                        } else showText += "오전";

                        showText = showText + " " + showHour + "시 " + min + "분";
                        startTime.setText(showText);

                        drunkEvent.setStart(hour, min);
                        checkselst = true;
                    }
                });
                d2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                d2.setView(dialogView);
                AlertDialog alertDialog2 = d2.create();
                alertDialog2.show();
            }
        });
        endtimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder d2 = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_timepicker, null);
                final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
                d2.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour = 0, min = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            hour = timePicker.getHour();
                            min = timePicker.getMinute();
                        }

                        String showText = "";
                        int showHour = hour;

                        if(hour >= 12) {
                            showText += "오후";
                            showHour -= 12;
                        } else showText += "오전";

                        showText = showText + " " + showHour + "시 " + min + "분";
                        endTime.setText(showText);

                        drunkEvent.setEnd(hour, min);
                        checkseled = true;
                    }
                });
                d2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                d2.setView(dialogView);
                AlertDialog alertDialog2 = d2.create();
                alertDialog2.show();
            }
        });
        countBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder d2 = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_numberpicker, null);
                final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);

                numberPicker.setMaxValue(9);
                numberPicker.setMinValue(0);
                numberPicker.setWrapSelectorWheel(false);

                String[] displayCount = {"0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5"};
                numberPicker.setDisplayedValues(displayCount);

                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        Log.d(TAG, "onValueChange: ");
                    }
                });
                d2.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: " + numberPicker.getValue());
                        pickCount = Float.parseFloat(displayCount[numberPicker.getValue()]);

                        count.setText(Float.toString(pickCount) + "병");
                        checkselct = true;
                        drunkEvent.setCount(pickCount);
                    }
                });
                d2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                d2.setView(dialogView);
                AlertDialog alertDialog2 = d2.create();
                alertDialog2.show();
            }
        });


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkselct || !checkseled || !checkselst) {
                    Toast.makeText(getContext(), "항목을 입력해주세요!", Toast.LENGTH_LONG).show();
                    return;
                }
                // 이벤트 등록하는 부분
                String date = transFormat(selectedDate);
                Date trans_date = null;
                try {
                    trans_date = dateFormatForDisplaying.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long time = trans_date.getTime();

                Event ev = new Event(Color.GREEN, time, drunkEvent);
                compactCalendarView.addEvent(ev);

                float showCount = drunkEvent.getCount();
                countTextView.setText("이 날은 음주를 " + showCount + "병을 하셨네요");
                for(int i=1; i<=showCount; i++) {
                    sojus[i].setVisibility(View.VISIBLE);
                }
                if(showCount%1 != 0)    sojus[5].setVisibility(View.VISIBLE);

                int stHour = drunkEvent.getStHour();
                int stMin = drunkEvent.getStMin();
                int edHour = drunkEvent.getEdHour();
                int edMin = drunkEvent.getEdMin();
                int totalH, totalM;

                if(edHour < 12 && stHour >= 12) {
                    int tmpMin = stHour * 60 + stMin;
                    int todMin = 1440 - tmpMin;

                    todMin += edHour * 60 + edMin;
                    totalH = todMin / 60;
                    totalM = todMin % 60;
                } else {
                    int tmpMin = (edHour * 60 + edMin) - (stHour * 60 + stMin);
                    totalH = tmpMin / 60;
                    totalM = tmpMin % 60;
                }
                hourTextView.setText("이 날 총 " + totalH + "시간 " + totalM + "분 음주 하셨네요");

                // DB에 Insert 하는 부분
                String todayDate = transFormat(selectedDate);
                String startTime = stHour + ":" + stMin + ":00";
                String endTime = edHour + ":" + edMin + ":00";

                ContentValues values = new ContentValues();
                values.put("drunkDate", todayDate);
                values.put("drunkStart", startTime);
                values.put("drunkEnd", endTime);
                values.put("drunkCount", drunkEvent.getCount());
                db.insert("mytable",null, values);


                alertDialog.dismiss();
                clearDialog();
                resetCheck();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                clearDialog();
                resetCheck();
            }
        });
    }

    // Dialog 확인 및 취소시 호출하는 메서드
    private void clearDialog() {
        startTime.setText("");
        endTime.setText("");
        count.setText("");
        pickCount = 0;
    }

    void resetCheck() {
        checkselct = false;
        checkseled = false;
        checkselst = false;
    }
}