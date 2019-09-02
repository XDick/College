package com.college.xdick.findme.ui.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.GalleryLayoutManager;
import com.college.xdick.findme.MyClass.MyGlideEngine;
import com.college.xdick.findme.MyClass.ScaleTransformer;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.ActivityHistoryAdapter;
import com.college.xdick.findme.adapter.CommentAdapter;
import com.college.xdick.findme.adapter.GalleryAdapter;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.DetailActivityActivity;
import com.college.xdick.findme.ui.Activity.HostNotifyActivity;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Activity.MapActivity;
import com.college.xdick.findme.ui.Activity.SearchActivity;
import com.college.xdick.findme.ui.Activity.SearchUserActivity;
import com.college.xdick.findme.ui.Activity.SetDynamicsActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;
import com.college.xdick.findme.util.AppManager;
import com.college.xdick.findme.util.ClassFileHelper;
import com.college.xdick.findme.util.ExpUtil;
import com.college.xdick.findme.util.FileUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/4/12.
 */

public class StartActivityFragment extends BaseFragment {


    private String activityId,hostAvatar,hostID;
    private ImageView avatar1,avatar2,avatar3,avatar4,avatar5,avatar6;
    private TextView joincount,commentcount,
            loadmore_text, activityContentText,editTime,expText;
    private ImageView[] avatar;
    private Button joinButton;
    private int count=0,joinnum=0;
    private CommentAdapter adapter;
    public List<Comment> commentList = new ArrayList<>();

    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    public boolean ifJoin;
    public   static MyActivity activity;
   private GalleryAdapter galleryAdapter;
   private Map<String,Dynamics>picMap = new HashMap<>();
   private List<String> uriList = new ArrayList<>();
    private  int size =0;
    private boolean ifEmpty=false;
   public static int ADD=2,REFRESH=1,REPLY=3;
    private int commentCount;
    private NestedScrollView scroller;
    private LinearLayout avatarLayout,hostAvatarLayout,
            activityTimeLayout,activityMapLayout,editLayout;
    private boolean ifHavePeopleIn=false;
   private AlertDialog dialog=null;
    private AlertDialog dialog2=null;
    private AlertDialog dialog3=null;
    private long bmobTime=0;
    private int editCount=0;
    private List<String> historyList=new ArrayList<>();
    private EditText editNotify;
    private List<MyUser> joinList= new ArrayList<>();
    private List<MyUser> joinMember =new ArrayList<>();
    private String[] multiChoiceItems = null;
    private boolean[] defaultSelectedStatus = null;
    private int REQUEST_CODE_CHOOSE = 1;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       rootView =inflater.inflate(R.layout.fragment_startactivity,container,false);
        bmobTime = ((BaseActivity)getActivity()).getBmobTime();
        initBaseView();
        initJoin();
        initRecyclerView();
        initComment(REFRESH);
        initGallery();



