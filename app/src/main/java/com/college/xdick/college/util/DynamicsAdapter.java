package com.college.xdick.college.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.college.xdick.college.Activity.AskChatActivity;
import com.college.xdick.college.Activity.AskChatActivity;
import com.college.xdick.college.IM_util.AddFriendMessage;
import com.college.xdick.college.IM_util.Friend;
import com.college.xdick.college.R;

import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DynamicsAdapter extends RecyclerView.Adapter<DynamicsAdapter.ViewHolder> {

       private List<Dynamics> mDynamicsList;
       private Context mContext;


       static class ViewHolder extends RecyclerView.ViewHolder{

           TextView content,title,username,time;


           public ViewHolder(View view){
               super(view);
              content = view.findViewById(R.id.content_main);
              title= view.findViewById(R.id.title_main);
              username = view.findViewById(R.id.username_main);
              time = view.findViewById(R.id.time_main);
           }
       }


       public DynamicsAdapter(List<Dynamics> dynamics){
           mDynamicsList = dynamics;
       }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamics,parent,false);
        final ViewHolder holder = new ViewHolder(view);

       /* holder.username.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
              *//*  int position = holder.getAdapterPosition();
                Dynamics dynamics = mDynamicsList.get(position);*//*
                Intent intent = new Intent(mContext , AskChatActivity.class);
                mContext.startActivity(intent);

            }
        });*/


           return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dynamics dynamics = mDynamicsList.get(position);
        holder.content.setText(dynamics.getContent());
        holder.title.setText(dynamics.getTitle());
        holder.username.setText(dynamics.getUser());
        holder.time.setText(dynamics.getCreatedAt());

    }

    @Override
    public int getItemCount() {
        return mDynamicsList.size();
    }
}
