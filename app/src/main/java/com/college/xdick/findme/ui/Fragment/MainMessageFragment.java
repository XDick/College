package com.college.xdick.findme.ui.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.MessageFragmentStatePagerAdapter;
import com.college.xdick.findme.adapter.MyFragmentStatePagerAdapter;
import com.college.xdick.findme.bean.ActivityMessageBean;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;
import com.college.xdick.findme.util.AppManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;
import jp.wasabeef.glide.transformations.BlurTransformation;
import q.rorbin.badgeview.QBadgeView;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Administrator on 2018/5/23.
 */

public class MainMessageFragment extends BaseFragment implements MessageListHandler {

    private ViewPager mViewPager1;
    private TabLayout mTabLayout;
    private String[] tabTitle = {"私信", "通知"};//每个页面顶部标签的名字
    private QBadgeView badgeView;
    private int position=0;
    private AlertDialog.Builder builder=null ;
    private AlertDialog dialog2=null;

    private MessageFragment messageFragment;
    private PrivateConversationFragment privateConversationFragment;

    private MessageFragmentStatePagerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_main_message,container,false);
        initView();
        EventBus.getDefault().register(this);
        return  rootView;
    }

    private void initView(){


        ImageView background = rootView.findViewById(R.id.background);
        Glide.with(this).load(R.drawable.findme_background)
                .apply(bitmapTransform(new BlurTransformation(4, 3)))
                .into(background);
        Button clearButton = rootView.findViewById(R.id.clear_button);
       clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               initDialog2();
            }
        });

        mViewPager1 = rootView.findViewById(R.id.mViewPager1);
        mTabLayout =  rootView.findViewById(R.id.mTabLayout);
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
       messageFragment=((MessageFragment)adapter.instantiateItem(mViewPager1,1));
       privateConversationFragment=(( PrivateConversationFragment)adapter.instantiateItem(mViewPager1,0));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //在选中的顶部标签时，为viewpager设置currentitem
                mViewPager1.setCurrentItem(tab.getPosition());
                position=tab.getPosition();
                if (position==1){
                messageFragment.read();
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

    private void initDialog2() {

        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("清空记录");

        if (position==0){
            builder.setMessage("确定清空私信吗？");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog2.dismiss();
                }
            });
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    privateConversationFragment.deleteAllConversation();
                    dialog2.dismiss();
                }
            });
        }
        else if (position==1){
            builder.setMessage("确定清空通知吗？");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog2.dismiss();
                }
            });
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    messageFragment.deleteAll();
                    dialog2.dismiss();
                }
            });
        }



        dialog2 = builder.create();
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(final ReadEvent readEvent) {
        setBadgeItem();
    }
}
