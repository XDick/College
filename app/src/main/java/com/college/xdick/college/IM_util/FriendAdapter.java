
package com.college.xdick.college.IM_util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.college.xdick.college.R;
import com.college.xdick.college.util.DynamicsAdapter;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<Friend> mFriendList;
    private Context mContext;


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView friendName;


        public ViewHolder(View view){
            super(view);
            friendName = view.findViewById(R.id.friend_name_text);

        }
    }


    public FriendAdapter(List<Friend> friend){
        mFriendList = friend;
    }

    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_list,parent,false);
       final ViewHolder holder = new FriendAdapter.ViewHolder(view);
        holder.friendName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
               Friend friend = mFriendList.get(position);
                /*Intent intent = new Intent(mContext , InformationActivity.class);
                intent.putExtra(InformationActivity.INFORMATION_NAME,famousPeople.getTitle());
                intent.putExtra("image_url",famousPeople.getImageUrl());
                intent.putExtra("idiom_content",famousPeople.getContent());
                intent.putExtra("idiom_pre",famousPeople.getBody());
                mContext.startActivity(intent);*/
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendAdapter.ViewHolder holder, int position) {
        Friend friend = mFriendList.get(position);
        holder.friendName.setText(friend.getFriendUser().getUsername());


    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }
}
