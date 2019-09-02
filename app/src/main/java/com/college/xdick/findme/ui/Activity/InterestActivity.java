package com.college.xdick.findme.ui.Activity;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
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
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.util.AppManager;
import com.donkingliang.labels.LabelsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/5/9.
 */

public class InterestActivity extends BaseActivity {
   MyUser user = BmobUser.getCurrentUser(MyUser.class);
   List< String> selectTagList = new ArrayList<>();
    List<MainTagBean> mainTagBeanList = new ArrayList<>();
    @Override


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        final LabelsView labelsView =  findViewById(R.id.main_labels);
        final LabelsView labelsView2 = findViewById(R.id.add_labels);
        final LabelsView labelsView_sub1,labelsView_sub2 ,labelsView_sub3 ,
                labelsView_sub4 ;
        final List<LabelsView> labelsViewList = new ArrayList<>();
        labelsView_sub1= findViewById(R.id.setac_sub_labels1);
        labelsView_sub2= findViewById(R.id.setac_sub_labels2);
        labelsView_sub3= findViewById(R.id.setac_sub_labels3);
        labelsView_sub4= findViewById(R.id.setac_sub_labels4);

        labelsViewList.add(labelsView_sub1);
        labelsViewList.add(labelsView_sub2);
        labelsViewList.add(labelsView_sub3);
        labelsViewList.add(labelsView_sub4);

        Button confirm = findViewById(R.id.interest_confirm_button);




        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectTagList.isEmpty()){
                    Toast.makeText(InterestActivity.this,"请至少选择一个标签o(╥﹏╥)o",Toast.LENGTH_SHORT).show();
                    return;
                }
                MyUser myUser1 = new MyUser();
                myUser1.setObjectId(user.getObjectId());
                myUser1.addAllUnique("tag", selectTagList);
                myUser1.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        Toast.makeText(InterestActivity.this,
                                "提示:部分手机(如OV手机)需要系统里打开本应用通知权限才能接收消息o(╥﹏╥)o",
                                Toast.LENGTH_LONG).show();

                        AppManager.getAppManager().finishAllActivity();
                        startActivity(new Intent(InterestActivity.this, MainActivity.class));


                    }
                });
            }
        });


                  try {
                      if (user.isGod()) {
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
                  }
                  catch (Exception e){
                      e.printStackTrace();
                  }



                    BmobQuery<MainTagBean> query = new BmobQuery<MainTagBean>();
                    query.order("order");
                    query.findObjects(new FindListener<MainTagBean>() {


                        @Override
                        public void done(List<MainTagBean> object, BmobException e) {
                            if (e == null) {
                                Log.d("","遍历标签1");
                                mainTagBeanList.addAll(object);
                                labelsView.setLabels(object, new LabelsView.LabelTextProvider<MainTagBean>() {
                                    @Override
                                    public CharSequence getLabelText(TextView label, int position, MainTagBean data) {
                                        //根据data和position返回label需要显示的数据。
                                        return data.getMainTag();
                                    }
                                });

                                for (MainTagBean bean:object){
                                    labelsViewList.get(bean.getOrder()-1).setLabels(Arrays.asList(bean.getSubTag()));
                                }

                            } else {

                            }




                        }
                    });


                    BmobQuery<AddTagBean> query2 = new BmobQuery<AddTagBean>();
                    query2.setLimit(50);
                    query2.order("-updatedAt");
                    query2.findObjects(new FindListener<AddTagBean>() {
                        @Override
                        public void done(List<AddTagBean> object, BmobException e) {
                            if (e == null) {


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



                    labelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                        @Override
                        public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                            if (isSelect) {

                               labelsViewList.get(position).setVisibility(View.VISIBLE);
                              //  Log.d("TAG", "数据" + label.getText().toString());
                            }else {
                                labelsViewList.get(position).setVisibility(View.GONE);

                            }

                        }
                    });


        labelsView_sub1.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
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
        labelsView_sub2.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
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
        labelsView_sub3.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
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
        labelsView_sub4.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
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
