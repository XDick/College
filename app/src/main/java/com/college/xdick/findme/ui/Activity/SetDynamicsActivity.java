package com.college.xdick.findme.ui.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
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


import com.college.xdick.findme.MyClass.MyGlideEngine;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.GridViewAddImagesAdapter;
import com.college.xdick.findme.adapter.SelectActivityAdapter;
import com.college.xdick.findme.bean.City;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.bean.Province;
import com.college.xdick.findme.bean.School;
import com.college.xdick.findme.util.FileUtil;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;


import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by Administrator on 2018/4/3.
 */

public class SetDynamicsActivity extends AppCompatActivity {
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
                        .maxSelectable(9)
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
                    Toast.makeText(SetDynamicsActivity.this, "正在发送...", Toast.LENGTH_SHORT).show();

                    String content = contentEdit.getText().toString();
                    Dynamics dynamics = new Dynamics();
                    if(myActivity!=null){
                        dynamics.setActivityTitle(myActivity.getTitle());
                        dynamics.setActivityCover(myActivity.getCover());
                        dynamics.setActivityTime(myActivity.getTime());
                        dynamics.setActivityHost(myActivity.getHostName());
                        dynamics.setActivityId(myActivity.getObjectId());
                    }
                    dynamics.setContent(content);
                    dynamics.setIfAdd2Gallery(false);
                    dynamics.setUserId(BmobUser.getCurrentUser().getObjectId());
                    dynamics.setUser(BmobUser.getCurrentUser().getUsername());
                    dynamics.setLikeCount(0);
                    dynamics.setReplycount(0);
                    finish();
                    dynamics.save(new SaveListener<String>() {

                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                                user.addUnique("dynamics",objectId);
                                user.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                                Toast.makeText(SetDynamicsActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                    finish();
                } else {
                    Toast.makeText(SetDynamicsActivity.this, "正在发送...", Toast.LENGTH_SHORT).show();

                    for (Uri u : mSelected) {
                        picPath.add(FileUtil.uriToFile(u, this));
                    }
                    finish();
                        BmobFile.uploadBatch(picPath.toArray(new String[picPath.size()]), new UploadBatchListener() {
                            @Override
                            public void onSuccess(List<BmobFile> list, List<String> list1) {

                                if (list1.size() == picPath.size()) {
                                    String content = contentEdit.getText().toString();
                                    Dynamics dynamics = new Dynamics();
                                    dynamics.setContent(content);
                                    if(myActivity!=null){
                                    dynamics.setActivityTitle(myActivity.getTitle());
                                        dynamics.setActivityCover(myActivity.getCover());
                                        dynamics.setActivityTime(myActivity.getTime());
                                        dynamics.setActivityHost(myActivity.getHostName());
                                        dynamics.setActivityId(myActivity.getObjectId());
                                    }
                                    dynamics.setIfAdd2Gallery(ifAddPic2Ac);
                                    dynamics.setLikeCount(0);
                                    dynamics.setReplycount(0);
                                    dynamics.setUserId(BmobUser.getCurrentUser().getObjectId());
                                    dynamics.setUser(BmobUser.getCurrentUser().getUsername());
                                    dynamics.addAll("picture", list1);

                                    dynamics.save(new SaveListener<String>() {

                                        @Override
                                        public void done(String objectId, BmobException e) {
                                            if (e == null) {
                                                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                                                user.addUnique("dynamics",objectId);
                                                user.update(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {

                                                    }
                                                });
                                                Toast.makeText(SetDynamicsActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                            }
                                        }
                                    });
                                    finish();
                                }
                            }

                            @Override
                            public void onProgress(int i, int i1, int i2, int i3) {

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
        String[] join = myUser.getJoin();
        List<BmobQuery<MyActivity>> queries = new ArrayList<>();
        q1.addWhereEqualTo("hostName",myUser.getUsername());
        q2.addWhereContainedIn("objectId",Arrays.asList(join));

        queries.add(q1);
        queries.add(q2);
        query.order("-createdAt");
        query.or(queries);
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


}
