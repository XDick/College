package com.college.xdick.findme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.AddTagBean;
import com.college.xdick.findme.bean.MainTagBean;

import java.util.List;


/**
 * Created by Administrator on 2018/5/11.
 */

public class AddTagAdapter extends RecyclerView.Adapter<AddTagAdapter.ViewHolder> {

    private List<AddTagBean> tagList;
    private Context mContext;



    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_tag);

        }
    }


    public AddTagAdapter(List<AddTagBean> tag){
        tagList = tag;
    }

    @Override
    public AddTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tagbean,parent,false);
        final AddTagAdapter.ViewHolder holder = new AddTagAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AddTagAdapter.ViewHolder holder, int position) {

        AddTagBean tag= tagList.get(position);
        holder.title.setText(tag.getAddTag());


    }



    @Override
    public int getItemCount() {
        if(tagList!=null){
            return tagList.size();}
        return 0;
    }







}
