package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;


import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/4/2.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText accountEdit, passwordEdit;
    private Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

    }


    private void initView() {

        accountEdit = findViewById(R.id.account_login);
        passwordEdit = findViewById(R.id.password_login);
        login = findViewById(R.id.login_button);
        signup = findViewById(R.id.tosignup_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();

               final MyUser bu = new MyUser();
                bu.setUsername(account);
                bu.setPassword(password);
                bu.login(new SaveListener<MyUser>() {

                    @Override
                    public void done(MyUser bmobUser, BmobException e) {
                        if (e == null) {

                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            if (bmobUser.getTag()==null){
                                startActivity(new Intent(LoginActivity.this,InterestActivity.class));
                            }
                            IMconnectBomob();
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);



            }
        });
    }

    /*--------------------------------监听返回键--------------------*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();

    }

    private void IMconnectBomob() {

        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            if (!TextUtils.isEmpty(bmobUser.getObjectId())) {
                BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                          //  Toast.makeText(LoginActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            //连接成功
                        } else {
                            //连接失败
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }


    }
}