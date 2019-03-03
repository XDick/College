package com.college.xdick.findme.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.AddTagBean;
import com.college.xdick.findme.bean.MainTagBean;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
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

public class MyInterestActivity extends BaseActivity {
    private   LabelsView myTagLabel,myTagLabelmain,myTagLabeladd,labelsView_sub;
    private   Button add,delete,createButton;
    private   LinearLayout layout,chooselayout ;
    private   TextView mytagText,createText;
    private   android.support.v7.widget.Toolbar toolbar;
    private   MyUser user = BmobUser.getCurrentUser(MyUser.class);
    private  List< String> selectTagList = new ArrayList<>();
    private  List<String> selectAddTagList= new ArrayList<>();
    private  List< MainTagBean> mainTagList = new ArrayList<>();
    private  List<String> subTagList= new ArrayList<>();
    private  List< AddTagBean> addTagList = new ArrayList<>();
    private String createdTag="";
    private EditText createE;
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

        createE = findViewById(R.id.tag_edit);
        createButton= findViewById(R.id.tag_button);
        createText= findViewById(R.id.search_tag_text);
        mytagText = findViewById(R.id.mytag_text);
        myTagLabel = findViewById(R.id.mytag_labels);
        myTagLabel.setLabels(Arrays.asList(user.getTag()));
        myTagLabelmain = findViewById(R.id.mytag_main_labels);
        labelsView_sub = findViewById(R.id.setac_sub_labels);


        myTagLabelmain.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                if (isSelect) {
                    subTagList.clear();
                    for (String tag : Arrays.asList(mainTagList.get(position).getSubTag())) {

                        if(user.getTag()!=null) {
                            if (Arrays.toString(user.getTag()).contains(tag)) {
                                continue;
                            }
                            subTagList.add(tag);}
                    }

                    labelsView_sub.setLabels(subTagList);

                } else {
                    labelsView_sub.setLabels(null);
                }

            }
        });

        labelsView_sub.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
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
                    MyUser myUser1 = new MyUser();
                    myUser1.setObjectId(user.getObjectId());
                    myUser1.removeAll("tag", selectTagList);
                    myUser1.update(new UpdateListener() {
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

               if (selectAddTagList.isEmpty()&&createdTag.equals("")){

                   if (user.isGod()){
                       Toast.makeText(MyInterestActivity.this,"欢迎编辑标签，我的主人^_^",Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(MyInterestActivity.this,InterestActivity.class));
                   }
                   else{
                       Toast.makeText(MyInterestActivity.this,"请选择标签",Toast.LENGTH_SHORT).show();
                   }
               }
               else {
               final MyUser myUser1=new MyUser();
               myUser1.setObjectId(user.getObjectId());
                    if (createdTag.equals("")){
                        myUser1.addAll("tag", selectAddTagList);
                        myUser1.update(new UpdateListener() {
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
                    else {

                        BmobQuery<AddTagBean> query = new BmobQuery<AddTagBean>();
                        query.addWhereEqualTo("addTag", createText.getText().toString());
                        query.findObjects(new FindListener<AddTagBean>() {
                            @Override
                            public void done(List<AddTagBean> list, BmobException e) {
                                if (e==null){
                                    selectAddTagList.add(list.get(0).getAddTag());
                                    myUser1.addAll("tag", selectAddTagList);
                                    myUser1.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            selectAddTagList.clear();
                                            createdTag="";
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    user = BmobUser.getCurrentUser(MyUser.class);
                                                    myTagLabel.setLabels(Arrays.asList(user.getTag()));
                                                    initLabels();
                                                    createText.setVisibility(View.VISIBLE);
                                                    createText.setText("搜索标签");
                                                    createE.setVisibility(View.GONE);
                                                    createButton.setVisibility(View.GONE);
                                                    createE.setText("");
                                                    Toast.makeText(MyInterestActivity.this,"添加成功",Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }


                                    });




                                       list.get(0).UserCountAdd1();
                                       list.get(0).update( list.get(0).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                            }
                                        });

                                }
                                else {

                                    Toast.makeText(MyInterestActivity.this,"无此标签，可在发布活动时创建",Toast.LENGTH_SHORT).show();

                                }

                            } });
                    }




            }
            }

        });


        createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!createText.getText().equals("搜索标签")) {
                    createdTag="";
                }
                createText.setVisibility(View.GONE);
                createE.setVisibility(View.VISIBLE);
                createButton.setVisibility(View.VISIBLE);

                createE.setFocusable(true);
                createE.setFocusableInTouchMode(true);
                createE.requestFocus();
                createE.findFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(createE, 0);
            }


        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = createE.getText().toString();
                if (tag.equals("")) {
                    createText.setText("搜索标签");
                    createdTag="";
                }
                else {
                    createdTag=tag;
                    createText.setText(tag);
                }

                createText.setVisibility(View.VISIBLE);
                createE.setVisibility(View.GONE);
                createButton.setVisibility(View.GONE);

            }
        });
    }


    private void initLabels(){
       addTagList.clear();
       mainTagList.clear();
       subTagList.clear();
        BmobQuery<AddTagBean> query2 = new BmobQuery<AddTagBean>();
        query2.setLimit(50);
        query2.order("-updatedAt");
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
        query.findObjects(new FindListener<MainTagBean>() {


            @Override
            public void done(List<MainTagBean> object, BmobException e) {
                if (e == null) {
                    mainTagList.addAll(object);
                    myTagLabelmain.setLabels(object, new LabelsView.LabelTextProvider<MainTagBean>() {
                        @Override
                        public CharSequence getLabelText(TextView label, int position, MainTagBean data) {
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
