package com.college.xdick.findme.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.college.xdick.findme.BmobIM.newClass.PrivateConversation;
import com.college.xdick.findme.adapter.ConversationAdapter;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by Administrator on 2018/4/2.
 */

public class PrivateConversationFragment extends BaseFragment implements MessageListHandler {

    private List<PrivateConversation> conversationList = new ArrayList<>();
    private ConversationAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_conversation,container,false);
        initView();
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                initAllConversation();
            }
        });






        return rootView;


    }
    private void initView(){
        initRecyclerView();
        swipeRefresh =rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }
    public void initRecyclerView(){

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview_conversation);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationAdapter(conversationList);
        adapter.setFragment(this);
        recyclerView.setAdapter(adapter);


    }


    public void initAllConversation() {

       if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.CONNECTED)){
           if (BmobUser.getCurrentUser() != null) {
               conversationList.clear();
               List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
               for (BmobIMConversation c : list) {
                   conversationList.add(new PrivateConversation(c));

               }

               adapter.notifyDataSetChanged();
               swipeRefresh.setRefreshing(false);
           }
           else
           {
               //Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
           }
       }
      else {
            try {
                IMconnectBomob();
            }
            catch (Exception e1){
                e1.printStackTrace();
            }


        }







    }

    public void deleteAllConversation(){
        BmobIM.getInstance().clearAllConversation();
        initAllConversation();
    }

    @Override
    public void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);


        initAllConversation();
         initRecyclerView();
    }


    public void onMessageReceive(List<MessageEvent> list) {

        initAllConversation();
        initRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }

    private void IMconnectBomob() {

        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        final MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            if (!TextUtils.isEmpty(bmobUser.getObjectId())) {
                BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            BmobIM.getInstance().
                                    updateUserInfo(new BmobIMUserInfo(bmobUser.getObjectId(),
                                    bmobUser.getUsername(), bmobUser.getAvatar()));
                            initAllConversation();
                        } else {
                            //连接失败
                          //  Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initAllConversation();
                        }
                    });

                    Thread.sleep(2000);
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
