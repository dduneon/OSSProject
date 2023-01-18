package com.example.ossp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainFragment extends Fragment {
    // 이번달 마신 소주 양
    int monthCount = 0;
    DBHelper helper;
    SQLiteDatabase db;
    float sumCount = 0;
    boolean drunk = false;
    TextView calculateTextView;
    TextView statTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        drunk = false;
        sumCount = 0;

        calculateTextView = v.findViewById(R.id.calculateTextView);
        statTextView = v.findViewById(R.id.statTextView);

        LinearLayout countContainer = v.findViewById(R.id.countLayout);

        // 데이터베이스 생성 및 초기화
        helper = new DBHelper(getContext(), "mydb.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        String sql = "select * from mytable;";
        Cursor c = db.rawQuery(sql, null);
        StringTokenizer st;
        int endHour, endMin, stHour, stMin;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.KOREA);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DATE, -1); // 오늘날짜로부터 -1
        String yesterday = dateFormat1.format(c1.getTime()); // String으로 저장

        // 현재 시간
        LocalTime now = null;
        int nowHour = 0, nowMin, afterHour=0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalTime.now(ZoneId.of("Asia/Seoul"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String formatedNow = now.format(formatter);
            st = new StringTokenizer(formatedNow, ":");
            nowHour = Integer.parseInt(st.nextToken());
            nowMin = Integer.parseInt(st.nextToken());
        }
        float calCount = 0;
        String tmpCount = null;
        String calsCount = null;
        while(c.moveToNext()) {
            String tmpDate = c.getString(c.getColumnIndex("drunkDate"));
            if (tmpDate.contains(dateFormat.format(new Date()))) {
                tmpCount = c.getString(c.getColumnIndex("drunkCount"));
                sumCount += Float.parseFloat(tmpCount);
            }

            if (tmpDate.equals(dateFormat1.format(new Date()))) {
                String tmpEnd = c.getString(c.getColumnIndex("drunkEnd"));
                st = new StringTokenizer(tmpEnd, ":");
                endHour = Integer.parseInt(st.nextToken());
                endMin = Integer.parseInt(st.nextToken());

                calsCount = c.getString(c.getColumnIndex("drunkCount"));

                afterHour = nowHour - endHour;
                drunk = true;
            } else if (tmpDate.equals(yesterday)) {
                String tmpEnd = c.getString(c.getColumnIndex("drunkEnd"));
                st = new StringTokenizer(tmpEnd, ":");
                endHour = Integer.parseInt(st.nextToken());
                endMin = Integer.parseInt(st.nextToken());

                calsCount = c.getString(c.getColumnIndex("drunkCount"));

                String tmpStart = c.getString(c.getColumnIndex("drunkStart"));
                st = new StringTokenizer(tmpStart, ":");
                stHour = Integer.parseInt(st.nextToken());
                stMin = Integer.parseInt(st.nextToken());

                if(endHour < stHour) {
                    afterHour = nowHour - endHour;
                }else {
                    afterHour = 24 - endHour + nowHour;
                }


                if(afterHour < 24) drunk = true;
            }
        }
        monthCount = (int) sumCount;


        if(monthCount <=14) {
            addImage(countContainer, monthCount);
        } else if(monthCount <= 28) {
            addOneLine(countContainer);
            addImage(countContainer, monthCount-14);
        } else if(monthCount <= 42) {
            addOneLine(countContainer);
            addOneLine(countContainer);
            addImage(countContainer, monthCount-28);
        } else {
            addOneLine(countContainer);
            addOneLine(countContainer);
            addOneLine(countContainer);
        }

        /*
        C = A÷(10PR) - (βt) [1]

C = 최종 음주로부터 t시간 경과했을 때 추산된 혈중 알코올농도

A÷(10PR) = 음주한 사람의 혈중 알코올농도 중 최고수치(%)

A = 음주한 사람이 섭취한 알코올의 질량(g, = 음주량(ml) X (술의 도수(%)÷100) X 알코올의 비중(0.7894g/ml))

P = 음주한 사람의 체중(kg)

R = 음주한 사람의 성별 계수 (남자 = 0.86, 여자 = 0.64)

β = 시간당 혈중알코올농도 감소량 (평균적으로 0.015 %/h)

t = 경과 시간 (단위: h)
         */
        double result=0;
        calCount = Float.parseFloat(calsCount);

        if(drunk) {
            // A = 소주 1병당 33.82
            result = (33.82 * calCount) / (10 * 70 * 0.86);
            result -= 0.015 * afterHour;
        }
        if(result <= 0) {
            calculateTextView.setText("0%");
        } else {
            calculateTextView.setText(result + "%");
            if(result >= 0.2) {
                statTextView.setText("1년 이상 5년 이하의 징역이나\n1천만원 이상 2천만원 이하의 벌금");
            }else if (result >= 0.08) {
                statTextView.setText("1년 이상 2년 이하의 징역이나\n500만원 이상 1천만원 이하의 벌금");
            }else if(result >= 0.03) {
                statTextView.setText("1년 이하의 징역이나 500만원 이하의 벌금");
            }
        }


        return v;
    }

    void addOneLine(LinearLayout v) {
        LinearLayout linear = new LinearLayout(getContext());
        linear.setOrientation(LinearLayout.HORIZONTAL);
        linear.setGravity(Gravity.CENTER);
        linear.setPadding(0,0,0,20);
        for(int i=0; i<14; i++) {
            ImageView img = new ImageView(getContext());
            img.setImageResource(R.drawable.one);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = 70;
            params.height = 210;
            linear.addView(img, params);
        }
        v.addView(linear);
    }

    void addImage(LinearLayout v, int size) {
        LinearLayout linear = new LinearLayout(getContext());
        linear.setOrientation(LinearLayout.HORIZONTAL);
        linear.setGravity(Gravity.CENTER);
        linear.setPadding(0,0,0,20);
        for(int i=0; i<size; i++) {
            ImageView img = new ImageView(getContext());
            img.setImageResource(R.drawable.one);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = 70;
            params.height = 210;
            linear.addView(img, params);
        }
        v.addView(linear);
    }
}