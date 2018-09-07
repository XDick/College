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
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.SearchUserActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;

import java.io.Serializable;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/5/19.
 */

public class UserCenterUserAdapter extends RecyclerView.Adapter<UserCenterUserAdapter.ViewHolder>
      //  implements ItemTouchHelperAdapter
{
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;




    private List<MyUser> mActivityList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;





   private List<MyUser> allList;


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView username,school;
        ImageView cover;
        CardView cardView;


        public ViewHolder(View view){
            super(view);
            username= view.findViewById(R.id.username_user);
            cardView =view.findViewById(R.id.cardview_user);
            cover = view.findViewById(R.id.cover_user);
            school =view.findViewById(R.id.school_user);


        }


    }



    public UserCenterUserAdapter(List<MyUser> activity){
        mActivityList = activity;
    }



   public  void setList(List<MyUser> list){
        allList=list;

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
                    .inflate(R.layout.item_usercenter_user, parent, false);
            final ViewHolder holder = new UserCenterUserAdapter.ViewHolder(view);


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    MyUser activity = mActivityList.get(position);
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
    public void onBindViewHolder(final UserCenterUserAdapter.ViewHolder holder, int position) {

        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_FOOTER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        int realPos = getRealItemPosition(position);

        final MyUser myUser = mActivityList.get(realPos);


         holder.username.setText(myUser.getUsername());

         holder.school.setText(myUser.getSchool());

         Glide.with(mContext).load(myUser.getAvatar())
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



}


  /*  @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mActivityList,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        //移除数据
        mActivityList.remove(position);
        notifyItemRemoved(position);
    }*/
