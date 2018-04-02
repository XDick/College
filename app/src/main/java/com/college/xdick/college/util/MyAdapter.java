package com.college.xdick.college.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.college.xdick.college.R;

import java.util.List;

/**
 * Created by Administrator on 2018/4/2.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context mContext;
    private List<Strategy> mStrategyList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView informationImage;
        TextView informationName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            informationImage = view.findViewById(R.id.information_image);
            informationName =  view.findViewById(R.id.information_name);

        }
    }



    public MyAdapter(List<Strategy> informationList){
        mInformationList = informationList;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.information_item, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                Strategy strategy = mInformationList.get(position);
                Intent intent = new Intent(mContext , InformationActivity.class);
                intent.putExtra(InformationActivity.INFORMATION_NAME,information.getName());
                intent.putExtra(InformationActivity.INFORMATION_IMAGE_ID,information.getImageId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Strategy strategy= mStrategyList.get(position);
        holder.informationName.setText(information.getName());
        Glide.with(mContext).load(information.getImageId()).into(holder.informationImage);

    }
    @Override
    public int getItemCount(){
        return mStrategyList.size();
    }

}

