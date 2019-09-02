package com.college.xdick.findme.ui.Fragment;


import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.ActivityBaseFragment;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;


/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityschoolFragment extends ActivityBaseFragment {


    @Override
    protected BmobQuery<MyActivity> condition(String order,long aLong) {
        BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();

        query.addWhereEqualTo("hostSchool",  BmobUser.getCurrentUser(MyUser.class).getSchool());
        if (order.equals("date")) {
            query.addWhereGreaterThan("endDate", aLong );
        }
        query.order(order);
        query.setSkip(size);
        query.setLimit(10);
        return query;
    }




}
