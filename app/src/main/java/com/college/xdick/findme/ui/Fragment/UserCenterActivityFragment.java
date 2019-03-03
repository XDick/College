package com.college.xdick.findme.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.ActivityAdapter2;
import com.college.xdick.findme.adapter.ActivityAdapter3;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseFragment;

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

public class UserCenterActivityFragment extends BaseFragment {

    private List<MyActivity> activityList= new ArrayList<>();
    private ActivityAdapter3 adapter;
    private MyUser nowUser ;
    private int size = 0;
    private  boolean ifEmpty=false;
    public final int ADD =1;
    public final int REFRESH=2;
    private SwipeRefreshLayout swipeRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_usercenter_activity,container,false);
        nowUser=(MyUser)getActivity(). getIntent().getSerializableExtra("USER");
         initView();
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                size = 0;
                initData(REFRESH);
            }
        });
        return rootView;
    }

    private void initView(){
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        final  LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefresh = rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        adapter = new ActivityAdapter3(activityList);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addFooterView(footer);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_activity, recyclerView, false);
        adapter.setEmptyView(empty);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(ifEmpty){
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                }else
                {
                    adapter.changeMoreStatus(ActivityAdapter3.LOADING_MORE);}
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&  layoutManager.findLastVisibleItemPosition()+1  == adapter.getItemCount()) {
                    if (ifEmpty){
                        //null
                    }
                    else {initData(ADD);}
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }
    public void initData(final int state){
        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.addWhereEqualTo("host",nowUser.getObjectId());
        query.order("-createdAt");
        query.setLimit(10);
        query.setSkip(size);
        query.include("host[username|avatar]");
        final int listsize = activityList.size();

        query.findObjects(new FindListener<MyActivity>() {
                              @Override
                              public void done(List<MyActivity> object, BmobException e) {
                                  if(e==null){



                                      activityList.addAll(object);
                                      if (state==ADD){
                                          if (listsize==activityList.size()){
                                              ifEmpty=true;
                                              adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                              adapter.notifyDataSetChanged();
                                          }
                                          else if (listsize+10>activityList.size()){
                                              ifEmpty=true;
                                              adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                              adapter.notifyItemInserted(adapter.getItemCount()-1);

                                          }

                                          else  {

                                              adapter.changeMoreStatus(ActivityAdapter.PULLUP_LOAD_MORE);
                                              adapter.notifyItemInserted(adapter.getItemCount()-1);
                                              size = size + 10;
                                          }
                                      }
                                      else if (state==REFRESH){
                                          activityList.clear();
                                          if(object.size()<10){
                                              ifEmpty=true;
                                              activityList.addAll(object);
                                              adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                              adapter.notifyDataSetChanged();
                                          }
                                          else {
                                              ifEmpty=false;
                                              activityList.addAll(object);
                                              adapter.notifyDataSetChanged();}
                                          size =  10;
                                          swipeRefresh.setRefreshing(false);
                                      }

                                  }else{

                                      Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                  }
                              }


                          }
        );
    }



    private void refresh(){
        size =0;
        swipeRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                    initData(REFRESH);
                if (getActivity() == null){
                    return;}
            }
        }).start();
    }
}
