package com.college.xdick.findme.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;

import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.DetailActivityActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Base.BaseActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;


    private int load_more_status;
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;

    public static final int  NO_MORE=2;


    private List<MyActivity> mActivityList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;







    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView place,title,host,time,join,gps,tag1,endTime,
        tag2,tag3,tag4,date,comment,endDate;
        ImageView cover,avatar,finish;
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
            tag1 = view.findViewById(R.id.tag1_ac);
            tag2 = view.findViewById(R.id.tag2_ac);
            tag3 = view.findViewById(R.id.tag3_ac);
            tag4 = view.findViewById(R.id.tag4_ac);
            date = view. findViewById(R.id.date_ac);
            avatar = view.findViewById(R.id.host_avatar);
            comment = view.findViewById(R.id.comment_ac);
            finish=view.findViewById(R.id.finished_ac);
            endDate = view. findViewById(R.id.end_date_ac);
            endTime = view.findViewById(R.id.end_time_ac);
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
                    intent.putExtra("ACTIVITY",activity);
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
                || type == ITEM_TYPE_EMPTY) {
            return;
        }

        if (type == ITEM_TYPE_FOOTER) {
            TextView textView= mFooterView.findViewById(R.id.footer_text);
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    textView.setText("上拉加载更多");
                    break;
                case LOADING_MORE:
                    textView.setText("正在加载数据...");
                    break;

                case NO_MORE:
                    textView.setText("没有更多了");
                    break;

            }
            return;
        }


        int realPos = getRealItemPosition(position);
        MyActivity activity = mActivityList.get(realPos);
        int joincount=0;
        try{
            joincount=activity.getJoinUser().length;
        }
        catch (Exception e){
            e.printStackTrace();
        }


        String gps[]=activity.getGps();
        String tag[] =activity.getTag();

        String rawTime=activity.getTime();





       String time = rawTime.substring(rawTime.indexOf(" "));
       String date = rawTime.substring(0,rawTime.indexOf(" "));

        String endTime = activity.getEndTime().substring(activity.getEndTime().indexOf(" "));
        String endDate = activity.getEndTime().substring(0,activity.getEndTime().indexOf(" "));



        final DateFormat sdf = new SimpleDateFormat("yyyy");
        String  nowYear = sdf.format(new Date( ));
        String year = date.substring(0,4);
        if (nowYear.equals(year)) {

            if (date.equals(endDate)){
                holder.date.setText(date.substring(5));
                holder.endDate.setText( endDate.substring(5));
                holder.endDate.setVisibility(View.INVISIBLE);
            }
            else {
                holder.date.setText(date.substring(5));
                holder.endDate.setText( endDate.substring(5));
                holder.endDate.setVisibility(View.VISIBLE);
            }

        }
        else
        {
            if (date.equals(endDate)){
                holder.date.setText(date);
                holder.endDate.setText( endDate);
                holder.endDate.setVisibility(View.INVISIBLE);
            }
            else {
                holder.date.setText(date);
                holder.endDate.setText(endDate);
                holder.endDate.setVisibility(View.VISIBLE);
            }

        }

        holder.time.setText(time);
        holder.endTime.setText(endTime);
        holder.title.setText(activity.getTitle());
        holder.place.setText(activity.getPlace());
        holder.host.setText(activity.getHost().getUsername());
        Glide.with(mContext).load(new mGlideUrl(activity.getHost().getAvatar()+"!/fp/10000"))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .apply(bitmapTransform(new CircleCrop())).into(holder.avatar);



        holder.comment.setText(activity.getCommentCount()+"评论");

        if (tag[0].equals("泛觅活动")){
            holder.tag1.setBackgroundResource(R.drawable.label_bg2);
            holder.tag1.setTextColor(Color.parseColor( "#e91111"));
        }
        else {
            holder.tag1.setBackgroundResource(R.drawable.label_text_color2);
            holder.tag1.setTextColor(Color.parseColor( "#121212"));
        }
        holder.tag1.setText(tag[0]);
        holder.tag2.setText(tag[1]);
        holder.tag3.setText(tag[2]);
        holder.tag4.setText(tag[3]);

        //holder.cover.setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);


       if (gps!=null){
        holder.gps.setText(gps[2]);}
        Glide.with(mContext).load(new mGlideUrl(activity.getCover()+"!/fp/20000"))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))/*.apply(bitmapTransform(new BlurTransformation(9, 3)))*/.into(holder.cover);

           holder.join.setText( joincount +"人参与");




        if (activity.getEndDate()< ((BaseActivity)mContext).getBmobTime()){
               holder.finish.setBackground(mContext.getDrawable(R.drawable.finish));
        }

        else {
            holder.finish.setBackground(mContext.getDrawable(R.drawable.blank_pic));
        }


          if (activity.getDate()<=  ((BaseActivity)mContext).getBmobTime()
                  && ((BaseActivity)mContext).getBmobTime()<=activity.getEndDate())
        {
            holder.time.setTextColor(Color.parseColor("#e91111"));
            holder.date.setTextColor(Color.parseColor("#e91111"));
            holder.time.setTypeface(Typeface.DEFAULT_BOLD);
            holder.date.setTypeface(Typeface.DEFAULT_BOLD);

            holder.endTime.setTextColor(Color.parseColor("#e91111"));
            holder.endDate.setTextColor(Color.parseColor("#e91111"));
            holder.endTime.setTypeface(Typeface.DEFAULT_BOLD);
            holder.endDate.setTypeface(Typeface.DEFAULT_BOLD);

        }
        else {
              holder.time.setTextColor(Color.parseColor("#000000"));
              holder.date.setTextColor(Color.parseColor("#000000"));
              holder.time.setTypeface(Typeface.DEFAULT);
              holder.date.setTypeface(Typeface.DEFAULT);

              holder.endTime.setTextColor(Color.parseColor("#000000"));
              holder.endDate.setTextColor(Color.parseColor("#000000"));
              holder.endTime.setTypeface(Typeface.DEFAULT);
              holder.endDate.setTypeface(Typeface.DEFAULT);
          }





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


    public void changeMoreStatus(int status){
        load_more_status=status;
    }






}