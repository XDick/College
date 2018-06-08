package com.college.xdick.findme.ui.Fragment;


import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.DynamicsAdapter;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import pl.tajchert.waitingdots.DotsTextView;

/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivitygpsFragment extends Fragment {
    static int flag_gps =0;
    public final int ADD =1;
    public final int REFRESH=2;
    public final int SORT =3;
    private static int size =0;

    View rootview;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    static private List<MyActivity> activityList2= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private ActivityAdapter adapter;
    private DotsTextView dots;
    private LinearLayout loadlayout;
    static boolean ifsort = false;
    static boolean ifEmpty= false;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_activity_gps,container,false);



        initBaseView();
        initRecyclerView();
        if(flag_gps==0){
            loadlayout.setVisibility(View.VISIBLE);
            dots.start();
            initData(REFRESH);
            flag_gps=1;
        }
        if (bmobUser==null){
            loadlayout.setVisibility(View.GONE);
        }

        return rootview;
    }










    private void initBaseView(){

        dots = rootview.findViewById(R.id.dots);
        loadlayout= rootview.findViewById(R.id.loading_layout);
        swipeRefresh =rootview.findViewById(R.id.swipe_refresh_ac_gps);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });



    }



    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_ac_gps);
       final  LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
       adapter = new ActivityAdapter(activityList2);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty, recyclerView, false);
        adapter.setEmptyView(empty);
         adapter.addFooterView(footer);



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
            }
        });

    }

    public void initData(final int state){
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {


                if(bmobUser!=null){



                    String[] gps = bmobUser.getGps();

                    BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
                    if (gps!=null){
                    query.addWhereEqualTo("gps", gps[2]);}
                    if (e==null){
                    query.addWhereGreaterThan("date", aLong*1000L-1.5*60*60*24*1000);}
                    query.order("-createdAt");
                    query.setSkip(size);
                    query.setLimit(10);
                final int listsize = activityList2.size();
                    query.findObjects(new FindListener<MyActivity>() {
                        @Override
                        public void done(List<MyActivity> object, BmobException e) {
                            if(e==null){
                                ifsort=false;
                                activityList2.addAll(object);
                                if (state==ADD){
                                    if (listsize==activityList2.size()){
                                        ifEmpty=true;
                                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                        adapter.notifyDataSetChanged();


                                    } else if (listsize+10>activityList2.size()){
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
                                    ifEmpty=false;
                                    activityList2.clear();
                                    activityList2.addAll(object);
                                    adapter.notifyDataSetChanged();
                                    size =  10;
                                }

                            }else{
                                Toast.makeText(getContext(),"网络不佳",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });



    }




    private void refresh(){
        size =0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (ifsort){
                        sortData(REFRESH);
                    }else {
                        initData(REFRESH);}
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





    public void sortData(final int state) {

        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {




                if(bmobUser!=null){



                    String[] gps = bmobUser.getGps();

                    BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
                    if (gps!=null){
                        query.addWhereEqualTo("gps", gps[2]);}
                    if (e==null){
                        query.addWhereGreaterThan("date", aLong*1000L-1.5*60*60*24*1000);}
                    query.setSkip(size);
                    query.setLimit(10);
                    query.order("date");
                    final int listsize = activityList2.size();
                    query.findObjects(new FindListener<MyActivity>() {
                        @Override
                        public void done(List<MyActivity> object, BmobException e) {
                            if(e==null){
                                ifsort=true;
                                activityList2.addAll(object);
                                if (state==SORT){
                                    if (listsize==activityList2.size()){
                                        ifEmpty=true;
                                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                        adapter.notifyDataSetChanged();
                                    } else if (listsize+10>activityList2.size()){
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
                                      ifEmpty=false;
                                    activityList2.clear();
                                    activityList2.addAll(object);
                                    adapter.notifyDataSetChanged();
                                    size =  10;
                                }

                            }else{
                                Toast.makeText(getContext(),"网络不佳",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });





    }


    public void setSize(int size1){
        size=size1;
    }

}
