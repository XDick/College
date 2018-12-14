package com.college.xdick.findme.ui.Fragment;



import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.ActivityBaseFragment;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;


/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivitygpsFragment extends ActivityBaseFragment {


    @Override
    protected BmobQuery<MyActivity> condition(String order,long aLong) {
        String[] gps = BmobUser.getCurrentUser(MyUser.class).getGps();

        BmobQuery<MyActivity> query = new BmobQuery<>();
        if (gps!=null){
            query.addWhereEqualTo("gps", gps[2]);}
        else {
            ifEmpty=true;
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
            return null;
        }
        if (order.equals("date")) {
            query.addWhereGreaterThan("date", aLong * 1000L - 60 * 60 * 24 * 1000);
        }
           query.order(order);
        query.setSkip(size);
        query.setLimit(10);
        return query;
    }

    @Override
    protected void onCreateViewOperation() {
        if (myUser!=null){
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(true);
                    size=0;
                    initData(REFRESH);
                }
            });
        }
    }
}
