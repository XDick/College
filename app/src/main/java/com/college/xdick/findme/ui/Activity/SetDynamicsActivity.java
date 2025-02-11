package com.college.xdick.findme.ui.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.MyGlideEngine;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.GridViewAddImagesAdapter;
import com.college.xdick.findme.adapter.SelectActivityAdapter;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.util.ClassFileHelper;
import com.college.xdick.findme.util.FileUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;


/**
 * Created by Administrator on 2018/4/3.
 */

public class SetDynamicsActivity extends BaseActivity {
    private EditText contentEdit;
    private MyActivity myActivity;
    private LinearLayout layout,selectActivity,checkboxLayout;
    private int REQUEST_CODE_CHOOSE = 1;
    private List<Uri> mSelected = new ArrayList<>();
    private GridViewAddImagesAdapter adapter;
    private List<String> picPath = new ArrayList<>();
    private   View parent;
    private  ListView mActivityListView;
    private  PopupWindow mPopWindow;
    private  SelectActivityAdapter mActivityAdapter;
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private  TextView activityText;
    private ImageView remove;
    private CheckBox checkBox;
    private boolean ifAddPic2Ac=false;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdynamics);
        initView();

    }


    private void initView() {
        initPopView(this);
        contentEdit = findViewById(R.id.dynamics_content_edittext);
        adapter = new GridViewAddImagesAdapter(mSelected, this);
        adapter.setMaxImages(10);
        Toolbar toolbar = findViewById(R.id.toolbar_dynamics);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        layout = findViewById(R.id.daynamics_content_linearlayout);
        final float scale = getResources().getDisplayMetrics().density;

        activityText = findViewById(R.id.choose_activity_text);
            checkBox = findViewById(R.id.checkbox);
            checkboxLayout = findViewById(R.id.checkbox_layout);
        remove = findViewById(R.id.remove);
        remove.setVisibility(View.INVISIBLE);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityText.setText("插入活动");
                myActivity=null;
                checkboxLayout.setVisibility(View.GONE);
                remove.setVisibility(View.INVISIBLE);
                ifAddPic2Ac=false;
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ifAddPic2Ac=true;
                }
                else {
                    ifAddPic2Ac=false;
                }
            }
        });
        selectActivity = findViewById(R.id.select_activity);
        selectActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initActivity();
                showPopWindow();
            }
        });

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


        GridView gridView = findViewById(R.id.choosegrid);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Matisse.from(SetDynamicsActivity.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(10-adapter.getCount())
                        .gridExpectedSize((int) (120 * scale + 0.5f))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.6f)
                        .theme( R.style.Matisse_FindMe)
                        .imageEngine(new MyGlideEngine())
                        .showSingleMediaType(true)
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_dynamics_menu, menu);
        return true;
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.send:


                if (mSelected.isEmpty()) {

                    if(TextUtils.isEmpty(contentEdit.getText().toString()))
                        return false;

                    Toast.makeText(SetDynamicsActivity.this, "正在发送...", Toast.LENGTH_SHORT).show();

                    String content = contentEdit.getText().toString();
                    Dynamics dynamics = new Dynamics();
                    if(myActivity!=null){
                        dynamics.setActivity(myActivity);
                    }
                    dynamics.setContent(content);
                    dynamics.setIfAdd2Gallery(false);
                    dynamics.setMyUser(BmobUser.getCurrentUser(MyUser.class));
                    dynamics.setLikeCount(0);
                    dynamics.setReplycount(0);
                    finish();
                    dynamics.save(new SaveListener<String>() {

                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                                user.increment("dynamicsCount",1);
                                user.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                                Toast.makeText(SetDynamicsActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SetDynamicsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    finish();
                } else {
                    Toast.makeText(SetDynamicsActivity.this, "正在发送...", Toast.LENGTH_SHORT).show();
                    int num=0;
                    for (Uri u : mSelected) {
                        String path=FileUtil.uriToFile(u, this);

                        File file=new File(path);
                        final String fileType = path
                                .substring(path.lastIndexOf("."));

                        final String renamePath="data/user/0/com.college.xdick.findme/cache/"
                                +"dynamics_image_"+"_"
                                +BmobUser.getCurrentUser().getObjectId()+"_"+num
                                +fileType;
                        final File newFile = new File(renamePath);
                        try{
                            ClassFileHelper.copyFileTo(file,newFile);}
                        catch (IOException e){
                            e.printStackTrace(); }
                        picPath.add(renamePath);
                          ++num;
                    }
                           finish();
                        BmobFile.uploadBatch(picPath.toArray(new String[picPath.size()]), new UploadBatchListener() {


                            @Override
                            public void onSuccess(List<BmobFile> list, List<String> list1) {

                                if (list1.size() == picPath.size()) {

                                    for (String path :picPath){

                                        ClassFileHelper.deleteFile(new File(path));
                                    }

                                    final String content = contentEdit.getText().toString();
                                    final Dynamics dynamics = new Dynamics();
                                    dynamics.setContent(content);
                                    if(myActivity!=null){
                                    dynamics.setActivity(myActivity);
                                        if (myActivity.getHost().getObjectId().equals(myUser.getObjectId())){
                                            dynamics.setIfAdd2Gallery(ifAddPic2Ac);
                                        }else {
                                            dynamics.setIfAdd2Gallery(false);
                                        }
                                    }

                                    dynamics.setLikeCount(0);
                                    dynamics.setReplycount(0);
                                    dynamics.setMyUser(BmobUser.getCurrentUser(MyUser.class));
                                    dynamics.addAll("picture", list1);

                                    dynamics.save(new SaveListener<String>() {

                                        @Override
                                        public void done(final String objectId, BmobException e) {
                                            if (e == null) {
                                                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                                                user.increment("dynamicsCount",1);
                                                user.update(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                 if (ifAddPic2Ac){
                                                     sendMessage(myUser.getUsername()+"请求添加图片到活动#"+myActivity.getTitle()+"#",
                                                     new BmobIMUserInfo(myActivity.getHost().getObjectId(),myActivity.getHost().getUsername(),""),
                                                             content+";"+myActivity.getTitle(),objectId+";"+myActivity.getObjectId());

                                                 }
                                                    }
                                                });
                                                Toast.makeText(SetDynamicsActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SetDynamicsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onProgress(int i, int i1, int i2, int i3) {
                               // Toast.makeText(SetDynamicsActivity.this, i1+"%", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int i, String s) {

                            }

                        });
                    }



                break;

            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
    }





    private void initActivity(){
        BmobQuery<MyActivity> query = new BmobQuery<>();
        BmobQuery<MyActivity> q1 = new BmobQuery<>();
        BmobQuery<MyActivity> q2 = new BmobQuery<>();

        List<BmobQuery<MyActivity>> queries = new ArrayList<>();
        q1.addWhereEqualTo("host",myUser);
        q2.addWhereContainsAll("joinUser",Arrays.asList(myUser.getObjectId()));

        queries.add(q1);
        queries.add(q2);
        query.order("-createdAt");
        query.or(queries);
        query.include("host[username|avatar]");
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                      if (e==null){

                          mActivityAdapter.setList(list);
                          mActivityAdapter.notifyDataSetChanged();
                      }
            }
        });

    }


    public void initPopView(Activity activity ) {
        parent = activity.getWindow().getDecorView();
        View popView = View.inflate( activity, R.layout.item_popup_select_activity, null);



        mActivityListView = popView.findViewById(R.id.activity_list);
        mActivityListView.setOnItemClickListener(itemListener);

        mActivityAdapter = new SelectActivityAdapter( activity);
        mActivityListView.setAdapter(mActivityAdapter);





        int width =  activity.getResources().getDisplayMetrics().widthPixels * 3 / 4;
        int height =  activity.getResources().getDisplayMetrics().heightPixels * 3 / 5;
        mPopWindow = new PopupWindow(popView, width, height);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mPopWindow.setBackgroundDrawable(dw);
        mPopWindow.setFocusable(true);
        mPopWindow.setTouchable(true);
        mPopWindow.setOutsideTouchable(true);//允许在外侧点击取消
    }



    public void showPopWindow() {
        mPopWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }










    /**
     * ListView Item点击事件
     */

    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            MyActivity activity = (MyActivity) mActivityListView.getItemAtPosition(position);
              ifAddPic2Ac=true;
              myActivity=activity;
              remove.setVisibility(View.VISIBLE);
              checkboxLayout.setVisibility(View.VISIBLE);
              activityText.setText(activity.getTitle());
              mPopWindow.dismiss();


        }

    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            mSelected.addAll(Matisse.obtainResult(data));
            adapter.notifyDataSetChanged();



        }

    }
    public void sendMessage(String content ,BmobIMUserInfo info,String title,String id) {

        if(!myUser.getObjectId().equals(info.getUserId())){

            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
            BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
            ActivityMessage msg = new ActivityMessage();
            msg.setContent(content);//给对方的一个留言信息
            Map<String, Object> map = new HashMap<>();
            map.put("currentuser", info.getUserId());
            map.put("userid", myUser.getObjectId());
            map.put("username",myUser.getUsername());
            map.put("useravatar",myUser.getAvatar());
            map.put("activityid",id);
            map.put("activityname",title);
            map.put("type","dynamics_picture");
            msg.setExtraMap(map);
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {//发送成功
                    } else {//发送失败
                        Toast.makeText(SetDynamicsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}
