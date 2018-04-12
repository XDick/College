package com.college.xdick.college.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.college.xdick.college.R;

/**
 * Created by Administrator on 2018/4/12.
 */

public class StartactivityFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view =inflater.inflate(R.layout.fragment_ac_main,container,false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_activity);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                view.findViewById(R.id.collapsing_toolbar_activity);
        ImageView activityImageView = view.findViewById(R.id.activity_image_view);
        TextView activityContentText =  view.findViewById(R.id.activity_content_text);
        TextView activityHostText =  view.findViewById(R.id.activity_host_text);
        TextView activityTimeText =  view.findViewById(R.id.activity_time_text);
        TextView activityPlaceText =  view.findViewById(R.id.activity_place_text);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent =getActivity().getIntent();
        String activityTitle = intent.getStringExtra("ACTIVITY_TITLE");
        String activityHost = intent.getStringExtra("ACTIVITY_HOST");
        String activityCover = intent.getStringExtra("ACTIVITY_COVER");
        String activityContent = intent.getStringExtra("ACTIVITY_CONTENT");
        String activityTime = intent.getStringExtra("ACTIVITY_TIME");
        String activityPlace = intent.getStringExtra("ACTIVITY_PLACE");



       // collapsingToolbar.setExpandedTitleGravity(Gravity.BOTTOM|Gravity.CENTER);//设置收缩后标题的位置
        collapsingToolbar.setTitle(activityTitle);
        Glide.with(this).load(activityCover).into(activityImageView);
        activityContentText.setText(activityContent);
        activityTimeText.setText("时间:"+activityTime);
        activityPlaceText.setText("地点:"+activityPlace);
        activityHostText.setText("发起人:"+activityHost);







       return view;
    }
}
