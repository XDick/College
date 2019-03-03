package com.college.xdick.findme.ui.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.college.xdick.findme.MyClass.GalleryLayoutManager;
import com.college.xdick.findme.MyClass.PicDynamicsMap;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.MorePicAdapter;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.ui.Base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class GalleryActivity extends BaseActivity {

    private MorePicAdapter adapter;
    private Map<String,Dynamics> map= new HashMap<>();
    private PicDynamicsMap picDynamicsMap;
    private List<String> picList= new ArrayList<>();
    private RecyclerView recyclerView;
    private String activityId;
    private int LIMIT=10;
    private int size=LIMIT;
    private boolean ifEmpty=false;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);
        picDynamicsMap= (PicDynamicsMap) getIntent().getSerializableExtra("MAP");
        picList = getIntent().getStringArrayListExtra("PIC");
        activityId=getIntent().getStringExtra("ACTIVITYID");
        map=picDynamicsMap.getMap();
        if (picList!=null){
            if (picList.size()<8){
                ifEmpty=true;
            }
        }
        adapter= new MorePicAdapter(picList,map);

        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer_gallery, recyclerView, false);
        adapter.addFooterView(footer);

        if (ifEmpty){
            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
            adapter.notifyDataSetChanged();
        }
        recyclerView = findViewById(R.id.gallery_recycler);
        final GridLayoutManager layoutManager =  new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(ifEmpty){
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                }else
                {
                    adapter.changeMoreStatus(ActivityAdapter.LOADING_MORE);}
                //判断到底部的条件
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition()+1  == adapter.getItemCount()) {
                    if (ifEmpty){
                    }
                    else {
                       initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }


    private void initData(){

        BmobQuery<Dynamics> query = new BmobQuery<>();

        BmobQuery<Dynamics> q1 = new BmobQuery<>();
        q1.addWhereEqualTo("ifAdd2Gallery",true);
        query.and(Arrays.asList(q1));

        query.addWhereEqualTo("activityId",activityId);
        query.order("-createdAt");
        query.setLimit(LIMIT);
        query.setSkip(size);
        query.findObjects(new FindListener<Dynamics>() {
            @Override
            public void done(List<Dynamics> list, BmobException e) {
                if (e==null){
                    recyclerView.setVisibility(View.VISIBLE);

                    for (Dynamics dynamics: list){
                        picList.addAll(Arrays.asList(dynamics.getPicture()));
                        List<String>uri = new ArrayList<>(Arrays.asList(dynamics.getPicture()));
                        for (String str:uri){
                            map.put(str,dynamics);
                        }
                    }

                    if (list.isEmpty()){
                        ifEmpty=true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                  else if (list.size()<7){
                        ifEmpty=true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyItemInserted(adapter.getItemCount()-1);
                        return;
                    }
                  size=size+LIMIT;
                    adapter.notifyItemInserted(adapter.getItemCount()-1);



                }
            }
        });



    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case  android.R.id.home:
            {
                finish();
                break;
            }


            default:
                break;
        }

        return true;
    }
}
