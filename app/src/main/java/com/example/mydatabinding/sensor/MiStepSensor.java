package com.example.mydatabinding.sensor;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.example.mydatabinding.enity.Step;
import com.example.mydatabinding.enity.Steps;

import java.util.LinkedList;
import java.util.TimeZone;

public class MiStepSensor {

    public ContentResolver resolver;

    public static String[] projection = new String[] {
            Steps.ID,
            Steps.BEGIN_TIME,
            Steps.END_TIME,
            Steps.MODE,
            Steps.STEPS
    };

    //构造方法，初始化resolver
    public MiStepSensor(Context context) {
        this.resolver = context.getContentResolver();
    }

    //返回记录的数据
    public LinkedList<Step> getAllSteps(String selection, String[] args) {
        LinkedList<Step> steps = new LinkedList<Step>();

        Cursor cursor = resolver.query(Steps.CONTENT_URI, projection, selection, args,
                Steps.DEFAULT_SORT_ORDER);
        if (cursor.moveToFirst()) {
            do {
                Step s = new Step(cursor.getInt(0), cursor.getLong(1), cursor.getLong(2),
                        cursor.getInt(3),
                        cursor.getInt(4));
                steps.add(s);
            } while (cursor.moveToNext());
        }
        return steps;
    }

    //返回当天的步数
    public int getTodaySteps(TextView todaySteps_tv) {
        //获取当天0点的时间戳
        long current = System.currentTimeMillis();
        long zero = current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
        int today_steps = 0;
        Cursor cursor = resolver.query(Steps.CONTENT_URI, projection, null, null,
                Steps.DEFAULT_SORT_ORDER);
        if (cursor.moveToFirst()) {
            do {
                //只记录当天的数据
                if(cursor.getLong(1)>zero){
                    today_steps += cursor.getInt(4);
                }
            } while (cursor.moveToNext());
        }
        Log.d("zhu","当天步数："+today_steps);
        todaySteps_tv.setText("今日步数:"+today_steps);
        return today_steps;
    }
}
