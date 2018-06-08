package com.college.xdick.findme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.School;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/7.
 */

public class SelectActivityAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyActivity> mList = new ArrayList<>();


    public SelectActivityAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<MyActivity> list){
        this.mList = list;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyActivity schoolList = mList.get(position);
        ViewHolder holder = null;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_select_activity, null, false);
            holder = new ViewHolder();
            holder.nameView = (TextView)view.findViewById(R.id.select_activity);
            view.setTag(holder);
        }else {
           view=convertView;
            holder = (ViewHolder) view.getTag();

        }
        holder.nameView.setText(schoolList.getTitle());
        return view;
    }

    private static class ViewHolder {
        TextView nameView;
    }
}