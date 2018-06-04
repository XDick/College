package com.college.xdick.findme.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.ActivityAdapter2;
import com.college.xdick.findme.adapter.ActivityAdapter3;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/5/31.
 */

public class UserCenterActivityFragment extends Fragment {
   private View rootView;
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private List<MyActivity> activityList= new ArrayList<>();
    private ActivityAdapter3 adapter;
    private MyUser nowUser ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_usercenter_activity,container,false);
        nowUser=(MyUser)getActivity(). getIntent().getSerializableExtra("USER");
         initView();
        initData();
        return rootView;
    }

    private void initView(){
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ActivityAdapter3(activityList);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addFooterView(footer);
        recyclerView.setAdapter(adapter);


    }
    public void initData(){
        activityList.clear();
        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.addWhereEqualTo("host",nowUser);
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                if (e==null){

                    for (MyActivity activity: list){

                        activityList.add(activity);
                    }
                    Collections.sort(activityList);
                    Collections.reverse(activityList);
                    adapter.notifyDataSetChanged();


                    }


            }
        });


    }
}
