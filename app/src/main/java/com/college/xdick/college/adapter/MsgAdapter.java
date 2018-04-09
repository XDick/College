package com.college.xdick.college.adapter;

import android.content.Context;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.college.xdick.college.R;

import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import static org.greenrobot.eventbus.EventBus.TAG;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE =6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //视频
    private final int TYPE_SEND_VIDEO =8;
    private final int TYPE_RECEIVER_VIDEO = 9;

    //同意添加好友成功后的样式
    private final int TYPE_AGREE = 10;

    /**
     * 显示时间间隔:10分钟
     */
    private final long TIME_INTERVAL = 10 * 60 * 1000;
    private List<BmobIMMessage> mMsgList;
    private String currentUid="";
    BmobIMConversation c;


    public MsgAdapter(Context context,BmobIMConversation c) {
        try {
            currentUid = BmobUser.getCurrentUser().getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.c =c;

    }


    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while(index-- > 0) {
            if(message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;
        while(index-- > 0) {
            if(this.getItemId(index) == id) {
                position = index;
                break;
            }
        }
        return position;
    }
    public void addMessages(List<BmobIMMessage> messages) {
        mMsgList.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        mMsgList.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }
    public void remove(int position){
        mMsgList.remove(position);
        notifyDataSetChanged();
    }

    public BmobIMMessage getFirstMessage() {
        if (null != mMsgList &&mMsgList.size() > 0) {
            return mMsgList.get(0);
        } else {
            return null;
        }
    }
    public int getCount() {
        return this.mMsgList == null?0:this.mMsgList.size();
    }

    public BmobIMMessage getItem(int position){
        return this.mMsgList == null?null:(position >= this.mMsgList.size()?null:this.mMsgList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = mMsgList.get(position);
        if(message.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
        }else if(message.getMsgType().equals(BmobIMMessageType.LOCATION.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_LOCATION: TYPE_RECEIVER_LOCATION;
        }else if(message.getMsgType().equals(BmobIMMessageType.VOICE.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VOICE: TYPE_RECEIVER_VOICE;
        }else if(message.getMsgType().equals(BmobIMMessageType.TEXT.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }else if(message.getMsgType().equals(BmobIMMessageType.VIDEO.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VIDEO: TYPE_RECEIVER_VIDEO;
        }else if(message.getMsgType().equals("agree")) {//显示欢迎
            return TYPE_AGREE;
        }else{
            return -1;
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout,rightLayout;

        TextView leftMsg,rightMsg,time;




        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftMsg = view. findViewById(R.id.left_msg);
            rightMsg = view.findViewById(R.id.right_msg);
            time=view.findViewById(R.id.msg_time);
        }
    }

    public MsgAdapter(List<BmobIMMessage> msgList){
        mMsgList = msgList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,
                parent,false);
        currentUid = BmobUser.getCurrentUser().getObjectId();
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BmobIMMessage msg = mMsgList.get(position);
      //String createTime =  String.valueOf(msg.getCreateTime());
      // holder.time.setText(createTime);
      // BmobIMConversation conversation =msg.getBmobIMConversation();
       // BmobIMUserInfo info = new BmobIMUserInfo(conversation.getConversationId(),conversation.getConversationTitle(),null);

        if(msg.getFromId().equals( currentUid)){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());

        }
        else {

            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setVisibility(View.VISIBLE);
            holder.leftMsg.setText(msg.getContent());
        }}


    @Override
    public int getItemCount() {
        return mMsgList.size();
    }




}