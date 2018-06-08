package com.college.xdick.findme.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.ui.Activity.ActivityActivity;

import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/5/19.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>

{
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;




    private List<String> mPicList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;






    static class ViewHolder extends RecyclerView.ViewHolder{


        ImageView pic;
        TextView more;




        public ViewHolder(View view){
            super(view);
        pic = view.findViewById(R.id.gallery_pic);
        more = view.findViewById(R.id.gallery_more);

        }

        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            }else{
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }



    public GalleryAdapter(List<String> pic){
        mPicList = pic;
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
                    .inflate(R.layout.item_gallery, parent, false);
            final ViewHolder holder = new GalleryAdapter.ViewHolder(view);

            WindowManager wm = (WindowManager)mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();


            FrameLayout frameLayout = view.findViewById(R.id.gallery_layout);
            ViewGroup.LayoutParams para = frameLayout.getLayoutParams();
            para.width=width-dp2px(120);
            frameLayout.setLayoutParams(para);






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
        if (null != mEmptyView && mPicList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }


    @Override
    public void onBindViewHolder(final GalleryAdapter.ViewHolder holder, int position) {

        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_FOOTER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        int realPos = getRealItemPosition(position);
        String picUri= mPicList.get(realPos);
        if (realPos==0){
            Glide.with(mContext).load(picUri).apply(bitmapTransform(new BlurTransformation(10, 8))).into(holder.pic);
            holder.more.setVisibility(View.VISIBLE);
        }
        else {
            holder.more.setVisibility(View.INVISIBLE);
            Glide.with(mContext).load(picUri).into(holder.pic);
        }


    }
    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }



    @Override
    public int getItemCount() {

        if (mPicList!=null) {

            int itemCount = mPicList.size();
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

    private int dp2px(int dpValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,
                mContext.getResources().getDisplayMetrics());
    }

}




