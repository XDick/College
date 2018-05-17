package com.college.xdick.findme.Listener;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;

import java.util.Arrays;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/5/7.
 */

public class MyLocationListener implements BDLocationListener {

    private String country,province,city,district,street;




    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        country=bdLocation.getCountry();
        city= bdLocation.getCity();
        street= bdLocation.getStreet();
        district = bdLocation.getDistrict();
        province = bdLocation.getProvince();
        final String[] gps={country,province,city,district,street};
         int count = 0;


        for(int i =0; i<gps.length;i++){
            if(gps[i]==null){
                count++;
            }
        }
         MyUser myUser = BmobUser.getCurrentUser(MyUser.class);


       if (myUser != null && count!=5&& gps!=null&&gps!=myUser.getGps()) {
        myUser.setGps(gps);
        myUser.update(myUser.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.d("TAG","更新地址2"+ Arrays.toString(gps));
                }

                else {


                }}
        });}
    }
}
