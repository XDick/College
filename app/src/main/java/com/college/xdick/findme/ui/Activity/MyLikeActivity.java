package com.college.xdick.findme.ui.Activity;


import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.ui.Base.MyBaseActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import cn.bmob.v3.BmobQuery;


/**
 * Created by Administrator on 2018/5/19.
 */

public class MyLikeActivity extends MyBaseActivity {


    @Override
    protected BmobQuery<MyActivity> condition() {
        if (myUser.getLike()==null){
            return null;
        }
        BmobQuery<MyActivity> query = new BmobQuery<>();
        final List<String> list =new ArrayList<>(Arrays.asList(myUser.getLike()));


        query.addWhereContainedIn("objectId", list);
        query.order("-date");

        query.setLimit(10);
        query.setSkip(size);
        query.include("host[username|avatar|Exp]");
        return query;
    }
}
