package com.college.xdick.findme.ui.Fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.MessageFragmentStatePagerAdapter;
import com.college.xdick.findme.adapter.MyFragmentStatePagerAdapter;
import com.college.xdick.findme.bean.ActivityMessageBean;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;

import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by Administrator on 2018/5/23.
 */

public class MainMessageFragment extends Fragment implements MessageListHandler {
     View rootView;
    private ViewPager mViewPager1;
    private TabLayout mTabLayout;
    private String[] tabTitle = {"私信", "通知"};//每个页面顶部标签的名字
    private QBadgeView badgeView;

    private MessageFragmentStatePagerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_main_message,container,false);
        initView();

        return  rootView;
    }

    private void initView(){

        mViewPager1 = (ViewPager) rootView.findViewById(R.id.mViewPager1);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.mTabLayout);
        adapter = new MessageFragmentStatePagerAdapter(getChildFragmentManager(), tabTitle);

        for (int i = 0; i < tabTitle.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[i]));
        }
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        badgeView=  new QBadgeView(getContext());
        badgeView.bindTarget(mTabLayout)
                .setBadgeGravity(Gravity.CENTER|Gravity.END)
                .setShowShadow(false)
                .setBadgeBackgroundColor(getResources().getColor(R.color.white_red))
                .setGravityOffset(30,true);


        //这里注意的是，因为我是在fragment中创建MyFragmentStatePagerAdapter，所以要传getChildFragmentManager()
        mViewPager1.setAdapter(adapter);


        //在设置viewpager页面滑动监听时，创建TabLayout的滑动监听
        mViewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        final MessageFragment fragment=((MessageFragment)adapter.instantiateItem(mViewPager1,1));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //在选中的顶部标签时，为viewpager设置currentitem
                mViewPager1.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==1){
                fragment.read();
                    ((MainActivity)getContext()).setBadgeItem();
                    setBadgeItem();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
                setBadgeItem();
    }

    //TODO 消息接收：8.4、通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        setBadgeItem();
    }

    @Override
    public void onResume() {
        super.onResume();
        setBadgeItem();
    }

    public void setBadgeItem(){
        if (BmobUser.getCurrentUser()!=null)   {
            List<ActivityMessageBean> list = DataSupport.where("currentuserId =? and ifCheck =?"
                    , BmobUser.getCurrentUser().getObjectId(), "false").find(ActivityMessageBean.class);
            int msgNum  = list.size();


            if( msgNum ==0){
                badgeView.hide(true);
                badgeView.setBadgeNumber(msgNum);
            } else{
                badgeView.hide(false);
                badgeView.setBadgeNumber(msgNum);
            }
        }
    }
}
