package com.college.xdick.findme.ui.Fragment;


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

public class ActivityschoolFragment extends Fragment {
  static int flag_school =0;

    View rootview;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    static private List<MyActivity> activityList3= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private ActivityAdapter adapter;
    private DotsTextView dots;
    private LinearLayout loadlayout;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_activity_school,container,false);


        initBaseView();
        initRecyclerView();

        if(flag_school==0){
            loadlayout.setVisibility(View.VISIBLE);
            dots.start();
            initData();
            flag_school=1;
        }
        if (bmobUser==null){
            loadlayout.setVisibility(View.GONE);
        }

        return rootview;
    }










    private void initBaseView(){

        dots = rootview.findViewById(R.id.dots);
        loadlayout= rootview.findViewById(R.id.loading_layout);
        swipeRefresh =rootview.findViewById(R.id.swipe_refresh_ac_school);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });



    }



    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_ac_school);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
       adapter = new ActivityAdapter(activityList3);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);

         adapter.addFooterView(footer);


        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setDuration(250);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        alphaAdapter.setFirstOnly(false);
        recyclerView.setAdapter(alphaAdapter);

    }

    private void initData(){

        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
                if(activityList3!=null) {activityList3.clear();}
                if (bmobUser!=null) {
                    BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(99);
                    query.addWhereEqualTo("hostSchool", bmobUser.getSchool());
                    if (e==null){
                        query.addWhereGreaterThan("date", aLong*1000L-1.5*60*60*24*1000);}
                    query.findObjects(new FindListener<MyActivity>() {
                        @Override
                        public void done(List<MyActivity> object, BmobException e) {
                            if (e == null) {
                                for (MyActivity activity : object) {
                                    activityList3.add(activity);
                                }
                                Collections.reverse(activityList3); // 倒序排列
                                loadlayout.setVisibility(View.GONE);
                               adapter.notifyDataSetChanged();


                            } else {
                                Toast.makeText(getContext(), "网络不佳", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });



    }




    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    initData();
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