       return rootView;
    }



    private void initBaseView(){
        activity = (MyActivity)getActivity().getIntent().getSerializableExtra("ACTIVITY");



        String activityTitle = activity.getTitle();
        final String activityHost = activity.getHost().getUsername();
        String activityCover =activity.getCover();
        String activityContent = activity.getContent();
        final String activityTime = activity.getTime();
        final String activityEndTime = activity.getEndTime();
        String activityPlace = activity.getPlace();
        String activityGps = activity.getGps()[2];
        final String[] activityTag= activity.getTag();
        hostID=  activity.getHost().getObjectId();
        commentCount=activity.getCommentCount();

        hostAvatar =activity.getHost().getAvatar();

        activityId = activity.getObjectId();
        scroller = rootView.findViewById(R.id.scroll);
        avatarLayout= rootView.findViewById(R.id.avatar_layout);

        expText= rootView.findViewById(R.id.user_exp);
        expText.setText(ExpUtil.ConvertExp(activity.getHost().getExp()));
        expText=ExpUtil.colorTextView(getContext(),expText,activity.getHost().getExp());



        Toolbar toolbar =rootView.findViewById(R.id.toolbar_activity);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                rootView.findViewById(R.id.collapsing_toolbar_activity);

        hostAvatarLayout = rootView.findViewById(R.id.host_avatar_layout);
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
        activityContentText =  rootView.findViewById(R.id.activity_content_text);
        TextView activityHostText =  rootView.findViewById(R.id.activity_host_text);
        TextView activityTimeText =  rootView.findViewById(R.id.activity_time_text);
        TextView activityDateText =  rootView.findViewById(R.id.activity_date_text);
        TextView activityPlaceText =  rootView.findViewById(R.id.activity_place_text);
        TextView activityTagText1 =  rootView.findViewById(R.id.activity_tag1_text);
        TextView activityHostSchoolText = rootView.findViewById(R.id.activity_school_text);
        TextView findme_text = rootView.findViewById(R.id.activity_findme_text);
        final TextView activityTagText2 =  rootView.findViewById(R.id.activity_tag2_text);
        final TextView activityTagText3 =  rootView.findViewById(R.id.activity_tag3_text);
        final TextView activityTagText4 =  rootView.findViewById(R.id.activity_tag4_text);

                    ImageView hostAvatarImg= rootView.findViewById(R.id.host_avatar);
                    Glide.with(getActivity()).load(new mGlideUrl(hostAvatar+"!/fp/1000"))
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                            .apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(hostAvatarImg);

        commentcount = rootView.findViewById(R.id.activity_comment_count);
        joincount = rootView.findViewById(R.id.ac_main_joincount);
        joinButton = rootView.findViewById(R.id.join_ac_button);
        loadmore_text = rootView.findViewById(R.id.activity_loadmore_text);


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        collapsingToolbar.setTitle(activityTitle);

        collapsingToolbar.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);


        Glide.with(this).load(new mGlideUrl(activityCover+"!/fp/1000"))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation())).into(activityImageView);
        activityContentText.setText(activityContent);
        activityDateText.setText( activityTime);
        activityTimeText.setText("- "+activityEndTime);
        if(activity.getHostSchool().equals("")){
            activityHostSchoolText.setVisibility(View.GONE);
        }
        activityHostSchoolText.setText(activity.getHostSchool());

        activityPlaceText.setText(activityPlace+(activityGps==null?"":"("+activityGps+")"));
        activityHostText.setText(""+activityHost);
        activityTagText1.setText(activityTag[0]);
        if (activityTag[0].equals("泛觅活动")){
            findme_text.setVisibility(View.VISIBLE);
        }

        activityTagText2.setText(activityTag[1]);
        activityTagText3.setText(activityTag[2]);
        activityTagText4.setText(activityTag[3]);

        activityTagText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("SEARCH",activityTagText2.getText().toString());
                startActivity(intent);
            }
        });
        activityTagText3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("SEARCH",activityTagText3.getText().toString());
                startActivity(intent);
            }
        });

        activityTagText4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("SEARCH",activityTagText4.getText().toString());
                startActivity(intent);
            }
        });

        editTime= rootView.findViewById(R.id.edit_time_text);

        if (activity.getEditTime()==null){
            editTime.setVisibility(View.GONE);

        }
        else {
            initEditTextDialog();
            editTime.setVisibility(View.VISIBLE);
            List<String> edit=Arrays.asList(activity.getEditTime());
            editCount=edit.size();
            editTime.setText(edit.get(edit.size()-1)+" 第"+editCount
                    +"次修改 >");

            editTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.show();
                }
            });


        }
        editLayout = rootView.findViewById(R.id.edit_activity);
        if (myUser!=null){
            if (myUser.getObjectId().equals(activity.getHost().getObjectId())){
                editLayout.setVisibility(View.VISIBLE);
                initDialog();
                editLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                    }
                });
            }
        }


        activityMapLayout = rootView.findViewById(R.id.activity_map_layout);
        activityMapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });
        activityTimeLayout = rootView.findViewById(R.id.activity_time_layout);
        activityTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
                    Calendar calendar = Calendar.getInstance();

                    calendar.set( Integer.valueOf(activityTime.substring(0,
                            activityTime.indexOf("年"))),
                            Integer.valueOf(activityTime.substring(activityTime.indexOf("年")+1,
                                    activityTime.indexOf("月")))-1,
                            Integer.valueOf(activityTime.substring(activityTime.indexOf("月")+1,
                                    activityTime.indexOf("日"))),
                            Integer.valueOf(activityTime.substring(activityTime.indexOf(" ")+1,
                            activityTime.indexOf(":"))),Integer.valueOf(activityTime.substring( activityTime.indexOf(":")+1)));

                    calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());

                calendar.set( Integer.valueOf(activityEndTime.substring(0,
                        activityEndTime.indexOf("年"))),
                        Integer.valueOf(activityEndTime.substring(activityEndTime.indexOf("年")+1,
                                activityEndTime.indexOf("月")))-1,
                        Integer.valueOf(activityEndTime.substring(activityEndTime.indexOf("月")+1,
                                activityEndTime.indexOf("日"))),
                        Integer.valueOf(activityEndTime.substring(activityEndTime.indexOf(" ")+1,
                                activityEndTime.indexOf(":"))),Integer.valueOf(activityEndTime.substring( activityTime.indexOf(":")+1)));
                    calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis());
                    calendarIntent.putExtra(CalendarContract.Events.TITLE, activity.getTitle());
                    calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, activity.getGps());
                    calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, activity.getContent());
                    calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, CalendarContract.EXTRA_EVENT_ALL_DAY);
                calendarIntent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");
                    startActivity(calendarIntent);
            }
        });




   /*  if (activityTag[0].equals("二手交易")){
            joinButton.setVisibility(View.GONE);
            LinearLayout linearLayout = rootView.findViewById(R.id.join_num_layout);
            linearLayout.setVisibility(View.INVISIBLE);
            for (int i=0;i<6;i++){
                avatar[i].setVisibility(View.GONE);
            }
        }*/
        try {
         joinnum=activity.getJoinUser().length;
        }catch (Exception e){
         e.printStackTrace();
        }
        joincount.setText(joinnum+"");
        hostAvatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUser.getCurrentUser(MyUser.class)==null){

                    AppManager.getAppManager().finishAllActivity();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    return;

                }
                BmobQuery<MyUser>  query1 = new BmobQuery<>();
                query1.getObject(activity.getHost().getObjectId(), new QueryListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if (e==null){
                            Intent intent = new Intent(getActivity(), UserCenterActivity.class);
                            intent.putExtra("USER",myUser);
                            startActivity(intent);
                        }
                    }
                });
            }
        });


        loadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadmore_text.getText().equals("加载更多∨")){
                    activityContentText.setSingleLine(false);
                    loadmore_text.setText("收起∧");}

                else {
                    activityContentText.setMaxLines(7);
                    loadmore_text.setText("加载更多∨");

                }

            }
        });

          ifJoin();

        if (MyUser.getCurrentUser(MyUser.class)==null) {
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.getAppManager().finishAllActivity();
                    startActivity(new Intent(getContext(), LoginActivity.class));


                }
                // Toast.makeText(getActivity(),"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
            });

        }

                if(myUser !=null&&activity.getHost().getObjectId().equals(myUser.getObjectId())){
              joinButton.setText("发送通知");

              joinButton.setBackgroundResource(R.drawable.join_button_radius_notify);
              joinButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      if (activity.getJoinUser()==null||Arrays.asList(activity.getJoinUser()).isEmpty()){
                          return;
                      }

                      /*   Intent intent = new Intent(getActivity(), HostNotifyActivity.class);
                         intent.putExtra("ACTIVITY",activity);
                         startActivity(intent);*/

                         dialog3.show();
                         editNotify.clearFocus();
                  }
              });
          }

          else  if (activity.getEndDate()<bmobTime){

           return;
        }
          else {

              joinButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {


                      if(!ifJoin){


                          ((ActivityActivity)getActivity()).joinChange(1);
                          MyActivity myActivity = new MyActivity();
                          myActivity.setObjectId(activityId);
                          myActivity.setDate(activity.getDate());
                          myActivity.setEndDate(activity.getEndDate());
                          myActivity.addUnique("joinUser", myUser.getObjectId());
                          myActivity.update(new UpdateListener() {
                              @Override
                              public void done(BmobException e) {
                                  if (e==null){
                                      getActivity().runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              joincount.setText(++joinnum+"");
                                              joinButton.setText("取消加入");
                                              joinButton.setBackgroundResource(R.drawable.join_button_radius_true);
                                          }
                                      });
                                       if (!ifHavePeopleIn){
                                           Glide.with(getContext()).load(new mGlideUrl(myUser.getAvatar()+"!/fp/10000"))
                                                   .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[count]);
                                       }
                                       else {
                                          Glide.with(getContext()).load(new mGlideUrl(myUser.getAvatar()+"!/fp/10000"))
                                                  .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[++count]);

                                      }

                                      sendMessage(myUser.getUsername()+"加入了你的"+"#"+activity.getTitle()+"#活动"
                                              ,new BmobIMUserInfo(hostID,activity.getHost().getUsername(),hostAvatar));
                                      ifJoin=true;
                                      Toast.makeText(getContext(), "成功加入", Toast.LENGTH_SHORT).show();
                                  }
                                  else {
                                      Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });

                      }

                      else
                      {


                          ((ActivityActivity)getActivity()).joinChange(0);
                          MyActivity myActivity = new MyActivity();
                          myActivity.setObjectId(activityId);
                          myActivity.setDate(activity.getDate());
                          myActivity.setEndDate(activity.getEndDate());
                          myActivity.removeAll("joinUser",Arrays.asList(myUser.getObjectId()));
                          myActivity.update(new UpdateListener() {
                              @Override
                              public void done(BmobException e) {
                                  if (e==null){
                                      if (count<5) {
                                          Glide.with(getContext()).load(R.drawable.blank_pic)
                                                  .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[count--]);
                                      }
                                      sendMessage(myUser.getUsername()+"退出了你的"+"#"+activity.getTitle()+"#活动"
                                              ,new BmobIMUserInfo(hostID,activity.getHost().getUsername(),hostAvatar));
                                      getActivity().runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              joinButton.setText("加 入");
                                              joincount.setText(--joinnum+"");
                                              joinButton.setBackgroundResource(R.drawable.join_button_radius_false);
                                          }
                                      });
                                      ifJoin = false;
                                      Toast.makeText(getContext(), "取消加入", Toast.LENGTH_SHORT).show();
                                  }
                                  else {
                                      Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });

                      }

                  }
              });


          }




    }


