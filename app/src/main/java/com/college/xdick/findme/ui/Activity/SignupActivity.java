package com.college.xdick.findme.ui.Activity;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;

import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.util.SelectSchoolUtil;



import cn.bmob.newim.BmobIM;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/4/2.
 */

public class SignupActivity  extends BaseActivity {
   private EditText accountEdit,passwordEdit,phoneEdit,identityEdit;
   private Button signup,selectSchoolButton,sendIdentifyButton,confirmIdentityButton;
   private boolean ifConfirm=false;
   private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();




    }


    private void initView(){


        accountEdit = findViewById(R.id.account_signup);
        passwordEdit =findViewById(R.id.password_signup);
        phoneEdit = findViewById(R.id.phone_signup);
        identityEdit = findViewById(R.id.identify_signup);
        signup = findViewById(R.id.signup_button);
        selectSchoolButton= findViewById(R.id.select_school_button);
        sendIdentifyButton = findViewById(R.id.sendidentity_button);
        confirmIdentityButton =findViewById(R.id.identify_confirm_button);
        TextInputLayout  mTextInputLayoutPhone =  findViewById(R.id.textInputLayout_phone_signup);
        //设置可以计数
        mTextInputLayoutPhone.setCounterEnabled(true);
        //计数的最大值
        mTextInputLayoutPhone.setCounterMaxLength(11);


        SelectSchoolUtil.initPopView(this,selectSchoolButton,null,null);

        selectSchoolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SelectSchoolUtil.showPopWindow();
            }
        });



      sendIdentifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.requestSMSCode(phoneEdit.getText().toString(), "验证",new QueryListener<Integer>() {

                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        if(ex==null){//验证码发送成功
                           number = phoneEdit.getText().toString();
                           ifConfirm=false;
                           Toast.makeText(SignupActivity.this,"验证码已发送，请在十分钟内完成操作",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignupActivity.this,ex.getMessage()+"",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        confirmIdentityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.verifySmsCode(phoneEdit.getText().toString(),identityEdit.getText().toString() , new UpdateListener() {

                    @Override
                    public void done(BmobException ex) {
                        if(ex==null){//短信验证码已验证成功
                            ifConfirm=true;
                            Toast.makeText(SignupActivity.this,"验证码正确",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignupActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String phone = phoneEdit.getText().toString();


                final MyUser bu = new MyUser();
                bu.setUsername(account);
                bu.setPassword(password);
                bu.setMobilePhoneNumber(phone);
                bu.setSchool(SelectSchoolUtil.getSchool());
                bu.setMobilePhoneNumberVerified(true);
                bu.setEmailVerified(false);
                bu.setGod(false);
                bu.setBanned(false);
                bu.setBannedReason("");
                bu.setAvatar("http://bmob-cdn-18038.b0.upaiyun.com/2018/05/18/425ce45f40a6b2208074aa1dbce9f76c.png");
                bu.setSchool("北京大学");
                bu.setSetAcCount(0);
                bu.setDynamicsCount(0);
                if (password.length()<8){
                    Toast.makeText(SignupActivity.this,"密码至少8位",Toast.LENGTH_SHORT).show();
                }
                else {

                   if (ifConfirm && phoneEdit.getText().toString().equals(number)) {
                        bu.setMobilePhoneNumberVerified(true);
                        bu.setEmailVerified(false);
                        bu.setFollowChartId("");
                        bu.signUp(new SaveListener<MyUser>() {
                            @Override
                            public void done(final MyUser s, BmobException e) {
                                if (e == null) {
/*
                                    FollowChart followChart= new FollowChart();
                                    followChart.setFansCount(0);
                                    followChart.setHostId(s.getObjectId());
                                    followChart.setUserName(s.getUsername());
                                    followChart.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s2, BmobException e) {
                                            if (e==null){
                                                s.setFollowChartId(s2);
                                                s.update();
                                            }
                                        }
                                    });*/


                                    Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    BmobUser.logOut();   //清除缓存用户对象
                                    BmobIM.getInstance().disConnect();
                                    finish();
                                } else {
                                    Toast.makeText(SignupActivity.this, "注册失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                  } else {

                        Toast.makeText(SignupActivity.this, "请确保手机信息正确", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }


}



