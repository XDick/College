package com.college.xdick.findme.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.provider.CalendarContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;

import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;


    private List<MyActivity> mActivityList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;
    private int JoinSize;

    private String selectedcolor[]={"#7e07ce","#0d80c2","#c4c414","#0daf33","#cf0003"};
    private List<String> colorList = Arrays.asList(selectedcolor);
    private boolean ifShuffle=true;





    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView place,title,host,time,join,gps,tag,date;
        ImageView cover;
        CardView cardView;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_ac);
            place= view.findViewById(R.id.place_ac);
            host = view.findViewById(R.id.host_ac);
            time = view.findViewById(R.id.time_ac);
            cardView =view.findViewById(R.id.cardview_ac);
            join=view.findViewById(R.id.join_ac);
            cover = view.findViewById(R.id.cover_ac);
            gps=view.findViewById(R.id.gps_ac);
            tag = view.findViewById(R.id.tag_ac);
            date = view. findViewById(R.id.date_ac);
        }
    }


    public ActivityAdapter(List<MyActivity> activity){
        mActivityList = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(ifShuffle) {
            Collections.shuffle(colorList);
        ifShuffle=false;
        }

        if(mContext == null){
            mContext = parent.getContext();
        }


        if (viewType == ITEM_TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else if (viewType == ITEM_TYPE_EMPTY) {
            return new ViewHolder(mEmptyView);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new ViewHolder(mFooterView);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activity, parent, false);
            final ViewHolder holder = new ActivityAdapter.ViewHolder(view);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    MyActivity activity = mActivityList.get(position);
                    Intent intent = new Intent(mContext, ActivityActivity.class);
                    intent.putExtra("ACTIVITY_CONTENT", activity.getContent())
                            .putExtra("ACTIVITY_TIME", activity.getTime())
                            .putExtra("ACTIVITY_COVER", activity.getCover())
                            .putExtra("ACTIVITY_HOST", activity.getHostName())
                            .putExtra("HOST_ID", activity.getHost().getObjectId())
                            .putExtra("ACTIVITY_TITLE", activity.getTitle())
                            .putExtra("ACTIVITY_PLACE", activity.getPlace())
                            .putExtra("ACTIVITY_ID", activity.getObjectId());
                    mContext.startActivity(intent);


                }
            });
            return holder;
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (null != mHeaderView && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (null != mFooterView
                && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        }
        if (null != mEmptyView && mActivityList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }


    @Override
    public void onBindViewHolder(final ActivityAdapter.ViewHolder holder, int position) {

        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_FOOTER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        int realPos = getRealItemPosition(position);

        String[] newColor =  (String[]) colorList.toArray();
        String color;
        color = newColor[(realPos)%5];
        MyActivity activity = mActivityList.get(realPos);
        String gps[]=activity.getGps();
        String tag =Arrays.toString(activity.getTag());
        if(tag.equals(null)){
            tag=" ";
        }
        tag = tag.replace("[","");
        tag = tag.replace("]","");
        tag = tag.replace(","," ");
        String rawTime=activity.getTime();
       String time = rawTime.substring(rawTime.indexOf(" "));
       String date = rawTime.substring(0,rawTime.indexOf(" "));

        holder.title.setText(activity.getTitle());
        holder.place.setText(activity.getPlace());
        holder.host.setText("发起人"+activity.getHostName());


        holder.time.setText(time);
        holder.date.setText(date);



        holder.tag.setText(tag);

        holder.cover.setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);


       if (gps!=null){
        holder.gps.setText(gps[2]);}
        Glide.with(mContext).load(activity.getCover()).apply(bitmapTransform(new BlurTransformation(9, 3))).into(holder.cover);



        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereContainsAll("join", Arrays.asList(activity.getObjectId()));
        query.setLimit(99999);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    JoinSize = list.size();
                    holder.join.setText("有" + JoinSize + "个人参与");
                }
            }
        });


    }
    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }



    @Override
    public int getItemCount() {

        if (mActivityList!=null) {

            int itemCount = mActivityList.size();
            if (null != mEmptyView && itemCount == 0) {
                itemCount++;
            }
            if (null != mHeaderView) {
                itemCount++;
            }
            if (null != mFooterView) {
                itemCount++;
            }
            return itemCount;
        }

        return 0;

    }


    public void addHeaderView(View view) {
        mHeaderView = view;
        notifyItemInserted(0);
    }

    public void addFooterView(View view) {
        mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setEmptyView(View view) {
        mEmptyView = view;
        notifyDataSetChanged();
    }






}