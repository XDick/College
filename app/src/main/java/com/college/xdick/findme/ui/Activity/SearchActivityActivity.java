package com.college.xdick.findme.ui.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.SearchActivityAdapter;
import com.college.xdick.findme.adapter.SearchUserAdapter;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/6/3.
 */

public class SearchActivityActivity extends BaseActivity {
    Toolbar toolbar;
    RecyclerView recyclerViewAcplace;
    SearchActivityAdapter adapter;
    List<MyActivity> placeActivityList = new ArrayList<>();

    private int size =10;
    private boolean ifEmpty=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activity);
        try {
            placeActivityList.addAll((List<MyActivity>)getIntent().getSerializableExtra("ACTIVITYLIST"));
        }
        catch (Exception e){
            e.printStackTrace();
        }







        toolbar =findViewById(R.id.toolbar_search);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);
        initRecyclerView();


    }

    private void initRecyclerView(){
        recyclerViewAcplace =  findViewById(R.id.recyclerview_acplace);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewAcplace.setLayoutManager(layoutManager);
        adapter= new SearchActivityAdapter(placeActivityList);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer,recyclerViewAcplace, false);
        adapter.addFooterView(footer);
        recyclerViewAcplace.setNestedScrollingEnabled(false);
        recyclerViewAcplace.setAdapter(adapter);
        if (placeActivityList.size()<10){
            adapter.changeMoreStatus(SearchActivityAdapter.NO_MORE);
            adapter.notifyDataSetChanged();
        }

        recyclerViewAcplace.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(ifEmpty){
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                }else
                {
                    adapter.changeMoreStatus(ActivityAdapter.LOADING_MORE);}
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&  layoutManager.findLastVisibleItemPosition()+1  == adapter.getItemCount()) {
                    if (ifEmpty){
                        //null
                    }
                    else {initData();}
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }



    private void initData(){
        BmobQuery<MyActivity> q1 = new BmobQuery<>();
        q1.addWhereEqualTo("hostSchool",getIntent().getStringExtra("EXTRA"));

        BmobQuery<MyActivity> q2 = new BmobQuery<>();
        q2.addWhereContainsAll("gps", Arrays.asList(getIntent().getStringExtra("EXTRA")));

        BmobQuery<MyActivity> q3 = new BmobQuery<>();
        q3.addWhereEqualTo("title",getIntent().getStringExtra("EXTRA"));

        BmobQuery<MyActivity> q4 = new BmobQuery<>();
        q4.addWhereContainsAll("tag",Arrays.asList(getIntent().getStringExtra("EXTRA")));

        List<BmobQuery<MyActivity>> queries = new ArrayList<>();
        queries.add(q1);
        queries.add(q2);
        queries.add(q3);
        queries.add(q4);

        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.or(queries);
        query.setLimit(10);
        query.setSkip(size);
        final int listsize = placeActivityList.size();
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(final List<MyActivity> list, BmobException e) {
                if(e==null){
                    placeActivityList.addAll( list);
                    if (listsize == placeActivityList.size()) {
                        ifEmpty = true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyDataSetChanged();
                    } else if (listsize + 10 > placeActivityList.size()) {
                        ifEmpty = true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    }


                    else {
                        adapter.notifyItemInserted(adapter.getItemCount()-1);
                        size = size + 10;
                    }
                }



                else
                {
                    Toast.makeText(SearchActivityActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
