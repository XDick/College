package com.college.xdick.college.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.college.R;

import com.college.xdick.college.bean.Dynamics;
import com.college.xdick.college.bean.MyActivity;
import com.college.xdick.college.ui.Activity.ActivityActivity;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<MyActivity> mActivityList;
    private Context mContext;



    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView place,title,host,time;
        ImageView cover;
        CardView cardView;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_ac);
            place= view.findViewById(R.id.place_ac);
            host = view.findViewById(R.id.host_ac);
            time = view.findViewById(R.id.time_ac);
            cardView =view.findViewById(R.id.cardview_ac);

            cover = view.findViewById(R.id.cover_ac);
        }
    }


    public ActivityAdapter(List<MyActivity> activity){
        mActivityList = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity,parent,false);
        final ViewHolder holder = new ActivityAdapter.ViewHolder(view);

       holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
               MyActivity activity = mActivityList.get(position);
                Intent intent = new Intent(mContext, ActivityActivity.class);
                intent.putExtra("ACTIVITY_CONTENT",activity.getContent())
                        .putExtra("ACTIVITY_TIME",activity.getTime())
               .putExtra("ACTIVITY_COVER",activity.getCover())
               .putExtra("ACTIVITY_HOST",activity.getHostBy())
                .putExtra("ACTIVITY_TIME",activity.getTime())
                        .putExtra("ACTIVITY_TITLE",activity.getTitle())
                        .putExtra("ACTIVITY_PLACE",activity.getPlace());

                mContext.startActivity(intent);


            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ActivityAdapter.ViewHolder holder, int position) {
        String selectedcolor[]={"#ab269f","#1f22bf","#c4c414","#128b14","#cc141d"};
        String color;
        Random random = new Random();
        int index = random.nextInt(selectedcolor.length);
        color = selectedcolor[index];

        MyActivity activity = mActivityList.get(position);
        holder.title.setText(activity.getTitle());
        holder.place.setText(activity.getPlace());
        holder.host.setText("发起人"+activity.getHostBy());
        holder.time.setText(activity.getTime());
        holder.cover.setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
        Glide.with(mContext).load(activity.getCover()).into(holder.cover);
    }



    @Override
    public int getItemCount() {
        if (mActivityList != null) {
            return mActivityList.size();
        }
        return 0;
    }

    }