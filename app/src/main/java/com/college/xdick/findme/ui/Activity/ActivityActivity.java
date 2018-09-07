package com.college.xdick.findme.ui.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Fragment.StartactivityFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/4/12.
 */

public class ActivityActivity extends AppCompatActivity {
   private EditText editComment;
   private ImageView comment,like,sendComment;
   private LinearLayout startEdit,statusbar;
   private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
   private boolean ifLike ;
   public boolean ifReply=false;
   private String activityId;
   public StartactivityFragment myfragment;
   private Comment replyComment,fromComment;
   private   InputMethodManager imm ;
    private TextView  commentcount;
    private MyActivity activity;
    public boolean ifJoin=false;




    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_activity);
         imm = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);



        myfragment=new StartactivityFragment();
        replaceFragment(myfragment);
        editComment = findViewById(R.id.activity_edit_comment_edittext);
        sendComment = findViewById(R.id.activity_send_comment_imageview);
        comment = findViewById(R.id.activity_edit_comment_imageview);
        statusbar = findViewById(R.id.activity_statusbar);
        like = findViewById(R.id.activity_like_imageview);
        startEdit = findViewById(R.id.activity_startedit);

        commentcount = findViewById(R.id.activity_comment_count);
        final Intent intent =getIntent();
        activity = (MyActivity)intent.getSerializableExtra("ACTIVITY");
        activityId =activity.getObjectId();
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MyUser.getCurrentUser(MyUser.class)==null){
                    startActivity(new Intent(ActivityActivity.this,LoginActivity.class));
                    finish();
                    Toast.makeText(ActivityActivity.this,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                    return;
                }


                    if (activity.getHostId().equals(myUser.getObjectId()))
                    {
                        startEdit.setVisibility(View.VISIBLE);
                        statusbar.setVisibility(View.GONE);
                        editComment.setFocusable(true);
                        editComment.setFocusableInTouchMode(true);
                        editComment.requestFocus();
                        editComment.findFocus();
                        imm.showSoftInput( editComment, 0);
                    }
                    else {
                        if (activity.getJoinUser() == null) {
                            Toast.makeText(ActivityActivity.this, "加入活动才可以评论", Toast.LENGTH_SHORT).show();
                            return;
                        }

                            if (ifJoin||Arrays.asList(activity.getJoinUser()).contains(myUser.getObjectId())) {
                                startEdit.setVisibility(View.VISIBLE);
                                statusbar.setVisibility(View.GONE);
                                editComment.setFocusable(true);
                                editComment.setFocusableInTouchMode(true);
                                editComment.requestFocus();
                                editComment.findFocus();
                                imm.showSoftInput(editComment, 0);
                            } else {
                                Toast.makeText(ActivityActivity.this, "加入活动才可以评论", Toast.LENGTH_SHORT).show();

                            }




                    }

            }
        });
        try{
         ifLike();

        }catch (Exception e){
            e.printStackTrace();
        }


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MyUser.getCurrentUser(MyUser.class)==null){
                    startActivity(new Intent(ActivityActivity.this,LoginActivity.class));
                   finish();
                    Toast.makeText(ActivityActivity.this,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!ifLike) {
                    myUser.addUnique("like",activityId);
                    myUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                like.setBackground(ActivityActivity.this.getResources().getDrawable(R.drawable.like_t));
                                ifLike = true;

                                Toast.makeText(ActivityActivity.this, "成功收藏", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                else{

                    myUser.removeAll("like", Arrays.asList(activityId));
                    myUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                            if(e==null){
                                like.setBackground(ActivityActivity.this.getResources().getDrawable(R.drawable.like_f));
                                ifLike = false;
                                Toast.makeText(ActivityActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                Toast.makeText(ActivityActivity.this,"失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });


        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
               if(ifReply){
                   replyComment.setContent(editComment.getText().toString());
                   replyComment.setUserName(myUser.getUsername());
                   replyComment.setUserID(myUser.getObjectId());

                   fromComment.addReply();
                   fromComment.update();
                   final String content=editComment.getText().toString();
                   editComment.setText("");






                   replyComment.save(new SaveListener<String>() {
                       @Override
                       public void done(String s, BmobException e) {
                           if(e==null){
                               Toast.makeText(ActivityActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                               myfragment.sendMessage(myUser.getUsername()+"回复了你:"+content
                                       ,new BmobIMUserInfo(replyComment.getReplyuserId(),replyComment.getReplyusername(),null));

                               startEdit.setVisibility(View.GONE);
                               statusbar.setVisibility(View.VISIBLE);
                               myfragment.commentList.clear();
                               myfragment.setSize(0);
                               myfragment.initComment(StartactivityFragment.REPLY);
                               ifReply=false;



                           }else{
                               Toast.makeText(ActivityActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                           }
                       }
                   });


               }
               else {


                final Comment comment = new Comment();

                comment.setUserName(myUser.getUsername());
                comment.setUserID(myUser.getObjectId());
                comment.setContent(editComment.getText().toString());
                comment.setActivityID(activityId);
                   final String content=editComment.getText().toString();
                   editComment.setText("");
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(ActivityActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                            myfragment.sendMessage(myUser.getUsername()+"评论了你:"+content
                                    ,new BmobIMUserInfo(myfragment.activity.getHostId(),
                                            myfragment.activity.getHostName(),null));

                            startEdit.setVisibility(View.GONE);
                            statusbar.setVisibility(View.VISIBLE);
                            myfragment.commentList.clear();
                            myfragment.setSize(0);
                            myfragment.initComment(StartactivityFragment.REPLY);
                        }else{
                            Toast.makeText(ActivityActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });}

            }
        });



    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_startactivity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
            {finish();
                return true;}
            case R.id.menu_delete_activity: {
                if (!myUser.isGod()||activity.getHostId().equals(myUser.getObjectId())){
                    myUser.increment("setAcCount",-1);
                }
                myUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            MyActivity myActivity = new MyActivity();
                            myActivity.setObjectId(activity.getObjectId());
                            myActivity.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e==null){
                                        if (myUser.isGod()){
                                            try {
                                                sendMessage("因你的活动违规已被删除",new BmobIMUserInfo(activity.getHostId(),activity.getHostName(),
                                                        null));
                                            }
                                            catch (Exception e1){
                                                e1.printStackTrace();
                                            }

                                        }
                                        BmobQuery<Comment>query =new BmobQuery<>();
                                        query.addWhereEqualTo("ActivityID",activity.getObjectId());
                                        query.findObjects(new FindListener<Comment>() {
                                            @Override
                                            public void done(List<Comment> list, BmobException e) {
                                                List<BmobObject> commentList= new ArrayList<BmobObject>();
                                                commentList.addAll(list);
                                                new BmobBatch().deleteBatch(commentList).doBatch(new QueryListListener<BatchResult>() {
                                                    @Override
                                                    public void done(List<BatchResult> list, BmobException e) {
                                                        if (e==null){

                                                        }
                                                    }
                                                });
                                            }
                                        });

                                        BmobFile file = new BmobFile();
                                        file.setUrl(activity.getCover());
                                        file.delete(new UpdateListener() {

                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){

                                                }else{

                                                }
                                            }
                                        });

                                        finish();
                                        Toast.makeText(ActivityActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ActivityActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                });
                return true;
            }
            case R.id.menu_upload_pic:{

                if (activity.getHostId().equals(MyUser.getCurrentUser(MyUser.class).getObjectId())){
                    startActivity(new Intent(ActivityActivity.this,SetDynamicsActivity.class));
                    Toast.makeText(this,"插入活动上传照片",Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    if (activity.getJoinUser()==null) {
                        Toast.makeText(this,"加入活动才可以评论",Toast.LENGTH_SHORT).show();
                       return true;
                    }
                    if (myfragment.ifJoin||Arrays.asList(activity.getJoinUser()).contains(MyUser.getCurrentUser(MyUser.class).getObjectId()))
                    {
                        startActivity(new Intent(ActivityActivity.this,SetDynamicsActivity.class));
                        Toast.makeText(this,"插入活动上传照片",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else {
                        Toast.makeText(this,"加入活动才可以评论",Toast.LENGTH_SHORT).show();
                    }


                }


            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(!activity.getHostId().equals(myUser.getObjectId())&&!myUser.isGod())
        {
            menu.findItem(R.id.menu_delete_activity).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_ac, fragment);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        if(startEdit.isShown()){
           startEdit.setVisibility(View.GONE);
           statusbar.setVisibility(View.VISIBLE);
           ifReply=false;
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            editComment.setHint("发表你的评论");
        }

        else {
            finish();
        }

    }

    private void ifLike(){


        if(!Arrays.toString(myUser.getLike()).contains(activityId) ){
            ifLike= false;
        }
        else
        {
            ifLike= true;
            like.setBackground(ActivityActivity.this.getResources().getDrawable(R.drawable.like_t));
        }
    }


    public void reply(Comment fromcomment,Comment tocomment){
        startEdit.setVisibility(View.VISIBLE);
        statusbar.setVisibility(View.GONE);
        editComment.setFocusable(true);
        editComment.setFocusableInTouchMode(true);
        editComment.requestFocus();
        editComment.findFocus();

        imm.showSoftInput( editComment, 0);
        replyComment=tocomment;
        fromComment=fromcomment;
        editComment.setHint("@"+ fromComment.getUserName());

        ifReply=true;
    }

    public  void setText(int count){
        commentcount .setText("评论:"+count);
    }

    public void joinChange(int state){
        //0减少1增加
        if (state==0){
           ifJoin=false;
        }
        else if (state==1){
            ifJoin=true;
        }

    }

    public void sendMessage(String content ,BmobIMUserInfo info) {

        if(!myUser.getObjectId().equals(info.getUserId())){

            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
            BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
            ActivityMessage msg = new ActivityMessage();
            msg.setContent(content);//给对方的一个留言信息
            Map<String, Object> map = new HashMap<>();
            map.put("currentuser", info.getUserId());
            map.put("userid", "4d820b1379");
            map.put("username","泛觅");
            map.put("useravatar", "http://bmob-cdn-18038.b0.upaiyun.com/2018/09/05/077f49d24e434680b55edef4ce015be2.jpg");
            map.put("activityid",activity.getObjectId());
            map.put("activityname",activity.getTitle());
            map.put("type","user");
            msg.setExtraMap(map);
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {//发送成功

                    } else {//发送失败
                        Toast.makeText(ActivityActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}




