package com.college.xdick.findme.ui.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.MyClass.WaveView3;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Activity.MyInterestActivity;
import com.college.xdick.findme.ui.Activity.MyJoinActivity;
import com.college.xdick.findme.ui.Activity.MyLikeActivity;
import com.college.xdick.findme.ui.Activity.MySetActivity;
import com.college.xdick.findme.ui.Activity.SettingUserActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;
import com.college.xdick.findme.util.ClassFileHelper;
import com.college.xdick.findme.util.ExpUtil;
import com.college.xdick.findme.util.SelectSchoolUtil;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;


import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * Created by Administrator on 2018/4/2.
 */

public class UserFragment extends BaseFragment {

    private String picturePath;
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private TextView user,setcountText,dynamicscountText,expText,expFigure;
    private ImageView avatar,setting,background,edit_school;
    private ImagePicker imagePicker;
    private LinearLayout maytag,userset,userjoin,userlike,
            userexit,usersetting,userdynamics,userUpdate,upgrade;
    private WaveView3 waveView3;
    private LinearLayout about;
    private Button register;
    private ProgressBar expBar;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_user,container,false);



        startImagePicker();
        initView();
        BmobCheckIfLogin();
        return rootView;
    }

    private void initView(){
        user= rootView.findViewById(R.id.user_username);
        avatar=rootView.findViewById(R.id.user_avatar);
        maytag = rootView.findViewById(R.id.user_mytag);
        setting = rootView.findViewById(R.id.setting);
        upgrade = rootView.findViewById(R.id.user_upgrade);
        setcountText = rootView.findViewById(R.id.user_set_count);
        userset = rootView.findViewById(R.id.user_set);
        userjoin = rootView.findViewById(R.id.user_join);
        userlike=rootView.findViewById(R.id.user_like);
        edit_school = rootView.findViewById(R.id.userschool_edit);

        expFigure= rootView.findViewById(R.id.exp_figure);
        expFigure.setText(ExpUtil.expFigure(myUser.getExp()));


        expBar = rootView.findViewById(R.id.exp_progress);
        expBar.setProgress(ExpUtil.Percent(myUser.getExp())==0?5:ExpUtil.Percent(myUser.getExp()));

        expText= rootView.findViewById(R.id.user_exp);


        if(myUser.getExp()==null) myUser.setExp(0);
        expText.setText(ExpUtil.ConvertExp(myUser.getExp()));
        expText = ExpUtil.colorTextView(getContext(),expText,myUser.getExp());

        register = rootView.findViewById(R.id.button_register);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date( ((MainActivity)getContext()).getBmobTime()));
        if(date.equals(myUser.getRegisterTime())) {
            register.setBackground(getResources().getDrawable(R.drawable.join_button_radius_true));
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"明天再来签到吧o(╥﹏╥)o",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String date = sdf.format(new Date( ((MainActivity)getContext()).getBmobTime()));

                    myUser.increment("Exp",5);
                    myUser.setRegisterTime(date);
                    myUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            myUser.setExp(myUser.getExp()+5);
                            String level =ExpUtil.ConvertExp(myUser.getExp());
                            if(!expText.getText().toString().equals(level)){
                                Toast.makeText(getContext(),"升级！签到成功经验+5",Toast.LENGTH_SHORT).show();
                                expText.setText(level);
                                expText = ExpUtil.colorTextView(getContext(),expText,myUser.getExp());

                            }
                            else
                                Toast.makeText(getContext(),"签到成功经验+5",Toast.LENGTH_SHORT).show();

                            expFigure.setText(ExpUtil.expFigure(myUser.getExp()));
                            expBar.setProgress(ExpUtil.Percent(myUser.getExp())==0?5:ExpUtil.Percent(myUser.getExp()));
                            register.setBackground(getResources().getDrawable(R.drawable.join_button_radius_true));
                           register.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(),"明天再来签到吧o(╥﹏╥)o",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            });

        }



        dynamicscountText = rootView.findViewById(R.id.user_dynamics_count);
        userdynamics = rootView.findViewById(R.id.user_dynamics);
        TextView schoolText = rootView.findViewById(R.id.userschool);
        background = rootView.findViewById(R.id.user_background);
        try {
            schoolText.setText("学校:"+ myUser.getSchool());

            SelectSchoolUtil.initPopView(getActivity(),null,schoolText, myUser);


        }catch (Exception e){
            e.printStackTrace();
        }

        edit_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectSchoolUtil.showPopWindow();
            }
        });
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        // TODO Auto-generated method stub


                        if (updateStatus == UpdateStatus.Yes) {//版本有更新

                        }else if(updateStatus == UpdateStatus.No){
                            Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
                        }/*else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                             Toast.makeText(getActivity(), "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.IGNORED){
                             Toast.makeText(getActivity(), "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
                             Toast.makeText(getActivity(), "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.TimeOut){
                             Toast.makeText(getActivity(), "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                         }*/
                    }
                });
                BmobUpdateAgent.forceUpdate(getActivity());
            }
        });





        userdynamics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(), UserCenterActivity.class);
               intent.putExtra("USER", myUser);
                startActivity(intent);
            }
        });



        final EasyPopup easyPopup =EasyPopup.create()
                .setContentView(getContext(),R.layout.popup_setting)
                .apply();

        userexit = easyPopup.findViewById(R.id.exit_setting);
        usersetting = easyPopup.findViewById(R.id.account_setting);
        about = easyPopup.findViewById(R.id.user_set_about);
        userUpdate = easyPopup.findViewById(R.id.update_setting);
         userUpdate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

                     @Override
                     public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                         // TODO Auto-generated method stub


                         if (updateStatus == UpdateStatus.Yes) {//版本有更新

                         }else if(updateStatus == UpdateStatus.No){
                             Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
                         }/*else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                             Toast.makeText(getActivity(), "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.IGNORED){
                             Toast.makeText(getActivity(), "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
                             Toast.makeText(getActivity(), "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.TimeOut){
                             Toast.makeText(getActivity(), "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                         }*/
                     }
                 });
                 BmobUpdateAgent.forceUpdate(getActivity());
                 easyPopup.dismiss();
             }
         });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("关于");
                builder.setMessage("本应用由XDick-谢德锟开发，有Bug和意见欢迎加QQ群938641787 反馈");
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
                                            myUser = BmobUser.getCurrentUser(MyUser.class); // 现在的currentUser是null了
                                          //  Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
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


        waveView3 = (WaveView3) rootView.findViewById(R.id.wave_view);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2,-2);
        lp.gravity = Gravity.BOTTOM|Gravity.CENTER;
        waveView3.setOnWaveAnimationListener(new WaveView3.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0,0,0,(int)y+2);
                avatar.setLayoutParams(lp);
            }
        });
        Glide.with(this).load(new mGlideUrl(myUser.getAvatar()+"!/fp/10000"))
                        .apply(bitmapTransform(new BlurTransformation
                                (7, 7))
                        .error(R.drawable.head))
                        .into(background);


    }



    private void showUserInfo(){



            setcountText.setText(myUser.getSetAcCount()+"");



            dynamicscountText.setText(myUser.getDynamicsCount()+"");




                user.setText(myUser.getUsername());
                Glide.with(getActivity()).load(new mGlideUrl(myUser.getAvatar()+"!/fp/10000")).apply(bitmapTransform(new CropCircleTransformation())
                        .error(R.drawable.head)).into(avatar);




    }
    private void exitUserInfo(){
            setcountText.setText(0+"");

            dynamicscountText.setText("");
                user.setText("点击头像登录");
                userexit.setVisibility(View.GONE);
                Glide.with(getActivity()).load(R.drawable.head).apply(bitmapTransform(new CropCircleTransformation())).into(avatar);
    }

    private void BmobCheckIfLogin(){


        if(myUser != null){
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
                                                        File file=new File(picturePath);
                                                        final String fileType = picturePath
                                                                .substring(picturePath.lastIndexOf("."));

                                                        if(picturePath!=null){
                                                           final String renamePath=picturePath.
                                                                    substring(0,picturePath.lastIndexOf("/")+1)
                                                            +"avatar_"+myUser.getObjectId()
                                                                    +fileType;
                                                            //Toast.makeText(getContext(),renamePath,Toast.LENGTH_LONG).show();


                                                            file.renameTo(new File(renamePath));
                                                            //Toast.makeText(getContext(),fileFinish+"",Toast.LENGTH_LONG).show();

                                                            final BmobFile bmobFile = new  BmobFile(new File(renamePath));

                                                            bmobFile.uploadblock(new UploadFileListener() {
                                                                                     @Override
                                                                                     public void done(BmobException e) {
                                                                                         if (e==null){
                                                                                             if (!myUser.getAvatar().equals("http://bmob-cdn-18038.b0.upaiyun.com/2018/05/18/425ce45f40a6b2208074aa1dbce9f76c.png"))
                                                                                             {
                                                                                                 BmobFile file = new BmobFile();
                                                                                                 file.setUrl(myUser.getAvatar());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
                                                                                                 file.delete(new UpdateListener() {

                                                                                                     @Override
                                                                                                     public void done(BmobException e) {
                                                                                                         if(e==null){

                                                                                                         }else{
                                                                                                         }
                                                                                                     }
                                                                                                 });

                                                                                             }
                                                                                             final MyUser myUser1= new MyUser();
                                                                                             myUser1.setObjectId(myUser.getObjectId());
                                                                                             myUser1.setAvatar(bmobFile.getFileUrl());
                                                                                             myUser1.update(myUser.getObjectId(),new UpdateListener() {
                                                                                                 @Override
                                                                                                 public void done(BmobException e) {
                                                                                                     if(e==null){

                                                                                                         ClassFileHelper.deleteFile(new File(renamePath));
                                                                                                         Toast.makeText(getContext(),"修改头像成功",Toast.LENGTH_SHORT).show();
                                                                                                                 Glide.with(getContext()).load(new mGlideUrl(myUser1.getAvatar())).apply(bitmapTransform(new CropCircleTransformation()).error(R.drawable.head)).into(avatar);
                                                                                                         Glide.with(getContext()).load(new mGlideUrl(myUser1.getAvatar()))
                                                                                                                 .apply(bitmapTransform(new BlurTransformation(9, 7))
                                                                                                                 .error(R.drawable.head))
                                                                                                                 .into(background);
                                                                                                                 picturePath =null;
                                                                                                     }else{
                                                                                                         Toast.makeText(getContext(),"修改头像失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                                                                     }
                                                                                                 }
                                                                                             });
                                                                                         }
                                                                                         else {
                                                                                             Toast.makeText(getContext(),"上传文件失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                                                         }

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
          myUser =BmobUser.getCurrentUser(MyUser.class);


            setcountText.setText(myUser.getSetAcCount()+"");



           dynamicscountText.setText(myUser.getDynamicsCount()+"");





    }
}

