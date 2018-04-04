package com.college.xdick.college.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.college.xdick.college.R;
import com.college.xdick.college.util.User;

import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class AskChatActivity extends Activity {
    private User bmobUser = BmobUser.getCurrentUser(User.class);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.activitiy_askchat);
        Button accetp =findViewById(R.id.accept_addfriend);
        Button cancel = findViewById(R.id.cancel_addfriend);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        accetp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AskChatActivity.this,
                        ChatActivity.class));
                finish();
            }
        });
    }

    /**
     * 发送添加好友的请求
     */
    //TODO 好友管理：9.7、发送添加好友请求
    private void sendAddFriendMessage() {
        User currentUser = BmobUser.getCurrentUser(User.class);
        BmobIMUserInfo info = new BmobIMUserInfo();
        info.setUserId(currentUser.getObjectId().toString());
        info.setName(currentUser.getUsername());
    }
}
