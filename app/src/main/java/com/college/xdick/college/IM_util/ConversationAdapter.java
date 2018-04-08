package com.college.xdick.college.IM_util;

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

import com.college.xdick.college.Activity.AskChatActivity;
import com.college.xdick.college.Activity.ChatActivity;
import com.college.xdick.college.R;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2018/4/8.
 */

public class ConversationAdapter extends RecyclerView.Adapter< ConversationAdapter.ViewHolder> {

    private List<BmobIMConversation> ConversationList;
    private Context mContext;
    private String userId;



    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title_TextView,content_TextView;
        LinearLayout linearLayout ;



        public ViewHolder(View view){
            super(view);
            title_TextView = view.findViewById(R.id.title_find);
            content_TextView = view.findViewById(R.id.content_find);
            linearLayout = view.findViewById(R.id.parent_conversation);

        }
    }


    public  ConversationAdapter(List<BmobIMConversation> conversation){
        ConversationList = conversation;
    }

    @Override
    public  ConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation,parent,false);
        final ViewHolder holder = new  ConversationAdapter.ViewHolder(view);
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                BmobIMConversation conversation = ConversationList.get(position);
                BmobIM.getInstance().startPrivateConversation( new BmobIMUserInfo(conversation.getConversationId(),conversation.getConversationTitle(),""), new ConversationListener() {
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
                            Toast.makeText(mContext
                                    ,"successful",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext
                                    ,e.getMessage()+"("+e.getErrorCode()+")",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }


        });
        return holder;
    }

    @Override
    public void onBindViewHolder( ConversationAdapter.ViewHolder holder, int position) {
       BmobIMConversation conversation = ConversationList.get(position);

        String title=conversation.getConversationTitle();
        List<BmobIMMessage> content= conversation.getMessages();
        holder.title_TextView.setText(title);

       for(BmobIMMessage str : content){
        holder.content_TextView.setText(str.getContent());
        break;
    }}

    @Override
    public int getItemCount() {
        return ConversationList.size();}}