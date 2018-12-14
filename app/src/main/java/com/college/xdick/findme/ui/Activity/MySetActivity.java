package com.college.xdick.findme.ui.Activity;


import com.college.xdick.findme.bean.MyActivity;

import com.college.xdick.findme.ui.Base.MyBaseActivity;


import cn.bmob.v3.BmobQuery;


/**
 * Created by Administrator on 2018/5/19.
 */

public class MySetActivity extends MyBaseActivity {

    @Override
    protected BmobQuery<MyActivity> condition() {
        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.addWhereEqualTo("hostId",myUser.getObjectId());
        query.order("-date");
        query.setLimit(10);
        query.setSkip(size);
        return query;
    }
}


