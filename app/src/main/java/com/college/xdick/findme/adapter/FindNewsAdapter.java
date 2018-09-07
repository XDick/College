package com.college.xdick.findme.adapter;

import android.content.Context;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.FindNews;
import com.college.xdick.findme.ui.Activity.NewsActivity;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;


/**
 * Created by Administrator on 2018/5/6.
 */

public class FindNewsAdapter extends RecyclerView.Adapter<FindNewsAdapter.ViewHolder> {
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;


    private List<FindNews> mNewsList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;




    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,pre;
        ImageView cover;
        LinearLayout layout;



        public ViewHolder(View view){
            super(view);
             title= view.findViewById(R.id.news_title);
             pre = view.findViewById(R.id.news_pre);
             cover = view.findViewById(R.id.news_cover);
             layout= view .findViewById(R.id.news_linearlayout);
        }
    }


    public FindNewsAdapter(List<FindNews> news){
        mNewsList = news;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if(mContext == null){
            mContext = parent.getContext();
        }


        if (viewType == ITEM_TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else if (viewType == ITEM_TYPE_EMPTY) {
            return new ViewHolder(mEmptyView);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new ViewHolder(mFooterView);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news, parent, false);
            final ViewHolder holder = new FindNewsAdapter.ViewHolder(view);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getRealItemPosition(holder.getAdapterPosition());
                   FindNews news = mNewsList.get(position);
                    Intent intent= new Intent(mContext, NewsActivity.class);
                    intent.putExtra("URL",news.getContent());
                    mContext.startActivity(intent);
                }
            });

            return holder;
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (null != mHeaderView && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (null != mFooterView
                && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        }
        if (null != mEmptyView && mNewsList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }


    @Override
    public void onBindViewHolder(final FindNewsAdapter.ViewHolder holder, int position) {

        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_FOOTER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        int realPos = getRealItemPosition(position);


        FindNews news =mNewsList.get(realPos);
        holder.title.setText(news.getTitle());
        Glide.with(mContext).load(news.getPic())
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(holder.cover);
        holder.pre.setText(news.getPre());






    }
    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }



    @Override
    public int getItemCount() {

        int itemCount = mNewsList.size();
        if (null != mEmptyView && itemCount == 0) {
            itemCount++;
        }
        if (null != mHeaderView) {
            itemCount++;
        }
        if (null != mFooterView) {
            itemCount++;
        }
        return itemCount;

    }


    public void addHeaderView(View view) {
        mHeaderView = view;
        notifyItemInserted(0);
    }

    public void addFooterView(View view) {
        mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setEmptyView(View view) {
        mEmptyView = view;
        notifyDataSetChanged();
    }


}