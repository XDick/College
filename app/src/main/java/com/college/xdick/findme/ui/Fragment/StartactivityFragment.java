package com.college.xdick.findme.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.CommentAdapter;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/4/12.
 */

public class StartactivityFragment extends Fragment{


    private String activityId;
    private ImageView avatar1,avatar2,avatar3,avatar4,avatar5,avatar6;
    private TextView joincount,commentcount,loadmore_text;
    private ImageView[] avatar;
    private Button joinButton;
    private int count=0,joinnum=0;
    private CommentAdapter adapter;
    public List<Comment> commentList = new ArrayList<>();
    private View rootView;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private boolean ifJoin;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       rootView =inflater.inflate(R.layout.fragment_ac_main,container,false);

        Toolbar toolbar =rootView.findViewById(R.id.toolbar_activity);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                rootView.findViewById(R.id.collapsing_toolbar_activity);


        avatar1=rootView.findViewById(R.id.ac_main_avatar1);
        avatar2=rootView.findViewById(R.id.ac_main_avatar2);
        avatar3=rootView.findViewById(R.id.ac_main_avatar3);
        avatar4=rootView.findViewById(R.id.ac_main_avatar4);
        avatar5=rootView.findViewById(R.id.ac_main_avatar5);
         avatar6=rootView.findViewById(R.id.ac_main_avatar6);
        avatar =new ImageView[6];
        avatar[0]=avatar1;
        avatar[1]=avatar2;
        avatar[2]=avatar3;
        avatar[3]=avatar4;
        avatar[4]=avatar5;
        avatar[5]=avatar6;


        final LinearLayout loadmore=  rootView.findViewById(R.id.activity_loadmore);
        ImageView activityImageView = rootView.findViewById(R.id.activity_image_view);
        final TextView activityContentText =  rootView.findViewById(R.id.activity_content_text);
        TextView activityHostText =  rootView.findViewById(R.id.activity_host_text);
        TextView activityTimeText =  rootView.findViewById(R.id.activity_time_text);
        TextView activityPlaceText =  rootView.findViewById(R.id.activity_place_text);
        commentcount = rootView.findViewById(R.id.activity_comment_count);
        joincount = rootView.findViewById(R.id.ac_main_joincount);
        joinButton = rootView.findViewById(R.id.join_ac_button);
        loadmore_text = rootView.findViewById(R.id.activity_loadmore_text);


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent =getActivity().getIntent();
        String activityTitle = intent.getStringExtra("ACTIVITY_TITLE");
        final String activityHost = intent.getStringExtra("ACTIVITY_HOST");
        String activityCover = intent.getStringExtra("ACTIVITY_COVER");
        String activityContent = intent.getStringExtra("ACTIVITY_CONTENT");
        String activityTime = intent.getStringExtra("ACTIVITY_TIME");
        String activityPlace = intent.getStringExtra("ACTIVITY_PLACE");
        final String hostID=  intent.getStringExtra("HOST_ID");
        activityId = intent.getStringExtra("ACTIVITY_ID");


        ifJoin();
        initJoin();
        initData();


       // collapsingToolbar.setExpandedTitleGravity(Gravity.BOTTOM|Gravity.CENTER);//设置收缩后标题的位置
        collapsingToolbar.setTitle(activityTitle);
        Glide.with(this).load(activityCover).into(activityImageView);
        activityContentText.setText(activityContent);
        activityTimeText.setText("时间:"+activityTime);
        activityPlaceText.setText("地点:"+activityPlace);
        activityHostText.setText("发起人:"+activityHost);





         activityHostText.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startChatting(hostID,activityHost);
             }
         });


        loadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(loadmore_text.getText().equals("加 载 更 多")){
                activityContentText.setSingleLine(false);
                loadmore_text.setText("收 起");}

                else {
                   activityContentText.setMaxLines(5);
                   loadmore_text.setText("加 载 更 多");

               }

            }
        });



        joinButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

                      if(!ifJoin){


                          bmobUser.add("join",activityId);
                          bmobUser.update(new UpdateListener() {
                              @Override
                              public void done(BmobException e) {
                                  if (e == null) {

                                                  ifJoin=true;

                                           getActivity().runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                                                  joincount.setText(++joinnum+"");
                                                   joinButton.setText("取消加入");
                                               }
                                           });

                                                  Toast.makeText(getContext(), "成功加入", Toast.LENGTH_SHORT).show();
                                              }

                                              else {
                                             Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                                  Toast.makeText(getContext(),"失败",Toast.LENGTH_SHORT).show();
                                              }
                                          }
                                      });

                      }

                      else
                      {
                          bmobUser.removeAll("join", Arrays.asList(activityId));
                          bmobUser.update(new UpdateListener() {
                              @Override
                              public void done(BmobException e) {

                                                  if(e==null){

                                                      getActivity().runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              joinButton.setText("加 入!");
                                                              joincount.setText(--joinnum+"");
                                                          }
                                                      });


                                                          ifJoin = false;
                                                          Toast.makeText(getContext(), "取消加入", Toast.LENGTH_SHORT).show();
                                                      }

                                           else {
                                            Toast.makeText(getContext(),"失败",Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });
                              }
                  }
      });

       return rootView;
    }





public  void initJoin(){
    BmobQuery<MyUser> query = new BmobQuery<MyUser>();
    query.addWhereContainsAll("join", Arrays.asList(activityId));
    query.setLimit(99999);
    query.findObjects(new FindListener<MyUser>() {
        @Override
        public void done(List<MyUser> list, BmobException e) {
            if(e==null){
                  joinnum=list.size();
                joincount.setText(joinnum+"");
                for(final MyUser user :list){
                    avatar[count].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                           String id = user.getObjectId();
                           String name = user.getUsername();
                           startChatting(id ,name);

                        }
                    });
                    Glide.with(getContext()).load(user.getAvatar()).into(avatar[count]);

                    if(count==5){
                        break;
                    }
                    count++;
                }
            }
            else
            {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }

    });




}


    private void initRecyclerView(){

        RecyclerView recyclerView = rootView.findViewById(R.id.activity_comment_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.recycler_decoration));

        recyclerView.addItemDecoration(decoration);

    }


    public void initData(){

        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereEqualTo("ActivityID", activityId);
        query.setLimit(50);
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {

                    commentcount.setText("(" + list.size() + ")");
                    ((ActivityActivity)getActivity()).setText(list.size());
                    Collections.reverse(list); // 倒序排列
                    for (Comment comment : list) {
                        commentList.add(comment);
                        initRecyclerView();
                    }

                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }

            }

        });
    }




    private void ifJoin() {

        if (Arrays.toString(bmobUser.getJoin()).indexOf(activityId) < 0) {
            ifJoin = false;
            joinButton.setText("加 入!");

        } else {
            ifJoin = true;
            joinButton.setText("取消加入");
        }
    }



    private void startChatting(String id,String name){
        BmobIM.getInstance().startPrivateConversation( new BmobIMUserInfo(id,name,""), new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if(e==null){
                    //在此跳转到聊天页面
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    Intent intent = new Intent(getContext(),
                            ChatActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }else{
                    Toast.makeText(getContext()
                            ,e.getMessage()+"("+e.getErrorCode()+")",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
