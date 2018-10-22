package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.jyn.vcview.VerificationCodeView;

import org.json.JSONObject;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ConfirmPhoneNumberActivity extends BaseActivity {
 private boolean ifConfirm =false;
 private String number= null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_phone_number);
        VerificationCodeView verificationCodeView =
                findViewById(R.id.verificationcodeview);



     /*   verificationCodeView.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
            @Override
            public void onComplete(String content) {
                number=content;
                BmobSMS.requestSMSCode(phoneEdit.getText().toString(), "验证",new QueryListener<Integer>() {

                    @Override
                    public void done(Integer smsId,BmobException ex) {
                        if(ex==null){//验证码发送成功

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
*/
    }


}
