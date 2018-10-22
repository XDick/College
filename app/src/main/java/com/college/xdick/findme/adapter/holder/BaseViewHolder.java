package com.college.xdick.findme.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    protected Context context;
    public BaseViewHolder(Context context,ViewGroup root, int layoutRes) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
        this.context=context;
    }


    public Context getContext() {
        return itemView.getContext();
    }

    public abstract void bindData(List<BmobIMMessage> list,
                                  int status, int position,int count);


}
