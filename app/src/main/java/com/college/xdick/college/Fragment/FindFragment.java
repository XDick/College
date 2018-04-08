package com.college.xdick.college.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.college.xdick.college.IM_util.FriendList;
import com.college.xdick.college.R;
import com.college.xdick.college.IM_util.Friend;
import com.college.xdick.college.IM_util.FriendAdapter;
import com.college.xdick.college.util.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static cn.bmob.newim.db.MigrationHelper.TAG;

/**
 * Created by Administrator on 2018/4/2.
 */

public class FindFragment extends Fragment {
    View rootview;
    private List<FriendList> friendList = new ArrayList<>();
    private FriendAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_find,container,false);
        initRecyclerView();


        String name[] = {"a","SUGAR","李凯凯","叉地克"};

        for(int i = 0; i<4;i++){

            FriendList friend=new FriendList();
            friend.setName(name[i]);
            Log.d(TAG,friend.getName());
            friendList.add(friend);
        }






        return rootview;


    }
    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendAdapter(friendList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    }
}
