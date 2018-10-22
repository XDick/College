package com.college.xdick.findme.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.college.xdick.findme.MyClass.ItemTouchHelperAdapter;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MyJoinActivity;
import com.college.xdick.findme.ui.Activity.MyLikeActivity;
import com.college.xdick.findme.ui.Activity.MySetActivity;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/5/19.
 */

public class ActivityAdapter2 extends RecyclerView.Adapter<ActivityAdapter2.ViewHolder>
      //  implements ItemTouchHelperAdapter
{
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;

    private int currentActivity;
    private MyJoinActivity activityjoin;
    private MyLikeActivity activitylike;
    private MySetActivity activityset;


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

        TextView title,time,host,join;
        ImageView cover;
        CardView cardView;
        Button delete;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_ac);
            cardView =view.findViewById(R.id.cardview_ac);
            cover = view.findViewById(R.id.cover_ac);
            time= view.findViewById(R.id.time_ac);
            delete = view.findViewById(R.id.delete);
            host = view.findViewById(R.id.host_ac);
            join = view.findViewById(R.id.join_ac);

        }

        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            }else{
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }



    public ActivityAdapter2(List<MyActivity> activity){
        mActivityList = activity;
    }

    public void setJoin(MyJoinActivity activity){
         activityjoin=activity;
        currentActivity = 0;
    }
    public void setLike (MyLikeActivity activity){
        activitylike=activity;
        currentActivity = 1;
    }public void setSet(MySetActivity activity){
      activityset=activity;
       currentActivity = 2;
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
                    .inflate(R.layout.item_activity2, parent, false);
            final ViewHolder holder = new ActivityAdapter2.ViewHolder(view);


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
    public void onBindViewHolder(final ActivityAdapter2.ViewHolder holder, int position) {

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

        final MyActivity activity = mActivityList.get(realPos);


         holder.title.setText(activity.getTitle());

         holder.time.setText(activity.getTime());

         Glide.with(mContext).load(activity.getCover())
                 .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(holder.cover);

          holder.host.setText("由"+activity.getHostName()+"发起");

        try {
            holder.join.setText(activity.getJoinUser().length+"人参与");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final MyActivity activity = mActivityList.get(position);
                final MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
                switch (currentActivity){

                    case 0: {

                        MyActivity myActivity = new MyActivity();
                        myActivity.setObjectId(activity.getObjectId());
                        myActivity.setDate(activity.getDate());
                        myActivity.removeAll("joinUser", Arrays.asList(myUser.getObjectId()));
                        myActivity.increment("joinCount",-1);
                        myActivity.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e==null){
                                    holder.setVisibility(false);
                                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext,"删除失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        break;

                    }
                    case 1:
                    {   MyUser myUser1= new MyUser();
                        myUser1.setObjectId(myUser.getObjectId());
                        myUser1.removeAll("like", Arrays.asList(activity.getObjectId()));
                        myUser1.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {

                                    holder.setVisibility(false);
                                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(mContext,"删除失败",Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                        break;
                    }

                    case 2:

                    {   MyUser myUser1= new MyUser();
                        myUser1.setObjectId(myUser.getObjectId());
                        myUser1.increment("setAcCount",-1);
                        myUser1.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyActivity myActivity = new MyActivity();
                                    myActivity.setObjectId(activity.getObjectId());
                                    myActivity.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e==null){

                                                BmobQuery<Comment>query =new BmobQuery<>();
                                                query.addWhereEqualTo("ActivityID",activity.getObjectId());
                                                query.findObjects(new FindListener<Comment>() {
                                                    @Override
                                                    public void done(List<Comment> list, BmobException e) {
                                                        List<BmobObject> commentList= new ArrayList<BmobObject>();
                                                           commentList.addAll(list);
                                                        new BmobBatch().deleteBatch(commentList).doBatch(new QueryListListener<BatchResult>() {
                                                            @Override
                                                            public void done(List<BatchResult> list, BmobException e) {
                                                                 if (e==null){

                                                                 }
                                                            }
                                                        });
                                                    }
                                                });

                                                BmobFile file = new BmobFile();
                                                file.setUrl(activity.getCover());
                                                file.delete(new UpdateListener() {

                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e==null){

                                                        }else{

                                                        }
                                                    }
                                                });

                                                holder.setVisibility(false);
                                                Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(mContext,"删除失败",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }

                        });


                        break;

                    }

                    default:
                        break;
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


    public void changeMoreStatus(int status){
        load_more_status=status;
    }
}


  /*  @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mActivityList,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        //移除数据
        mActivityList.remove(position);
        notifyItemRemoved(position);
    }*/
