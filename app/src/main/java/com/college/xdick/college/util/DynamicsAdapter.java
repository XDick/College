package com.college.xdick.college.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.college.Activity.AskChatActivity;
import com.college.xdick.college.R;

import java.util.List;

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
       private String userId;


       static class ViewHolder extends RecyclerView.ViewHolder{

           TextView content,title,username,time;


           public ViewHolder(View view){
               super(view);
              content = view.findViewById(R.id.content_main);
              title= view.findViewById(R.id.title_main);
              username = view.findViewById(R.id.username_main);
              time = view.findViewById(R.id.time_main);
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
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                Dynamics dynamics = mDynamicsList.get(position);
                String myFriendName= dynamics.getUser();
                String FriendID = getFriendId(myFriendName);
                Intent intent = new Intent(mContext , AskChatActivity.class);
                intent.putExtra("FRIEND_ID",FriendID);
                intent.putExtra("FRIEND_NAME",myFriendName);
                Toast.makeText(mContext,"你在和"+myFriendName+"聊天,ID:"+FriendID,Toast.LENGTH_SHORT).show();
                mContext.startActivity(intent);

            }
        });


           return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dynamics dynamics = mDynamicsList.get(position);
        holder.content.setText(dynamics.getContent());
        holder.title.setText(dynamics.getTitle());
        holder.username.setText(dynamics.getUser());
        holder.time.setText(dynamics.getCreatedAt());

    }

    @Override
    public int getItemCount() {
        return mDynamicsList.size();
    }




    public String getFriendId(String friendName){
        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();

        query.addWhereEqualTo("username",  friendName);
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> object,BmobException e) {
                if(e==null){
                    for(BmobUser user :object){
                        userId= user.getObjectId();
                    }

                }
                else{

                }
            }
        });

        return  userId;

    }
}
