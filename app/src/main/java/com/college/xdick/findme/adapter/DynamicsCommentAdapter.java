package com.college.xdick.findme.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.DynamicsComment;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Administrator on 2018/5/3.
 */

public class DynamicsCommentAdapter extends RecyclerView.Adapter<DynamicsCommentAdapter.ViewHolder> {

    private List<DynamicsComment> mCommentList;
    private Context mContext;



    static class ViewHolder extends RecyclerView.ViewHolder{

       TextView username,content,time,replyto,replycontent;
        LinearLayout replylayout,layout;
        ImageView avatar;


        public ViewHolder(View view){
            super(view);
             username= view.findViewById(R.id.dynamics_comment_username);
             content= view.findViewById(R.id.dynamics_comment_content);
             time = view.findViewById(R.id.dynamics_comment_time);
             avatar = view.findViewById(R.id.dynamics_comment_avatar);
             replyto = view.findViewById(R.id.comment_fromwho_dynamics);
             replycontent = view.findViewById(R.id.comment_fromcontent_dynamics);
             replylayout=  view. findViewById(R.id.comment_replylayout_dynamics);
             layout = view.findViewById(R.id.comment_layout_dynamics);

        }
    }


    public DynamicsCommentAdapter(List<DynamicsComment> activity){
        mCommentList = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamics_comment,parent,false);
       final   ViewHolder holder = new  DynamicsCommentAdapter.ViewHolder(view);

       holder.layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

              MainDynamicsActivity activity = (MainDynamicsActivity)mContext;
                activity.ifReply=false;
               int position = holder.getAdapterPosition();
               DynamicsComment comment = mCommentList.get(position);
               DynamicsComment comment2 = new DynamicsComment();
               comment2.setReplyusername(comment.getUserName());
               comment2.setReplyuserId(comment.getUserID());
               comment2.setReplycontent(comment.getContent());
               comment2.setDynamicsID(comment.getDynamicsID());
               activity.reply(comment,comment2);

           }
       });

       holder.replyto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int position = holder.getAdapterPosition();
               DynamicsComment comment = mCommentList.get(position);
               String id = comment.getReplyuserId();
               String name = comment.getReplyusername();
               startChatting(id,name);
           }
       });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                DynamicsComment comment = mCommentList.get(position);
                String id = comment.getUserID();
                String name = comment.getUserName();
               startChatting(id,name);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final  DynamicsCommentAdapter.ViewHolder holder, int position) {


        DynamicsComment comment = mCommentList.get(position);
        String fromwho,fromcontent;



        if (comment.getReplyuserId()==null&&comment.getReplycontent()==null){

            holder.replylayout.setVisibility(View.GONE);
        }
        else {
            fromwho=comment.getReplyusername();
            fromcontent= comment.getReplycontent();
            holder.replyto.setText("@"+  fromwho);
            holder.replycontent.setText(":"+ fromcontent);
        }

        holder.username.setText(comment.getUserName());
        holder.content.setText(comment.getContent());
        holder.time.setText(comment.getCreatedAt());
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.getObject(comment.getUserID(), new QueryListener<MyUser>() {

            @Override
            public void done(MyUser object, BmobException e) {
                if(e==null){


                    Glide.with(mContext).load(object.getAvatar()).into(holder.avatar);


                }else{

                }
            }

        });



    }



    @Override
    public int getItemCount() {
        if (mCommentList != null) {
            return mCommentList.size();
        }
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
