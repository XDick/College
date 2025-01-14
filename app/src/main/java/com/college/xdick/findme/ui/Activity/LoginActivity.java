package com.college.xdick.findme.ui.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.BmobIM.UserModel;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.util.AppManager;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/4/2.
 */

public class LoginActivity extends BaseActivity {
    private EditText accountEdit, passwordEdit;
    private Button login, signup;
    private LinearLayout qqLogin;
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private ProgressDialog dialog=null;
    private android.app.AlertDialog.Builder builder=null ;
    private android.app.AlertDialog dialog2=null;
    private ImageView background;

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
        background = findViewById(R.id.background);
        Glide.with(this).load(R.drawable.findme_background)
                /*.apply(bitmapTransform(new BlurTransformation(6, 6)))*/
                .into(background);

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
                if (dialog==null){
                    initDialog1();
                }
                dialog.show();
                UserModel.getInstance().login(account,password, new LogInListener() {

                    @Override
                    public void done(Object o, BmobException e) {


                        if (e == null) {

                           final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                                @Override
                                public void onChange(ConnectionStatus status) {
                                    if (BmobUser.getCurrentUser()!=null){
                                        if (status.getMsg().equals("connected")) {

                                            if (dialog!=null){
                                                dialog.dismiss();}
                                            if (dialog2!=null){
                                                dialog2.dismiss();}
                                            //Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                            startActivity(intent);
                                            finish();

                                        }
                                        else if (status.getMsg().equals("connecting")) {



                                            if (dialog==null){
                                                initDialog1();
                                            }
                                            dialog.show();
                                            if (dialog2==null){
                                                initDialog2();
                                            }
                                            if (dialog2!=null) {
                                                dialog2.dismiss();
                                            }


                                        }



                                        else {

                                            IMconnectBomob();



                                            if (dialog!=null){
                                                dialog.dismiss();
                                            }

                                            if (dialog2==null){
                                                initDialog2();
                                            }
                                            dialog2.show();


                                        }
                                    }


                                }
                            });
                            IMconnectBomob();
                        } else {

                            if (dialog!=null){
                                dialog.dismiss();
                            }

                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
           // Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();

            if (dialog==null){
                initDialog1();
            }
            dialog.show();

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
                                   BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                                       @Override
                                       public void onChange(ConnectionStatus status) {
                                           if (BmobUser.getCurrentUser()!=null){


                                               if (status.getMsg().equals("connected")) {

                                                   if (dialog!=null){
                                                       dialog.dismiss();}
                                                   if (dialog2!=null){
                                                       dialog2.dismiss();}

                                                   if (BmobUser.getCurrentUser().getUpdatedAt()==null){
                                                       try {
                                                           String nickName = ((JSONObject) response).getString("nickname");
                                                           Intent intent =new Intent(LoginActivity.this, ModifyNameActivity.class);
                                                           intent.putExtra("NICKNAME",nickName);
                                                           intent.putExtra("ACCESSTOKEN",accessToken);
                                                           intent.putExtra("EXPIRES",expires);
                                                           intent.putExtra("OPENID",openID);
                                                           startActivity(intent);
                                                           Toast.makeText(LoginActivity.this, "完善一下信息吧(#^.^#)", Toast.LENGTH_SHORT).show();
                                                           finish();

                                                       }
                                                       catch (Exception e2){
                                                           e2.printStackTrace();
                                                       }

                                                   }

                                                   else {
                                                      // Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                                       startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                       finish();

                                                   }


                                               }
                                               else if (status.getMsg().equals("connecting")) {



                                                   if (dialog==null){
                                                       initDialog1();
                                                   }
                                                   dialog.show();
                                                   if (dialog2==null){
                                                       initDialog2();
                                                   }
                                                   if (dialog2!=null) {
                                                       dialog2.dismiss();
                                                   }


                                               }



                                               else {

                                                   IMconnectBomob();



                                                   if (dialog!=null){
                                                       dialog.dismiss();
                                                   }

                                                   if (dialog2==null){
                                                       initDialog2();
                                                   }
                                                   dialog2.show();


                                               }
                                           }


                                           // Toast.makeText(getBaseContext(),status.getMsg(),Toast.LENGTH_SHORT).show();

                                       }
                                   });
                                   IMconnectBomob();


                               }

                               else {

                                   if (dialog!=null){
                                       dialog.dismiss();
                                   }

                                   if (dialog2==null){
                                       initDialog2();
                                   }
                                   dialog2.show();

                               }

                            }
                        });

                        Log.e("zzz","登录成功"+response.toString());

                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e("","登录失败"+uiError.toString());
                        if (dialog!=null){
                            dialog.dismiss();
                        }

                        if (dialog2==null){
                            initDialog2();
                        }
                        dialog2.show();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("","登录取消");
                        if (dialog!=null){
                            dialog.dismiss();
                        }

                        if (dialog2==null){
                            initDialog2();
                        }
                        dialog2.show();

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

    private void IMconnectBomob() {

        final MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            if (!TextUtils.isEmpty(bmobUser.getObjectId())) {
                BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            try {
                                BmobIM.getInstance().
                                        updateUserInfo(new BmobIMUserInfo( bmobUser.getObjectId(),
                                                bmobUser.getUsername(),  bmobUser.getAvatar()));
                            }
                            catch (Exception e2){
                                e2.printStackTrace();
                            }

                        } else {
                            // startActivity(intent);
                            //finish();
                            //连接失败
                            dialog.dismiss();
                            Toast.makeText(getBaseContext(),"无法连接服务器", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }



    private void initDialog1() {
        dialog = new ProgressDialog(AppManager.getAppManager().currentActivity());
        dialog.setMessage("正在连接服务器...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


    }

    private void initDialog2() {
        builder = new android.app.AlertDialog.Builder(AppManager.getAppManager().currentActivity());
        builder.setTitle("连接断开");
        builder.setMessage("你可以点击重试重新连接");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();
                startActivity(new Intent(AppManager.getAppManager().currentActivity(),MainActivity.class));
                finish();
            }
        });
        builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IMconnectBomob();
                dialog2.dismiss();
            }
        });


        dialog2 = builder.create();
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);
    }

}