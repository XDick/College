package com.college.xdick.findme.ui.Fragment;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;

import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.college.xdick.findme.ui.Activity.MainActivity.flag;

/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityFragment extends Fragment {


    View rootview;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    static private List<MyActivity> activityList= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private ActivityAdapter adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_activity,container,false);



        if(flag==0){
            FirstInitFromActivity();
        }

        initBaseView();
        initRecyclerView();


        return rootview;
    }










    private void initBaseView(){


        swipeRefresh =rootview.findViewById(R.id.swipe_refresh_ac);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });



    }



    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_ac);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
       adapter = new ActivityAdapter(activityList);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
         adapter.addFooterView(footer);

       // int resId = R.anim.recycler_animation;
       // LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
       // recyclerView.setLayoutAnimation(animation);
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

                if(activityList!=null) {activityList.clear();}

                BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();

                if(bmobUser!=null&&bmobUser.getTag()!=null){
                    /*String gps[]=bmobUser.getGps();
                    if (gps!=null){
                        query.addWhereEqualTo("gps",gps[1]);}*/

                }

                if (e==null){
                    query.addWhereGreaterThan("date", aLong*1000L-1.5*60*60*24*1000);}

                Log.d("","时间"+aLong);
                //返回50条数据，如果不加上这条语句，默认返回10条数据
                query.setLimit(99);
//执行查询方法
                query.findObjects(new FindListener<MyActivity>() {
                    @Override
                    public void done(List<MyActivity> object, BmobException e) {
                        if(e==null){
                            for (MyActivity activity : object) {
                                if (bmobUser != null) {
                                    String acTag[] = activity.getTag();
                                    String tag[] = bmobUser.getTag();
                                    int ArrayLength = acTag.length;

                                    if (Arrays.toString(tag).contains(acTag[0]) || Arrays.toString(tag).contains(acTag[ArrayLength - 1])) {
                                        activityList.add(activity);
                                    }

                                }
                                else
                                {
                                    activityList.add(activity);

                                }

                            }

                            Collections.reverse(activityList); // 倒序排列

                            adapter.notifyDataSetChanged();

                            // Toast.makeText(getContext(),"成功接收内容",Toast.LENGTH_SHORT).show();

                        }else{

                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }




    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    initData();
                    Thread.sleep(1000);
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






    private  void FirstInitFromActivity(){
        MainActivity activity = (MainActivity) getActivity();


            activityList = activity.getListData();

        flag=1;

    }



}
