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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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



    private List<BmobIMMessage> mMsgList;

    BmobIMConversation c;





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
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.leftLayout.setVisibility(View.VISIBLE);
        holder.rightLayout.setVisibility(View.VISIBLE);

        String currentUid="";
        currentUid = BmobUser.getCurrentUser().getObjectId();
        BmobIMMessage msg = mMsgList.get(position);
        Log.d(TAG,"位置:"+position+"消息是"+msg.getContent()+"id:"+msg.getFromId()+"当前ID"+currentUid);
        long time=Long.valueOf(msg.getCreateTime());
        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String createTime = sdf.format(new Date(time));
        holder.time.setText(createTime);



        if(msg.getFromId().equals(currentUid)) {

            holder.rightMsg.setText(msg.getContent());
            holder.leftLayout.setVisibility(View.GONE);



           Log.d(TAG,"左边消失，右边出现消息是"+msg.getContent()+"id:"+msg.getFromId()+"当前ID"+currentUid);
       }
       else {

            holder.leftMsg.setText(msg.getContent());
            holder.rightLayout.setVisibility(View.GONE);

           Log.d(TAG,"右边消失，左边出现,消息是"+msg.getContent()+"id:"+msg.getFromId()+"当前ID"+currentUid);
        }
    }


    @Override
    public int getItemCount() {
        return mMsgList.size();
    }




}