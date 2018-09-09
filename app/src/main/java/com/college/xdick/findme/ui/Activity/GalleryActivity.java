package com.college.xdick.findme.ui.Activity;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.college.xdick.findme.MyClass.PicDynamicsMap;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.MorePicAdapter;
import com.college.xdick.findme.bean.Dynamics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GalleryActivity extends BaseActivity {

    private MorePicAdapter adapter;
    private Map<String,Dynamics> map;
    private PicDynamicsMap picDynamicsMap;
    private List<String> picList= new ArrayList<>();
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        picDynamicsMap= (PicDynamicsMap) getIntent().getSerializableExtra("MAP");
        picList = getIntent().getStringArrayListExtra("PIC");
        map=picDynamicsMap.getMap();

        recyclerView = findViewById(R.id.gallery_recycler);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter= new MorePicAdapter(picList,map);
        recyclerView.setAdapter(adapter);
    }
}
