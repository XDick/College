package com.college.xdick.findme.ui.Activity;



import  android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.college.xdick.findme.MyClass.KeyboardStatusDetector;
import com.college.xdick.findme.adapter.MsgAdapter;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.sqk.emojirelease.Emoji;
import com.sqk.emojirelease.FaceFragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class ChatActivity extends AppCompatActivity implements MessageListHandler {

    private RecyclerView msgRecyclerView;
    private EditText inputText;
    private Button send,usercenter;
    private MsgAdapter adapter;
    private Toolbar toolbar;
    private BmobIMConversation conversation;
   private LinearLayoutManager layoutManager;
   private TextView title ;
    private ImageView emoji;
    private List<BmobIMMessage> msgList = new ArrayList<>();
    private FaceFragment faceFragment;
    private boolean ifEmojiShown=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);
        BaseIMoperation();
        initView();
        initAllChattingRecord();





    }

    private void initView(){


        toolbar = findViewById(R.id.toolbar_chat);
         toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);

        title = findViewById(R.id.title_chat);
        title.setText(conversation.getConversationTitle());
        inputText = findViewById(R.id.input_text);
        inputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgRecyclerView.scrollToPosition(msgList.size()-1);
            }
        });

        send = findViewById(R.id.send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);

        msgRecyclerView.setAdapter(adapter);
       faceFragment = FaceFragment.Instance();
        faceFragment.setListener(new FaceFragment.OnEmojiClickListener() {
            @Override
            public void onEmojiDelete() {
                int index =  inputText.getSelectionStart();
                Editable editable = inputText.getText();

                    if (index-1>=0){
                        if (editable.toString().length()-1==editable.toString().lastIndexOf("]")&&editable.toString().contains("[")){
                        editable.delete(editable.toString().lastIndexOf("["), editable.toString().lastIndexOf("]")+1);
                    }
                    else {
                            editable.delete(index-1, index);
                        }
                }


            }

            @Override
            public void onEmojiClick(Emoji emoji) {
                int index =  inputText.getSelectionStart();
                Editable editable = inputText.getText();
                editable.insert(index, emoji.getContent());
            }
        });



        new KeyboardStatusDetector()
                .setmVisibilityListener(new KeyboardStatusDetector.KeyboardVisibilityListener() {
                    @Override
                    public void onVisibilityChanged(boolean keyboardVisible) {
                        if(keyboardVisible) {
                            getSupportFragmentManager().popBackStack();
                            msgRecyclerView.scrollToPosition(msgList.size()-1);
                            ifEmojiShown=false;
                        }else {


                        }


                    }
                }).registerActivity(this);

         emoji=findViewById(R.id.emoji);
         emoji.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                  final FragmentManager manager = getSupportFragmentManager();
                  FragmentTransaction transaction=manager.beginTransaction();
                     if (!ifEmojiShown){
                         ifEmojiShown=true;
                         if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                             transaction.replace(R.id.chat_layout,null).addToBackStack(null).commit();
                             msgRecyclerView.scrollToPosition(msgList.size()-1);
                         } else {
                             InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                             if (imm != null) {
                                 imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                 transaction.replace(R.id.chat_layout, faceFragment).addToBackStack(null).commit();
                                 msgRecyclerView.scrollToPosition(msgList.size()-1);
                             }
                         }
                         Log.d("","碎片的格数"+getFragmentManager().getBackStackEntryCount());
                 }
                 else {     ifEmojiShown=false;
                         getSupportFragmentManager().popBackStack();

                     }


                 }


         });




        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendIMMsg();
                }
            }

        );

        usercenter= findViewById(R.id.usercenter_chat);
        usercenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<MyUser> query = new BmobQuery<>();
                query.getObject(conversation.getConversationId(), new QueryListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        Intent intent = new Intent(ChatActivity.this,UserCenterActivity.class);
                        intent.putExtra("USER",myUser);
                        startActivity(intent);
                    }
                });


            }
        });
    }

    private void BaseIMoperation(){


//在聊天页面的onCreate方法中，通过如下方法创建新的会话实例,这个obtain方法才是真正创建一个管理消息发送的会话
        conversation=BmobIMConversation.obtain(BmobIMClient.getInstance(),
                (BmobIMConversation) getIntent().getSerializableExtra("c"));

    }


    private void sendIMMsg(){

        if (BmobIM.getInstance().getCurrentStatus()!= ConnectionStatus.CONNECTED){
            Toast.makeText(this,"正在连接服务器请稍等...",Toast.LENGTH_SHORT).show();
            IMconnectBomob();
            return;
        }

        final  String content = inputText.getText().toString();


            BmobIMTextMessage msg = new BmobIMTextMessage();
            msg.setContent(content);

            conversation.sendMessage(msg, new MessageSendListener() {
                @Override
                public void onStart(BmobIMMessage msg) {
                    super.onStart(msg);
                    inputText.setText("");
                    if (!"".equals(content)) {
                        msg.setContent(content);
                        msgList.add(msg);
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        adapter.setSendStatus(0);
                        adapter.notifyDataSetChanged();
                        //adapter.notifyItemInserted(msgList.size() - 1);
                        Log.d("a", "执行3");
                    }
                    //当有新消息 刷新ListVIEW中的显示
                }

                @Override
                public void done(BmobIMMessage msg, BmobException e) {


                    adapter.setSendStatus(1);
                    adapter.notifyDataSetChanged();
                    //讲ListView定位到最后一行

                    if (e != null) {
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    @Override
    public void onMessageReceive(List<MessageEvent> list) {

        //BmobNotificationManager.getInstance(this).showNotification(null,conversation.getConversationTitle() , null, null,new Intent(ChatActivity.this,ChatActivity.class));

        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i = 0; i < list.size(); i++) {
            addMessage2Chat(list.get(i));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);

        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();

        super.onResume();
    }

    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (conversation != null && event != null && conversation.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {//并且不为暂态消息
            if (adapter.findPosition(msg) < 0) {//如果未添加到界面中
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size()-1);
                //更新该会话下面的已读状态
                conversation.updateReceiveStatus(msg);
            }
            msgRecyclerView.scrollToPosition(msgList.size()-1);
        } else {
           Log.d("a","不是与当前聊天对象的消息");
        }
    }


    private void addUnReadMessage() {
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if (cache.size() > 0) {
            int size = cache.size();
            for (int i = 0; i < size; i++) {
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
            msgRecyclerView.scrollToPosition(msgList.size()-1);}
        }

    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }


    private void initAllChattingRecord(){
        //首次加载，可设置msg为null，下拉刷新的时候，可用消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
//TODO 消息：5.2、查询指定会话的消息记录
        conversation.queryMessages(null,999,new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {

                if (e == null) {
                    if (null != list && list.size() > 0) {

                                adapter.addMessages(list);
                                adapter.notifyDataSetChanged();
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                    }
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                } else {
                   Toast.makeText(ChatActivity.this,e.getMessage() + "(" + e.getErrorCode() + ")",Toast.LENGTH_SHORT).show();
                }
            }
        });
        msgRecyclerView.scrollToPosition(msgList.size()-1);
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case  android.R.id.home:
            {
               finish();
               break;
            }


            default:
                break;
        }

        return true;
    }

    private void IMconnectBomob() {

        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        final MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            if (!TextUtils.isEmpty(bmobUser.getObjectId())) {
                BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            try {
                                BmobIM.getInstance().
                                        updateUserInfo(new BmobIMUserInfo( bmobUser.getObjectId(),
                                                bmobUser.getUsername(),  bmobUser.getAvatar()));
                            }
                            catch (Exception e2){
                                e2.printStackTrace();
                            }


                        } else {
                            //连接失败
                            Toast.makeText(ChatActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onBackPressed() {
        ifEmojiShown = false;
        super.onBackPressed();
    }
}

