package com.college.xdick.college.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.college.xdick.college.R;
import com.college.xdick.college.bean.MyCircleBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/10.
 */

public class SearchFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_search,container,false);

        MyCircleBanner mBanner = view.findViewById(R.id.banner);

// 设置数据源
        List<String> mInfos = new ArrayList<>();
        mInfos.add("http://img5.imgtn.bdimg.com/it/u=987578735,3398275072&fm=27&gp=0.jpg");
        mInfos.add("http://i1.sinaimg.cn/ent/y/2014-07-01/U11075P28T3D4167845F329DT20140701154641.jpg");
        mInfos.add("http://img2.imgtn.bdimg.com/it/u=1218940759,4287671795&fm=27&gp=0.jpg");
        // 使用mBanner的接口，直接自动播放
        mBanner.play(mInfos);

        return view;
    }
}
