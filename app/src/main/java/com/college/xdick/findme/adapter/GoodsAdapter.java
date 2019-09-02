package com.college.xdick.findme.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Goods;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.GoodsActivity;
import com.college.xdick.findme.ui.Base.BaseActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/4/11.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;


    private int load_more_status;
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;

    public static final int  NO_MORE=2;


    private List<Goods> mGoodsList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;







    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,price,originalPrice,saleGoods;

        ImageView cover;
        CardView cardView;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_goods);
            price=view.findViewById(R.id.price_goods);
            originalPrice =view.findViewById(R.id.original_price_goods);
            saleGoods = view.findViewById(R.id.sale_goods);
            cardView =view.findViewById(R.id.cardview_goods);
            cover = view.findViewById(R.id.cover_goods);

        }
    }


    public GoodsAdapter(List<Goods> activity){
        mGoodsList = activity;
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
                    .inflate(R.layout.item_goods, parent, false);
            final ViewHolder holder = new GoodsAdapter.ViewHolder(view);



            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   int position = holder.getAdapterPosition();
                     Goods goods = mGoodsList.get(position);
                    Intent intent = new Intent(mContext, GoodsActivity.class);
                    intent.putExtra("GOODS",goods);
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
        if (null != mEmptyView && mGoodsList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }


    @Override
    public void onBindViewHolder(final GoodsAdapter.ViewHolder holder, int position) {

        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }

        if (type == ITEM_TYPE_FOOTER) {
            TextView textView= mFooterView.findViewById(R.id.footer_text);
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    textView.setText("上拉加载更多");
                    break;
                case LOADING_MORE:
                    textView.setText("正在加载数据...");
                    break;

                case NO_MORE:
                    textView.setText("没有更多了");
                    break;

            }
            return;
        }


        int realPos = getRealItemPosition(position);
        Goods goods = mGoodsList.get(realPos);
        holder.saleGoods.setText("限量："+goods.getSaleNumber());
        holder.originalPrice.setText("原价￥"+goods.getOriginalPrice());
        holder.price.setText("￥"+goods.getPrice());
        holder.title.setText(goods.getTitle());
        Glide.with(mContext).load(new mGlideUrl(goods.getCover() +"!/fp/20000")).into(holder.cover);




    }
    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }



    @Override
    public int getItemCount() {

        if (mGoodsList!=null) {

            int itemCount = mGoodsList.size();
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

        return 0;

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


    public void changeMoreStatus(int status){
        load_more_status=status;
    }






}