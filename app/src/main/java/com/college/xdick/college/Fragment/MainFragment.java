package com.college.xdick.college.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.college.Activity.DynamicsActivity;
import com.college.xdick.college.Activity.LoginActivity;
import com.college.xdick.college.Activity.MainActivity;
import com.college.xdick.college.R;
import com.college.xdick.college.util.Dynamics;
import com.college.xdick.college.util.MyAdapter;
import com.college.xdick.college.util.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/4/2.
 */

public  class MainFragment extends Fragment {
    View rootview;
    private   NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private View headview;
    private TextView username;
    private  User bmobUser = BmobUser.getCurrentUser(User.class);
    static private List<Dynamics> dynamicsList= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private MyAdapter adapter;
    static private int flag=0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_main,container,false);

        if(flag==0){
            FirstInitFromActivity();
        }
        initBaseView();
        initRecyclerView();
        setHasOptionsMenu(true);
        BmobCheckIfLogin();







        return rootview;
    }










    private void initBaseView(){
        Toolbar toolbar =rootview.findViewById(R.id.toolbar_main);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        swipeRefresh =rootview.findViewById(R.id.swipe_refresh_main);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mDrawerLayout = rootview.findViewById(R.id.drawer_layout);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.drawer);
        navigationView =rootview.findViewById(R.id.nav_view);
        headview = navigationView.inflateHeaderView(R.layout.nav_header);
        navigationView.inflateMenu(R.menu.nav_menu);
        headview = navigationView.getHeaderView(0);
        username =headview.findViewById(R.id.username);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


             switch (item.getItemId()){

                case R.id.me:

                break;

                default:
                    break;

             }

                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }


    private void showUserInfo(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                username.setText(bmobUser.getUsername());
            }
        });
    }
    private void exitUserInfo(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                username.setText("点击头像以登录");
            }
        });
    }



    private void BmobCheckIfLogin(){


        if(bmobUser != null){
            showUserInfo();

            headview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                    intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                    startActivityForResult(intent, 1);

                }}

            );

        }else{
            headview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                }

            });
        }
    }


    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(dynamicsList);
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
                 // Toast.makeText(getContext(),"成功接收内容",Toast.LENGTH_SHORT).show();

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

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

                case R.id.add:
                    Intent intent = new Intent(getContext(), DynamicsActivity.class);
                    startActivity(intent);

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
                  Thread.sleep(2000);
              }
              catch (InterruptedException e){
                  e.printStackTrace();
              }
              getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {

                      adapter.notifyDataSetChanged();
                      swipeRefresh.setRefreshing(false);
                  }
              });
          }
      }).start();
    }


    private  void FirstInitFromActivity(){
        MainActivity activity = (MainActivity) getActivity();
        if (activity.getListData().isEmpty()){
            Toast.makeText(getContext(),"糟糕，没加载出来TAT",Toast.LENGTH_SHORT).show();
        }
        if(dynamicsList.isEmpty()){
            dynamicsList = activity.getListData();
        }
        flag++;
    }

}
