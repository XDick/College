package com.college.xdick.findme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/7.
 */

public class ProvinceAdapter extends BaseAdapter {
    private Context mContext;
    private List<Province> mList = new ArrayList<>();

    public ProvinceAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<Province> list){
        mList = list;
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
        Province provinceList = mList.get(position);
        ViewHolder holder ;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_province_list, null, false);
            holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.province_name);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.nameView.setText(provinceList.getDepartName());
        return view;
    }

    private static class ViewHolder {
        TextView nameView;
    }
}