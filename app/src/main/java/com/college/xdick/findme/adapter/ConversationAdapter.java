package com.college.xdick.findme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.BmobIM.newClass.PrivateConversation;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Fragment.PrivateConversationFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.newim.BmobIM;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/4/8.
 */

public class ConversationAdapter extends RecyclerView.Adapter< ConversationAdapter.ViewHolder> {

    private List<PrivateConversation> ConversationList;
    private Context mContext;
    private PrivateConversationFragment fragment;
    private  QBadgeView badgeView;



    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title_TextView,content_TextView,time_TextView;
        LinearLayout linearLayout ;
        ImageView avatar;
        Button deletebutton ;



        public ViewHolder(View view){
            super(view);
            title_TextView = view.findViewById(R.id.title_message);
            content_TextView = view.findViewById(R.id.content_message);
            linearLayout = view.findViewById(R.id.parent_conversation);
            time_TextView=view.findViewById(R.id.conversation_time);
            avatar = view. findViewById(R.id.conversation_avatar);
            deletebutton=view.findViewById(R.id.delete);


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


    public  ConversationAdapter(List<PrivateConversation> conversation){
        ConversationList = conversation;
    }
    public void setFragment(PrivateConversationFragment fragment){
        this.fragment = fragment;
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
                PrivateConversation conversation = ConversationList.get(position);
                conversation.onClick(mContext);

            }


        });

        badgeView=  new QBadgeView(mContext);
        badgeView.bindTarget(holder.linearLayout)
                .setBadgeGravity(Gravity.BOTTOM|Gravity.END)
                   .setShowShadow(false)
                    .setBadgeBackgroundColor(mContext.getResources().getColor(R.color.red))
                    .setGravityOffset(20,true);
        holder.deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                PrivateConversation conversation = ConversationList.get(position);
                conversation.deleteConversation();
                fragment.initAllConversation();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder( ConversationAdapter.ViewHolder holder, int position) {
        PrivateConversation conversation = ConversationList.get(position);
         long count =BmobIM.getInstance().getUnReadCount(conversation.getcId());
        String title=conversation.getcName();

        Glide.with(mContext).load(conversation.getAvatar()).apply(bitmapTransform(new CropCircleTransformation())).into(holder.avatar);

        long t=Long.valueOf(conversation.getLastMessageTime());

        SimpleDateFormat sdf1= new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String time1 = sdf1.format(new Date(t));//long转化成Date

        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String  nowtime = sdf.format(new Date());


         String  rawdate =sdf.format(new Date(t));


        String time = time1.substring(time1.indexOf(" ")+1);

        if (nowtime.equals(rawdate)){
            holder.time_TextView.setText(time);
        }
        else{
            holder.time_TextView.setText(time1);
        }

        holder.title_TextView.setText(title);

        holder.content_TextView.setText(conversation.getLastMessageContent());



            if(count!=0){
                badgeView.hide(false);
                badgeView.setBadgeNumber((int)count);
            }
            else{
                     badgeView.hide(true);
            }


    }

    @Override
    public int getItemCount() {
        return ConversationList.size();}










}