package com.college.xdick.findme.ui.Fragment;



import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Base.ActivityBaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;


/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityFragment extends ActivityBaseFragment {


    @Override
    protected BmobQuery<MyActivity> condition(String order,long aLong) {
        BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
        List<BmobQuery<MyActivity>> queries = new ArrayList<>();
         if (order.equals("date")){
             query.addWhereGreaterThan("date", aLong);
         }


        if(BmobUser.getCurrentUser(MyUser.class).getTag()!=null) {
            String tag[] =  BmobUser.getCurrentUser(MyUser.class).getTag();
            if (tag.length==0){
                ifEmpty=true;
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                return null;

            }
            for (int i = 0; i < tag.length; i++) {
                BmobQuery<MyActivity> q = new BmobQuery<MyActivity>();
                q.addWhereContainsAll("tag", Arrays.asList(tag[i]));
                queries.add(q);
            }
        }
        query.setLimit(10);
        query.setSkip(size);
        query.or(queries);
        query.order(order);
        return query;
    }


    @Override
    protected void onCreateViewOperation() {
       // MainActivity activity = (MainActivity) getActivity();
        List<MyActivity>list=(List<MyActivity>)getActivity().getIntent().getSerializableExtra("LISTDATA");

        if (list!=null) {
            activityList.addAll(list);
            if (list.size()<10){
                ifEmpty=true;
            }

            else {
                ifEmpty=false;

            }
            adapter.notifyDataSetChanged();
            size=10;
        }
        else {
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
