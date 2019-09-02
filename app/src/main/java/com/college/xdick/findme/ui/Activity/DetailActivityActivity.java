package com.college.xdick.findme.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.MyFragmentStatePagerAdapter;
import com.college.xdick.findme.bean.MainTagBean;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.ui.Fragment.ActivityFollowFragment;
import com.college.xdick.findme.ui.Fragment.ActivitygpsFragment;
import com.college.xdick.findme.ui.Fragment.ActivityschoolFragment;
import com.college.xdick.findme.ui.Fragment.GoodsFragment;
import com.donkingliang.labels.LabelsView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.b.V;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class DetailActivityActivity extends BaseActivity {
    private ViewPager mViewPager1;
    private TabLayout mTabLayout;
    private String[] tabTitle = {"商城","同校", "同城","关注" };
    private Toolbar toolbar;
    private ImageView background;
    private MyFragmentStatePagerAdapter adapter;
    private LinearLayout sortLayout;
    private TextView title;
    private boolean ifSort=false;
    public MainTagBean tagBean=null;
    private int picPath;
    private String titleText="泛觅";
    private LabelsView labelsView;
    private FloatingActionButton floatingActionButton,floatingActionButton2;
    String subTag=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);


            tagBean = (MainTagBean)getIntent().getSerializableExtra("TAG");
            if (tagBean!=null){
                labelsView = findViewById(R.id.labels);
                titleText=tagBean.getMainTag();
                List<String>labels =new ArrayList<>();
                labels.add("全部");
                labels.addAll(Arrays.asList(tagBean.getSubTag()));

                labelsView.setLabels(labels);
                labelsView.setSelects(0);
                labelsView.setSelectType(LabelsView.SelectType.SINGLE_IRREVOCABLY);


                labelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                    @Override
                    public void onLabelSelectChange(TextView label, Object data, boolean isSelect,
                                                    int position) {
                        if (isSelect){

                            subTag=label.getText().toString().equals("全部")?null:label.getText().toString();
                            refreshFragment(ifSort,subTag);
                        }


                    }


                });


            }

            picPath = getIntent().getIntExtra("IMG",R.drawable.findme_all_activity);


        initView();


    }



    private void initView(){
        final MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        floatingActionButton = findViewById(R.id.floatactionbutton);
        floatingActionButton2 = findViewById(R.id.floatactionbutton2);
        if (bmobUser.isGod()){
            floatingActionButton2.setVisibility(View.VISIBLE);
            floatingActionButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivity(new Intent(DetailActivityActivity.this,
                           SetGoodsActivity.class));
                }
            });
        }



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(new Date(bmobTime));
                        if (bmobUser != null) {

                            if (bmobUser.getGps()==null){
                                Toast.makeText(DetailActivityActivity.this, "请先定位", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            if (!date.equals(bmobUser.getSetAcTime()) || bmobUser.isGod()) {

                                Intent intent = new Intent(DetailActivityActivity.this, SetActivitiyActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(DetailActivityActivity.this, "今天已经发过活动了o(╥﹏╥)o", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            startActivity(new Intent(DetailActivityActivity.this,LoginActivity.class));
                            finish();
                            // Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                        }
                    }


        });
        sortLayout = findViewById(R.id.sort_layout);
        final TextView sortText = findViewById(R.id.sort_text);
        sortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifSort){
                    refreshFragment(false,subTag);
                    ifSort=false;
                    sortText.setText("发布排序");
                }
                else {
                    refreshFragment(true,subTag);
                    ifSort=true;
                    sortText.setText("日期排序");

                }
            }
        });



        title = findViewById(R.id.title);
        title.setText(titleText);
        background=findViewById(R.id.background);
        Glide.with(this).load(picPath).apply(bitmapTransform(new BlurTransformation(5, 3))
        ).into(background);
        mViewPager1 =  findViewById(R.id.mViewPager1);
        mTabLayout = findViewById(R.id.mTabLayout);
        toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);

        for (int i = 0; i < tabTitle.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[i]));
        }
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new MyFragmentStatePagerAdapter(getSupportFragmentManager(), tabTitle);
        mViewPager1.setAdapter(adapter);
        mViewPager1 .setOffscreenPageLimit(3);


        mViewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager1.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager1.setCurrentItem(1);


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
    private void refreshFragment (boolean ifsort,String subTag) {


       GoodsFragment fragment0 = ((  GoodsFragment) adapter.instantiateItem(mViewPager1, 0));


        ActivityFollowFragment fragment1 = ((ActivityFollowFragment) adapter.instantiateItem(mViewPager1, 3));
                fragment1.setSORT(ifsort);

                ActivityschoolFragment fragment2 = ((ActivityschoolFragment) adapter.instantiateItem(mViewPager1, 1));
                fragment2.setSORT(ifsort);

                ActivitygpsFragment fragment3 = ((ActivitygpsFragment) adapter.instantiateItem(mViewPager1, 2));
                fragment3.setSORT(ifsort);

                    fragment0.setSubTag(subTag);
                    fragment1.setSubTag(subTag);
                    fragment2.setSubTag(subTag);
                    fragment3.setSubTag(subTag);

               fragment0.refresh();
               fragment1.refresh();
                fragment2.refresh();
                fragment3.refresh();
    }




}
