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

import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityMessageAdapter;
import com.college.xdick.findme.bean.ActivityMessageBean;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/5/23.
 */

public class MessageFragment extends BaseFragment implements MessageListHandler {
    View rootView;
    private List<ActivityMessageBean> activityList= new ArrayList<>();
    private ActivityMessageAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_message,container,false);
        initView();
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                initData();
            }
        });





        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
        initData();
        initRecyclerView();
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
       initRecyclerView();

    }
    public void onMessageReceive(List<MessageEvent> list) {

      //  initData();
       // initRecyclerView();
    }


    private void initRecyclerView(){
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

            swipeRefresh.setRefreshing(false);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void read(){

        for (ActivityMessageBean bean :activityList){
            if (!bean.getType().equals("dynamics_picture")){
                if (bean.getIfCheck().equals("false")){
                    bean.setIfCheck("true");
                    bean.save();
                }

            }

        }
        ((MainActivity)getContext()).setBadgeItem();
    }
    public  void deleteAll(){
        DataSupport.deleteAll(ActivityMessageBean.class,"currentuserId = ?", BmobUser.getCurrentUser().getObjectId());
        initData();
    }
    private void refresh(){


                            initData();


                if (getActivity() == null){
                    return;
                }







        //setBadgeItem();
    }

}
