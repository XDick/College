package com.college.xdick.college.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.college.R;
import com.college.xdick.college.bean.MyUser;


import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UserFragment extends Fragment {
    private View rooview;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    TextView user,email,ifconfirm,school,goconfirm;
    ImageView avatar;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rooview =inflater.inflate(R.layout.fragment_user,container,false);
        initView();
        return rooview;
    }

    private void initView(){
        user= rooview.findViewById(R.id.user_username);
        avatar=rooview.findViewById(R.id.user_avatar);
        email =rooview.findViewById(R.id.user_useremail);
        ifconfirm=rooview.findViewById(R.id.user_ifconfirm);
        school=rooview.findViewById(R.id.user_userschool);
        goconfirm=rooview.findViewById(R.id.user_goconfirm);
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
                    BmobIM.getInstance().disConnect();





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
                user.setText(bmobUser.getUsername());
                Glide.with(getActivity()).load(bmobUser.getAvatar()).into(avatar);
                email.setText("邮箱："+bmobUser.getEmail());
                school.setText("学校："+bmobUser.getSchool());
                if(bmobUser.getIfConfirm().equals(false)){
                    ifconfirm.setText("是否认证：未认证");}
                else {
                    ifconfirm.setText("是否认证：已认证");
                }
                if(ifconfirm.getText().toString().equals("是否认证：未认证")){
                    goconfirm.setText("立马认证");
                    goconfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                }

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

