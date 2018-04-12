package com.college.xdick.college.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.college.ui.Activity.AskChatActivity;
import com.college.xdick.college.R;
import com.college.xdick.college.bean.Dynamics;



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
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        Dynamics dynamics = mDynamicsList.get(position);
                        final String FriendName = dynamics.getUser();
                        Toast.makeText(mContext, "要开始聊天了：）", Toast.LENGTH_SHORT).show();
                        startChatting(dynamics, FriendName);
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
        if(mDynamicsList!=null){
           return mDynamicsList.size();}
           return 0;
    }




    public void startChatting( Dynamics dynamics , final String name){

           BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username",name);
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> object,BmobException e) {
                String userId;
                if(e==null){
                    for(BmobUser user :object){
                        userId= user.getObjectId();
                        Toast.makeText(mContext,"找到ID："+userId,Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(mContext , AskChatActivity.class);
                        intent.putExtra("FRIEND_ID",userId);
                        intent.putExtra("FRIEND_NAME",name);
                        Toast.makeText(mContext,"你在和"+name+"聊天,ID:"+userId,Toast.LENGTH_SHORT).show();
                        mContext.startActivity(intent);

                    }

                }
                else{
                       Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
