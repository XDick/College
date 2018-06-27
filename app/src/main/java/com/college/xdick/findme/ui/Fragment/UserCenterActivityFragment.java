package com.college.xdick.findme.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.ActivityAdapter2;
import com.college.xdick.findme.adapter.ActivityAdapter3;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/5/31.
 */

public class UserCenterActivityFragment extends Fragment {
   private View rootView;
    private List<MyActivity> activityList= new ArrayList<>();
    private ActivityAdapter3 adapter;
    private MyUser nowUser ;
    private int size = 0;
    private  boolean ifEmpty=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_usercenter_activity,container,false);
        nowUser=(MyUser)getActivity(). getIntent().getSerializableExtra("USER");
         initView();
        initData();
        return rootView;
    }

    private void initView(){
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        final  LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ActivityAdapter3(activityList);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addFooterView(footer);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(ifEmpty){
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                }else
                {
                    adapter.changeMoreStatus(ActivityAdapter3.LOADING_MORE);}
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
    public void initData(){
        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.addWhereEqualTo("host",nowUser);
        query.order("-createdAt");
        query.setLimit(10);
        query.setSkip(size);
        final int listsize = activityList.size();
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                if (e==null){

                    activityList.addAll(list);

                    if (listsize== activityList.size()){
                        ifEmpty=true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyDataSetChanged();
                    }else if (listsize+10>activityList.size()){
                        ifEmpty=true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyItemInserted(adapter.getItemCount()-1);

                    }


                    else {
                        adapter.notifyItemInserted(adapter.getItemCount()-1);
                        size = size + 10;
                    }
                }else{
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }



            }
        });


    }
}
