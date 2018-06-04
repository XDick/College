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

import com.college.xdick.findme.BmobIM.newClass.PrivateConversation;
import com.college.xdick.findme.adapter.ConversationAdapter;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2018/4/2.
 */

public class PrivateConversationFragment extends Fragment implements MessageListHandler {
    View rootview;
    private List<PrivateConversation> conversationList = new ArrayList<>();
    private ConversationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_conversation,container,false);
        initRecyclerView();
        return rootview;


    }
    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_conversation);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationAdapter(conversationList);
        adapter.setFragment(this);
        recyclerView.setAdapter(adapter);


    }


    public void initAllConversation() {

        try {
            if (BmobUser.getCurrentUser() != null) {
                conversationList.clear();
                List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
                for (BmobIMConversation c : list) {

                    conversationList.add(new PrivateConversation(c));

                }
                adapter.notifyDataSetChanged();
            }
            else
            {
                //Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();

            }
            }
        catch (Exception e){
            e.printStackTrace();

            try {
                IMconnectBomob();
            }
            catch (Exception e1){
                e1.printStackTrace();
            }


        }







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
                        } else {
                            //连接失败
                            //Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


}