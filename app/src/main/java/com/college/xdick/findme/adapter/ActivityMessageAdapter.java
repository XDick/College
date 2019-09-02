package com.college.xdick.findme.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.BmobIM.newClass.PrivateConversation;
import com.college.xdick.findme.MyClass.DownLoadImageService;
import com.college.xdick.findme.MyClass.ImageDownLoadCallBack;
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.ActivityMessageBean;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;
import com.college.xdick.findme.ui.Activity.MyJoinActivity;
import com.college.xdick.findme.ui.Activity.MyLikeActivity;
import com.college.xdick.findme.ui.Activity.MySetActivity;
import com.college.xdick.findme.ui.Activity.SetDynamicsActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.ui.Fragment.MessageFragment;
import com.college.xdick.findme.ui.Fragment.PrivateConversationFragment;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/5/19.
 */

public class ActivityMessageAdapter extends RecyclerView.Adapter<ActivityMessageAdapter.ViewHolder>
      //  implements ItemTouchHelperAdapter
{
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;




    private List<ActivityMessageBean> mActivityList;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;

    private Dialog mDialog;
    private ViewPagerFixed pager;
    private MessageFragment fragment;




    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,time,content,acname;
        ImageView cover,notifyPic;
        CardView cardView;
        Button delete;
        LinearLayout askLayout,acceptLayout,rejectLayout;


        public ViewHolder(View view){
            super(view);
            title= view.findViewById(R.id.title_ac);
            cardView =view.findViewById(R.id.cardview_ac);
            cover = view.findViewById(R.id.cover_ac);
            time= view.findViewById(R.id.time_ac);
            delete = view.findViewById(R.id.delete);
            content = view.findViewById(R.id.content_ac);
            acname = view.findViewById( R.id.acname_ac);
            acceptLayout= view.findViewById(R.id.pic_accept);
            rejectLayout= view.findViewById(R.id.pic_reject);
            askLayout=view.findViewById(R.id.pic_ask_layout);
            notifyPic = view.findViewById(R.id.notify_pic);
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


    public ActivityMessageAdapter(List<ActivityMessageBean> activity){
        mActivityList = activity;
    }

    public void setFragment(MessageFragment fragment) {
        this.fragment = fragment;
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
                    .inflate(R.layout.item_activity_message, parent, false);
            final ViewHolder holder = new ActivityMessageAdapter.ViewHolder(view);










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
    public void onBindViewHolder(final ActivityMessageAdapter.ViewHolder holder, final int position) {

        final int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_FOOTER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        int realPos = getRealItemPosition(position);

        final ActivityMessageBean activity = mActivityList.get(realPos);
        holder.notifyPic.setVisibility(View.GONE);
        holder.title.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.time.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.content.setTextColor(mContext.getResources().getColor(R.color.black));
        if (activity.getType().equals("dynamics_picture")) {
            holder.acname.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.askLayout.setVisibility(View.VISIBLE);
            holder.acceptLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dynamics dynamics = new Dynamics();
                    dynamics.setObjectId(activity.getActivityId().substring(0,activity.getActivityId().lastIndexOf(";")));
                    dynamics.setIfAdd2Gallery(true);
                    dynamics.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sendMessage(BmobUser.getCurrentUser().getUsername() + "已同意添加" + activity.getActivityname().substring(0,activity.getActivityname().lastIndexOf(";"))
                                                + "的照片",
                                        new BmobIMUserInfo(activity.getUserId(), activity.getUsername(), activity.getUserAvatar())
                                        , activity.getActivityname().substring(activity.getActivityname().lastIndexOf(";") + 1)
                                        , activity.getActivityId().substring(activity.getActivityId().lastIndexOf(";") + 1));
                                activity.setIfCheck("true");
                                activity.setActivityname("(已同意)"+activity.getActivityname());
                                activity.save();
                                holder.askLayout.setVisibility(View.GONE);
                                EventBus.getDefault().post(new ReadEvent(""));
                                Toast.makeText(mContext,"已同意",Toast.LENGTH_SHORT).show();
                            }

                            else{


                            }
                        }
                    });
                }
            });
            holder.rejectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(BmobUser.getCurrentUser().getUsername() + "拒绝添加" + activity.getActivityname().substring(0,activity.getActivityname().lastIndexOf(";"))
                                    + "的照片",
                            new BmobIMUserInfo(activity.getUserId(), activity.getUsername(), activity.getUserAvatar())
                            , activity.getActivityname().substring(activity.getActivityname().lastIndexOf(";") + 1)
                            , activity.getActivityId().substring(activity.getActivityId().lastIndexOf(";") + 1));
                    activity.setIfCheck("true");
                    activity.setActivityname("(拒绝)"+activity.getActivityname());
                    activity.save();
                    holder.askLayout.setVisibility(View.GONE);
                    EventBus.getDefault().post(new ReadEvent(""));
                    Toast.makeText(mContext,"已拒绝",Toast.LENGTH_SHORT).show();
                }
            });

        } else if (activity.getType().equals("dynamics")) {
            holder.acname.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (activity.getType().equals("activity")) {
            holder.acname.setTextColor(Color.parseColor("#2758e1"));
        } else if (activity.getType().equals("user")) {
            holder.acname.setTextColor(Color.parseColor("#e21fec"));
        }
        else if (activity.getType().equals("notify")){
            holder.title.setTextColor(Color.parseColor("#032483"));

        }
        else if (activity.getType().equals("notifyPic")){
            holder.title.setTextColor(Color.parseColor("#032483"));
            final String url =activity.getContent()
                    .substring(activity.getContent()
                            .indexOf("[URL]")+5) ;
            holder.notifyPic.setVisibility(View.VISIBLE);

            Glide.with(mContext).
                    load(new mGlideUrl(url+"!/fp/30000"))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE).error(R.drawable.image_failed)).
                    listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.notifyPic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            holder.notifyPic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showPictureDialog(0,
                                            url);
                                }
                            });
                            return false;
                        }
                    }).into(holder.notifyPic);



        }




        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = sdf.format(new Date(Long.parseLong(activity.getTime())));
        holder.title.setText(activity.getUsername());
        if (activity.getIfCheck().equals("false")) {
            holder.title.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.time.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.content.setTextColor(mContext.getResources().getColor(R.color.red));

        } else {
            holder.askLayout.setVisibility(View.GONE);
        }

        holder.time.setText(time);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final ActivityMessageBean activity = mActivityList.get(position);
                DataSupport.deleteAll(ActivityMessageBean.class, "username=?and time=?", activity.getUsername(), activity.getTime());

                fragment.initData();
                fragment.initRecyclerView();



            }


        });
        holder.content.setText(activity.getContent());
       if (activity.getType().equals("dynamics_picture"))
        {   try {

            holder.acname.setText(activity.getActivityname().substring(0,activity.getActivityname().lastIndexOf(";")));
        }
        catch (Exception e){

        }

            }
            else if (activity.getType().equals("notify")){
           holder.title.setText(activity.getUsername());
           holder.acname.setText(activity.getActivityname());
       }
       else if (activity.getType().equals("notifyPic")){
           holder.acname.setText(activity.getUsername());
           holder.acname.setText(activity.getActivityname());
           holder.content.setText(activity.getContent().substring(0,activity.getContent().indexOf("[URL]")));

       }
            else {
            holder.acname.setText(activity.getActivityname());
        }


         Glide.with(mContext).load(new mGlideUrl(activity.getUserAvatar() +"!/fp/10000")
         )
                 .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE).error(R.drawable.head)).apply(bitmapTransform(new CropCircleTransformation())).into(holder.cover);

      holder.cover.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             BmobQuery<MyUser> query =new BmobQuery<>();
             query.getObject(activity.getUserId(), new QueryListener<MyUser>() {
                 @Override
                 public void done(MyUser myUser, BmobException e) {
                     if (e==null){Intent intent = new Intent(mContext, UserCenterActivity.class);
                     intent.putExtra("USER",myUser);
                     mContext.startActivity(intent);}
                 }
             });
          }
      });

      holder.cardView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (activity.getType().equals("activity")||
                      activity.getType().equals("notify")
              ||activity.getType().equals("notifyPic")){
                 BmobQuery<MyActivity> query = new BmobQuery<>();
                 query.include("host[username|avatar|Exp]");
                  query.getObject(activity.getActivityId(), new QueryListener<MyActivity>() {
                      @Override
                      public void done(MyActivity activity, BmobException e) {
                          if (e==null){Intent intent = new Intent(mContext, ActivityActivity.class);
                              intent.putExtra("ACTIVITY",activity);
                              mContext.startActivity(intent);}

                      }
                  });
              }
              else if (activity.getType().equals("dynamics")){

                                   final BmobQuery<Dynamics> query = new BmobQuery<>();
                  query.include("myUser[avatar|username|school],activity[title|time|cover|].host.[username]");
                                   query.getObject(activity.getActivityId(), new QueryListener<Dynamics>() {
                                       @Override
                                       public void done(final Dynamics dynamics, BmobException e) {
                                        if (e==null){
                                            BmobQuery<MyUser> query1 = new BmobQuery<>();
                                            query1.getObject(dynamics.getMyUser().getObjectId(), new QueryListener<MyUser>() {
                                                @Override
                                                public void done(MyUser myUser, BmobException e) {
                                                    if (e==null){
                                                        Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                                        intent.putExtra("DYNAMICS", dynamics);
                                                        intent.putExtra("USER",myUser);
                                                        mContext.startActivity(intent);

                                                    }
                                                }
                                            });
                                        }

                    }
                });


              }
              else if (activity.getType().equals("dynamics_picture")){
                  final BmobQuery<Dynamics> query = new BmobQuery<>();
                  query.include("myUser[avatar|username|school],activity[title|time|cover|].host.[username]");
                  query.getObject(activity.getActivityId().substring(0,activity.getActivityId().lastIndexOf(";")), new QueryListener<Dynamics>() {
                      @Override
                      public void done(final Dynamics dynamics, BmobException e) {
                          if (e==null){  BmobQuery<MyUser> query1 = new BmobQuery<>();
                              query1.getObject(dynamics.getMyUser().getObjectId(), new QueryListener<MyUser>() {
                                  @Override
                                  public void done(MyUser myUser, BmobException e) {
                                      if (e==null){
                                          Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                          intent.putExtra("DYNAMICS", dynamics);
                                          intent.putExtra("USER",myUser);
                                          mContext.startActivity(intent);

                                      }
                                  }
                              });}


                      }
                  });
              }
              else if (activity.getType().equals("user")){
                  BmobQuery<MyUser> query = new BmobQuery<>();
                  query.getObject(activity.getUserId(), new QueryListener<MyUser>() {
                      @Override
                      public void done(MyUser myUser, BmobException e) {
                          if (e==null){
                               Intent intent =new Intent(mContext,UserCenterActivity.class);
                               intent.putExtra("USER",myUser);
                               mContext.startActivity(intent);
                          }
                      }
                  });


              }


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
    public void sendMessage(String content ,BmobIMUserInfo info,String title,String id) {
  MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        if(!myUser.getObjectId().equals(info.getUserId())){

            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
            BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
            ActivityMessage msg = new ActivityMessage();
            msg.setContent(content);//给对方的一个留言信息
            Map<String, Object> map = new HashMap<>();
            map.put("currentuser", info.getUserId());
            map.put("userid", myUser.getObjectId());
            map.put("username",myUser.getUsername());
            map.put("useravatar",myUser.getAvatar());
            map.put("activityid",id);
            map.put("activityname",title);
            map.put("type","activity");
            msg.setExtraMap(map);
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {//发送成功
                    } else {//发送失败
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void showPictureDialog(final int mPosition, final String uri) {




        //创建dialog
        mDialog = new Dialog(mContext, R.style.PictureDialog);
        final Window window1 = mDialog.getWindow() ;
        WindowManager m = ((Activity)mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window1.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 1.0); // 改变的是dialog框在屏幕中的位置而不是大小
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕
        window1.setAttributes(p);
        View inflate = View.inflate(mContext, R.layout.chat_picture_dialog, null);//该layout在后面po出
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.setContentView(inflate, layoutParams);



        pager = inflate.findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        final List<String> mListPicPath = new ArrayList<>();
        mListPicPath.add(uri);
        PicturePageAdapter adapter = new PicturePageAdapter((ArrayList<String>) mListPicPath, mContext);
        pager.setAdapter(adapter);
        pager.setPageMargin(0);
        pager.setCurrentItem(mPosition);
        window1.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });
        mDialog.show();
        adapter.setOnPictureClickListener(new PicturePageAdapter.OnPictureClickListener() {
            @Override
            public void OnClick() {
                window1.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mDialog.dismiss();
            }
        });
        //长按图片保存
        adapter.setOnPictureLongClickListener(new PicturePageAdapter.OnPictureLongClickListener() {
            @Override
            public void OnLongClick() {
                //展示保存取消dialog
                final EasyPopup savePop = EasyPopup.create()
                        .setContentView(mContext, R.layout.popup_savepic)
                        .setWidth(400)
                        .setBackgroundDimEnable(true)
                        //变暗的透明度(0-1)，0为完全透明
                        .setDimValue(0.4f)
                        //变暗的背景颜色
                        .apply();


                savePop.showAtAnchorView(pager, YGravity.CENTER, XGravity.CENTER, 0, 0);

                LinearLayout savepic = savePop.findViewById(R.id.save_pic);
                savepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadFile(uri);
                            }
                        }).start();

                        savePop.dismiss();
                    }
                });
            }
        });
    }


    public void downloadFile(final String url) {
        DownLoadImageService service = new DownLoadImageService(getApplicationContext(),
                url,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {
                    }
                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap) {
                        // 在这里执行图片保存方法
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,"保存成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
        //启动图片下载线程
        new Thread(service).start();


    }


}