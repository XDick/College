package com.college.xdick.college.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.college.Activity.LoginActivity;
import com.college.xdick.college.R;
import com.college.xdick.college.util.User;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UserFragment extends Fragment {
    private View rooview;
    private User bmobUser = BmobUser.getCurrentUser(User.class);
    TextView user;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rooview =inflater.inflate(R.layout.fragment_user,container,false);
        initView();
        return rooview;
    }

    private void initView(){
        user= rooview.findViewById(R.id.userinfo_user);
        final Button button = rooview.findViewById(R.id.exit_button_user);

        if(bmobUser != null){
            showUserInfo();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BmobUser.logOut();   //清除缓存用户对象
                    BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
                    Toast.makeText(getActivity(),"已退出登录",Toast.LENGTH_SHORT).show();
                    exitUserInfo();
                    button.setVisibility(View.INVISIBLE);


                }
            });



        }else{

            button.setVisibility(View.INVISIBLE);

                }


        }


    private void showUserInfo(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                user.setText(bmobUser.getUsername()+bmobUser.getEmail());
            }
        });
    }
    private void exitUserInfo(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                user.setText("请先登录");
            }
        });
    }
}
