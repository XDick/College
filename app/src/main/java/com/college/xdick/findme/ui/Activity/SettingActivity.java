package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/5/17.
 */


public class SettingActivity extends AppCompatActivity {

    private Button exitButton;
    private MyUser user = BmobUser.getCurrentUser(MyUser.class);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

    }


    private void initView()

    {
        exitButton = findViewById(R.id.exit_button_user);
        if (user != null) {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BmobUser.logOut();   //清除缓存用户对象
                    user = BmobUser.getCurrentUser(MyUser.class); // 现在的currentUser是null了
                    Toast.makeText(SettingActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();
                    exitButton.setVisibility(View.INVISIBLE);
                    BmobIM.getInstance().disConnect();
                    startActivity(new Intent(SettingActivity.this,MainActivity.class));
                    finish();


                }
            });


        } else {
            exitButton.setVisibility(View.INVISIBLE);
        }
    }


}
