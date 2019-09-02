package com.college.xdick.findme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList = new ArrayList<>();


    public ActivityHistoryAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<String> list){
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
        String schoolList = mList.get(position);
        ActivityHistoryAdapter.ViewHolder holder = null;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_history_activity, null, false);
            holder = new ViewHolder();
            holder.timeView = (TextView)view.findViewById(R.id.time_activity);
            holder.nameView = (TextView)view.findViewById(R.id.select_activity);
            view.setTag(holder);
        }else {
            view=convertView;
            holder = (ViewHolder) view.getTag();

        }
        holder.timeView.setText(schoolList.substring(0,schoolList.indexOf("!")));
        holder.nameView.setText(schoolList.substring(schoolList.indexOf("!")+1));
        return view;
    }

    private static class ViewHolder {
        TextView nameView;
        TextView timeView;
    }
}
