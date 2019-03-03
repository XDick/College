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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.DynamicsComment;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/5/3.
 */

public class DynamicsCommentAdapter extends RecyclerView.Adapter<DynamicsCommentAdapter.ViewHolder> {

    private List<DynamicsComment> mCommentList;
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

    public static final int DONTSHOW=3;

    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;



    static class ViewHolder extends RecyclerView.ViewHolder{

       TextView username,content,time,replyto,replycontent;
        LinearLayout replylayout,layout;
        ImageView avatar;


        public ViewHolder(View view){
            super(view);
             username= view.findViewById(R.id.dynamics_comment_username);
             content= view.findViewById(R.id.dynamics_comment_content);
             time = view.findViewById(R.id.dynamics_comment_time);
             avatar = view.findViewById(R.id.dynamics_comment_avatar);
             replyto = view.findViewById(R.id.comment_fromwho_dynamics);
             replycontent = view.findViewById(R.id.comment_fromcontent_dynamics);
             replylayout=  view. findViewById(R.id.comment_replylayout_dynamics);
             layout = view.findViewById(R.id.comment_layout_dynamics);

        }
    }


    public DynamicsCommentAdapter(List<DynamicsComment> activity){
        mCommentList = activity;
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
                .inflate(R.layout.item_dynamics_comment,parent,false);
       final   ViewHolder holder = new  DynamicsCommentAdapter.ViewHolder(view);


       final MainDynamicsActivity activity = (MainDynamicsActivity)mContext;
       final Dynamics dynamics=(Dynamics)activity.getIntent().getSerializableExtra("DYNAMICS");
       holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    final DynamicsComment comment = mCommentList.get(position);
                    final EasyPopup deletePop = EasyPopup.create()
                            .setContentView(mContext, R.layout.popup_comment)
                            .setWidth(400)
                            .setBackgroundDimEnable(true)
                            //变暗的透明度(0-1)，0为完全透明
                            .setDimValue(0.4f)
                            //变暗的背景颜色
                            .apply();

                    deletePop.showAtAnchorView(holder.layout, YGravity.CENTER, XGravity.CENTER, 0, 0);

                    LinearLayout delete =  deletePop.findViewById(R.id.delete_comment);
                    if (!BmobUser.getCurrentUser().getObjectId().equals(dynamics.getMyUser().getObjectId())&&!BmobUser.getCurrentUser().getObjectId().equals(comment.getUser().getObjectId()))
                        delete.setVisibility(View.GONE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            comment.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e==null){
                                        Dynamics dynamics1 = new Dynamics();
                                        dynamics1.setObjectId(dynamics.getObjectId());
                                        dynamics1.increment("replycount",-1);
                                        dynamics1.setIfAdd2Gallery(dynamics.isIfAdd2Gallery());
                                        dynamics1.update();
                                         activity.setSize(0);
                                         activity.setCount(0);
                                         activity.initComment(MainDynamicsActivity.REFRESH);

                                        deletePop.dismiss();
                                        Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(mContext,"删除失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                    return true;
                }
            });

            holder.layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


                activity.ifReply=false;
               int position = holder.getAdapterPosition();
               DynamicsComment comment = mCommentList.get(position);
               DynamicsComment comment2 = new DynamicsComment();
               comment2.setReplyUser(comment.getUser());
               comment2.setReplyComment(comment);
               comment2.setDynamics(comment.getDynamics());
               activity.reply(comment,comment2);

           }
       });

       holder.replyto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int position = holder.getAdapterPosition();
               DynamicsComment comment = mCommentList.get(position);
               final String id = comment.getReplyUser().getObjectId();
               BmobQuery<MyUser> query = new BmobQuery<>();
               query.getObject(id, new QueryListener<MyUser>() {
                   @Override
                   public void done(MyUser myUser, BmobException e) {

                       if (e==null){
                               Intent intent = new Intent(mContext, UserCenterActivity.class);
                               intent.putExtra("USER",myUser);
                               mContext.startActivity(intent);}


                   }
               });

           }
       });



        return holder;}
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
        if (null != mEmptyView && mCommentList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }
    @Override
    public void onBindViewHolder(final  DynamicsCommentAdapter.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER

                || type == ITEM_TYPE_EMPTY) {
            return;
        }
        if (type == ITEM_TYPE_FOOTER) {
            TextView textView= mFooterView.findViewById(R.id.footer_text);
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    mFooterView.setVisibility(View.VISIBLE);
                    textView.setText("上拉加载更多");
                    break;
                case LOADING_MORE:
                    mFooterView.setVisibility(View.VISIBLE);
                    textView.setText("正在加载数据...");
                    break;

                case NO_MORE:
                    mFooterView.setVisibility(View.VISIBLE);
                    textView.setText("没有更多了");
                    break;
                case DONTSHOW:
                    mFooterView.setVisibility(View.GONE);
                    break;

            }
            return;
        }


        int realPos = getRealItemPosition(position);

        final DynamicsComment comment = mCommentList.get(realPos);
        String fromwho,fromcontent;



        if (comment.getReplyUser()==null&&comment.getReplyComment()==null){

            holder.replylayout.setVisibility(View.GONE);
        }
        else {
            fromwho=comment.getReplyUser().getUsername();
            if (comment.getReplyComment().getContent()==null){
                fromcontent= "该评论已被删除";
            }else {
                fromcontent= comment.getReplyComment().getContent();
            }
            holder.replyto.setText("@"+  fromwho);
            holder.replycontent.setText(":"+ fromcontent);
        }

        holder.username.setText(comment.getUser().getUsername());
        holder.content.setText(comment.getContent());
        holder.time.setText(comment.getCreatedAt());


                    holder.avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                    Intent intent = new Intent(mContext, UserCenterActivity.class);
                                    intent.putExtra("USER",comment.getUser());
                                    mContext.startActivity(intent);

                        }
                    });
                    Glide.with(mContext).load(comment.getUser().getAvatar())
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                            .apply(bitmapTransform(new CropCircleTransformation())).into(holder.avatar);




    }


    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }




    @Override
    public int getItemCount() {
        if (mCommentList!=null) {

            int itemCount =mCommentList.size();
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
