package com.college.xdick.findme.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.SetDynamicsActivity;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.adapter.DynamicsAdapter;
import com.college.xdick.findme.ui.Base.BaseFragment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


/**
 * Created by Administrator on 2018/4/2.
 */

public  class DynamicsFragment extends BaseFragment {
    View rootview;
    private RecyclerView recyclerView;

    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private static List<Dynamics> dynamicsList= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private DynamicsAdapter adapter;
    private static int size =0;
    private static boolean ifEmpty=false;
    static public int REFRESH=1;
    static public int ADD=2;
    private FloatingActionButton floatingActionButton;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_dynamics,container,false);

        initBaseView();

        initRecyclerView();

       if(bmobUser!=null){
           swipeRefresh.post(new Runnable() {
               @Override
               public void run() {
                   swipeRefresh.setRefreshing(true);
                   size=0;
                   initData(REFRESH);
               }
           });

       }


        setHasOptionsMenu(true);



        return rootview;
    }










    private void initBaseView() {
      /*  toolbar = rootview.findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);*/
        swipeRefresh = rootview.findViewById(R.id.swipe_refresh_main);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

         floatingActionButton = rootview.findViewById(R.id.floatactionbutton);
         floatingActionButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(bmobUser!=null){


                     Intent intent = new Intent(getContext(), SetDynamicsActivity.class);
                     startActivity(intent);}


                 else
                 {Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                 }

             }
         });



    }











    private void initRecyclerView(){

        recyclerView = rootview.findViewById(R.id.recyclerview_main);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DynamicsAdapter(dynamicsList);
        adapter.setFragment(this);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addFooterView(footer);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_dynamics, recyclerView, false);
        adapter.setEmptyView(empty);
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setDuration(250);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        alphaAdapter.setFirstOnly(false);
        recyclerView.setAdapter(alphaAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.recycler_decoration));
        recyclerView.addItemDecoration(decoration);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                  else {initData(ADD);}
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

  public void initData(final int state){
                  BmobQuery<Dynamics> query = new BmobQuery<Dynamics>();

                  String following[] = BmobUser.getCurrentUser(MyUser.class).getFollowing();
      if (following!=null){
          final List<String> list =new ArrayList<>(Arrays.asList(following));
          list.add(bmobUser.getObjectId());

          query.addWhereContainedIn("userId", list);
          query.order("-createdAt");
          query.setLimit(20);
          query.setSkip(size);
          final int listsize = dynamicsList.size();


          query.findObjects(new FindListener<Dynamics>() {
              @Override
              public void done(List<Dynamics> object, BmobException e) {
                  if (e == null) {
                      dynamicsList.addAll(object);
                      if (state==ADD) {
                          if (listsize == dynamicsList.size()) {
                              ifEmpty = true;
                              adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                              adapter.notifyDataSetChanged();
                          } else if (listsize + 20 > dynamicsList.size()) {
                              ifEmpty = true;
                              adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                              adapter.notifyItemInserted(adapter.getItemCount() - 1);

                          }


                          else {
                              adapter.notifyItemInserted(adapter.getItemCount()-1);
                              size = size + 20;
                          }
                      }
                      else if (state==REFRESH){

                          dynamicsList.clear();

                          if(object.size()<20){
                              ifEmpty=true;
                              dynamicsList.addAll(object);
                              adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                              adapter.notifyDataSetChanged();
                          }
                          else {
                              ifEmpty=false;
                              dynamicsList.addAll(object);
                              adapter.notifyDataSetChanged();}
                          size=20;

                          swipeRefresh.setRefreshing(false);

                      }

                  } else {
                      Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


                  }
              }

          });


      }
      else {
          ifEmpty = true;
          swipeRefresh.setRefreshing(false);

      }



  }





    private void refresh(){


        dynamicsList.clear();
        size=0;
        swipeRefresh.setRefreshing(true);



      new Thread(new Runnable() {
          @Override
          public void run() {

                 
                  initData(REFRESH);

              if (getActivity() == null)
                  return;
              getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {

                  }
              });
          }
      }).start();
    }


public void setSize(int size){
        this.size=size;
}


}
