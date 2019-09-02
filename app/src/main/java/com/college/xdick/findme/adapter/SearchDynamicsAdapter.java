package com.college.xdick.findme.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.SearchActivity;
import com.college.xdick.findme.ui.Activity.SearchUserActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;

import java.io.Serializable;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/5/19.
 */

public class SearchDynamicsAdapter extends RecyclerView.Adapter<SearchDynamicsAdapter.ViewHolder>
      //  implements ItemTouchHelperAdapter
{
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


    private List< Dynamics> mActivityList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;





   private List<Dynamics> allList;

   private String searchMark;


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView username,activity,dynamics;
        ImageView cover;
        CardView cardView;


        public ViewHolder(View view){
            super(view);
            username= view.findViewById(R.id.username_user);
            cardView =view.findViewById(R.id.cardview_dynamics);
            cover = view.findViewById(R.id.cover_user);
            activity =view.findViewById(R.id.activityName_dynamics);
            dynamics =view.findViewById(R.id.content_dynamics);

        }


    }



    public SearchDynamicsAdapter(List< Dynamics> activity){
        mActivityList = activity;
    }



   public  void setList(List<Dynamics> list){
        allList=list;

   }

   public  void setSearchMark(String mark){searchMark=mark;}

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

            final ViewHolder holder= new ViewHolder(mFooterView);

            if (mContext instanceof SearchActivity){
                mFooterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SearchUserActivity.class);
                        intent.putExtra("USERLIST",(Serializable) allList);
                        intent.putExtra("EXTRA",searchMark);
                        intent.putExtra("SIGNAL",SearchUserActivity.SEARCH);
                        mContext.startActivity(intent);
                    }
                });
            }

            return holder;


        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_user, parent, false);
            final ViewHolder holder = new SearchDynamicsAdapter.ViewHolder(view);


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Dynamics activity = mActivityList.get(position);
                    Intent intent = new Intent(mContext, UserCenterActivity.class);
                    intent.putExtra("USER",activity);
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
        if (null != mEmptyView && mActivityList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }


    @Override
    public void onBindViewHolder(final SearchDynamicsAdapter.ViewHolder holder, int position) {

        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        if (type == ITEM_TYPE_FOOTER) {
            if (!(mContext instanceof SearchActivity)) {
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

            }}
            return;
        }

        int realPos = getRealItemPosition(position);

        final Dynamics myUser = mActivityList.get(realPos);


         holder.username.setText(myUser.getMyUser().getUsername());

        holder.activity.setText(myUser.getActivity().getTitle());

        holder.dynamics.setText(myUser.getContent());
         Glide.with(mContext).load(new mGlideUrl(myUser.getMyUser().getAvatar() +"!/fp/15000"))
                 .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                 .apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(holder.cover);













    }
    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }



    @Override
    public int getItemCount() {

        if (mActivityList!=null) {

            int itemCount = mActivityList.size();
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


