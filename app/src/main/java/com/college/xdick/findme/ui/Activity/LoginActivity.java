package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.college.xdick.findme.BmobIM.UserModel;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/4/2.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText accountEdit, passwordEdit;
    private Button login, signup,qqLogin;
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        mTencent = Tencent.createInstance("1106736587"
                ,LoginActivity.this.getApplicationContext());

    }


    private void initView() {

        accountEdit = findViewById(R.id.account_login);
        passwordEdit = findViewById(R.id.password_login);
        login = findViewById(R.id.login_button);
        signup = findViewById(R.id.tosignup_button);
        qqLogin= findViewById(R.id.qq_button);


        qqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIUiListener = new BaseUiListener();
                //all表示获取所有权限
                mTencent.login(LoginActivity.this,"all", mIUiListener);
            }
        });




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                UserModel.getInstance().login(account,password, new LogInListener() {

                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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


    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e("", "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                final String accessToken = obj.getString("access_token");
                final String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(final Object response) {
                        BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth("qq",accessToken, expires,openID);
                        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {

                            @Override
                            public void done(JSONObject userAuth,BmobException e) {
                               if (e==null){

                                   if (BmobUser.getCurrentUser().getUpdatedAt()==null){
                                       try {
                                           String nickName = ((JSONObject) response).getString("nickname");
                                           Intent intent =new Intent(LoginActivity.this, ModifyNameActivity.class);
                                           intent.putExtra("NICKNAME",nickName);
                                           intent.putExtra("ACCESSTOKEN",accessToken);
                                           intent.putExtra("EXPIRES",expires);
                                           intent.putExtra("OPENID",openID);
                                           startActivity(intent);
                                           finish();

                                       }
                                       catch (Exception e2){
                                           e2.printStackTrace();
                                       }

                                  }

                                  else {

                                      startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                      finish();

                                  }


                               }

                            }
                        });

                        Log.e("zzz","登录成功"+response.toString());

                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e("","登录失败"+uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        Log.e("","登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();

        }

    }


    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }











    /*--------------------------------监听返回键--------------------*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();

    }



}