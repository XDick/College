package com.college.xdick.findme.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.ActivityMessageAdapter;
import com.college.xdick.findme.adapter.DynamicsAdapter;
import com.college.xdick.findme.bean.ActivityMessageBean;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by Administrator on 2018/5/31.
 */

public class UserCenterDynamicsFragment extends BaseFragment {


    private List<Dynamics> dynamicsList= new ArrayList<>();
    private DynamicsAdapter adapter;

    private MyUser nowUser ;
    private  boolean ifEmpty=false;
    private  int size =0;
    static public int ADD=0;
    static public int REFRESH=1;
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private SwipeRefreshLayout swipeRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_usercenter_dynamics,container,false);

        nowUser=(MyUser)getActivity(). getIntent().getSerializableExtra("USER");
        initView();

        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                size = 0;
                initData(REFRESH);
            }
        });

        return rootView;
    }


    private void initView(){

        swipeRefresh = rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
       final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DynamicsAdapter(dynamicsList);
        adapter.setFragment(this);
        adapter.SetFlag(1);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addFooterView(footer);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_dynamics, recyclerView, false);
        adapter.setEmptyView(empty);
        recyclerView.setAdapter(adapter);
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
        query.addWhereEqualTo("myUser",nowUser.getObjectId());
        query.order("-createdAt");
        query.setLimit(20);
        query.include("myUser[avatar|username|school|Exp]" +
                ",activity[title|time|cover].host.[username]");
        query.setSkip(size);
        final int listsize = dynamicsList.size();
                    query.findObjects(new FindListener<Dynamics>() {
                        @Override
                        public void done(List<Dynamics> object, BmobException e) {
                            if(e==null) {
                                dynamicsList.addAll(object);
                                if (state == ADD) {
                                    if (listsize == dynamicsList.size()) {
                                        ifEmpty = true;
                                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                        adapter.notifyDataSetChanged();

                                    } else if (listsize + 20 > dynamicsList.size()) {
                                        ifEmpty = true;
                                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                        adapter.notifyItemInserted(adapter.getItemCount() - 1);

                                    } else {
                                        adapter.notifyItemInserted(adapter.getItemCount() - 1);
                                        size = size + 20;
                                    }
                                } else if (state == REFRESH) {

                                    dynamicsList.clear();

                                    if (object.size() < 20) {
                                        ifEmpty = true;
                                        dynamicsList.addAll(object);
                                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                                        adapter.notifyDataSetChanged();

                                    } else {
                                        ifEmpty = false;
                                        dynamicsList.addAll(object);
                                        adapter.notifyDataSetChanged();
                                    }
                                    size = 20;
                                    swipeRefresh.setRefreshing(false);
                                } else {
                                    Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                                }

                                if (ifEmpty&&dynamicsList.size()!=myUser.getDynamicsCount()&&nowUser.getObjectId().equals(myUser.getObjectId())){
                                    MyUser myUser1= new MyUser();
                                    myUser1.setObjectId(myUser.getObjectId());

                                    myUser1.setDynamicsCount(dynamicsList.size());
                                    myUser1.update(BmobUser.getCurrentUser(MyUser.class).getObjectId(),new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e==null){

                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }

    public void setSize(int size){
        this.size=size;
    }

    private void refresh(){
        size =0;
        swipeRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData(REFRESH);
                if (getActivity() == null){
                    return;}
            }
        }).start();
    }




}