public  void initJoin(){
     if (activity.getJoinUser()==null||Arrays.asList(activity.getJoinUser()).isEmpty()){
         avatarLayout.setVisibility(View.GONE);

         return;
     }
     BmobQuery<MyUser> query = new BmobQuery<MyUser>();
    query.addWhereContainedIn("objectId", Arrays.asList(activity.getJoinUser()));
    query.setLimit(100);
    query.findObjects(new FindListener<MyUser>() {
        @Override
        public void done(final List<MyUser> list, BmobException e) {
            if(e==null){

                joinList.addAll(list);

                 multiChoiceItems = new String[list.size()];
                defaultSelectedStatus = new boolean[list.size()];

                    for (int i=0;i<joinList.size();i++){
                        multiChoiceItems[i]=joinList.get(i).getUsername();
                        defaultSelectedStatus[i]=true;
                    }


                initNotifyDialog();

                ifHavePeopleIn=true;
                avatarLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyUser.getCurrentUser(MyUser.class)==null){

                            AppManager.getAppManager().finishAllActivity();
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            return;

                        }
                        Intent intent = new Intent(getActivity(),SearchUserActivity.class);
                        intent.putExtra("USERLIST",(Serializable)list);
                        intent.putExtra("SIGNAL",SearchUserActivity.JOIN);
                        intent.putExtra("EXTRA",activity.getJoinUser());
                        startActivity(intent);

                    }
                });


                for(final MyUser user :list){
                    Glide.with(getContext()).load(new mGlideUrl(user.getAvatar()+"!/fp/1000"))
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[count]);

                    if(count==list.size()-1||count==5){
                        break;
                    }
                    count++;
                }

            }
            else
            {      if (getActivity()!=null){
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            }

        }

    });




}

    private void initRecyclerView(){

        RecyclerView recyclerView = rootView.findViewById(R.id.activity_comment_recyclerview);
       final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(commentList);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_comment, recyclerView, false);
        adapter.setEmptyView(empty);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addFooterView(footer);
        recyclerView.setAdapter(adapter);


        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.recycler_decoration));

        recyclerView.addItemDecoration(decoration);

        scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) {


                    ((ActivityActivity)getActivity()).ifHideBar(true);
                    //Log.i(TAG, "Scroll DOWN");

                }
                if (scrollY < oldScrollY) {
                    ((ActivityActivity)getActivity()).ifHideBar(false);
                    //Log.i(TAG, "Scroll UP");
                }

                if (scrollY == 0) {
                    // Log.i(TAG, "TOP SCROLL");
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    ((ActivityActivity)getActivity()).ifHideBar(false);
                    if (ifEmpty) {
                        adapter.changeMoreStatus(CommentAdapter.NO_MORE);

                    } else {
                        adapter.changeMoreStatus(CommentAdapter.LOADING_MORE);
                    }

                    if (ifEmpty) {
                        //null
                    } else {
                        initComment(ADD);
                    }
                    //  Log.i(TAG, "BOTTOM SCROLL");
                }
            }
        });

    }

    public void initComment(final int state){

        BmobQuery<Comment> query = new BmobQuery<Comment>();

        query.addWhereEqualTo("activity", activityId);
       // query.addWhereDoesNotExists("replyComment");
        query.order("-createdAt");
        query.include("user[avatar|username|following|school]," +
                "replyUser[avatar|username],replyComment[content]"
        );
        query.setLimit(10);
        query.setSkip(size);
        final int listsize = commentList.size();
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {

                    commentList.addAll(list);

            if (state==REFRESH) {
                commentList.clear();
                if(list.size()<10){
                    ifEmpty=true;
                    commentList.addAll(list);
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                    adapter.notifyDataSetChanged();
                }
                else {
                    ifEmpty=false;
                    commentList.addAll(list);
                    adapter.notifyDataSetChanged();}
                size =  10;

                commentcount.setText("("+commentCount+")");
                ((ActivityActivity)getActivity()).setText(commentCount);
            }

            else if (state == ADD){
                if (listsize == commentList.size()) {
                    ifEmpty = true;
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                    adapter.notifyDataSetChanged();
                } else if (listsize + 10 > commentList.size()) {
                    ifEmpty = true;
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);


                }
                else {
                    adapter.notifyItemInserted(adapter.getItemCount()-1);
                    size = size + 10;
                }


            }

            else if (state ==REPLY){
                ifEmpty=false;
                size=10;
                initRecyclerView();
                MyActivity myActivity = new MyActivity();
                myActivity.setObjectId(activityId);
                myActivity.setDate(activity.getDate());
                myActivity.setEndDate(activity.getEndDate());
                myActivity.increment("commentCount",1);
                myActivity.update();
                commentcount.setText("("+ ++commentCount+")");
                ((ActivityActivity)getActivity()).setText(commentCount);
            }




                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }

            }

        });
    }





    public void setSize(int size){

       this. size =size;
    }



    private void ifJoin() {
       if (myUser ==null){
           joinButton.setText("未登录");
           return;
       }

       if (activity.getEndDate()<bmobTime){
           joinButton.setText("已结束");
           joinButton.setBackgroundResource(R.drawable.join_button_radius_finished);
           return;
       }

        if (!Arrays.toString(activity.getJoinUser()).contains(myUser.getObjectId())) {
            ifJoin = false;
            joinButton.setText("加 入");
            joinButton.setBackgroundResource(R.drawable.join_button_radius_false);

        } else {
            ifJoin = true;
            joinButton.setText("取消加入");
            joinButton.setBackgroundResource(R.drawable.join_button_radius_true);
        }
    }




     private void initGallery(){
        final  RecyclerView recyclerView = rootView.findViewById(R.id.gallery);
         GalleryLayoutManager layoutManager = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);
         layoutManager.attach(recyclerView,1);
         layoutManager.setItemTransformer(new ScaleTransformer());
         galleryAdapter = new GalleryAdapter(picMap,uriList,activity.getObjectId());

         recyclerView.setAdapter(galleryAdapter);
      BmobQuery<Dynamics> query = new BmobQuery<>();

      BmobQuery<Dynamics> q1 = new BmobQuery<>();
      q1.addWhereEqualTo("ifAdd2Gallery",true);
      query.and(Arrays.asList(q1));
      query.include("myUser[avatar|username|school|Exp],activity[title|time|cover|].host.[username]");
      query.addWhereEqualTo("activity",activity.getObjectId());
      query.order("-createdAt");
      query.setLimit(10);
      query.findObjects(new FindListener<Dynamics>() {
          @Override
          public void done(List<Dynamics> list, BmobException e) {
          if (e==null){
                 recyclerView.setVisibility(View.VISIBLE);
              if (uriList.isEmpty()){
                  picMap.put(activity.getCover(),null)                                  ;
                  uriList.add(activity.getCover());
              }
              for (Dynamics dynamics: list){
                  uriList.addAll(Arrays.asList(dynamics.getPicture()));
                  List<String>uri = new ArrayList<>(Arrays.asList(dynamics.getPicture()));
                  for (String str:uri){
                      picMap.put(str,dynamics);
                  }
              }

              galleryAdapter.notifyDataSetChanged();



          }
          }
      });


         layoutManager.setCallbackInFling(true);//should receive callback when flinging, default is false
         layoutManager.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
             @Override
             public void onItemSelected(RecyclerView recyclerView, View item, int position) {
                 //.....
             }
         });



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
        map.put("username", myUser.getUsername());
        map.put("useravatar", myUser.getAvatar());
        map.put("activityid",activity.getObjectId());
        map.put("activityname",activity.getTitle());
        map.put("type","activity");
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功

                } else {//发送失败
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    }

    public void setCount(int state){
        if (state==0){
            commentCount=--commentCount;
        }
        else if (state==1){
            commentCount=++commentCount;
        }

    }

    private void initDialog(){
        AlertDialog.Builder builder ;
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("编辑内容");
        builder.setIcon(R.drawable.icon);
        final EditText editText=new EditText(getContext());
        editText.setText(activityContentText.getText().toString());
        builder.setView(editText);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (editText.getText().toString().length()<50){
                    Toast.makeText(getContext(), "字数小于50", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                final String  dates = dateFormat.format(new Date(bmobTime));
                MyActivity myActivity = new MyActivity();
                myActivity.setObjectId(activityId);
                myActivity.setDate(activity.getDate());
                myActivity.setEndDate(activity.getEndDate());
                myActivity.setContent(editText.getText().toString());
                myActivity.add("editTime", dates);
                myActivity.add("editContent", activity.getContent());
                final String content=activityContentText.getText().toString();
                myActivity.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activityContentText.setText(editText.getText().toString());
                                    editTime.setVisibility(View.VISIBLE);
                                    editTime.setText(dates+" 第"+(++editCount)
                                            +"次修改 >");
                                    historyList.add(0,dates+"!"+
                                            content);



                                }
                            });
                            Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });


        dialog = builder.create();
    }

    private void initEditTextDialog(){

        ActivityHistoryAdapter adapter = new ActivityHistoryAdapter(getContext());
        adapter.setList(historyList);

        for (int i=0; i<Arrays.asList(activity.getEditTime()).size();i++){
            historyList.add(Arrays.asList(activity.getEditTime()).get(i)+"!"+
                    Arrays.asList(activity.getEditContent()).get(i));
            }
         Collections.reverse(historyList);
        dialog2 = new AlertDialog
                .Builder(getContext())
                .setTitle("历史编辑")
                .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToastUtil.showToastShort("信息：");
                        dialog.dismiss();
                    }
                }).create();



    }



    private void initNotifyDialog(){

        if (activity.getJoinUser()==null){
            return;
        }



        AlertDialog.Builder builder ;
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("发送通知");
        builder.setIcon(R.drawable.icon);


        editNotify=new EditText(getContext());
        editNotify.setText("");
        editNotify.setHint("输入通知");
        editNotify.setBackground(getActivity().getDrawable(R.drawable.my_edittext));
        builder.setView(editNotify)
    /*.setMultiChoiceItems(multiChoiceItems, defaultSelectedStatus, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                defaultSelectedStatus[which] = isChecked;
            }
        })*/;



        final float scale = getResources().getDisplayMetrics().density;
        builder.setNegativeButton("图片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editNotify.getText().toString())){
                    Toast.makeText(getContext(),"先打一些字吧~",Toast.LENGTH_SHORT).show();
                    return;
                }
                Matisse.from(getActivity())
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(9)
                        .gridExpectedSize((int) (120 * scale + 0.5f))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.6f)
                        .theme( R.style.Matisse_FindMe)
                        .imageEngine(new MyGlideEngine())
                        .showSingleMediaType(true)
                        .forResult(REQUEST_CODE_CHOOSE);

            }
        });
        builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(editNotify.getText().toString())){
                    Toast.makeText(getContext(),"先打一些字吧~",Toast.LENGTH_SHORT).show();
                    return;
                }
              /*  for (int i=0;i<joinList.size();i++){
                    if (defaultSelectedStatus[i]){
                        joinMember.add(joinList.get(i));
                    }

                }*/

                final ActivityMessage msg = new ActivityMessage();
                      sendTextMsg(msg,0);


            }  });


        dialog3 = builder.create();

    }

    public void sendLocalImageMessage(String path) {

     /*   for (int i=0;i<joinList.size();i++){
            if (defaultSelectedStatus[i]){
                joinMember.add(joinList.get(i));
            }

        }*/
        File file=new File(path);
        final String fileType = path
                .substring(path.lastIndexOf("."));

        final String renamePath="data/user/0/com.college.xdick.findme/cache/"
                +"chat_image_"
                +BmobUser.getCurrentUser().getObjectId()
                +fileType;
        final File newFile = new File(renamePath);
        try{
            ClassFileHelper.copyFileTo(file,newFile);}
        catch (IOException e){
            e.printStackTrace(); }







        final ActivityMessage msg = new ActivityMessage();
        msg.setContent(editNotify.getText().toString());//给对方的一个留言信息
        uploadImage(newFile,msg);


    }


