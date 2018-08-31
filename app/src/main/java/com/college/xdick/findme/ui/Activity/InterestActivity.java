package com.college.xdick.findme.ui.Activity;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.AddTagBean;
import com.college.xdick.findme.bean.MainTagBean;
import com.college.xdick.findme.bean.MyUser;
import com.donkingliang.labels.LabelsView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/5/9.
 */

public class InterestActivity extends AppCompatActivity {
   MyUser user = BmobUser.getCurrentUser(MyUser.class);
   List< String> selectTagList = new ArrayList<>();
    @Override


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        final LabelsView labelsView =  findViewById(R.id.main_labels);
        final LabelsView labelsView2 = findViewById(R.id.add_labels);
        Button confirm = findViewById(R.id.interest_confirm_button);




        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.addAllUnique("tag", selectTagList);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                       finish();
                        Toast.makeText(InterestActivity.this,"成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



                    if (user.getUsername().equals("叉地克")) {
                        final EditText editText = findViewById(R.id.tag_edit);
                        Button addmain = findViewById(R.id.addmain);
                        final Button addadd = findViewById(R.id.addadd);
                        Button delmain = findViewById(R.id.deletemain);
                        Button deladd = findViewById(R.id.deleteadd);


                        editText.setVisibility(View.VISIBLE);
                        addadd.setVisibility(View.VISIBLE);
                        addmain.setVisibility(View.VISIBLE);
                        deladd.setVisibility(View.VISIBLE);
                        delmain.setVisibility(View.VISIBLE);


                        addmain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tag = editText.getText().toString();
                                MainTagBean maintag = new MainTagBean(tag);
                                maintag.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        Toast.makeText(InterestActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        addadd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tag = editText.getText().toString();
                                AddTagBean addtag = new AddTagBean(tag);
                                addtag.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        Toast.makeText(InterestActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        delmain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tag = editText.getText().toString();
                                BmobQuery query = new BmobQuery<MainTagBean>();
                                query.addWhereEqualTo("mainTag", tag);
                                query.findObjects(new FindListener<MainTagBean>() {
                                    @Override
                                    public void done(List<MainTagBean> object, BmobException e) {

                                        for (MainTagBean mainTagBean : object) {

                                            mainTagBean.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    Toast.makeText(InterestActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                });

                            }
                        });


                        deladd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tag = editText.getText().toString();
                                BmobQuery query = new BmobQuery<AddTagBean>();
                                query.addWhereEqualTo("addTag", tag);
                                query.findObjects(new FindListener<AddTagBean>() {
                                    @Override
                                    public void done(List<AddTagBean> object, BmobException e) {

                                        for (AddTagBean mainTagBean : object) {

                                            mainTagBean.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    Toast.makeText(InterestActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                });

                            }
                        });
                    }

//LabelsView可以设置任何类型的数据，而不仅仅是String。


                    BmobQuery<MainTagBean> query = new BmobQuery<MainTagBean>();

//返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(999);
//执行查询方法
                    query.findObjects(new FindListener<MainTagBean>() {


                        @Override
                        public void done(List<MainTagBean> object, BmobException e) {
                            if (e == null) {
                                Log.d("","遍历标签1");

                                labelsView.setLabels(object, new LabelsView.LabelTextProvider<MainTagBean>() {
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


                    BmobQuery<AddTagBean> query2 = new BmobQuery<AddTagBean>();

//返回50条数据，如果不加上这条语句，默认返回10条数据
                    query2.setLimit(999);
//执行查询方法
                    query2.findObjects(new FindListener<AddTagBean>() {
                        @Override
                        public void done(List<AddTagBean> object, BmobException e) {
                            if (e == null) {

                                Collections.sort(object);
                                Collections.reverse(object); // 倒序排列
                                labelsView2.setLabels(object, new LabelsView.LabelTextProvider<AddTagBean>() {
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


//标签的点击监听

//标签的选中监听
                    labelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
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



//标签的选中监听
                    labelsView2.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
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

                }


    @Override
    public void onBackPressed() {


    }


}
