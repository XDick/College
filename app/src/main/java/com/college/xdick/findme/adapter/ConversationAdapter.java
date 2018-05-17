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
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.R;

import java.text.SimpleDateFormat;
import java.util.Date;
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

        TextView title_TextView,content_TextView,time_TextView;
        LinearLayout linearLayout ;
        ImageView avatar;



        public ViewHolder(View view){
            super(view);
            title_TextView = view.findViewById(R.id.title_message);
            content_TextView = view.findViewById(R.id.content_message);
            linearLayout = view.findViewById(R.id.parent_conversation);
            time_TextView=view.findViewById(R.id.conversation_time);
            avatar = view. findViewById(R.id.conversation_avatar);

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
                          //  Toast.makeText(mContext,"successful",Toast.LENGTH_SHORT).show();
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
         long count =BmobIM.getInstance().getUnReadCount(conversation.getConversationId());
        String title=conversation.getConversationTitle();
        List<BmobIMMessage> content= conversation.getMessages();

        Glide.with(mContext).load(conversation.getConversationIcon()).into(holder.avatar);




        long t=Long.valueOf(conversation.getUpdateTime());
        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String time = sdf.format(new Date(t));//long转化成Date


        holder.title_TextView.setText(title);

       for(BmobIMMessage str : content){
        holder.content_TextView.setText(str.getContent());
        break;}
        holder. time_TextView.setText(time);

            if(count!=0){
                holder.title_TextView.setText(title+"<"+count+">");
                holder.title_TextView.setBackgroundResource(R.color.red);
            }


    }

    @Override
    public int getItemCount() {
        return ConversationList.size();}










}