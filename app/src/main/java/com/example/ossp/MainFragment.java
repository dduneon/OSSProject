package com.example.ossp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainFragment extends Fragment {
    // 이번달 마신 소주 양
    int monthCount = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        LinearLayout countContainer = v.findViewById(R.id.countLayout);

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