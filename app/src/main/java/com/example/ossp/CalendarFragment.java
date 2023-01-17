package com.example.ossp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    final String TAG = "calendar test";
    private Date selectedDate = null;
    private AlertDialog alertDialog;
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy년 MM월", Locale.KOREA);
    private SimpleDateFormat dateFormatForMonth2 = new SimpleDateFormat("yyyy-MM", Locale.KOREA);
    TextView startTime, endTime, count;
    float pickCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        final CompactCalendarView compactCalendarView = (CompactCalendarView) v.findViewById(R.id.compactcalendar_view);

        TextView textView_month = (TextView) v.findViewById(R.id.textView_month);
        TextView textView_result = (TextView) v.findViewById(R.id.textView_result);
        TextView textView_result2 = (TextView) v.findViewById(R.id.textView_result2);

        textView_month.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // NumberPicker 초기화하기
        initDialog();

        // 이벤트를 추가하는 버튼
        Button event_addBtn = (Button) v.findViewById(R.id.okBtn) ;
        event_addBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedDate == null)    return;

                String date = transFormat(selectedDate);
                Date trans_date = null;
                try {
                    trans_date = dateFormatForDisplaying.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long time = trans_date.getTime();

                Event ev = new Event(Color.GREEN, time, "이벤트");
                compactCalendarView.addEvent(ev);

                alertDialog.show();
            }
        });

        Button button_remove_events = (Button) v.findViewById(R.id.cancleBtn) ;
        button_remove_events.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                compactCalendarView.removeAllEvents();
            }
        });


        /*
        이벤트 가져오는 부분

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

        // 이벤트 관련 코드
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                selectedDate = dateClicked;
                List<Event> events = compactCalendarView.getEvents(dateClicked);

                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date1 = transFormat.format(dateClicked);

                String event_name = "";
                String event_date = "";

                if (events.size() > 0) {
                    event_name = events.get(0).getData().toString();
                    long time1 = events.get(0).getTimeInMillis();
                    event_date = transFormat.format(new Date(time1));
                }

                textView_result.setText("클릭한 날짜 " + date1 + " event 정보 " + event_name + " " + event_date);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView_month.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });
        return v;
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
                alertDialog.dismiss();
                clearDialog();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                clearDialog();
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

}