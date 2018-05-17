package com.college.xdick.findme.adapter;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.college.xdick.findme.R;

import com.college.xdick.findme.bean.MainTagBean;


import java.util.List;



/**
 * Created by Administrator on 2018/5/11.
 */

public class MainTagAdapter extends RecyclerView.Adapter<MainTagAdapter.ViewHolder> {

    private List<MainTagBean> tagList;
    private Context mContext;



    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_tag);

        }
    }


    public MainTagAdapter(List<MainTagBean> tag){
        tagList = tag;
    }

    @Override
    public MainTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tagbean,parent,false);
        final MainTagAdapter.ViewHolder holder = new MainTagAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MainTagAdapter.ViewHolder holder, int position) {

        MainTagBean tag= tagList.get(position);
        holder.title.setText(tag.getMainTag());
        Log.d("","标签数据"+tag.getMainTag());

    }



    @Override
    public int getItemCount() {
        if(tagList!=null){
            return tagList.size();}
        return 0;
    }







}
