package com.college.xdick.findme.ui.Activity;



import android.app.Activity;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import  android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.college.xdick.findme.BmobIM.BmobIMApplication;
import com.college.xdick.findme.Listener.SoftKeyBoardListener;
import com.college.xdick.findme.MyClass.KeyboardStatusDetector;
import com.college.xdick.findme.MyClass.MyGlideEngine;
import com.college.xdick.findme.adapter.MsgAdapter;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.SelectActivityAdapter;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.util.ClassFileHelper;
import com.college.xdick.findme.util.FileUtil;
import com.sqk.emojirelease.Emoji;
import com.sqk.emojirelease.FaceFragment;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
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
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class ChatActivity extends BaseActivity implements MessageListHandler {

    private RecyclerView msgRecyclerView;
    private EditText inputText;
    private Button send, usercenter, add;
    private MsgAdapter adapter;
    private Toolbar toolbar;
    private BmobIMConversation conversation;
    private LinearLayoutManager layoutManager;
    private TextView title;
    private ImageView emoji, event;
    private List<BmobIMMessage> msgList = new ArrayList<>();
    private FaceFragment faceFragment;
    private boolean ifEmojiShown = false;
    private int REQUEST_CODE_CHOOSE = 1;
    private BmobIMMessage firstMessage = null;
    private boolean ifShown = true;
    private ListView mActivityListView;
    private PopupWindow mPopWindow;
    private SelectActivityAdapter mActivityAdapter;
    private View parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);
        BaseIMoperation();
        initView();
        initChattingRecord();

        msgRecyclerView.scrollToPosition(adapter.getCount() - 1);

    }

    private void initView() {

        initPopView(ChatActivity.this);
        toolbar = findViewById(R.id.toolbar_chat);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);

        title = findViewById(R.id.title_chat);
        title.setText(conversation.getConversationTitle());
        inputText = findViewById(R.id.input_text);
        inputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
            }
        });

        send = findViewById(R.id.send);
        add = findViewById(R.id.add);
        event = findViewById(R.id.event);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initActivity();
                showPopWindow();

            }
        });
        final float scale = getResources().getDisplayMetrics().density;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Matisse.from(ChatActivity.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(9)
                        .gridExpectedSize((int) (120 * scale + 0.5f))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.6f)
                        .theme(R.style.Matisse_FindMe)
                        .imageEngine(new MyGlideEngine())
                        .showSingleMediaType(true)
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        adapter.setConversation(conversation);
        msgRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstCompletelyVisibleItemPosition == 0) {
                    if (!msgList.isEmpty()) {
                        initChattingRecord();
                    }
                }

            }
        });

        msgRecyclerView.setAdapter(adapter);
        faceFragment = FaceFragment.Instance();
        faceFragment.setListener(new FaceFragment.OnEmojiClickListener() {
            @Override
            public void onEmojiDelete() {
                int index = inputText.getSelectionStart();
                Editable editable = inputText.getText();

                if (index - 1 >= 0) {
                    if (editable.toString().length() - 1 == editable.toString().lastIndexOf("]") && editable.toString().contains("[")) {
                        editable.delete(editable.toString().lastIndexOf("["), editable.toString().lastIndexOf("]") + 1);
                    } else {
                        editable.delete(index - 1, index);
                    }
                }


            }

            @Override
            public void onEmojiClick(Emoji emoji) {
                int index = inputText.getSelectionStart();
                Editable editable = inputText.getText();
                editable.insert(index, emoji.getContent());
            }
        });


        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (ifEmojiShown) {
                    getSupportFragmentManager().popBackStack();
                    ifEmojiShown = false;
                }
                msgRecyclerView.scrollToPosition(adapter.getCount() - 1);
            }

            @Override
            public void keyBoardHide(int height) {
                inputText.clearFocus();
            }
        });


        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    add.setVisibility(View.VISIBLE);
                    send.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.VISIBLE);
                    add.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        emoji = findViewById(R.id.emoji);
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                if (!ifEmojiShown) {
                    inputText.clearFocus();
                    ifEmojiShown = true;
                    if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                        transaction.replace(R.id.chat_layout, null).addToBackStack(null).commit();

                    } else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                            transaction.replace(R.id.chat_layout, faceFragment).addToBackStack(null).commit();
                        }
                    }
                    msgRecyclerView.scrollToPosition(adapter.getCount() - 1);
                    //Log.d("","碎片的格数"+getFragmentManager().getBackStackEntryCount());
                } else {

                    ifEmojiShown = false;

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

        usercenter = findViewById(R.id.usercenter_chat);
        usercenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<MyUser> query = new BmobQuery<>();
                query.getObject(conversation.getConversationId(), new QueryListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        Intent intent = new Intent(ChatActivity.this, UserCenterActivity.class);
                        intent.putExtra("USER", myUser);
                        startActivity(intent);
                    }
                });


            }
        });
    }

    private void BaseIMoperation() {


//在聊天页面的onCreate方法中，通过如下方法创建新的会话实例,这个obtain方法才是真正创建一个管理消息发送的会话
        conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(),
                (BmobIMConversation) getIntent().getSerializableExtra("c"));

    }


    private void sendIMMsg() {

        if (BmobIM.getInstance().getCurrentStatus() != ConnectionStatus.CONNECTED) {
            // Toast.makeText(this,"正在连接服务器请稍等...",Toast.LENGTH_SHORT).show();
            IMconnectBomob();
            return;
        }

        final String content = inputText.getText().toString();


        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(content);

        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void onStart(BmobIMMessage msg) {
                super.onStart(msg);
                inputText.setText("");
                if (!"".equals(content)) {
                    msg.setContent(content);
                    if (firstMessage == null) {
                        firstMessage = msg;
                    }

                    msgList.add(msg);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    adapter.notifyDataSetChanged();
                    //adapter.notifyItemInserted(msgList.size() - 1);
                    //Log.d("a", "执行3");
                }
                //当有新消息 刷新ListVIEW中的显示
            }

            @Override
            public void done(BmobIMMessage msg, BmobException e) {


                adapter.notifyDataSetChanged();


                if (e != null) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onMessageReceive(List<MessageEvent> list) {


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
        ifShown = true;
        int PUSH_NOTIFICATION_ID_CHAT = (0x002);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ((BmobIMApplication) getApplicationContext()).clearPushNum();
        manager.cancel(PUSH_NOTIFICATION_ID_CHAT);

        super.onResume();
    }

    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (conversation != null && event != null && conversation.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {//并且不为暂态消息
            if (adapter.findPosition(msg) < 0) {//如果未添加到界面中
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1);
                //更新该会话下面的已读状态
                conversation.updateReceiveStatus(msg);
            }
            msgRecyclerView.scrollToPosition(msgList.size() - 1);
        } else {
            Log.d("a", "不是与当前聊天对象的消息");
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
            msgRecyclerView.scrollToPosition(msgList.size() - 1);
        }
    }

    @Override
    protected void onPause() {
        //移除页面消息监听器
        //BmobIM.getInstance().removeMessageListHandler(this);
        ifShown = false;
        super.onPause();
    }


    private void initChattingRecord() {
        //首次加载，可设置msg为null，下拉刷新的时候，可用消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
//TODO 消息：5.2、查询指定会话的消息记录
        conversation.queryMessages(firstMessage, 1000, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {

                if (e == null) {
                    if (null != list && list.size() > 0) {

                        adapter.addMessages(list);
                        firstMessage = list.get(0);
                        /*LinearLayoutManager mLayoutManager =
                                (LinearLayoutManager) msgRecyclerView.getLayoutManager();

                        mLayoutManager.scrollToPositionWithOffset(list.size(), 50);*/
                        msgRecyclerView.scrollToPosition(adapter.findPosition(firstMessage));
                    }

                } else {
                    Toast.makeText(ChatActivity.this, e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                finish();
                break;
            }


            default:
                break;
        }

        return true;
    }

    /**
     * 发送本地图片文件
     */
    public void sendLocalImageMessage(String path) {


        File file = new File(path);
        final String fileType = path
                .substring(path.lastIndexOf("."));

        final String renamePath = "data/user/0/com.college.xdick.findme/cache/"
                + "chat_image_"
                + BmobUser.getCurrentUser().getObjectId()
                + fileType;
        final File newFile = new File(renamePath);
        try {
            ClassFileHelper.copyFileTo(file, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO 发送消息：6.2、发送本地图片消息
        //正常情况下，需要调用系统的图库或拍照功能获取到图片的本地地址，开发者只需要将本地的文件地址传过去就可以发送文件类型的消息

        BmobIMImageMessage image = new BmobIMImageMessage(renamePath);
        Map<String, Object> map = new HashMap<>();
        map.put("localPath", path);
        image.setExtraMap(map);
        conversation.sendMessage(image, new MessageSendListener() {
            @Override
            public void onStart(BmobIMMessage msg) {
                super.onStart(msg);
                if (firstMessage == null) {
                    firstMessage = msg;

                }

                msgList.add(msg);
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                adapter.notifyDataSetChanged();
                //当有新消息 刷新ListVIEW中的显示
            }

            @Override
            public void done(BmobIMMessage msg, BmobException e) {


                if (e == null) {
                    adapter.notifyDataSetChanged();
                    ClassFileHelper.deleteFile(newFile);
                } else {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

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
                                        updateUserInfo(new BmobIMUserInfo(bmobUser.getObjectId(),
                                                bmobUser.getUsername(), bmobUser.getAvatar()));
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }


                        } else {
                            //连接失败
                            // Toast.makeText(ChatActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            for (Uri u : Matisse.obtainResult(data)) {
                sendLocalImageMessage(FileUtil.uriToFile(u, this));
            }


        }

    }

    @Override
    public void onBackPressed() {
        ifEmojiShown = false;
        add.setVisibility(View.VISIBLE);
        send.setVisibility(View.GONE);
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onDestroy();
    }

    public boolean isShown() {
        return ifShown;
    }

    private void initActivity() {
        BmobQuery<MyActivity> query = new BmobQuery<>();
        BmobQuery<MyActivity> q1 = new BmobQuery<>();
        BmobQuery<MyActivity> q2 = new BmobQuery<>();

        List<BmobQuery<MyActivity>> queries = new ArrayList<>();
        q1.addWhereEqualTo("host", BmobUser.getCurrentUser());
        q2.addWhereContainsAll("joinUser", Arrays.asList(BmobUser.getCurrentUser().getObjectId()));

        queries.add(q1);
        queries.add(q2);
        query.order("-createdAt");
        query.or(queries);
        query.include("host[username|avatar]");
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                if (e == null) {

                    mActivityAdapter.setList(list);
                    mActivityAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    public void initPopView(Activity activity) {
        parent = activity.getWindow().getDecorView();
        View popView = View.inflate(activity, R.layout.item_popup_select_activity, null);


        mActivityListView = popView.findViewById(R.id.activity_list);
        mActivityListView.setOnItemClickListener(itemListener);

        mActivityAdapter = new SelectActivityAdapter(activity);
        mActivityListView.setAdapter(mActivityAdapter);


        int width = activity.getResources().getDisplayMetrics().widthPixels * 3 / 4;
        int height = activity.getResources().getDisplayMetrics().heightPixels * 3 / 5;
        mPopWindow = new PopupWindow(popView, width, height);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mPopWindow.setBackgroundDrawable(dw);
        mPopWindow.setFocusable(true);
        mPopWindow.setTouchable(true);
        mPopWindow.setOutsideTouchable(true);//允许在外侧点击取消
    }

    AdapterView.OnItemClickListener itemListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    MyActivity activity = (MyActivity) mActivityListView.getItemAtPosition(position);
                    BmobIMTextMessage msg = new BmobIMTextMessage();
                    msg.setContent(activity.getTitle());
                    Map<String, Object> map = new HashMap<>();
                    map.put("activityid", activity.getObjectId());
                    map.put("activitycover", activity.getCover());
                    map.put("activityhost", activity.getHost().getUsername());
                    map.put("activitytime", activity.getTime());
                    map.put("activitytitle", activity.getTitle());
                    msg.setExtraMap(map);
                    conversation.sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void onStart(BmobIMMessage msg) {
                            super.onStart(msg);

                                mPopWindow.dismiss();
                                msgList.add(msg);
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                                adapter.notifyDataSetChanged();
                                //adapter.notifyItemInserted(msgList.size() - 1);
                                //Log.d("a", "执行3");

                            //当有新消息 刷新ListVIEW中的显示
                        }

                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {


                            adapter.notifyDataSetChanged();


                            if (e != null) {
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            };

    public void showPopWindow() {
        mPopWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

}
