package com.college.xdick.findme.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/5/21.
 */

public class HostNotifyActivity extends BaseActivity {

    List<MyUser> joinMember = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostnotify);
        initView();
    }

    private void initView() {

        Intent intent = getIntent();
        final MyActivity activity = (MyActivity) intent.getSerializableExtra("ACTIVITY");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.setLimit(500);
        query.addWhereContainedIn("objectId", Arrays.asList(activity.getJoinUser()));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    joinMember = list;
                }
                else {
                    Toast.makeText(HostNotifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        final EditText editMessage = findViewById(R.id.edit_message);
        Button sendButton = findViewById(R.id.send);
        LinearLayout layout = findViewById(R.id.notify_linearlayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMessage.setFocusable(true);
                editMessage.setFocusableInTouchMode(true);
                editMessage.requestFocus();
                editMessage.findFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editMessage, 0);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editMessage.getText().toString().equals("")){
                    return;
                }
                if (BmobIM.getInstance().getCurrentStatus().
                        equals(ConnectionStatus.CONNECTED)) {
                    BmobIMConversation conversationEntrance;
                    BmobIMConversation messageManager;
                    BmobIMTextMessage msg = new BmobIMTextMessage();
                    msg.setContent(editMessage.getText().toString());
                    Map<String, Object> map = new HashMap<>();
                    map.put("activityid",activity.getObjectId());
                    map.put("activitycover",activity.getCover());
                    map.put("activityhost",activity.getHost().getUsername());
                    map.put("activitytime",activity.getTime());
                    map.put("activitytitle",activity.getTitle());
                    msg.setExtraMap(map);
                    finish();
                    Toast.makeText(HostNotifyActivity.this, "正在发送", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < joinMember.size(); i++) {
                        editMessage.setText("");
                        final int k = i;
                        MyUser user = joinMember.get(i);
                        conversationEntrance = BmobIM.getInstance()
                                .startPrivateConversation(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()), true, null);
                        messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
                        messageManager.sendMessage(msg, new MessageSendListener() {
                            @Override
                            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                                if (e == null) {
                                    if (k == joinMember.size() - 1) {
                                        Toast.makeText(HostNotifyActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else {
                                    Toast.makeText(HostNotifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(HostNotifyActivity.this, "正在连接服务器请稍等...", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(HostNotifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }

        });
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
}

