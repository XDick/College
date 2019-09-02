package com.college.xdick.findme.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.GoodsAdapter;
import com.college.xdick.findme.bean.Goods;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.ui.Activity.DetailActivityActivity;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class GoodsFragment extends BaseFragment {
    private String tag=null;
    private SwipeRefreshLayout swipeRefresh;
    private GoodsAdapter adapter;
    private List<Goods> goodsList = new ArrayList<>();
    protected boolean ifEmpty= false;
    public final int ADD =1;
    public final int REFRESH=2;
    protected  int size =0;
    private String subTag=null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_activity,container,false);
        if (((DetailActivityActivity)getActivity()).tagBean!=null){
            tag = ((DetailActivityActivity)getActivity()).tagBean.getMainTag();
        }


        initBaseView();
        initRecyclerView();
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                size=0;
                initData(REFRESH);
            }
        });
        return rootView;
    }

    private void initBaseView(){
        swipeRefresh =rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void initRecyclerView(){

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GoodsAdapter(goodsList);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_goods, recyclerView, false);



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
                if (ifEmpty) {
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                } else {
                    adapter.changeMoreStatus(ActivityAdapter.LOADING_MORE);
                }
                //判断到底部的条件
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition() + 1 == adapter.getItemCount()) {
                    if (ifEmpty) {
                        //null
                    } else {


                        initData(ADD);
                    }
                }

                }



        });




    }


    private void initData(final int state){
        BmobQuery<Goods> query = new BmobQuery<>();
        if (tag!=null){
            query.addWhereContainsAll("tag", Arrays.asList(tag));
        }
        if (subTag!=null){
            query.addWhereContainsAll("tag", Arrays.asList(subTag));
        }
        query.addWhereExists("content");
        query.setLimit(10);
        final int listsize =goodsList.size();
        query.findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {
                if(e==null){

                    if (state==ADD){
                        goodsList.addAll(object);
                        if (listsize==goodsList.size()){
                            ifEmpty=true;
                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                            adapter.notifyDataSetChanged();


                        } else if (listsize+10>goodsList.size()){
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
                        goodsList.clear();
                        if(object.size()<10){
                            ifEmpty=true;
                            goodsList.addAll(object);
                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            ifEmpty=false;
                            goodsList.addAll(object);
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
    public void refresh(){
        size =0;
        swipeRefresh.setRefreshing(true);

            initData(REFRESH);
        if (getActivity() == null){
            return;
        }
    }
    public void setSubTag(String subTag){this.subTag=subTag;}

}
