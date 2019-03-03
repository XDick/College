package com.college.xdick.findme.ui.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.GalleryLayoutManager;
import com.college.xdick.findme.MyClass.ScaleTransformer;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.CommentAdapter;
import com.college.xdick.findme.adapter.GalleryAdapter;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.HostNotifyActivity;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MapActivity;
import com.college.xdick.findme.ui.Activity.SearchActivity;
import com.college.xdick.findme.ui.Activity.SearchUserActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/4/12.
 */

public class StartActivityFragment extends BaseFragment {


    private String activityId,hostAvatar,hostID;
    private ImageView avatar1,avatar2,avatar3,avatar4,avatar5,avatar6;
    private TextView joincount,commentcount,loadmore_text;
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
            activityTimeLayout,activityMapLayout;
    private boolean ifHavePeopleIn=false;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       rootView =inflater.inflate(R.layout.fragment_startactivity,container,false);

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
        String activityPlace = activity.getPlace();
        String activityGps = activity.getGps()[2];
        final String[] activityTag= activity.getTag();
        hostID=  activity.getHost().getObjectId();
        commentCount=activity.getCommentCount();

        hostAvatar =activity.getHost().getAvatar();

        activityId = activity.getObjectId();
        scroller = rootView.findViewById(R.id.scroll);
        avatarLayout= rootView.findViewById(R.id.avatar_layout);



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
        final TextView activityContentText =  rootView.findViewById(R.id.activity_content_text);
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
                    Glide.with(getActivity()).load(hostAvatar)
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


        Glide.with(this).load(activityCover)
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation())).into(activityImageView);
        activityContentText.setText(activityContent);
        activityDateText.setText( activityTime.substring(0,activityTime.lastIndexOf(" ")));
        activityTimeText.setText(activityTime.substring(activityTime.indexOf(" ")+1));
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
                  //  calendar.set(2016, 10, 19, 10, 30);
                   // calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis());
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
                    startActivity(new Intent(getContext(),LoginActivity.class));
                    getActivity().finish();
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


          if(myUser !=null&&activity.getHost().getObjectId().equals(myUser.getObjectId())){
              joinButton.setText("发送通知");
              joinButton.setBackgroundResource(R.drawable.join_button_radius_notify);
              joinButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if (activity.getJoinUser()==null||Arrays.asList(activity.getJoinUser()).isEmpty()){
                          return;
                      }
                         Intent intent = new Intent(getActivity(), HostNotifyActivity.class);
                         intent.putExtra("ACTIVITY",activity);
                         startActivity(intent);
                  }
              });
          }
          else {
              ifJoin();
              joinButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {


                      if (MyUser.getCurrentUser(MyUser.class)==null){
                          startActivity(new Intent(getContext(),LoginActivity.class));
                          getActivity().finish();
                         // Toast.makeText(getActivity(),"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();

                      }

                      else {


                      if(!ifJoin){


                          ((ActivityActivity)getActivity()).joinChange(1);
                          MyActivity myActivity = new MyActivity();
                          myActivity.setObjectId(activityId);
                          myActivity.setDate(activity.getDate());
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
                                           Glide.with(getContext()).load(myUser.getAvatar())
                                                   .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[count]);
                                       }
                                       else {
                                          Glide.with(getContext()).load(myUser.getAvatar())
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
    query.setLimit(10);
    query.findObjects(new FindListener<MyUser>() {
        @Override
        public void done(final List<MyUser> list, BmobException e) {
            if(e==null){
                ifHavePeopleIn=true;
                avatarLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyUser.getCurrentUser(MyUser.class)==null){
                            startActivity(new Intent(getContext(),LoginActivity.class));
                            getActivity().finish();
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
                    Glide.with(getContext()).load(user.getAvatar())
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
      query.include("myUser[avatar|username]");
      query.addWhereEqualTo("activityId",activity.getObjectId());
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



}
