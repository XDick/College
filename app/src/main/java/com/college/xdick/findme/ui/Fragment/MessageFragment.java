package com.college.xdick.findme.ui.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityMessageAdapter;
import com.college.xdick.findme.bean.ActivityMessageBean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/5/23.
 */

public class MessageFragment extends Fragment {
    View rootView;
    private List<ActivityMessageBean> activityList= new ArrayList<>();
    private ActivityMessageAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_message,container,false);

        initView();
        initData();


        return rootView;
    }


    private void initView(){

        swipeRefresh =rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview_message);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActivityMessageAdapter(activityList);
        recyclerView.setAdapter(adapter);

    }

    private void initData(){
        try{

            activityList.clear();
            List<ActivityMessageBean> list = DataSupport.where("currentuserId = ?", BmobUser.getCurrentUser().getObjectId()).find(ActivityMessageBean.class);
            Collections.sort(list);
            activityList.addAll(list);

            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void read(){

        for (ActivityMessageBean bean :activityList){
            bean.setIfCheck("true");
            bean.save();
        }
    }
    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });

                    Thread.sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                if (getActivity() == null)
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

}
