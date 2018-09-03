package com.college.xdick.findme.ui.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.MyClass.WaveView3;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MyInterestActivity;
import com.college.xdick.findme.ui.Activity.MyJoinActivity;
import com.college.xdick.findme.ui.Activity.MyLikeActivity;
import com.college.xdick.findme.ui.Activity.MySetActivity;
import com.college.xdick.findme.ui.Activity.SettingUserActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.util.SelectSchoolUtil;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;


import java.io.File;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UserFragment extends Fragment {
    private View rootview;
    private String picturePath;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private TextView user,setcountText,dynamicscountText;
    private ImageView avatar,setting,background;
    private ImagePicker imagePicker;
    private LinearLayout maytag,userset,userjoin,userlike,userexit,usersetting,userdynamics;
    private WaveView3 waveView3;
    private LinearLayout about;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_user,container,false);



        startImagePicker();
        initView();
        BmobCheckIfLogin();
        return rootview;
    }

    private void initView(){
        user= rootview.findViewById(R.id.user_username);
        avatar=rootview.findViewById(R.id.user_avatar);
        maytag = rootview.findViewById(R.id.user_mytag);
        setting = rootview.findViewById(R.id.setting);

        setcountText = rootview.findViewById(R.id.user_set_count);
        userset = rootview.findViewById(R.id.user_set);
        userjoin = rootview.findViewById(R.id.user_join);
        userlike=rootview.findViewById(R.id.user_like);

        dynamicscountText = rootview.findViewById(R.id.user_dynamics_count);
        userdynamics = rootview.findViewById(R.id.user_dynamics);
        TextView schoolText = rootview.findViewById(R.id.userschool);
        background = rootview.findViewById(R.id.user_background);
        try {
            schoolText.setText("学校:"+bmobUser.getSchool());
            SelectSchoolUtil.initPopView(getActivity(),null,schoolText,bmobUser);
            schoolText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectSchoolUtil.showPopWindow();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }







        userdynamics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(), UserCenterActivity.class);
               intent.putExtra("USER",bmobUser);
                startActivity(intent);
            }
        });



        final EasyPopup easyPopup =EasyPopup.create()
                .setContentView(getContext(),R.layout.popup_setting)
                .apply();

        userexit = easyPopup.findViewById(R.id.exit_setting);
        usersetting = easyPopup.findViewById(R.id.account_setting);
        about = easyPopup.findViewById(R.id.user_set_about);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("关于");
                builder.setMessage("本应用由XDick-谢德锟开发，有Bug和意见欢迎反馈QQ470471350");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //toast("确定");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                easyPopup.dismiss();


            }
        });



        usersetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SettingUserActivity.class));
                easyPopup.dismiss();
            }
        });
        userjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MyJoinActivity.class));
            }
        });
        userlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MyLikeActivity.class));
            }
        });

        userset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MySetActivity.class));
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               easyPopup.showAtAnchorView(setting, YGravity.BELOW, XGravity.LEFT, 0, -50);
           }
       });

        userexit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BmobUser.logOut();   //清除缓存用户对象
                                            bmobUser = BmobUser.getCurrentUser(MyUser.class); // 现在的currentUser是null了
                                            Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
                                            BmobIM.getInstance().disConnect();
                                            BmobCheckIfLogin();
                                            easyPopup.dismiss();
                                            startActivity(new Intent(getActivity(),LoginActivity.class));
                                            getActivity().finish();
                                        }
                                    });


       maytag.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), MyInterestActivity.class);
               startActivity(intent);
           }
       });


        waveView3 = (WaveView3) rootview.findViewById(R.id.wave_view);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2,-2);
        lp.gravity = Gravity.BOTTOM|Gravity.CENTER;
        waveView3.setOnWaveAnimationListener(new WaveView3.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0,0,0,(int)y+2);
                avatar.setLayoutParams(lp);
            }
        });
        background.setColorFilter(Color.parseColor("#cf0606"), PorterDuff.Mode.DARKEN);
        Glide.with(this).load(bmobUser.getAvatar())
                        .apply(bitmapTransform(new BlurTransformation(9, 7)))
                        .into(background);


    }



    private void showUserInfo(){


        try{
            setcountText.setText(bmobUser.getSetAc().length+"");}
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            dynamicscountText.setText(bmobUser.getDynamics().length+"");
        }
        catch (Exception e){
            e.printStackTrace();
        }


                user.setText(bmobUser.getUsername());
                Glide.with(getActivity()).load(bmobUser.getAvatar()).apply(bitmapTransform(new CropCircleTransformation())).into(avatar);




    }
    private void exitUserInfo(){
            setcountText.setText(0+"");

            dynamicscountText.setText("");
                user.setText("点击头像登录");
                userexit.setVisibility(View.GONE);
                Glide.with(getActivity()).load(R.drawable.head).apply(bitmapTransform(new CropCircleTransformation())).into(avatar);
    }

    private void BmobCheckIfLogin(){


        if(bmobUser != null){
            showUserInfo();
            avatar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                imagePicker.startGallery(UserFragment.this, new ImagePicker.Callback() {
                                                    // 选择图片回调
                                                    @Override
                                                    public void onPickImage(Uri imageUri) {

                                                    }

                                                    // 裁剪图片回调
                                                    @Override
                                                    public void onCropImage(Uri imageUri) {

                                                        picturePath=imageUri.toString().replace("file:///","");
                                                        if(picturePath!=null){

                                                            final BmobFile bmobFile = new BmobFile(new File(picturePath));
                                                            bmobFile.uploadblock(new UploadFileListener() {
                                                                                     @Override
                                                                                     public void done(BmobException e) {
                                                                                         if (!bmobUser.getAvatar().equals("http://bmob-cdn-18038.b0.upaiyun.com/2018/05/18/425ce45f40a6b2208074aa1dbce9f76c.png"))
                                                                                         {
                                                                                             BmobFile file = new BmobFile();
                                                                                             file.setUrl(bmobUser.getAvatar());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
                                                                                             file.delete(new UpdateListener() {

                                                                                                 @Override
                                                                                                 public void done(BmobException e) {
                                                                                                     if(e==null){

                                                                                                     }else{
                                                                                                     }
                                                                                                 }
                                                                                             });

                                                                                         }

                                                                                         bmobUser.setAvatar(bmobFile.getFileUrl());
                                                                                         bmobUser.update(bmobUser.getObjectId(),new UpdateListener() {
                                                                                             @Override
                                                                                             public void done(BmobException e) {
                                                                                                 if(e==null){
                                                                                                     Toast.makeText(getContext(),"修改头像成功",Toast.LENGTH_SHORT).show();
                                                                                                     Glide.with(getContext()).load(bmobUser.getAvatar()).apply(bitmapTransform(new CropCircleTransformation())).into(avatar);
                                                                                                     picturePath =null;
                                                                                                 }else{

                                                                                                 }
                                                                                             }
                                                                                         });
                                                                                     }






                                                                                 }
                                                            );
                                                        }

                                                    }

                                                    // 自定义裁剪配置
                                                    @Override
                                                    public void cropConfig(CropImage.ActivityBuilder builder) {
                                                        builder
                                                                // 是否启动多点触摸
                                                                .setMultiTouchEnabled(false)
                                                                // 设置网格显示模式
                                                                .setGuidelines(CropImageView.Guidelines.ON)
                                                                // 圆形/矩形
                                                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                                                // 调整裁剪后的图片最终大小
                                                                .setRequestedSize(660, 660)
                                                                // 宽高比
                                                                .setAspectRatio(1, 1);
                                                    }

                                                    // 用户拒绝授权回调
                                                    @Override
                                                    public void onPermissionDenied(int requestCode, String[] permissions,
                                                                                   int[] grantResults) {
                                                        Toast.makeText(getContext(),"抱歉(＞人＜；)，功能将无法实现",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

            );

        }else{
           exitUserInfo();
           avatar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                }

            });
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        imagePicker.onActivityResult(this, requestCode, resultCode, data);




        }





    private  void startImagePicker() {
        imagePicker = new ImagePicker();
// 设置标题
        imagePicker.setTitle("选择头像");
// 设置是否裁剪图片
        imagePicker.setCropImage(true);

    }


    @Override
    public void onResume() {
        super.onResume();
          bmobUser=BmobUser.getCurrentUser(MyUser.class);

        try{
            setcountText.setText(bmobUser.getSetAc().length+"");}
        catch (Exception e){
            e.printStackTrace();
        }

        try {
           dynamicscountText.setText(bmobUser.getDynamics().length+"");
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }
}

