package com.college.xdick.findme.ui.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.MyClass.WaveView3;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.InterestActivity;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Activity.MyInterestActivity;
import com.college.xdick.findme.ui.Activity.SetActivitiyActivity;
import com.college.xdick.findme.ui.Activity.SettingActivity;
import com.college.xdick.findme.ui.Activity.SignupActivity;
import com.college.xdick.findme.util.FileUtil;
import com.college.xdick.findme.util.SelectSchoolUtil;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UserFragment extends Fragment {
    private View rooview;
    private String picturePath;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private TextView user,setcountText,joincountText;
    private ImageView avatar,setting;
    private ImagePicker imagePicker;
    private LinearLayout maytag;
    private WaveView3 waveView3;
    private List<MyActivity> setActivityList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rooview =inflater.inflate(R.layout.fragment_user,container,false);
        startImagePicker();
        initView();
        initMySetAc();
        BmobCheckIfLogin();

        return rooview;
    }

    private void initView(){
        user= rooview.findViewById(R.id.user_username);
        avatar=rooview.findViewById(R.id.user_avatar);
        maytag = rooview.findViewById(R.id.user_mytag);
        setting = rooview.findViewById(R.id.setting);
        joincountText = rooview.findViewById(R.id.user_join_count);
        setcountText = rooview.findViewById(R.id.user_set_count);
        setting.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getContext(), SettingActivity.class));
           }
       });


       maytag.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), MyInterestActivity.class);
               startActivity(intent);
           }
       });


        waveView3 = (WaveView3) rooview.findViewById(R.id.wave_view);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2,-2);
        lp.gravity = Gravity.BOTTOM|Gravity.CENTER;
        waveView3.setOnWaveAnimationListener(new WaveView3.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0,0,0,(int)y+2);
                avatar.setLayoutParams(lp);
            }
        });


    }



    private void showUserInfo(){
                String[]join = bmobUser.getJoin();
                try {
                    joincountText.setText(join.length+"");
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                user.setText(bmobUser.getUsername());
                Glide.with(getActivity()).load(bmobUser.getAvatar()).apply(bitmapTransform(new CropCircleTransformation())).into(avatar);




    }
    private void exitUserInfo(){
            setcountText.setText(0+"");
           joincountText.setText(0+"");
                user.setText("点击头像登录");
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

                                                        String originalpath = bmobUser.getAvatar();
                                                        picturePath=imageUri.toString().replace("file:///","");
                                                        if(picturePath!=null){

                                                            final BmobFile bmobFile = new BmobFile(new File(picturePath));
                                                            bmobFile.uploadblock(new UploadFileListener() {
                                                                                     @Override
                                                                                     public void done(BmobException e) {
                                                                                         bmobUser.setAvatar(bmobFile.getFileUrl());
                                                                                         bmobUser.update(bmobUser.getObjectId(),new UpdateListener() {
                                                                                             @Override
                                                                                             public void done(BmobException e) {
                                                                                                 if(e==null){
                                                                                                     Toast.makeText(getContext(),"修改头像成功",Toast.LENGTH_SHORT).show();
                                                                                                     Glide.with(getContext()).load(bmobUser.getAvatar()).into(avatar);
                                                                                                     picturePath =null;
                                                                                                 }else{

                                                                                                 }
                                                                                             }
                                                                                         });


                                                                                     }






                                                                                 }
                                                            );


                                                            BmobFile file = new BmobFile();
                                                            file.setUrl(originalpath);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
                                                            file.delete(new UpdateListener() {

                                                                @Override
                                                                public void done(BmobException e) {
                                                                    if(e==null){

                                                                    }else{

                                                                    }
                                                                }
                                                            });
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


    private void initMySetAc(){

        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.addWhereEqualTo("hostName",bmobUser.getUsername());
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                if (e==null){
                    setActivityList=list;
                    setcountText.setText(setActivityList.size()+"");
                }
            }
        });


    }



}

