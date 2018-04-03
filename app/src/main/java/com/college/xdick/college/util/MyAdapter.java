package com.college.xdick.college.util;

import android.media.Image;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.college.xdick.college.R;

import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

       private List<Dynamics> mDynamicsList;


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


       public MyAdapter(List<Dynamics> dynamics){
           mDynamicsList = dynamics;
       }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_dynamics,parent,false);
        ViewHolder holder = new ViewHolder(view);
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
