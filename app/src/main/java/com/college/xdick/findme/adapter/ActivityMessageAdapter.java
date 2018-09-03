package com.college.xdick.findme.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.ActivityMessageBean;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;
import com.college.xdick.findme.ui.Activity.MyJoinActivity;
import com.college.xdick.findme.ui.Activity.MyLikeActivity;
import com.college.xdick.findme.ui.Activity.MySetActivity;
import com.college.xdick.findme.ui.Activity.SetDynamicsActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/5/19.
 */

public class ActivityMessageAdapter extends RecyclerView.Adapter<ActivityMessageAdapter.ViewHolder>
      //  implements ItemTouchHelperAdapter
{
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;




    private List<ActivityMessageBean> mActivityList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;






    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,time,content,acname;
        ImageView cover;
        CardView cardView;
        Button delete;
        LinearLayout askLayout,acceptLayout,rejectLayout;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_ac);
            cardView =view.findViewById(R.id.cardview_ac);
            cover = view.findViewById(R.id.cover_ac);
            time= view.findViewById(R.id.time_ac);
            delete = view.findViewById(R.id.delete);
            content = view.findViewById(R.id.content_ac);
            acname = view.findViewById( R.id.acname_ac);
            acceptLayout= view.findViewById(R.id.pic_accept);
            rejectLayout= view.findViewById(R.id.pic_reject);
            askLayout=view.findViewById(R.id.pic_ask_layout);
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


    public ActivityMessageAdapter(List<ActivityMessageBean> activity){
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
                    .inflate(R.layout.item_activity_message, parent, false);
            final ViewHolder holder = new ActivityMessageAdapter.ViewHolder(view);










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
    public void onBindViewHolder(final ActivityMessageAdapter.ViewHolder holder, int position) {

        final int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_FOOTER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        int realPos = getRealItemPosition(position);

        final ActivityMessageBean activity = mActivityList.get(realPos);


        if (activity.getType().equals("dynamics_picture")) {
            holder.acname.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.askLayout.setVisibility(View.VISIBLE);
            holder.acceptLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dynamics dynamics = new Dynamics();
                    dynamics.setObjectId(activity.getActivityId().substring(0,activity.getActivityId().lastIndexOf(";")));
                    dynamics.setIfAdd2Gallery(true);
                    dynamics.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sendMessage(BmobUser.getCurrentUser().getUsername() + "已同意添加" + activity.getActivityname().substring(0,activity.getActivityname().lastIndexOf(";"))
                                                + "的照片",
                                        new BmobIMUserInfo(activity.getUserId(), activity.getUsername(), activity.getUserAvatar())
                                        , activity.getActivityname().substring(activity.getActivityname().lastIndexOf(";") + 1)
                                        , activity.getActivityId().substring(activity.getActivityId().lastIndexOf(";") + 1));
                                activity.setIfCheck("true");
                                activity.setActivityname("(已同意)"+activity.getActivityname());
                                activity.save();
                                holder.askLayout.setVisibility(View.GONE);
                                EventBus.getDefault().post(new ReadEvent(""));
                                Toast.makeText(mContext,"已同意",Toast.LENGTH_SHORT).show();
                            }

                            else{


                            }
                        }
                    });
                }
            });
            holder.rejectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(BmobUser.getCurrentUser().getUsername() + "拒绝添加" + activity.getActivityname().substring(0,activity.getActivityname().lastIndexOf(";"))
                                    + "的照片",
                            new BmobIMUserInfo(activity.getUserId(), activity.getUsername(), activity.getUserAvatar())
                            , activity.getActivityname().substring(activity.getActivityname().lastIndexOf(";") + 1)
                            , activity.getActivityId().substring(activity.getActivityId().lastIndexOf(";") + 1));
                    activity.setActivityname("(拒绝)"+activity.getActivityname().substring(0,activity.getActivityname().lastIndexOf(";")));
                    activity.setIfCheck("true");
                    activity.save();
                    holder.askLayout.setVisibility(View.GONE);
                    EventBus.getDefault().post(new ReadEvent(""));
                    Toast.makeText(mContext,"已拒绝",Toast.LENGTH_SHORT).show();
                }
            });

        } else if (activity.getType().equals("dynamics")) {
            holder.acname.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (activity.getType().equals("activity")) {
            holder.acname.setTextColor(Color.parseColor("#2758e1"));
        } else if (activity.getType().equals("user")) {
            holder.acname.setTextColor(Color.parseColor("#e21fec"));
        }
        holder.title.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.time.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.content.setTextColor(mContext.getResources().getColor(R.color.black));


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = sdf.format(new Date(Long.parseLong(activity.getTime())));
        holder.title.setText(activity.getUsername());
        if (activity.getIfCheck().equals("false")) {
            holder.title.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.time.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.content.setTextColor(mContext.getResources().getColor(R.color.red));

        } else {
            holder.askLayout.setVisibility(View.GONE);
        }

        holder.time.setText(time);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final ActivityMessageBean activity = mActivityList.get(position);
                DataSupport.deleteAll(ActivityMessageBean.class, "username=?and time=?", activity.getUsername(), activity.getTime());

                holder.setVisibility(false);
            }


        });
        holder.content.setText(activity.getContent());
       if (activity.getType().equals("dynamics_picture"))
        {
            holder.acname.setText(activity.getActivityname().substring(0,activity.getActivityname().lastIndexOf(";")));
            }
            else {
            holder.acname.setText(activity.getActivityname());
        }


         Glide.with(mContext).load(activity.getUserAvatar()).apply(bitmapTransform(new CropCircleTransformation())).into(holder.cover);

      holder.cover.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             BmobQuery<MyUser> query =new BmobQuery<>();
             query.getObject(activity.getUserId(), new QueryListener<MyUser>() {
                 @Override
                 public void done(MyUser myUser, BmobException e) {
                     if (e==null){Intent intent = new Intent(mContext, UserCenterActivity.class);
                     intent.putExtra("USER",myUser);
                     mContext.startActivity(intent);}
                 }
             });
          }
      });

      holder.cardView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (activity.getType().equals("activity")){
                  BmobQuery<MyActivity> query = new BmobQuery<>();
                  query.getObject(activity.getActivityId(), new QueryListener<MyActivity>() {
                      @Override
                      public void done(MyActivity activity, BmobException e) {
                          Intent intent = new Intent(mContext, ActivityActivity.class);
                          intent.putExtra("ACTIVITY",activity);
                          mContext.startActivity(intent);

                      }
                  });
              }
              else if (activity.getType().equals("dynamics")){




                                   final BmobQuery<Dynamics> query = new BmobQuery<>();
                                   query.getObject(activity.getActivityId(), new QueryListener<Dynamics>() {
                                       @Override
                                       public void done(final Dynamics dynamics, BmobException e) {
                                          BmobQuery<MyUser> query1 = new BmobQuery<>();
                                          query1.getObject(dynamics.getUserId(), new QueryListener<MyUser>() {
                                              @Override
                                              public void done(MyUser myUser, BmobException e) {
                                                  if (e==null){
                                                      Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                                      intent.putExtra("DYNAMICS", dynamics);
                                                      intent.putExtra("USER",myUser);
                                                      mContext.startActivity(intent);

                                                  }
                                              }
                                          });

                    }
                });


              }
              else if (activity.getType().equals("dynamics_picture")){
                  final BmobQuery<Dynamics> query = new BmobQuery<>();
                  query.getObject(activity.getActivityId().substring(0,activity.getActivityId().lastIndexOf(";")), new QueryListener<Dynamics>() {
                      @Override
                      public void done(final Dynamics dynamics, BmobException e) {
                          BmobQuery<MyUser> query1 = new BmobQuery<>();
                          query1.getObject(dynamics.getUserId(), new QueryListener<MyUser>() {
                              @Override
                              public void done(MyUser myUser, BmobException e) {
                                  if (e==null){
                                      Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                      intent.putExtra("DYNAMICS", dynamics);
                                      intent.putExtra("USER",myUser);
                                      mContext.startActivity(intent);

                                  }
                              }
                          });

                      }
                  });
              }
              else if (activity.getType().equals("user")){
                  BmobQuery<MyUser> query = new BmobQuery<>();
                  query.getObject(activity.getUserId(), new QueryListener<MyUser>() {
                      @Override
                      public void done(MyUser myUser, BmobException e) {
                          if (e==null){
                               Intent intent =new Intent(mContext,UserCenterActivity.class);
                               intent.putExtra("USER",myUser);
                               mContext.startActivity(intent);
                          }
                      }
                  });


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
    public void sendMessage(String content ,BmobIMUserInfo info,String title,String id) {
  MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        if(!myUser.getObjectId().equals(info.getUserId())){

            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
            BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
            ActivityMessage msg = new ActivityMessage();
            msg.setContent(content);//给对方的一个留言信息
            Map<String, Object> map = new HashMap<>();
            map.put("currentuser", info.getUserId());
            map.put("userid", myUser.getObjectId());
            map.put("username",myUser.getUsername());
            map.put("useravatar",myUser.getAvatar());
            map.put("activityid",id);
            map.put("activityname",title);
            map.put("type","activity");
            msg.setExtraMap(map);
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {//发送成功
                    } else {//发送失败
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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


}