package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
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

import org.json.JSONObject;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyNameActivity extends BaseActivity {
    private EditText accountEdit,phoneEdit,identityEdit;
    private Button signup,sendIdentifyButton,confirmIdentityButton;
    private boolean ifConfirm=false;
    private String number;
    private String nickName=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyname);
       // Toast.makeText(ModifyNameActivity.this,"取个好听的名字(｡･ω･｡)",Toast.LENGTH_SHORT).show();
        initView();









    }
    private void initView(){

        nickName= getIntent().getStringExtra("NICKNAME");
        final String accessToken = getIntent().getStringExtra("ACCESSTOKEN");
        final String expires = getIntent().getStringExtra("EXPIRES");
        final String openID = getIntent().getStringExtra("OPENID");
        accountEdit = findViewById(R.id.account_signup);
        phoneEdit = findViewById(R.id.phone_signup);
        identityEdit = findViewById(R.id.identify_signup);
        signup = findViewById(R.id.signup_button);
        sendIdentifyButton = findViewById(R.id.sendidentity_button);
        confirmIdentityButton =findViewById(R.id.identify_confirm_button);
        TextInputLayout mTextInputLayoutPhone =  findViewById(R.id.textInputLayout_phone_signup);
        //设置可以计数
        mTextInputLayoutPhone.setCounterEnabled(true);
        //计数的最大值
        mTextInputLayoutPhone.setCounterMaxLength(11);
        accountEdit.setText(nickName);




        sendIdentifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.requestSMSCode(phoneEdit.getText().toString(), "验证",new QueryListener<Integer>() {

                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        if(ex==null){//验证码发送成功
                            number = phoneEdit.getText().toString();
                            ifConfirm=false;
                            Toast.makeText(ModifyNameActivity.this,"验证码已发送，请在十分钟内完成操作",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ModifyNameActivity.this,ex.getMessage()+"",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ModifyNameActivity.this,"验证码正确",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ModifyNameActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String phone = phoneEdit.getText().toString();


                final MyUser bu = new MyUser();
                bu.setUsername(account);
                bu.setMobilePhoneNumber(phone);
                bu.setSchool(SelectSchoolUtil.getSchool());
                bu.setMobilePhoneNumberVerified(true);
                bu.setEmailVerified(false);
                bu.setGod(false);
                bu.setBanned(false);
                bu.setBannedReason("");
                bu.setAvatar("http://bmob-cdn-18038.b0.upaiyun.com/2018/05/18/425ce45f40a6b2208074aa1dbce9f76c.png");
                bu.setSchool("");
                bu.setSetAcCount(0);
                bu.setDynamicsCount(0);



                    if (ifConfirm && phoneEdit.getText().toString().equals(number)) {
                        bu.setMobilePhoneNumberVerified(true);
                        bu.setEmailVerified(false);
                        bu.setFollowChartId("");

                        bu.update(BmobUser.getCurrentUser(MyUser.class).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                                if (e==null){

                                    BmobUser.logOut();
                                    BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth("qq",accessToken, expires,openID);
                                    BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {

                                        @Override
                                        public void done(JSONObject userAuth,BmobException e) {
                                            if (e==null){
                                                //Toast.makeText(ModifyNameActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ModifyNameActivity.this, MainActivity.class));
                                                finish();


                                            }

                                        }
                                    });

                                }

                                else {
                                    Toast.makeText(ModifyNameActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        Toast.makeText(ModifyNameActivity.this, "请确保手机信息正确", Toast.LENGTH_SHORT).show();
                    }


                }

        });

    }
    @Override
    public void onBackPressed() {
        if (nickName==null){
            BmobUser.logOut();
            startActivity(new Intent(ModifyNameActivity.this,LoginActivity.class));
        }

        //

    }

}
