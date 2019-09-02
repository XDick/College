package com.college.xdick.findme.ui.Fragment;


import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.ActivityBaseFragment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;

public class ActivityFollowFragment extends ActivityBaseFragment {


    @Override
   protected BmobQuery<MyActivity> condition(String order ,long aLong) {
        String[] follow =  BmobUser.getCurrentUser(MyUser.class).getFollowing();

        BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
        if (follow!=null){
            if (follow.length==0){
                ifEmpty=true;
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                return null;
            }

            final List<String> list =new ArrayList<>(Arrays.asList(follow));

            list.add(myUser.getObjectId());

            query.addWhereContainedIn("host", list);}
        else {
            ifEmpty=true;
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
            return null;
        }
        if (order.equals("date")) {
            query.addWhereGreaterThan("endDate", aLong );
        }
        query.order(order);
        query.setSkip(size);
        query.setLimit(10);
        return query;
    }


}