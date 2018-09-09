package com.college.xdick.findme.ui.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.SearchActivityAdapter;
import com.college.xdick.findme.adapter.SearchUserAdapter;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/6/3.
 */

public class SearchActivity extends BaseActivity {
    private Toolbar toolbar;

     List<MyActivity> placeActivityList = new ArrayList<>();
     List<MyUser> userList = new ArrayList<>();
     String searchMark;
     SearchActivityAdapter adapterPlace;
     SearchUserAdapter adapterUser;

    RecyclerView recyclerViewAcplace,recyclerViewUser;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchMark=getIntent().getStringExtra("SEARCH");
        initView();


    }

    private void initView(){
        toolbar =findViewById(R.id.toolbar_search);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);


        initRecyclerView();
        initUser();
        initPlace();


    }

    private void initRecyclerView(){






        recyclerViewAcplace =  findViewById(R.id.recyclerview_acplace);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        recyclerViewAcplace.setLayoutManager(layoutManager2);

        adapterPlace= new SearchActivityAdapter(placeActivityList);
         adapterPlace.setSearchMark(searchMark);
        recyclerViewAcplace.setNestedScrollingEnabled(false);
        recyclerViewAcplace.setAdapter(adapterPlace);




        recyclerViewUser = findViewById(R.id.recyclerview_user);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);
        recyclerViewUser.setLayoutManager(layoutManager3);

        adapterUser = new SearchUserAdapter(userList);
        adapterUser.setSearchMark(searchMark);
        recyclerViewUser.setNestedScrollingEnabled(false);
        recyclerViewUser.setAdapter(adapterUser);





    }






    private void initUser(){


        BmobQuery<MyUser> q1 = new BmobQuery<>();
        q1.addWhereEqualTo("username",searchMark);

        BmobQuery<MyUser> q2 = new BmobQuery<>();
         q2.addWhereEqualTo("school",searchMark);

         List<BmobQuery<MyUser>> queries = new ArrayList<>();
         queries.add(q1);
         queries.add(q2);


        BmobQuery<MyUser> query = new BmobQuery<>();
        query.or(queries);
        query.setLimit(10);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e==null){

                    if (list.size()>3){
                        View footer = LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_footer_search,recyclerViewUser, false);
                      TextView  more= footer.findViewById(R.id.footer_text);
                        adapterUser.addFooterView(footer);
                        adapterUser.setList(list);
                        recyclerViewUser.setAdapter(adapterUser);
                        more.setText("查看更多");
                    }

                    for (MyUser user:list){
                       userList.add(user);
                        if (userList.size()==3){
                            break;
                        }
                    }
                    adapterUser.notifyDataSetChanged();
                }
            }
        });
    }


    private void initPlace(){

        BmobQuery<MyActivity> q1 = new BmobQuery<>();
        q1.addWhereEqualTo("hostSchool",searchMark);

        BmobQuery<MyActivity> q2 = new BmobQuery<>();
        q2.addWhereContainsAll("gps", Arrays.asList(searchMark));

        BmobQuery<MyActivity> q3 = new BmobQuery<>();
        q3.addWhereEqualTo("title",searchMark);

        BmobQuery<MyActivity> q4 = new BmobQuery<>();
        q4.addWhereContainsAll("tag",Arrays.asList(searchMark));

        List<BmobQuery<MyActivity>> queries = new ArrayList<>();
        queries.add(q1);
        queries.add(q2);
        queries.add(q3);
        queries.add(q4);

        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.or(queries);
        query.setLimit(10);
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                if(e==null){


                  if (list.size()>3){
                      View footer = LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_footer_search,recyclerViewAcplace, false);
                      TextView more= footer.findViewById(R.id.footer_text);
                      adapterPlace.addFooterView(footer);
                      adapterPlace.setList(list);
                      recyclerViewAcplace.setAdapter(adapterPlace);
                      more.setText("查看更多");
                  }
                     Collections.reverse(list);
                    for (MyActivity activity:list){

                        placeActivityList.add(activity);
                        if (placeActivityList.size()==3){
                            break;
                        }
                        adapterPlace.notifyDataSetChanged();

                    }





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
