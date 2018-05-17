package com.college.xdick.findme.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;


import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DynamicsAdapter extends RecyclerView.Adapter<DynamicsAdapter.ViewHolder> {

       private List<Dynamics> mDynamicsList;

       private Context mContext;



       static class ViewHolder extends RecyclerView.ViewHolder{

           TextView title,username,time,reply;
           LinearLayout layout;


           public ViewHolder(View view){
               super(view);

              title= view.findViewById(R.id.title_main);
              username = view.findViewById(R.id.username_main);
              time = view.findViewById(R.id.time_main);
              layout = view.findViewById(R.id.layout_main);
              reply = view.findViewById(R.id.reply_main);
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

                 holder.username.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        Dynamics dynamics = mDynamicsList.get(position);
                        final String FriendName = dynamics.getUser();
                        BmobQuery<BmobUser> query = new BmobQuery<>();
                        query.addWhereEqualTo("username",FriendName);
                        query.findObjects(new FindListener<BmobUser>() {
                            @Override
                            public void done(List<BmobUser> list, BmobException e) {
                                for (BmobUser user:list){
                                startChatting(user.getObjectId(),user.getUsername());
                                }
                            }
                        });

                    }
                });


                 holder.layout.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         int position = holder.getAdapterPosition();
                         Dynamics dynamics = mDynamicsList.get(position);

                         Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                         intent.putExtra("DYNAMICS_CONTENT", dynamics.getContent())
                                 .putExtra("DYNAMICS_TIME", dynamics.getCreatedAt())
                                 .putExtra("DYNAMICS_USER", dynamics.getUser())
                                 .putExtra("DYNAMICS_TITLE",dynamics.getTitle())
                                  .putExtra("DYNAMICS_ID",dynamics.getObjectId())
                                   .putExtra("DYNAMICS_REPLYCOUNT",dynamics.getReplycount())
                                 .putExtra("DYNAMICS_USERID",dynamics.getUserId());

                         mContext.startActivity(intent);
                     }
                 });

           return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

           Dynamics dynamics = mDynamicsList.get(position);
        holder.title.setText(dynamics.getTitle());
        holder.username.setText(dynamics.getUser());
        holder.time.setText(dynamics.getCreatedAt());
        holder.reply.setText("回复("+dynamics.getReplycount()+")");

        }



    @Override
    public int getItemCount() {
        if(mDynamicsList!=null){
           return mDynamicsList.size();}
           return 0;
    }



    private void startChatting(String id,String name){
        BmobIM.getInstance().startPrivateConversation( new BmobIMUserInfo(id,name,""), new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if(e==null){
                    //在此跳转到聊天页面
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    Intent intent = new Intent(mContext,
                            ChatActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);

                }else{
                    Toast.makeText(mContext
                            ,e.getMessage()+"("+e.getErrorCode()+")",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }





}
