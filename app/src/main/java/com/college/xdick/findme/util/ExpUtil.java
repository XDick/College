package com.college.xdick.findme.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.CalendarContract;
import android.widget.TextView;

import com.college.xdick.findme.R;

public class ExpUtil {



    public static String ConvertExp(int exp){

        if (0<=exp&&exp<5){
            return "Lv.1";
        }
        else if (5<=exp&&exp<20){

            return "Lv.2";

        }
        else if (20<=exp&&exp<40){

            return "Lv.3";
        }
        else if (40<=exp&&exp<60){

            return "Lv.4";
        }
        else if (60<=exp&&exp<300){  //5-10级/每40升一级

            int level= (exp-60)/40+5;
            return "Lv."+level;
        }
        else if (300<=exp&&exp<2700) {//11-50级/每60升一级

            int level= (exp-300)/60+11;
            return "Lv."+level;
        }
        else if (2700<=exp&&exp<=17750) {//51-200级/每100升一级
            int level= (exp-2700)/100+50;
            return "Lv."+level;
        }
         else return "Lv.200";



    }


    public static int Percent(int exp){
        String level = ConvertExp(exp);

        int lv = Integer.valueOf(level.substring(level.indexOf(".")+1));

        if (0<=exp&&exp<5){

            return exp*100/5;
        }
        else if (5<=exp&&exp<20){

            return (exp-5)*100/15;

        }
        else if (20<=exp&&exp<40){

            return (exp-20)*100/20;
        }
        else if (40<=exp&&exp<60){

            return (exp-40)*100/20;
        }
        else if (60<=exp&&exp<300){  //5-10级/每40升一级


            return (exp-60-(lv-5)*40)*100/40;
        }
        else if (300<=exp&&exp<2700) {//11-50级/每60升一级


            return (exp-300-(lv-11)*60)*100/60;
        }
        else if (2700<=exp&&exp<17750) {//51-200级/每100升一级

            return (exp-60-(lv-51)*100)*100/100;
        }
        else  return 100;


    }


    public static String expFigure(int exp){

        String level = ConvertExp(exp);

        int lv = Integer.valueOf(level.substring(level.indexOf(".")+1));

        if (0<=exp&&exp<5){

            return exp+"/"+5;
        }
        else if (5<=exp&&exp<20){

            return exp+"/"+20;

        }
        else if (20<=exp&&exp<40){

            return exp+"/"+40;
        }
        else if (40<=exp&&exp<60){

            return exp+"/"+60;
        }
        else if (60<=exp&&exp<300){  //5-10级/每40升一级


            return exp+"/"+(300-(10-lv)*40);
        }
        else if (300<=exp&&exp<2700) {//11-50级/每60升一级


            return exp+"/"+(2700-(50-lv)*60);
        }
        else if (2700<=exp&&exp<17750) {//51-200级/每100升一级

            return exp+"/"+(17750-(200-lv)*100);
        }
        else  return "MAX";

    }

    public static TextView colorTextView(Context context,TextView textView, int exp){

        String level = ConvertExp(exp);
        int lv = Integer.valueOf(level.substring(level.indexOf(".")+1));

        if(0<=lv&&lv<5)textView.setTextColor(context.getResources().getColor(R.color.Lv1));
        if(5<=lv&&lv<15)textView.setTextColor(context.getResources().getColor(R.color.Lv2));
        if(15<=lv&&lv<50)textView.setTextColor(context.getResources().getColor(R.color.Lv3));
        if(50<=lv&&lv<100)textView.setTextColor(context.getResources().getColor(R.color.Lv4));
        if(100<=lv&&lv<150)textView.setTextColor(context.getResources().getColor(R.color.Lv5));
        if(150<=lv&&lv<=200) textView.setTextColor(context.getResources().getColor(R.color.Lv6));

        textView.setTypeface(Typeface.DEFAULT_BOLD);

        return textView;
    }




}
