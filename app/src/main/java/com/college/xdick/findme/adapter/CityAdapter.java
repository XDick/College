package com.college.xdick.findme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.City;
import com.college.xdick.findme.bean.School;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/8.
 */

public class CityAdapter extends BaseAdapter {
    private Context mContext;
    private List<City> mList = new ArrayList<>();


    public CityAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<City> list){
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
        City cityList = mList.get(position);
        ViewHolder holder = null;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_city_list, null, false);
            holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.city_name);
            view.setTag(holder);
        }else {
            view= convertView;
            holder = (ViewHolder) convertView.getTag();



        }
        holder.nameView.setText(cityList.getLocationName());
        return view;
    }

    private static class ViewHolder {
        TextView nameView;
    }
}