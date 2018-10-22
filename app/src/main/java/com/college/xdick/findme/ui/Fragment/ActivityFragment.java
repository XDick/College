package com.college.xdick.findme.ui.Fragment;



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

import com.college.xdick.findme.MyClass.GalleryLayoutManager;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;

import com.college.xdick.findme.adapter.DynamicsAdapter;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;

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
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityFragment extends BaseFragment {

   public final int ADD =1;
   public final int REFRESH=2;
   public final int SORT =3;
   private static int size =0;

    private View rootview;
    private List<MyActivity> activityList= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private ActivityAdapter adapter;
    private boolean ifsort = false;
    private boolean ifEmpty= false;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_activity,container,false);
        initBaseView();
        initRecyclerView();

        MainActivity activity = (MainActivity) getActivity();
        List<MyActivity>list=(List<MyActivity>)activity.getIntent().getSerializableExtra("LISTDATA");;

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


         final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActivityAdapter(activityList);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
         adapter.addFooterView(footer);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_activity, recyclerView, false);
        adapter.setEmptyView(empty);

        if (ifEmpty){
            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
            adapter.notifyDataSetChanged();
        }


       // int resId = R.anim.recycler_animation;
       // LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
      // recyclerView.setLayoutAnimation(animation);
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
                if (e==null){
                    if (getContext()!=null) {
                        ((MainActivity) getContext()).setBmobTime(aLong * 1000L);
                    }

                    BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
                    List<BmobQuery<MyActivity>> queries = new ArrayList<>();

                    if( BmobUser.getCurrentUser(MyUser.class)!=null&& BmobUser.getCurrentUser(MyUser.class).getTag()!=null){
                    /*String gps[]=bmobUser.getGps();
                    if (gps!=null){
                        query.addWhereEqualTo("gps",gps[1]);}*/

                        String tag[] =  BmobUser.getCurrentUser(MyUser.class).getTag();
                        if (tag!=null){

                            if (tag.length==0){
                                ifEmpty=true;
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                                return;

                            }
                            for (int i =0; i<tag.length;i++){
                                BmobQuery<MyActivity> q = new BmobQuery<MyActivity>();
                                q.addWhereContainsAll("tag",Arrays.asList(tag[i]));
                                queries.add(q);
                                query.or(queries);
                            }

                        }
                        else {
                            ifEmpty=true;
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                            return;
                        }

                    }
                    query.addWhereGreaterThan("date", aLong*1000L-60*60*24*1000);


                Log.d("","时间"+aLong);
                //返回50条数据，如果不加上这条语句，默认返回10条数据

                query.order("-createdAt");
                query.setLimit(10);

                query.setSkip(size);
                final int listsize = activityList.size();
                query.findObjects(new FindListener<MyActivity>() {
                    @Override
                    public void done(List<MyActivity> object, BmobException e) {
                        if(e==null){


                              ifsort=false;

                              if (state==ADD){
                                  activityList.addAll(object);
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
                            swipeRefresh.setRefreshing(false);
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                );}


            }
        });

    }




    public void refresh(){
        swipeRefresh.setRefreshing(true);


                size =0;

                    if (ifsort){
                        sortData(REFRESH);
                    }else {
                        initData(REFRESH);}

                if (getActivity() == null)
                {
                    return;
                }




    }







    public void sortData(final int state) {

        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {


                if (e==null){
                    if (getContext()!=null) {
                        ((MainActivity) getContext()).setBmobTime(aLong * 1000L);
                    }

                    BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
                    List<BmobQuery<MyActivity>> queries = new ArrayList<>();

                    if( BmobUser.getCurrentUser(MyUser.class)!=null&& BmobUser.getCurrentUser(MyUser.class).getTag()!=null){
                    /*String gps[]=bmobUser.getGps();
                    if (gps!=null){
                        query.addWhereEqualTo("gps",gps[1]);}*/

                    }

                    else {
                        ifEmpty=true;
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        return;
                    }
                    query.addWhereGreaterThan("date", aLong*1000L-60*60*24*1000);



                Log.d("","时间"+aLong);
                if( BmobUser.getCurrentUser(MyUser.class)!=null&& BmobUser.getCurrentUser(MyUser.class).getTag()!=null) {
                    String tag[] =  BmobUser.getCurrentUser(MyUser.class).getTag();
                    if (tag.length==0){
                        ifEmpty=true;
                        adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                            return;

                    }
                    for (int i = 0; i < tag.length; i++) {
                        BmobQuery<MyActivity> q = new BmobQuery<MyActivity>();
                        q.addWhereContainsAll("tag", Arrays.asList(tag[i]));
                        queries.add(q);
                    }
                }
                //返回50条数据，如果不加上这条语句，默认返回10条数据
                query.setLimit(10);
                query.setSkip(size);
                query.or(queries);
                query.order("date");
                final int listsize = activityList.size();
                query.findObjects(new FindListener<MyActivity>() {
                    @Override
                    public void done(List<MyActivity> object, BmobException e) {
                        if(e==null){


                            ifsort=true;

                            if (state==SORT){
                                activityList.addAll(object);
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

                                else {

                                    adapter.changeMoreStatus(ActivityAdapter.PULLUP_LOAD_MORE);
                                    adapter.notifyItemInserted(adapter.getItemCount()-1);
                                    size = size + 10;
                                }
                            }
                            else if (state==REFRESH){
                                activityList.clear();
                               if(activityList.size()<10){
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
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });  }

            }
        });
    }

    public void setSORT(boolean ifsort){
        this.ifsort = ifsort;
    }
}
