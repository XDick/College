package com.college.xdick.college.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.college.xdick.college.R;
import com.college.xdick.college.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/4/2.
 */

public class SignupActivity  extends AppCompatActivity {
   private EditText accountEdit,passwordEdit,emailEdit;
   private Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
    }


    private void initView(){
        accountEdit = findViewById(R.id.account_signup);
        passwordEdit =findViewById(R.id.password_signup);
        emailEdit = findViewById(R.id.email_signup);
        signup = findViewById(R.id.signup_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String email = emailEdit.getText().toString();

                BmobUser bu = new BmobUser();
                bu.setUsername(account);
                bu.setPassword(password);
                bu.setEmail(email);
                bu.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User s, BmobException e) {
                        if(e==null){
                            Toast.makeText(SignupActivity.this,"注册成功", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(SignupActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SignupActivity.this,"注册失败"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this,LoginActivity.class));
        finish();

    }
}



