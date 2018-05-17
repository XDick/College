package com.college.xdick.findme.ui.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyUser;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DynamicsActivity extends AppCompatActivity {
   EditText contentEdit,titleEdit;
    private LinearLayout layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamics);
        initView();

    }


    private void initView(){
        contentEdit = findViewById(R.id.dynamics_content_edittext);
        titleEdit = findViewById(R.id.dynamics_title_edittext);
        Toolbar toolbar =findViewById(R.id.toolbar_dynamics);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
       if(actionBar!=null){ actionBar.setDisplayHomeAsUpEnabled(true);}

        layout =findViewById(R.id.daynamics_content_linearlayout);


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentEdit.setFocusable(true);
                contentEdit.setFocusableInTouchMode(true);
                contentEdit.requestFocus();
                contentEdit.findFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(contentEdit, 0);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.toolbar_dynamics_menu, menu);
        return true;
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.send:

                String content = contentEdit.getText().toString();
                String title = titleEdit.getText().toString();
                Dynamics dynamics = new Dynamics();
                dynamics.setContent(content);
                dynamics.setTitle(title);
                dynamics.setUserId(BmobUser.getCurrentUser().getObjectId());
                dynamics.setUser(BmobUser.getCurrentUser().getUsername());
                dynamics.save(new SaveListener<String>() {

                @Override
                public void done(String objectId, BmobException e) {
                    if(e==null){
                        Toast.makeText(DynamicsActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
                finish();

                break;

            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
    }
}
