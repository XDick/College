package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.FollowChart;
import com.college.xdick.findme.bean.MyUser;

import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyNameActivity extends BaseActivity {
     private EditText modifynameEdit;
     private Button confirmButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyname);
       // Toast.makeText(ModifyNameActivity.this,"取个好听的名字(｡･ω･｡)",Toast.LENGTH_SHORT).show();
        String nickName= getIntent().getStringExtra("NICKNAME");
        final String accessToken = getIntent().getStringExtra("ACCESSTOKEN");
        final String expires = getIntent().getStringExtra("EXPIRES");
        final String openID = getIntent().getStringExtra("OPENID");
        modifynameEdit=  findViewById(R.id.modifyname_editview);
        modifynameEdit.setText(nickName);
        confirmButton= findViewById(R.id.confirmname_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  FollowChart followChart= new FollowChart();
                followChart.setFansCount(0);
                followChart.setHostId(BmobUser.getCurrentUser(MyUser.class).getObjectId());
                followChart.setUserName(modifynameEdit.getText().toString());
                followChart.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e==null){*/
                            final MyUser myUser= new MyUser();
                            myUser.setUsername(modifynameEdit.getText().toString());
                            myUser.setMobilePhoneNumberVerified(false);
                            myUser.setGod(false);
                            //myUser.setFollowChartId(s);
                            myUser.update(BmobUser.getCurrentUser(MyUser.class).getObjectId(), new UpdateListener() {
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
                                        Toast.makeText(ModifyNameActivity.this,"此用户名已被注册",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
              /*      }
                });



            }*/
        });

    }
}