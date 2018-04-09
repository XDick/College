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

import com.college.xdick.college.adapter.ConversationAdapter;
import com.college.xdick.college.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;

/**
 * Created by Administrator on 2018/4/2.
 */

public class FindFragment extends Fragment {
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

        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        for(BmobIMConversation c:list){
            conversationList.add(c);
        }
    }
}
