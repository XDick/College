package com.college.xdick.findme.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.sqk.emojirelease.EmojiUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.view.View.GONE;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

public class TextViewHolder extends BaseViewHolder{


    private LinearLayout leftLayout,rightLayout,rightBackGround;

    private TextView leftMsg,rightMsg,time,title,time2,host;

    private ImageView avatar_me,avatar_you,cover;

    private CardView cardView;

    private Context mContext=getContext();

    public TextViewHolder(Context context,ViewGroup root){
        super(context, root, R.layout.item_msg);

        leftLayout = itemView.findViewById(R.id.left_layout);
        rightLayout = itemView.findViewById(R.id.right_layout);
        leftMsg = itemView. findViewById(R.id.left_msg);
        rightMsg = itemView.findViewById(R.id.right_msg);
        time=itemView.findViewById(R.id.msg_time);
        avatar_me = itemView.findViewById(R.id.msg_avatar_me);
        avatar_you = itemView.findViewById(R.id.msg_avatar_you);
        rightBackGround= itemView.findViewById(R.id.right_msg_layout);


        title= itemView.findViewById(R.id.title_ac);
        cardView =itemView.findViewById(R.id.cardview_ac);
        cover = itemView.findViewById(R.id.cover_ac);
        time2= itemView.findViewById(R.id.time_ac);

        host = itemView.findViewById(R.id.host_ac);

    }

    @Override
    public void bindData(List<BmobIMMessage> msgList,int status,
                         int position,int count) {
        leftLayout.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
        String currentUid="";
        currentUid = BmobUser.getCurrentUser().getObjectId();
        BmobIMMessage msg = msgList.get(position);

        long t=Long.valueOf(msg.getCreateTime());
        SimpleDateFormat sdf1= new SimpleDateFormat("yyyy/MM/dd HH:mm");

        String time1 = sdf1.format(new Date(t));//long转化成Date

        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String  nowtime = sdf.format(new Date());

        String  rawdate =sdf.format(new Date(t));

        String time3 = time1.substring(time1.indexOf(" ")+1);

        if (nowtime.equals(rawdate)){
           time.setText(time3);
            if (position-1>=0&&msg.getCreateTime()-msgList.get(position-1).getCreateTime()<60*1000){
                time.setText("");
            }else {
                time.setText(time3);
            }
        }
        else{

            time.setText(time1);
        }


        Glide.with(mContext).load(msg.getBmobIMUserInfo().getAvatar())
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar_you);

        Glide.with(mContext).load(BmobUser.getCurrentUser(MyUser.class)
                .getAvatar())
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar_me);



        if (!msg.getExtra().contains("activityid")){
            cardView.setVisibility(GONE);
        }
        else {
            leftMsg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));
            cardView.setVisibility(View.VISIBLE);
            try {
                JSONObject json = new JSONObject( msg.getExtra());
                String cover2 = json.getString("activitycover");
                String title2 = json.getString("activitytitle");
                String host2 = json.getString("activityhost");
                String time4= json.getString("activitytime");
                final String id = json.getString("activityid");
               title.setText(title2);

                time2.setText(time4);

                Glide.with(mContext).load(cover2)    .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(cover);

                host.setText("由"+host2+"发起");

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BmobQuery<MyActivity> query = new BmobQuery<>();
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


        if(msg.getFromId().equals(currentUid)) {
            try {

                EmojiUtil.handlerEmojiText( rightMsg,msg.getContent(),mContext);

            }
            catch (IOException e){
                e.printStackTrace();
            }

            // holder.rightMsg.setText(msg.getContent());
           leftLayout.setVisibility(View.GONE);




        }
        else {
            try {

                EmojiUtil.handlerEmojiText(leftMsg,msg.getContent(),mContext);
            }
            catch (IOException e){
                e.printStackTrace();
            }

            // holder.leftMsg.setText(msg.getContent());
            rightLayout.setVisibility(View.GONE);


        }



        switch (status){
            case 0:
                if (position==count-1){
                    rightBackGround.setBackgroundResource(R.drawable.bubble_right_not);
                    break;}
            case 1:
                rightBackGround.setBackgroundResource(R.drawable.bubble_right);
                break;
            default:
                break;
        }
    }
}