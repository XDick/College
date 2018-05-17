package com.college.xdick.findme.ui.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Fragment.StartactivityFragment;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
   private StartactivityFragment myfragment;
   private Comment replyComment,fromComment;
   private   InputMethodManager imm ;
    private TextView likecount, commentcount;




    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_activity);
         imm = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);

        if(myUser==null){
          //  startActivity(new Intent(ActivityActivity.this,LoginActivity.class));
            Toast.makeText(ActivityActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
        myfragment=new StartactivityFragment();
        replaceFragment(myfragment);
        editComment = findViewById(R.id.activity_edit_comment_edittext);
        sendComment = findViewById(R.id.activity_send_comment_imageview);
        comment = findViewById(R.id.activity_edit_comment_imageview);
        statusbar = findViewById(R.id.activity_statusbar);
        like = findViewById(R.id.activity_like_imageview);
        startEdit = findViewById(R.id.activity_startedit);
        likecount = findViewById(R.id.activity_like_count);
        commentcount = findViewById(R.id.activity_comment_count);
        final Intent intent =getIntent();
        activityId = intent.getStringExtra("ACTIVITY_ID");
        initLike();
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEdit.setVisibility(View.VISIBLE);
                statusbar.setVisibility(View.GONE);

                editComment.setFocusable(true);
                editComment.setFocusableInTouchMode(true);
                editComment.requestFocus();
                editComment.findFocus();

                imm.showSoftInput( editComment, 0);


            }
        });

        ifLike();
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ifLike) {
                    myUser.add("like", activityId);
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



                   replyComment.save(new SaveListener<String>() {
                       @Override
                       public void done(String s, BmobException e) {
                           if(e==null){
                               Toast.makeText(ActivityActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                               editComment.setText("");
                               startEdit.setVisibility(View.GONE);
                               statusbar.setVisibility(View.VISIBLE);
                               myfragment.commentList.clear();
                               myfragment.initData();
                               ifReply=false;



                           }else{
                               Toast.makeText(ActivityActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                           }
                       }
                   });


               }
               else {


                Comment comment = new Comment();

                comment.setUserName(myUser.getUsername());
                comment.setUserID(myUser.getObjectId());
                comment.setContent(editComment.getText().toString());
                comment.setActivityID(activityId);
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(ActivityActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                            editComment.setText("");
                            startEdit.setVisibility(View.GONE);
                            statusbar.setVisibility(View.VISIBLE);
                            myfragment.commentList.clear();
                            myfragment.initData();
                        }else{
                            Toast.makeText(ActivityActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });}

            }
        });}



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if(Arrays.toString(myUser.getLike()).indexOf(activityId) < 0){
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

    private void initLike(){
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();

        query.addWhereContainsAll("like", Arrays.asList(activityId));
        query.findObjects(new FindListener<MyUser>() {

            @Override
            public void done(List<MyUser> object, BmobException e) {
                if(e==null){
                   likecount.setText("喜欢:"+object.size());
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });

    }


}



