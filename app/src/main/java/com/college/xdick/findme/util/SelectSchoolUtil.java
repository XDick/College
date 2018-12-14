package com.college.xdick.findme.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.CityAdapter;
import com.college.xdick.findme.adapter.ProvinceAdapter;
import com.college.xdick.findme.adapter.SchoolAdapter;
import com.college.xdick.findme.bean.City;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.bean.Province;
import com.college.xdick.findme.bean.School;
import com.college.xdick.findme.bean.SelectSchool;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/5/8.
 */

public class SelectSchoolUtil {



    private  static View parent;
    private static ListView mCityListView;
    private static ListView mProvinceListView;
    private static ListView mSchoolListView;
    private static TextView mTitle;
    private static PopupWindow mPopWindow;
    private static ProvinceAdapter mProvinceAdapter;
    private static  SchoolAdapter mSchoolAdapter;
    private static  CityAdapter mCityAdapter;
    private static String provinceId,cityId;
    private static SelectSchool selectSchool;
    private static String JsonData;
    private static List<Province> provinceList;
    private static List<City> cityList;
    private static List<School> schoolList;
    private static School schoolName;
    private static Button schoolButton;
    private static TextView schoolTextView;
    private static MyUser user=null;


    public static void initPopView(Activity activity , Button button,TextView textView,MyUser myUser) {
        parent = activity.getWindow().getDecorView();
        View popView = View.inflate( activity, R.layout.item_select_school, null);
        mTitle = (TextView) popView.findViewById(R.id.select_list_title);
        mProvinceListView = (ListView) popView.findViewById(R.id.select_province);
        mSchoolListView = (ListView) popView.findViewById(R.id.select_school);
        schoolButton =button;
        schoolTextView = textView;
        user= myUser ;

        mCityListView = popView.findViewById(R.id.select_city);
        mProvinceListView.setOnItemClickListener(itemListener);
        mSchoolListView.setOnItemClickListener(itemListener);
        mCityListView.setOnItemClickListener(itemListener);

        mProvinceAdapter = new ProvinceAdapter( activity);
        mProvinceListView.setAdapter(mProvinceAdapter);


        mCityAdapter = new CityAdapter( activity);
        mCityListView.setAdapter(mCityAdapter);


        mSchoolAdapter = new SchoolAdapter( activity);
        mSchoolListView.setAdapter(mSchoolAdapter);


        int width =  activity.getResources().getDisplayMetrics().widthPixels * 3 / 4;
        int height =  activity.getResources().getDisplayMetrics().heightPixels * 3 / 5;
        mPopWindow = new PopupWindow(popView, width, height);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mPopWindow.setBackgroundDrawable(dw);
        mPopWindow.setFocusable(true);
        mPopWindow.setTouchable(true);
        mPopWindow.setOutsideTouchable(true);//允许在外侧点击取消
        loadProvince();
        mPopWindow.setOnDismissListener(listener);
    }



    public static void showPopWindow() {
        mPopWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }


    public static String getSchool(){
       if(schoolName==null){
           return "无";
       }
        return  schoolName.getName();
    }


    private static void loadProvince(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://bmob-cdn-18038.b0.upaiyun.com/2018/09/03/6d79c71840f01a4c800930a92c0b5b23.json")
                            .build();
                    Response response = client.newCall(request).execute();
                    JsonData = response.body().string();

                    Gson gson = new Gson();
                    selectSchool = gson.fromJson(JsonData,SelectSchool.class);
                    provinceList=selectSchool.getData();


                    mProvinceAdapter.setList(provinceList);
                    mProvinceAdapter.notifyDataSetChanged();

                }
                catch(Exception e){
                    e.printStackTrace();
                }


            }
        }).start();


    }


    private static void loadCity() {

        for (Province province  : provinceList){

            if (province.getDepartId()==provinceId){
                cityList=province.getCollegeLocations();
                break;
            }
        }

        mCityAdapter.setList(cityList);
        mCityAdapter.notifyDataSetChanged();
    }



    private static void loadSchool() {
        for (City city:cityList){

            if(city.getLocationId()==cityId){
                schoolList = city.getCollegeNames();
                break;
            }

        }
        mSchoolAdapter.setList(schoolList);
        mSchoolAdapter.notifyDataSetChanged();
    }





    /**
     * ListView Item点击事件
     */

    static AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent == mProvinceListView) {
                Province provinceName = (Province) mProvinceListView.getItemAtPosition(position);
                provinceId = provinceName.getDepartId();
                mTitle.setText("选择城市");
                mProvinceListView.setVisibility(View.GONE);
                mCityListView.setVisibility(View.VISIBLE);
                loadCity();
            }
            if (parent == mCityListView) {
                City cityName = (City) mCityListView.getItemAtPosition(position);
                cityId = cityName.getLocationId();
                mTitle.setText("选择学校");
                mCityListView.setVisibility(View.GONE);
                mSchoolListView.setVisibility(View.VISIBLE);
                loadSchool();


            } else if (parent == mSchoolListView) {
                schoolName = (School) mSchoolListView.getItemAtPosition(position);
                if(schoolButton!=null){
                    schoolButton.setText(schoolName.getName());
                }

                if(user!=null){
                    user.setSchool(schoolName.getName());
                    user.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                if(schoolTextView!=null){
                                    schoolTextView.setText("学校:"+schoolName.getName());
                                }
                                else
                                {
                                    Log.d("","更新学校失败"+e.getMessage());
                                }
                            }
                        }
                    });

                }
                mPopWindow.dismiss();
            }
        }
    }

            ;


    /**
     * popWindow消失监听事件
     */
    static PopupWindow.OnDismissListener listener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            mTitle.setText("选择地区");
            mProvinceListView.setVisibility(View.VISIBLE);
            mSchoolAdapter.setList(new ArrayList<School>());
            mSchoolAdapter.notifyDataSetChanged();
            mSchoolListView.setVisibility(View.GONE);
        }
    };
}
