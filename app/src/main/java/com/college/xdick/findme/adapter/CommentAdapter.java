package com.college.xdick.findme.adapter;

import android.app.Activity;
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
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.ui.Fragment.StartActivityFragment;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> mCommentList;

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
             username= view.findViewById(R.id.activity_comment_username);
             content= view.findViewById(R.id.activity_comment_content);
             time = view.findViewById(R.id.activity_comment_time);
             avatar = view.findViewById(R.id.activity_comment_avatar);
             replyto = view.findViewById(R.id.comment_fromwho);
             replycontent = view.findViewById(R.id.comment_fromcontent);
             replylayout=  view. findViewById(R.id.comment_replylayout);
             layout = view.findViewById(R.id.comment_layout);

        }
    }


    public CommentAdapter(List<Comment> activity){
        mCommentList = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }

        if (viewType == ITEM_TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else if (viewType == ITEM_TYPE_EMPTY) {
            return new ViewHolder(mEmptyView);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new ViewHolder(mFooterView);
        } else {   View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment,parent,false);
       final   ViewHolder holder = new  CommentAdapter.ViewHolder(view);

            final ActivityActivity activity = ( ActivityActivity)mContext;
           final MyActivity activity2=(MyActivity)activity.getIntent().getSerializableExtra("ACTIVITY");
       holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {

               final EasyPopup  deletePop = EasyPopup.create()
                       .setContentView(mContext, R.layout.popup_comment)
                       .setWidth(400)
                       .setBackgroundDimEnable(true)
                       //变暗的透明度(0-1)，0为完全透明
                       .setDimValue(0.4f)
                       //变暗的背景颜色
                       .apply();

               int position = holder.getAdapterPosition();
               final Comment comment = mCommentList.get(position);
               deletePop.showAtAnchorView(holder.layout, YGravity.CENTER, XGravity.CENTER, 0, 0);

               LinearLayout delete =  deletePop.findViewById(R.id.delete_comment);
               if (!BmobUser.getCurrentUser().getObjectId().equals(activity2.getHost().getObjectId())&&!BmobUser.getCurrentUser().getObjectId().equals(comment.getUser().getObjectId()))
                   delete.setVisibility(View.GONE);
                   delete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       comment.delete(new UpdateListener() {
                           @Override
                           public void done(BmobException e) {
                               if (e==null){
                                   MyActivity myActivity = new MyActivity();
                                   myActivity.setObjectId(activity2.getObjectId());
                                   myActivity.setDate(activity2.getDate());
                                   myActivity.increment("commentCount",-1);
                                   myActivity.update();
                                   activity.myfragment.setSize(0);
                                   activity.myfragment.setCount(0);
                                   activity.myfragment.initComment(StartActivityFragment.REFRESH);

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


                       if (MyUser.getCurrentUser(MyUser.class)==null){
                           mContext.startActivity(new Intent(mContext,LoginActivity.class));
                           ((Activity)mContext).finish();
                           //Toast.makeText(mContext,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                           return;
                       }
               activity.ifReply=false;
               int position = holder.getAdapterPosition();
               Comment comment = mCommentList.get(position);
               Comment comment2 = new Comment();
               comment2.setReplyUser(comment.getUser());

               comment2.setReplyComment(comment);
               comment2.setActivity(comment.getActivity());
               activity.reply(comment,comment2);
             /*  if (activity2.getHostId().equals(MyUser.getCurrentUser(MyUser.class).getObjectId())){
                       activity.ifReply=false;
                       int position = holder.getAdapterPosition();
                       Comment comment = mCommentList.get(position);
                       Comment comment2 = new Comment();
                       comment2.setReplyusername(comment.getUserName());
                       comment2.setReplyuserId(comment.getUserID());
                       comment2.setReplycontent(comment.getContent());
                       comment2.setActivityID(comment.getActivityID());
                       activity.reply(comment,comment2);
                   }
                   else{
                   if (activity2.getJoinUser()==null) {
                   Toast.makeText(mContext,"加入活动才可以评论",Toast.LENGTH_SHORT).show();
                   return;
               }
               if (activity.ifJoin||Arrays.asList(activity2.getJoinUser()).contains(MyUser.getCurrentUser(MyUser.class).getObjectId()))
               { activity.ifReply=false;
                   int position = holder.getAdapterPosition();
                   Comment comment = mCommentList.get(position);
                   Comment comment2 = new Comment();
                   comment2.setReplyusername(comment.getUserName());
                   comment2.setReplyuserId(comment.getUserID());
                   comment2.setReplycontent(comment.getContent());
                   comment2.setActivityID(comment.getActivityID());
                   activity.reply(comment,comment2);

               }
               else {
                   Toast.makeText(mContext,"加入活动才可以评论",Toast.LENGTH_SHORT).show();
               }


               }*/

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
    public void onBindViewHolder(final  CommentAdapter.ViewHolder holder,  int position) {
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

       final Comment comment = mCommentList.get(realPos);
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
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.getObject(comment.getUser().getObjectId(), new QueryListener<MyUser>() {

            @Override
            public void done(final MyUser object, BmobException e) {

                if(e==null){
                    holder.avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (MyUser.getCurrentUser(MyUser.class)==null){
                                mContext.startActivity(new Intent(mContext,LoginActivity.class));
                                ((Activity)mContext).finish();
                              //  Toast.makeText(mContext,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                                return;
                            }
                                    Intent intent = new Intent(mContext, UserCenterActivity.class);
                                    intent.putExtra("USER",object);
                                    mContext.startActivity(intent);


                        }
                    });

                    Glide.with(mContext).load(object.getAvatar())
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                            .apply(bitmapTransform(new CropCircleTransformation())).into(holder.avatar);


                }else{

                }


            }

        });

        holder.replyto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUser.getCurrentUser(MyUser.class)==null){
                    mContext.startActivity(new Intent(mContext,LoginActivity.class));
                    ((Activity)mContext).finish();
                   // Toast.makeText(mContext,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                    return;
                }
                final  String id = comment.getReplyUser().getObjectId();
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
