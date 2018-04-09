package com.college.xdick.college.ui.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.college.xdick.college.R;
import com.college.xdick.college.bean.User;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

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
              startChatting();
                finish();
            }
        });
    }


    private void startChatting() {

        Intent intent = getIntent();
         String id = intent . getStringExtra("FRIEND_ID");
        String name = intent . getStringExtra("FRIEND_NAME");

        BmobIM.getInstance().startPrivateConversation( new BmobIMUserInfo(id,name,""), new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if(e==null){
                    //在此跳转到聊天页面
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
           Intent intent = new Intent(AskChatActivity.this,
                   ChatActivity.class);
                 intent.putExtras(bundle);
                    startActivity(intent);
                    Toast.makeText(AskChatActivity.this
                            ,"successful",Toast.LENGTH_SHORT).show();
                }else{
                   Toast.makeText(AskChatActivity.this
                           ,e.getMessage()+"("+e.getErrorCode()+")",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
