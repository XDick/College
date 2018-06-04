package com.college.xdick.findme.ui.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.MessageFragmentStatePagerAdapter;
import com.college.xdick.findme.adapter.UserCenterFragmentStatePagerAdapter;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Fragment.MessageFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * Created by Administrator on 2018/5/31.
 */

public class UserCenterActivity extends AppCompatActivity {
    private ImageView background,avatar;
    private TextView username,follow_text,fanscount ,followingcount;
    private MyUser myUser =BmobUser.getCurrentUser(MyUser.class);
    private ViewPager mViewPager1;
    private TabLayout mTabLayout;
    private String[] tabTitle = {"动态", "活动"};//每个页面顶部标签的名字
    private UserCenterFragmentStatePagerAdapter adapter;
    private LinearLayout follow_layout,message_layout,statusbar;
    private MyUser nowUser;
    private CoordinatorLayout centerlayout;
    private Toolbar toolbar;
    private  List<String> followList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
      nowUser= (MyUser) getIntent().getSerializableExtra("USER");
        initView();
    }


    private void initView(){
        if (myUser.getFollowing()==null){
            followList  = new ArrayList<>();
        }else {
            List<String> list = Arrays.asList(myUser.getFollowing());
          followList = new ArrayList<>(list);
        }
        toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);
        message_layout = findViewById(R.id.center_message);
        follow_layout = findViewById(R.id.center_follow);
        follow_text  = findViewById(R.id.center_follow_text);
        statusbar = findViewById(R.id.center_statusbar);
        centerlayout = findViewById(R.id.center_layout);
        fanscount = findViewById(R.id.center_fans);
        followingcount = findViewById(R.id.center_following);



        try{
            followingcount.setText("关注: "+nowUser.getFollowing().length);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereContainsAll("following",Arrays.asList(nowUser.getObjectId()));
        query.setLimit(99999);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e==null){
                    fanscount.setText("粉丝: "+list.size());

                }
            }
        });

        if (nowUser.getUsername().equals(myUser.getUsername())){
            statusbar.setVisibility(View.GONE);
            setMargins(centerlayout,0,0,0,0);
        }

        if (Arrays.toString(myUser.getFollowing()).contains(nowUser.getObjectId())){
            follow_text.setText("已关注");}
            else {
            follow_text.setText("关注");
        }




            follow_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (followList.contains(nowUser.getObjectId())) {
                        myUser.removeAll("following", Arrays.asList(nowUser.getObjectId()));
                        myUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {

                                                followList.removeAll(Arrays.asList(nowUser.getObjectId()));

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        follow_text.setText("关注");

                                                    }
                                                });



                                } else {
                                    Toast.makeText(UserCenterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }


                        });

                    }else {
                        myUser.addUnique("following",nowUser.getObjectId());
                        myUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e==null){
                                                followList.add(nowUser.getObjectId());
                                                sendMessage(myUser.getUsername()+"关注了你",new BmobIMUserInfo(nowUser.getObjectId(),nowUser.getUsername(),nowUser.getAvatar()));


                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        follow_text.setText("已关注");
                                                    }
                                    });


                                }
                                else {
                                    Toast.makeText(UserCenterActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    }


            });








        background=findViewById(R.id.center_background);
        avatar=findViewById(R.id.center_useravatar);
        username= findViewById(R.id.center_username);
        Glide.with(this).load(nowUser.getAvatar()).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar);
        Glide.with(this).load(nowUser.getAvatar()).apply(bitmapTransform(new BlurTransformation(9, 7))
        ).into(background);
        message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatting(nowUser.getObjectId(),nowUser.getUsername(),nowUser.getAvatar());
            }
        });
        username.setText(nowUser.getUsername());
        mViewPager1 =  findViewById(R.id.mViewPager1);
        mTabLayout = findViewById(R.id.mTabLayout);
        adapter = new UserCenterFragmentStatePagerAdapter(getSupportFragmentManager(), tabTitle);

        for (int i = 0; i < tabTitle.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[i]));
        }
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager1.setAdapter(adapter);


        //在设置viewpager页面滑动监听时，创建TabLayout的滑动监听
        mViewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //在选中的顶部标签时，为viewpager设置currentitem
                mViewPager1.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


       private void startChatting(String id,String name,String avatar){

        BmobIMUserInfo info = new BmobIMUserInfo(id, name, avatar);
        BmobIMConversation conversationEntrance=  BmobIM.getInstance().startPrivateConversation( info,null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("c",conversationEntrance);
        Intent intent = new Intent(UserCenterActivity.this,
                ChatActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);


    }

    private void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
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

    public void sendMessage(String content ,BmobIMUserInfo info) {

        if(!myUser.getObjectId().equals(info.getUserId())){

            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
            BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
            ActivityMessage msg = new ActivityMessage();
            msg.setContent(content);//给对方的一个留言信息
            Map<String, Object> map = new HashMap<>();
            map.put("currentuser", info.getUserId());
            map.put("userid", myUser.getObjectId());
            map.put("username",myUser.getUsername());
            map.put("useravatar",myUser.getAvatar());
            map.put("activityid",null);
            map.put("activityname","新粉丝");
            map.put("type","user");
            msg.setExtraMap(map);
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {//发送成功

                    } else {//发送失败
                        Toast.makeText(UserCenterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
