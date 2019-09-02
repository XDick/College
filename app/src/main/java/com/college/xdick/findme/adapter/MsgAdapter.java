package com.college.xdick.findme.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.college.xdick.findme.MyClass.DownLoadImageService;
import com.college.xdick.findme.MyClass.ImageDownLoadCallBack;
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.holder.BaseViewHolder;
import com.college.xdick.findme.adapter.holder.TextViewHolder;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.util.ClassFileHelper;
import com.sqk.emojirelease.EmojiUtil;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.view.View.GONE;
import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static org.greenrobot.eventbus.EventBus.TAG;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.viewHolder> {


    //private int NOT = 0;
    //private int DONE = 1;

    private List<BmobIMMessage> mMsgList;
    private Context mContext;
    private String currentUid = BmobUser.getCurrentUser().getObjectId();
    private Dialog mDialog;
    private ViewPagerFixed pager;
    private BmobIMConversation conversation;

    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;

    private final int TYPE_RECEIVER_LOCATION = 5;

    static public class viewHolder extends RecyclerView.ViewHolder{


         LinearLayout leftLayout,rightLayout,rightBackGround,leftBackGround;

        TextView leftMsg,rightMsg,time,title,time2,host,
                time3,title2,host2;

         ImageView avatar_me,avatar_you,cover,cover2
                 ,rightImage,leftImage,sendError;
        ContentLoadingProgressBar image_progress,image_progress2;
        CardView cardView,cardView2;


        public viewHolder(View view) {
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftMsg = view.findViewById(R.id.left_msg);
            rightMsg = view.findViewById(R.id.right_msg);
            time = view.findViewById(R.id.msg_time);
            avatar_me = view.findViewById(R.id.msg_avatar_me);
            avatar_you = view.findViewById(R.id.msg_avatar_you);
            rightBackGround = view.findViewById(R.id.right_msg_layout);
             rightImage = view.findViewById(R.id.image_right);
             leftImage = view.findViewById(R.id.image_left);
             leftBackGround =view.findViewById(R.id.left_msg_layout);
            sendError= view.findViewById(R.id.send_error);
            title = view.findViewById(R.id.title_ac);
            title2 = view.findViewById(R.id.title_ac2);
            cardView = view.findViewById(R.id.cardview_ac);
            cardView2 = view.findViewById(R.id.cardview_ac2);
            cover = view.findViewById(R.id.cover_ac);
            time2 = view.findViewById(R.id.time_ac);
            cover2 = view.findViewById(R.id.cover_ac2);
            time3 = view.findViewById(R.id.time_ac2);
            image_progress= view.findViewById(R.id.image_progress);
            image_progress2= view.findViewById(R.id.image_progress_left);
            host = view.findViewById(R.id.host_ac);
            host2 = view.findViewById(R.id.host_ac2);
        }
        }

    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while (index-- > 0) {
            if (message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;
        while (index-- > 0) {
            if (this.getItemId(index) == id) {
                position = index;
                break;
            }
        }
        return position;
    }

    public void addMessages(List<BmobIMMessage> messages) {
        mMsgList.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        mMsgList.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mMsgList.remove(position);
        notifyDataSetChanged();
    }

    public BmobIMMessage getFirstMessage() {
        if (null != mMsgList && mMsgList.size() > 0) {
            return mMsgList.get(0);
        } else {
            return null;
        }
    }

    public int getCount() {
        return this.mMsgList == null ? 0 : this.mMsgList.size();
    }

    public BmobIMMessage getItem(int position) {
        return this.mMsgList == null ? null : (position >= this.mMsgList.size() ? null : this.mMsgList.get(position));
    }


    public MsgAdapter(List<BmobIMMessage> msgList) {
        mMsgList = msgList;
    }

   public void setConversation(BmobIMConversation c){
        conversation=c;
    }


    @Override
    public MsgAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
      /*  if (viewType == TYPE_SEND_TXT||viewType ==TYPE_RECEIVER_TXT) {
            return new TextViewHolder(mContext, parent);
        }*/
      /*  } else if (viewType == TYPE_SEND_IMAGE) {
            return new SendImageHolder(parent.getContext(), parent,c,onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_TXT) {
            return new ReceiveTextHolder(parent.getContext(), parent,onRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVER_IMAGE) {
            return new ReceiveImageHolder(parent.getContext(), parent,onRecyclerViewListener);
        } else{//开发者自定义的其他类型，可自行处理
            return null;
        }*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,
                parent,false);
        return new viewHolder(view);

    }



   /* @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder)holder).bindData(mMsgList,status,position,getCount());
   }*/



   @Override
    public void onBindViewHolder(final MsgAdapter.viewHolder holder, final int position) {

        holder.leftLayout.setVisibility(View.VISIBLE);
        holder.rightLayout.setVisibility(View.VISIBLE);
        String currentUid="";
        currentUid = BmobUser.getCurrentUser().getObjectId();
        final BmobIMMessage msg = mMsgList.get(position);

        long t=Long.valueOf(msg.getCreateTime());
        SimpleDateFormat sdf1= new SimpleDateFormat("yyyy/MM/dd HH:mm");

        String time1 = sdf1.format(new Date(t));//long转化成Date

        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String  nowtime = sdf.format(new Date());

        String  rawdate =sdf.format(new Date(t));

        String time = time1.substring(time1.indexOf(" ")+1);

        if (nowtime.equals(rawdate)){
            holder.time.setText(time);
            if (position-1>=0&&msg.getCreateTime()-mMsgList.get(position-1).getCreateTime()<60*1000){
                holder.time.setVisibility(GONE);
            }else {
                holder.time.setText(time);
            }
        }
        else{

            holder.time.setText(time1);
        }


        Glide.with(mContext).load(new mGlideUrl(TextUtils.isEmpty(conversation.getConversationIcon()) ? msg.getBmobIMUserInfo().getAvatar():conversation.getConversationIcon()))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())
        .error(R.drawable.head)).into(holder.avatar_you);

       holder.avatar_you.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               BmobQuery<MyUser> query = new BmobQuery<>();
               query.getObject(conversation.getConversationId(), new QueryListener<MyUser>() {
                   @Override
                   public void done(MyUser myUser, BmobException e) {
                       Intent intent = new Intent(mContext, UserCenterActivity.class);
                       intent.putExtra("USER", myUser);
                       mContext.startActivity(intent);
                   }

               });

           }});

       holder.avatar_me.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(mContext, UserCenterActivity.class);
               intent.putExtra("USER", MyUser.getCurrentUser(MyUser.class));
               mContext.startActivity(intent);
           }
       });

        Glide.with(mContext).load(new mGlideUrl(BmobUser.getCurrentUser(MyUser.class)
                .getAvatar() +"!/fp/2000"))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE).error(R.drawable.head)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(holder.avatar_me);






         holder.sendError.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 BmobIMImageMessage message=null;
                 if (msg.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
                     message = BmobIMImageMessage.buildFromDB(true, msg);

                  message.setExtra(msg.getExtra());

                 }

                conversation.resendMessage(message==null?msg:message, new MessageSendListener() {
                     @Override
                     public void onStart(BmobIMMessage msg) {

                         holder.sendError.setVisibility(View.GONE);

                        // holder.rightBackGround.setBackgroundResource(R.drawable.bubble_right_not);

                     }

                     @Override
                     public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                         if (e==null){


                         }
                         else {
                             Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                             notifyDataSetChanged();
                         }
                     }


                 });
             }
         });
        if(msg.getFromId().equals(currentUid)) {

            if (!msg.getExtra().contains("activityid")){
                holder.cardView2.setVisibility(GONE);
            }
            else {
         /*   holder.leftMsg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));*/
                holder.cardView2.setVisibility(View.VISIBLE);
                try {
                    JSONObject json = new JSONObject( msg.getExtra());
                    String cover = json.getString("activitycover");
                    String title = json.getString("activitytitle");
                    String host = json.getString("activityhost");
                    String time2 = json.getString("activitytime");
                    final String id = json.getString("activityid");
                    holder.title2.setText(title);

                    holder.time3.setText(time2);

                    Glide.with(mContext).load(new mGlideUrl(cover +"!/fp/2000"))
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(holder.cover2);

                    holder.host2.setText("由"+host+"发起");

                    holder.cardView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BmobQuery<MyActivity> query = new BmobQuery<>();
                            query.include("host[avatar|username|Exp]");
                            query.getObject(id, new QueryListener<MyActivity>() {
                                @Override
                                public void done(MyActivity activity, BmobException e) {
                                    if (e==null){

                                        Intent intent = new Intent(mContext, ActivityActivity.class);
                                        intent.putExtra("ACTIVITY",activity);
                                        mContext.startActivity(intent);
                                    }
                                }
                            });




                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }



            }

            try {

                if (msg.getMsgType().equals(BmobIMMessageType.IMAGE.getType()))
                {
                    holder.rightMsg.setVisibility(GONE);
                    holder.rightImage.setVisibility(View.VISIBLE);
                   final  List<String> localPath = new ArrayList<>();
                   localPath.add(null);
                    try {
                        String extra = msg.getExtra();
                        if (!TextUtils.isEmpty(extra)) {
                            JSONObject json = new JSONObject(extra);
                           localPath.add(json.getString("localPath"));
                        }
                    }
                            catch (Exception e){
                        e.printStackTrace();
                            }

                    if (msg.getSendStatus()== BmobIMSendStatus.SEND_FAILED.getStatus() ||msg.getSendStatus() == BmobIMSendStatus.UPLOAD_FAILED.getStatus()) {
                        holder.sendError.setVisibility(View.VISIBLE);
                        holder.image_progress.setVisibility(View.GONE);

                        Glide.with(mContext).
                                load(new mGlideUrl(localPath.get(localPath.size()-1) ))
                                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE).error(R.drawable.image_failed)).
                                listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                        holder.rightImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        });
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                        holder.rightImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showPictureDialog(
                                                        localPath.get(localPath.size()-1));
                                            }
                                        });

                                        return false;
                                    }
                                }).
                                into(holder.rightImage);
                    }
                    else if (msg.getSendStatus()== BmobIMSendStatus.SENDING.getStatus()) {
                           holder.image_progress.setVisibility(View.VISIBLE);

                           Glide.with(mContext).load(new mGlideUrl(localPath.get(localPath.size()-1))).apply(RequestOptions.bitmapTransform(new BlurTransformation())).
                                   into(holder.rightImage);
                            holder.rightImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //
                                }
                            });
                    } else {
                        holder.image_progress.setVisibility(View.GONE);
                        Glide.with(mContext).
                                load(localPath.get(localPath.size()-1))
                                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE).error(R.drawable.image_failed)).
                                listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                        holder.rightImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        });
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                        holder.rightImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showPictureDialog(
                                                        localPath.get(localPath.size()-1));
                                            }
                                        });

                                        return false;
                                    }
                                }).
                                into(holder.rightImage);
                    }




                }

                else if (msg.getMsgType().equals(BmobIMMessageType.TEXT.getType())) {
                    EmojiUtil.handlerEmojiText(holder.rightMsg, msg.getContent(), mContext);

                    if (msg.getSendStatus() == BmobIMSendStatus.SEND_FAILED.getStatus() || msg.getSendStatus() == BmobIMSendStatus.UPLOAD_FAILED.getStatus()) {
                        holder.sendError.setVisibility(View.VISIBLE);
                    } else if (msg.getSendStatus() == BmobIMSendStatus.SENDING.getStatus()) {
                        holder.rightBackGround.setBackgroundResource(R.drawable.bubble_right_not);

                    } else {
                        holder.rightBackGround.setBackgroundResource(R.drawable.bubble_right);
                    }
                }
                  }
            catch (IOException e){
                e.printStackTrace();
            }

            // holder.rightMsg.setText(msg.getContent());
            holder.leftLayout.setVisibility(View.GONE);
        }
        else {


            if (!msg.getExtra().contains("activityid")){
                holder.cardView.setVisibility(GONE);
            }
            else {
         /*   holder.leftMsg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));*/
                holder.cardView.setVisibility(View.VISIBLE);
                try {
                    JSONObject json = new JSONObject( msg.getExtra());
                    String cover = json.getString("activitycover");
                    String title = json.getString("activitytitle");
                    String host = json.getString("activityhost");
                    String time2 = json.getString("activitytime");
                    final String id = json.getString("activityid");
                    holder.title.setText(title);

                    holder.time2.setText(time2);

                    Glide.with(mContext).load(new mGlideUrl(cover +"!/fp/2000"))
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(holder.cover);

                    holder.host.setText("由"+host+"发起");

                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BmobQuery<MyActivity> query = new BmobQuery<>();
                            query.include("host[avatar|username|Exp]");
                            query.getObject(id, new QueryListener<MyActivity>() {
                                @Override
                                public void done(MyActivity activity, BmobException e) {
                                    if (e==null){

                                        Intent intent = new Intent(mContext, ActivityActivity.class);
                                        intent.putExtra("ACTIVITY",activity);
                                        mContext.startActivity(intent);
                                    }
                                }
                            });




                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }



            }
            try {
                if (msg.getMsgType().equals(BmobIMMessageType.IMAGE.getType()))
                {  final  BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(true, msg);
                    holder.leftMsg.setVisibility(GONE);
                    holder.leftImage.setVisibility(View.VISIBLE);
                    holder.image_progress2.setVisibility(View.VISIBLE);

                    Glide.with(mContext).
                            load(message.getRemoteUrl()/*+"!/scale/80"*/ )
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC).error(R.drawable.image_failed)).
                            listener(new RequestListener<Drawable>() {
                             @Override
                             public boolean onLoadFailed(@Nullable final GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                 holder.image_progress2.setVisibility(View.GONE);
                                 holder.leftImage.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                     e.logRootCauses("失败了");

                                     }
                                 });
                                 return false;
                             }

                             @Override
                             public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                 holder.image_progress2.setVisibility(View.GONE);
                                 holder.leftImage.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         showPictureDialog(
                                                 message.getRemoteUrl());
                                     }
                                 });
                                 return false;
                             }
                         }).into(holder.leftImage);



                }
                EmojiUtil.handlerEmojiText(holder.leftMsg,msg.getContent(),mContext);



            }
            catch (IOException e){
                e.printStackTrace();
            }

            // holder.leftMsg.setText(msg.getContent());
            holder.rightLayout.setVisibility(View.GONE);


        }





    }
    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = mMsgList.get(position);
        if(message.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
        }else if(message.getMsgType().equals(BmobIMMessageType.TEXT.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }else{
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }



    public void showPictureDialog( final String uri) {




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
        pager.setCurrentItem(0);
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

                LinearLayout downloadpic = savePop.findViewById(R.id.download_pic);
                downloadpic.setOnClickListener(new View.OnClickListener() {
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
                LinearLayout savepic = savePop.findViewById(R.id.save_pic);
                savepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadFile(uri+"!/scale/50");
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
        new Thread(service).start();}
}