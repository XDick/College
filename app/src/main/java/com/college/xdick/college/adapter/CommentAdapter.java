package com.college.xdick.college.adapter;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.college.R;
import com.college.xdick.college.bean.Comment;
import com.college.xdick.college.ui.Activity.ChatActivity;


import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2018/5/3.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> mCommentList;
    private Context mContext;



    static class ViewHolder extends RecyclerView.ViewHolder{

       TextView username,content,time,reply;

        ImageView avatar;


        public ViewHolder(View view){
            super(view);
             username= view.findViewById(R.id.activity_comment_username);
             content= view.findViewById(R.id.activity_comment_content);
             time = view.findViewById(R.id.activity_comment_time);
             reply=view.findViewById(R.id.activity_comment_reply);
             avatar = view.findViewById(R.id.activity_comment_avatar);

        }
    }


    public CommentAdapter(List<Comment> activity){
        mCommentList = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment,parent,false);
       final   ViewHolder holder = new  CommentAdapter.ViewHolder(view);


        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
               Comment comment = mCommentList.get(position);
               String id = comment.getUserID();
               String name = comment.getUserName();
               startChatting(id,name);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final  CommentAdapter.ViewHolder holder, int position) {


        Comment comment = mCommentList.get(position);
        holder.username.setText(comment.getUserName());
        holder.content.setText(comment.getContent());
        holder.time.setText(comment.getCreatedAt());
        holder.reply.setText(""+comment.getReplyNum());

        Glide.with(mContext).load(comment.getAvatar()).into(holder.avatar);

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