private void sendImageMsg(final ActivityMessage msg
        , final int count, final String url){

    Toast.makeText(getContext(), "正在发送", Toast.LENGTH_SHORT).show();
    final MyUser user = joinList.get(count);
    BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(
            new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()), true, null);
    BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
    msg.setContent(editNotify.getText().toString()+"[URL]"+url);//给对方的一个留言信息
    Map<String, Object> map = new HashMap<>();
    map.put("currentuser", user.getObjectId());
    map.put("userid", myUser.getObjectId());
    map.put("username", "[活动通知]"+myUser.getUsername());
    map.put("useravatar", myUser.getAvatar());
    map.put("activityid",activity.getObjectId());
    map.put("activityname",activity.getTitle());
    map.put("type","notifyPic");
    msg.setExtraMap(map);
    messageManager.sendMessage(msg, new MessageSendListener() {
        @Override
        public void done(BmobIMMessage bmobIMMessage, BmobException e) {
            if (e == null) {


                dialog3.dismiss();
                if (count+1 == joinList.size()) {
                    Toast.makeText(getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendImageMsg(msg,count+1,url);



            } else {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });



}
private void uploadImage(final File newFile, final ActivityMessage msg){

        final BmobFile bmobFile = new BmobFile(newFile);

    bmobFile.uploadblock(new UploadFileListener() {
        @Override
        public void done(BmobException e) {

            sendImageMsg(msg,0,bmobFile.getFileUrl());

        }

        @Override
        public void onProgress(Integer value) {
            super.onProgress(value);
        }


    });
}


  private  void sendTextMsg( final ActivityMessage msg,final int count){

      Toast.makeText(getContext(), "正在发送", Toast.LENGTH_SHORT).show();
      final MyUser user = joinList.get(count);
      BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(
              new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()), true, null);
      BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
      msg.setContent(editNotify.getText().toString());//给对方的一个留言信息
      Map<String, Object> map = new HashMap<>();
      map.put("currentuser", user.getObjectId());
      map.put("userid", myUser.getObjectId());
      map.put("username", "[活动通知]"+myUser.getUsername());
      map.put("useravatar", myUser.getAvatar());
      map.put("activityid",activity.getObjectId());
      map.put("activityname",activity.getTitle());
      map.put("type","notify");
      msg.setExtraMap(map);
      messageManager.sendMessage(msg, new MessageSendListener() {
          @Override
          public void done(BmobIMMessage bmobIMMessage, BmobException e) {
              if (e == null) {


                  dialog3.dismiss();
                  if (count+1 == joinList.size()) {

                      Toast.makeText(getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                      return;
                  }
                  sendTextMsg(msg,count+1);



              } else {
                  Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
              }
          }
      });



      }




        }


