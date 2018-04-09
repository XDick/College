package com.college.xdick.college.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.college.xdick.college.adapter.ConversationAdapter;
import com.college.xdick.college.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/2.
 */

public class FindFragment extends Fragment implements MessageListHandler {
    View rootview;
    private List<BmobIMConversation> conversationList = new ArrayList<>();
    private ConversationAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_find,container,false);
        initAllConversation();
        initRecyclerView();
        return rootview;


    }
    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationAdapter(conversationList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    }


    private void initAllConversation(){
        if(BmobUser.getCurrentUser()!=null){
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        for(BmobIMConversation c:list){
            conversationList.add(c);
        }}
else{
        Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();}
    }


    @Override
    public void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);

        initRecyclerView();
    }


    public void onMessageReceive(List<MessageEvent> list) {
        initRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }
}
