package com.college.xdick.findme.Listener;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.college.xdick.findme.MyClass.GpsEvent;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

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


        country = bdLocation.getCountry();
        city = bdLocation.getCity();
        street = bdLocation.getStreet();
        district = bdLocation.getDistrict();
        province = bdLocation.getProvince();
        final String[] gps = {country, province, city, district, street};
        EventBus.getDefault().post(new GpsEvent(gps));
        Log.d("TAG", "更新地址了"+Arrays.toString(gps));



    }


    }

