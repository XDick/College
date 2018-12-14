package com.college.xdick.findme.ui.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

 public abstract class ActivityBaseFragment extends BaseFragment {
    protected View rootview;
    protected MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    protected boolean ifsort = false;
    protected boolean ifEmpty= false;
    public final int ADD =1;
    public final int REFRESH=2;
    public final int SORT =3;
    protected  int size =0;
    protected List<MyActivity> activityList= new ArrayList<>();
    protected SwipeRefreshLayout swipeRefresh;
    protected ActivityAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_activity,container,false);
        initBaseView();
        initRecyclerView();
        onCreateViewOperation();
        return rootview;
    }


     protected abstract BmobQuery<MyActivity> condition(String order,long aLong);

     protected abstract void onCreateViewOperation();

    private void initBaseView(){
        swipeRefresh =rootview.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }
    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActivityAdapter(activityList);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_activity, recyclerView, false);
        adapter.setEmptyView(empty);
        adapter.addFooterView(footer);
        if (ifEmpty){
            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
            adapter.notifyDataSetChanged();
        }


        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setDuration(250);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        alphaAdapter.setFirstOnly(false);
        recyclerView.setAdapter(alphaAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

//设置加载的状态
                if(ifEmpty){
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                }else
                {
                    adapter.changeMoreStatus(ActivityAdapter.LOADING_MORE);}
                //判断到底部的条件
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&  layoutManager.findLastVisibleItemPosition()+1  == adapter.getItemCount()) {
                    if (ifEmpty){
                        //null
                    }
                    else {
                        if (ifsort){
                            sortData(SORT);
                        }
                        else {
                            initData(ADD);}
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy>0){
                    ((MainActivity)getActivity()).ifHideBar(true);
                }
                else {
                    ((MainActivity)getActivity()).ifHideBar(false);
                }
            }
        });

    }
    public void initData(final int state){

        initActivities(state,false, "-createdAt");
    }

    public void sortData(final int state) {

        initActivities(state,true, "date");
    }


    private void initActivities(final int state , final boolean ifSort, final String order){

        if(BmobUser.getCurrentUser(MyUser.class) ==null){
            swipeRefresh.setRefreshing(false);
            return;
        }
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {

                if (e==null){
                    if (getContext()!=null) {
                        ((MainActivity) getContext()).setBmobTime(aLong * 1000L);
                    }

                    if(BmobUser.getCurrentUser(MyUser.class) !=null){
                        final int listsize = activityList.size();
                        BmobQuery<MyActivity> query=condition(order,aLong);
                        query.findObjects(new FindListener<MyActivity>() {
                            @Override
                            public void done(List<MyActivity> object, BmobException e) {
                                if(e==null){
                                    ifsort=ifSort;

                                    if (state==ADD){
                                        activityList.addAll(object);
                                        if (listsize==activityList.size()){
                                            ifEmpty=true;
                                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                            adapter.notifyDataSetChanged();


                                        } else if (listsize+10>activityList.size()){
                                            ifEmpty=true;
                                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                            adapter.notifyItemInserted(adapter.getItemCount()-1);

                                        }

                                        else {

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
                                    swipeRefresh.setRefreshing(false);
                                    Toast.makeText(getContext(),"网络不佳",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }

            }
        });
    }




    public void refresh(){
        size =0;
        swipeRefresh.setRefreshing(true);
        if (ifsort){
            sortData(REFRESH);
        }else {
            initData(REFRESH);}
        if (getActivity() == null){
            return;
        }
    }

    public void setSORT(boolean ifsort){
        this.ifsort = ifsort;
    }



}
