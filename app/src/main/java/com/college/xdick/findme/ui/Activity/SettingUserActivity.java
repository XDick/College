package com.college.xdick.findme.ui.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.bean.School;
import com.college.xdick.findme.bean.SelectSchool;
import com.college.xdick.findme.util.SelectSchoolUtil;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/5/17.
 */

public class SettingUserActivity extends BaseActivity{

      MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);
        initView();

    }

    private void initView(){

        TextView schoolText = findViewById(R.id.userschool);
        schoolText.setText("学校:"+myUser.getSchool()+"(点击更换)");
        SelectSchoolUtil.initPopView(this,null,schoolText,myUser);
        schoolText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectSchoolUtil.showPopWindow();
            }
        });
    }
}
