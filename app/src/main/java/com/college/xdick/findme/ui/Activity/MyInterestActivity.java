package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.AddTagBean;
import com.college.xdick.findme.bean.MainTagBean;
import com.college.xdick.findme.bean.MyUser;
import com.donkingliang.labels.LabelsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/5/11.
 */

public class MyInterestActivity extends AppCompatActivity{
       LabelsView myTagLabel,myTagLabelmain,myTagLabeladd;
       Button add,delete;
       LinearLayout layout ,chooselayout;
       TextView mytagText;
      android.support.v7.widget.Toolbar toolbar;
       MyUser user = BmobUser.getCurrentUser(MyUser.class);
    List< String> selectTagList = new ArrayList<>();
    List<String> selectAddTagList= new ArrayList<>();
    List< MainTagBean> mainTagList = new ArrayList<>();
    List< AddTagBean> addTagList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinterest);
        initView();
        initLabels();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_mytag);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);


        mytagText = findViewById(R.id.mytag_text);
        myTagLabel = findViewById(R.id.mytag_labels);
        myTagLabel.setLabels(Arrays.asList(user.getTag()));
        myTagLabelmain = findViewById(R.id.mytag_main_labels);


        myTagLabelmain.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                if (isSelect) {
                    selectAddTagList.add(label.getText().toString());
                } else {
                    selectAddTagList.remove(label.getText().toString());
                }

            }
        });

        myTagLabeladd = findViewById(R.id.mytag_add_labels);


        myTagLabeladd.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                if (isSelect) {
                    selectAddTagList.add(label.getText().toString());

                } else {
                    selectAddTagList.remove(label.getText().toString());
                }

            }
        });

        myTagLabel.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                if (isSelect) {
                    selectTagList.add(label.getText().toString());
                    Log.d("TAG", "数据" + label.getText().toString());
                } else {
                    selectTagList.remove(label.getText().toString());
                }

            }
        });
        delete = findViewById(R.id.delete_mytag_button);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectTagList.isEmpty()){

                    Toast.makeText(MyInterestActivity.this,"请选择标签",Toast.LENGTH_SHORT).show();
                }
                else{

                user.removeAll("tag", selectTagList);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        selectTagList.clear();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                user = BmobUser.getCurrentUser(MyUser.class);
                                myTagLabel.setLabels(Arrays.asList(user.getTag()));
                                initLabels();
                                Toast.makeText(MyInterestActivity.this,"删除成功",Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });


            }
            }
        });

        layout = findViewById(R.id.mytag_show_lable_layout);
        chooselayout = findViewById(R.id.mytag_show_lable);
        chooselayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mytagText.getText().equals("添加标签")){
                    layout.setVisibility(View.VISIBLE);
                    mytagText.setText("收 起");
                }

                else {
                   layout.setVisibility(View.GONE);
                    mytagText.setText("添加标签");

                }

            }

        });




        add = findViewById(R.id.mytag_add_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (selectAddTagList.isEmpty()){

                   if (user.getUsername().equals("叉地克")){
                       Toast.makeText(MyInterestActivity.this,"欢迎编辑标签，我的主人^_^",Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(MyInterestActivity.this,InterestActivity.class));
                   }
                   else{
                       Toast.makeText(MyInterestActivity.this,"请选择标签",Toast.LENGTH_SHORT).show();
                   }
               }
               else {

                user.addAll("tag", selectAddTagList);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        selectAddTagList.clear();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                user = BmobUser.getCurrentUser(MyUser.class);
                                myTagLabel.setLabels(Arrays.asList(user.getTag()));
                                initLabels();
                                Toast.makeText(MyInterestActivity.this,"添加成功",Toast.LENGTH_SHORT).show();

                            }
                        });

                    }


                });
            }
            }

        });
    }


    private void initLabels(){
       addTagList.clear();
       mainTagList.clear();
        BmobQuery<AddTagBean> query2 = new BmobQuery<AddTagBean>();

//返回50条数据，如果不加上这条语句，默认返回10条数据
        query2.setLimit(999);
//执行查询方法
        query2.findObjects(new FindListener<AddTagBean>() {
            @Override
            public void done(List<AddTagBean> object, BmobException e) {
                if (e == null) {

                    for (AddTagBean tag : object){
                       if(user.getTag()!=null) {
                           if (Arrays.toString(user.getTag()).contains(tag.getAddTag())) {
                              continue;
                           }
                       }
                        addTagList.add(tag);
                    }

                    Collections.sort(addTagList);
                    Collections.reverse(addTagList); // 倒序排列

                    myTagLabeladd.setLabels(addTagList, new LabelsView.LabelTextProvider<AddTagBean>() {
                        @Override
                        public CharSequence getLabelText(TextView label, int position, AddTagBean data) {
                            //根据data和position返回label需要显示的数据。
                            return data.getAddTag();
                        }
                    });
                } else {
                }
            }
        });

        BmobQuery<MainTagBean> query = new BmobQuery<MainTagBean>();

//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(999);
//执行查询方法
        query.findObjects(new FindListener<MainTagBean>() {


            @Override
            public void done(List<MainTagBean> object, BmobException e) {
                if (e == null) {
                    Log.d("", "遍历标签1");
                    for (MainTagBean tag : object) {

                        if(user.getTag()!=null) {
                        if (Arrays.toString(user.getTag()).contains(tag.getMainTag())) {
                                continue;
                        }
                            mainTagList.add(tag);}
                    }
                    myTagLabelmain.setLabels(mainTagList, new LabelsView.LabelTextProvider<MainTagBean>() {
                        @Override
                        public CharSequence getLabelText(TextView label, int position, MainTagBean data) {
                            //根据data和position返回label需要显示的数据。
                            return data.getMainTag();
                        }
                    });

                } else {

                }
            }
        });

    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case  android.R.id.home:
            {
                finish();
                break;
            }


            default:
                break;
        }

        return true;
    }

}