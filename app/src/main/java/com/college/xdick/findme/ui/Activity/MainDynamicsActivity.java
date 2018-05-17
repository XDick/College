package com.college.xdick.findme.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.DynamicsCommentAdapter;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.DynamicsComment;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/5/16.
 */

public class MainDynamicsActivity extends AppCompatActivity {

    private TextView title1,content1,time1,user1,commentcount;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DynamicsCommentAdapter adapter;
    private List<DynamicsComment> commentList=new ArrayList<>();
    public boolean ifReply=false;
    private ImageView sendComment;
    private EditText editComment;
    private DynamicsComment replyComment,fromComment;
    private ImageView avatar;
    private    Dynamics dynamics=new Dynamics();
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private int dynamicsReplycount;
    private String dynamicsId;
    private InputMethodManager imm ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dynamics);
        initView();
        initRecycler();
        initDynamics();
        initComment();
    }

    private void initView(){
        imm = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);
        title1 = findViewById(R.id.title_main_dynamics);
        content1 = findViewById(R.id.content_main_dynamics);
        time1 = findViewById(R.id.time_main_dynamics);
        user1 = findViewById(R.id.username_main_dynamics);
        toolbar =findViewById(R.id.toolbar_maindynamics);
        commentcount = findViewById(R.id.dynamics_main_comment_count);
        avatar = findViewById(R.id.dynamics_main_avatar);

        editComment = findViewById(R.id.maindynamics_edit_comment_edittext);
        sendComment = findViewById(R.id.maindynamics_send_comment_imageview);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);




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
                                Toast.makeText(MainDynamicsActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                                editComment.setText("");
                                commentList.clear();
                                initComment();
                                ifReply=false;

                                dynamics.setReplycount(++dynamicsReplycount);
                                dynamics.update(dynamicsId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });



                            }else{
                                Toast.makeText(MainDynamicsActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
                else {


                    DynamicsComment comment = new DynamicsComment ();

                    comment.setUserName(myUser.getUsername());
                    comment.setUserID(myUser.getObjectId());
                    comment.setContent(editComment.getText().toString());
                    comment.setDynamicsID(dynamicsId);
                    comment.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {

                                dynamics.setReplycount(++dynamicsReplycount);
                                dynamics.update(dynamicsId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e!=null){
                                            Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });

                                Toast.makeText(MainDynamicsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                editComment.setText("");
                                commentList.clear();
                                initComment();
                            } else {
                                Toast.makeText(MainDynamicsActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                            }

                        }

                });
                }



    }
        });


    }

    private void initRecycler(){
        recyclerView = findViewById(R.id.recyclerview_main_dynamics);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.recycler_decoration));
        recyclerView.addItemDecoration(decoration);
        adapter = new DynamicsCommentAdapter(commentList);
        recyclerView.setAdapter(adapter);
    }


    private void initDynamics(){
        Intent intent = getIntent();
        String title = intent.getStringExtra("DYNAMICS_TITLE");
        String time = intent.getStringExtra("DYNAMICS_TIME");
        final  String user = intent.getStringExtra("DYNAMICS_USER");
        String content = intent.getStringExtra("DYNAMICS_CONTENT");
        dynamicsId = intent.getStringExtra("DYNAMICS_ID");
        dynamicsReplycount = intent.getIntExtra("DYNAMICS_REPLYCOUNT",0);
        final String userid=  intent.getStringExtra("DYNAMICS_USERID");


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatting(userid,user);
            }
        });




        BmobQuery<MyUser> query = new BmobQuery<>();

        query.getObject(userid, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e==null){
                    Glide.with(MainDynamicsActivity.this).load(myUser.getAvatar()).into(avatar);
                }
            }
        });

        title1.setText(title);
        time1.setText(time);
        content1.setText(content);
        user1.setText(user);

    }

    public void reply(DynamicsComment fromcomment, DynamicsComment  tocomment){

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


    private void initComment(){

        BmobQuery<DynamicsComment> query = new BmobQuery<DynamicsComment>();
        query.addWhereEqualTo("dynamicsID", dynamicsId);
        query.setLimit(50);
        query.findObjects(new FindListener<DynamicsComment>() {
            @Override
            public void done(List<DynamicsComment> list, BmobException e) {
                if (e == null) {

                   commentcount.setText("(" + list.size() + ")");
                    Collections.reverse(list); // 倒序排列
                    for (DynamicsComment comment : list) {
                        commentList.add(comment);
                      initRecycler();
                    }

                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }

            }

        });}


    private void startChatting(String id,String name){
        BmobIM.getInstance().startPrivateConversation( new BmobIMUserInfo(id,name,""), new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if(e==null){
                    //在此跳转到聊天页面
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    Intent intent = new Intent(MainDynamicsActivity.this,
                            ChatActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }else{
                    Toast.makeText(MainDynamicsActivity.this
                            ,e.getMessage()+"("+e.getErrorCode()+")",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
