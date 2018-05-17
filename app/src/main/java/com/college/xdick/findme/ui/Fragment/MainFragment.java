package com.college.xdick.findme.ui.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.DynamicsActivity;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.adapter.DynamicsAdapter;




import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import pl.tajchert.waitingdots.DotsTextView;

/**
 * Created by Administrator on 2018/4/2.
 */

public  class MainFragment extends Fragment {
    View rootview;
    private RecyclerView recyclerView;
    private DotsTextView dots;
    private LinearLayout loadlayout;
    private   Toolbar toolbar;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private static List<Dynamics> dynamicsList= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private DynamicsAdapter adapter;
    private static boolean flag=true;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_main,container,false);

        initBaseView();

        initRecyclerView();
        if (flag){
            loadlayout.setVisibility(View.VISIBLE);
            dots.start();
        initData();
        flag=false;}
        setHasOptionsMenu(true);



        return rootview;
    }










    private void initBaseView() {
        toolbar = rootview.findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        swipeRefresh = rootview.findViewById(R.id.swipe_refresh_main);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        dots = rootview.findViewById(R.id.dots);
        loadlayout= rootview.findViewById(R.id.loading_layout);




    }











    private void initRecyclerView(){

        recyclerView = rootview.findViewById(R.id.recyclerview_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DynamicsAdapter(dynamicsList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    }

  private void initData(){

        dynamicsList.clear();

      BmobQuery<Dynamics> query = new BmobQuery<Dynamics>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
      query.setLimit(99);
//执行查询方法
      query.findObjects(new FindListener<Dynamics>() {
          @Override
          public void done(List<Dynamics> object, BmobException e) {
              if(e==null){
                  for (Dynamics dynamics : object) {
                      dynamicsList.add(dynamics);}
                  Collections.reverse(dynamicsList); // 倒序排列
                  loadlayout.setVisibility(View.GONE);
                 // Toast.makeText(getContext(),"成功接收内容",Toast.LENGTH_SHORT).show();
                  adapter.notifyDataSetChanged();


              }else{
                  Toast.makeText(getContext(),"网络不佳",Toast.LENGTH_SHORT).show();
              }
          }
      });
  }






    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.toolbar_main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {



                case R.id.add:

                    if(bmobUser!=null){


                    Intent intent = new Intent(getContext(), DynamicsActivity.class);
                    startActivity(intent);}


                    else
                    {Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                    }
                    break;

            default:
                break;
        }

        return true;
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




}
