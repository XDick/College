package com.college.xdick.college.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.college.xdick.college.R;
import com.college.xdick.college.bean.Comment;
import com.college.xdick.college.bean.MyUser;
import com.college.xdick.college.ui.Fragment.StartactivityFragment;

import java.util.Arrays;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/4/12.
 */

public class ActivityActivity extends AppCompatActivity {
   private EditText editComment;
   private ImageView comment,like,sendComment;
   private LinearLayout startEdit;
   private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
   private boolean ifLike ;
   private String activityId;




    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        replaceFragment(new StartactivityFragment());

        editComment = findViewById(R.id.activity_edit_comment_edittext);
        sendComment = findViewById(R.id.activity_send_comment_imageview);
        comment = findViewById(R.id.activity_edit_comment_imageview);
        like = findViewById(R.id.activity_like_imageview);
        startEdit = findViewById(R.id.activity_startedit);
        final Intent intent =getIntent();
        activityId = intent.getStringExtra("ACTIVITY_ID");

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEdit.setVisibility(View.VISIBLE);
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
                Comment comment = new Comment();
                comment.setAvatar(myUser.getAvatar());
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
                        }else{
                            Toast.makeText(ActivityActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

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


}




