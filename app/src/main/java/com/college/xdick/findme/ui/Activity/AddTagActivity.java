package com.college.xdick.findme.ui.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.AddTagAdapter;
import com.college.xdick.findme.adapter.DynamicsAdapter;
import com.college.xdick.findme.adapter.MainTagAdapter;
import com.college.xdick.findme.bean.AddTagBean;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MainTagBean;
import com.college.xdick.findme.bean.MyUser;
import com.donkingliang.labels.LabelsView;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/5/11.
 */

public class AddTagActivity extends AppCompatActivity {
    private MyUser user = BmobUser.getCurrentUser(MyUser.class);
   private List<MainTagBean> mainTagList= new ArrayList<>();
    private MainTagAdapter adapter;
    private List<AddTagBean> addTagList= new ArrayList<>();
    private AddTagAdapter adapter2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtag);


        initData();
        initRecyclerView();

    }


    private void initRecyclerView(){

        RecyclerView recyclerView1 = findViewById(R.id.recyclerview_addmaintag);
        GridLayoutManager layoutManager1 = new  GridLayoutManager(this,1);
        recyclerView1.setLayoutManager(layoutManager1);
        adapter = new MainTagAdapter(mainTagList);
        recyclerView1.setAdapter(adapter);



        RecyclerView recyclerView2 = findViewById(R.id.recyclerview_addaddtag);
        GridLayoutManager layoutManager2 = new  GridLayoutManager(this,1);
        recyclerView2.setLayoutManager(layoutManager2);
        adapter2 = new AddTagAdapter(addTagList);
        recyclerView2.setAdapter(adapter2);

    }


    private void initData() {
        BmobQuery<MainTagBean> query = new BmobQuery<MainTagBean>();
        query.setLimit(999);
//执行查询方法
        query.findObjects(new FindListener<MainTagBean>() {


            @Override
            public void done(List<MainTagBean> object, BmobException e) {
                if (e == null) {
                    for (MainTagBean tag : object) {
                       if (user != null) {
                            String mytag[] = user.getTag();
                            if (!Arrays.toString(mytag).contains(tag.getMainTag())) {
                                mainTagList.add(tag);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        });


        BmobQuery<AddTagBean> query2 = new BmobQuery<AddTagBean>();
        query2.setLimit(999);
//执行查询方法
        query2.findObjects(new FindListener<AddTagBean>() {


            @Override
            public void done(List<AddTagBean> object, BmobException e) {
                if (e == null) {
                    for (AddTagBean tag : object) {
                        if (user != null) {
                            String mytag[] = user.getTag();
                            if (!Arrays.toString(mytag).contains(tag.getAddTag())) {
                                addTagList.add(tag);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        });

    }



}
