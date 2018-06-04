package com.college.xdick.findme.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/5/21.
 */

public class HostNotifyActivity extends AppCompatActivity {

    List<MyUser> joinMember = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostnotify);
        initView();
    }

    private void initView(){

        Intent intent = getIntent();
       final MyActivity activity = (MyActivity) intent.getSerializableExtra("ACTIVITY");

        BmobQuery<MyUser> query =new BmobQuery<>();
        query.addWhereContainsAll("join", Arrays.asList(activity.getObjectId()));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                       if (e==null){
                           joinMember =list;
                       }
            }
        });



        final EditText editMessage = findViewById(R.id.edit_message);
        Button sendButton = findViewById(R.id.send);
        LinearLayout layout = findViewById(R.id.notify_linearlayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMessage .setFocusable(true);
                editMessage .setFocusableInTouchMode(true);
                editMessage .requestFocus();
                editMessage .findFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editMessage , 0);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobIMConversation conversationEntrance;
                BmobIMConversation messageManager;
                BmobIMTextMessage msg = new BmobIMTextMessage();
                msg.setContent(editMessage.getText().toString()+'\n'+'\n'+"【来自活动#"+activity.getTitle()+"#】");
                 finish();
               for ( int i=0;i<joinMember.size();i++){
                   editMessage.setText("");
                   final int k =i;
                MyUser user = joinMember.get(i);
                   conversationEntrance = BmobIM.getInstance()
                           .startPrivateConversation(new BmobIMUserInfo(user.getObjectId(),user.getUsername(),user.getAvatar()), true, null);
                   messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
                   messageManager.sendMessage(msg, new MessageSendListener() {
                       @Override
                       public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                           if (e==null){
                                if (k==joinMember.size()-1) {
                                    Toast.makeText(HostNotifyActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                }   }
                       }
                   });
               }







            }
        });

    }
}
