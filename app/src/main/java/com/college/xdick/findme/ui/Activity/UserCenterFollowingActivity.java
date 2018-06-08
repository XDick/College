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

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.UserCenterUserAdapter;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/6/3.
 */

public class UserCenterFollowingActivity extends AppCompatActivity {
     Toolbar toolbar;
    RecyclerView recyclerViewUser;
    UserCenterUserAdapter adapterUser;
    List<MyUser> userList = new ArrayList<>();
    MyUser nowUser ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
          nowUser = (MyUser)getIntent().getSerializableExtra("USER");


        toolbar =findViewById(R.id.toolbar_search);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);




        recyclerViewUser = findViewById(R.id.recyclerview_user);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);
        recyclerViewUser.setLayoutManager(layoutManager3);

        adapterUser = new  UserCenterUserAdapter(userList);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer,recyclerViewUser, false);
        adapterUser.addFooterView(footer);
        recyclerViewUser.setNestedScrollingEnabled(false);
        recyclerViewUser.setAdapter(adapterUser);
        initFollowing();

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


    private void initFollowing(){

        BmobQuery<MyUser> query = new BmobQuery<>();
        String[] following= nowUser.getFollowing();
        query.addWhereContainedIn("objectId", Arrays.asList(following));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
            if (e==null){
                userList.addAll(list);
                adapterUser.notifyDataSetChanged();
            }
            }
        });


    }
}
