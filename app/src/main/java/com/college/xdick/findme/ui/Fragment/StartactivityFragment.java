package com.college.xdick.findme.ui.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.DownLoadImageService;
import com.college.xdick.findme.MyClass.GalleryLayoutManager;
import com.college.xdick.findme.MyClass.ImageDownLoadCallBack;
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ScaleTransformer;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
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
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.youth.banner.Transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/4/12.
 */

public class StartactivityFragment extends Fragment{


    private String activityId,hostAvatar,hostID;
    private ImageView avatar1,avatar2,avatar3,avatar4,avatar5,avatar6;
    private TextView joincount,commentcount,loadmore_text;
    private ImageView[] avatar;
    private Button joinButton;
    private int count=0,joinnum=0;
    private CommentAdapter adapter;
    public List<Comment> commentList = new ArrayList<>();
    private View rootView;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    public boolean ifJoin;
   public   MyActivity activity;
   private GalleryAdapter galleryAdapter;
   private Map<String,Dynamics>picMap = new HashMap<>();
   private List<String> uriList = new ArrayList<>();
    private  int size =0;
    private boolean ifEmpty=false;
   public static int ADD=2,REFRESH=1,REPLY=3;
    private int commentCount;
    private NestedScrollView scroller;


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
        final String activityHost = activity.getHostName();
        String activityCover =activity.getCover();
        String activityContent = activity.getContent();
        String activityTime = activity.getTime();
        String activityPlace = activity.getPlace();
        final String[] activityTag= activity.getTag();
        hostID=  activity.getHostId();
        commentCount=activity.getCommentCount();
        BmobQuery<MyUser>query = new BmobQuery<>();
        query.getObject(hostID, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e==null){
                    hostAvatar =myUser.getAvatar();
                }
            }
        });
        activityId = activity.getObjectId();
        scroller = rootView.findViewById(R.id.scroll);



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


        BmobQuery<MyUser> query1= new BmobQuery<>();
        query1.getObject(activity.getHostId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e==null){
                    ImageView hostAvatarImg= rootView.findViewById(R.id.host_avatar);
                    String hostAvatarUri=myUser.getAvatar();
                    Glide.with(getActivity()).load(hostAvatarUri)
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                            .apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(hostAvatarImg);
                    hostAvatarImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BmobQuery<MyUser>  query1 = new BmobQuery<>();
                            query1.getObject(activity.getHostId(), new QueryListener<MyUser>() {
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
                }
            }
        });

        final LinearLayout loadmore=  rootView.findViewById(R.id.activity_loadmore);
        ImageView activityImageView = rootView.findViewById(R.id.activity_image_view);
        final TextView activityContentText =  rootView.findViewById(R.id.activity_content_text);
        TextView activityHostText =  rootView.findViewById(R.id.activity_host_text);
        TextView activityTimeText =  rootView.findViewById(R.id.activity_time_text);
        TextView activityPlaceText =  rootView.findViewById(R.id.activity_place_text);
        TextView activityTagText =  rootView.findViewById(R.id.activity_tag_text);
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
        Glide.with(this).load(activityCover)
                .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation())).into(activityImageView);
        activityContentText.setText(activityContent);
        activityTimeText.setText(""+activityTime);
        activityPlaceText.setText(""+activityPlace);
        activityHostText.setText(""+activityHost);
        String tag=Arrays.toString(activity.getTag());
        tag = tag.replace("[","");
        tag = tag.replace("]","");
        tag = tag.replace(","," ");
        activityTagText.setText(""+tag);




     if (activityTag[0].equals("二手交易")){
            joinButton.setVisibility(View.GONE);
            LinearLayout linearLayout = rootView.findViewById(R.id.join_num_layout);
            linearLayout.setVisibility(View.INVISIBLE);
            for (int i=0;i<6;i++){
                avatar[i].setVisibility(View.GONE);
            }
        }
        try {
         joinnum=activity.getJoinUser().length;
        }catch (Exception e){
         e.printStackTrace();
        }
        joincount.setText(joinnum+"");
        activityHostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               BmobQuery<MyUser>  query1 = new BmobQuery<>();
               query1.getObject(activity.getHostId(), new QueryListener<MyUser>() {
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
                if(loadmore_text.getText().equals("加 载 更 多")){
                    activityContentText.setSingleLine(false);
                    loadmore_text.setText("收 起");}

                else {
                    activityContentText.setMaxLines(5);
                    loadmore_text.setText("加 载 更 多");

                }

            }
        });


          if(bmobUser!=null&&activity.getHostName().equals(bmobUser.getUsername())){
              joinButton.setText("发送通知");
              joinButton.setBackgroundResource(R.drawable.join_button_radius_notify);
              joinButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
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
                          Toast.makeText(getActivity(),"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();

                      }

                      else {


                      if(!ifJoin){


                          ((ActivityActivity)getActivity()).joinChange(1);
                          MyActivity myActivity = new MyActivity();
                          myActivity.setObjectId(activityId);
                          myActivity.setDate(activity.getDate());
                          myActivity.addUnique("joinUser",bmobUser.getObjectId());
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
                                      if (count<5){
                                          Glide.with(getContext()).load(bmobUser.getAvatar())
                                                  .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[++count]);
                                      }

                                      sendMessage(bmobUser.getUsername()+"加入了你的"+"#"+activity.getTitle()+"#活动"
                                              ,new BmobIMUserInfo(hostID,activity.getHostName(),hostAvatar));
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
                          myActivity.removeAll("joinUser",Arrays.asList(bmobUser.getObjectId()));
                          myActivity.update(new UpdateListener() {
                              @Override
                              public void done(BmobException e) {
                                  if (e==null){
                                      if (count<5) {
                                          Glide.with(getContext()).load(R.drawable.blank_pic)
                                                  .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[count--]);
                                      }
                                      getActivity().runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              joinButton.setText("加 入!");
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
     if (activity.getJoinUser()==null){
         return;
     }
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
    query.addWhereContainedIn("objectId", Arrays.asList(activity.getJoinUser()));
    query.setLimit(6);
    query.findObjects(new FindListener<MyUser>() {
        @Override
        public void done(List<MyUser> list, BmobException e) {
            if(e==null){

                for(final MyUser user :list){
                    avatar[count].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (MyUser.getCurrentUser(MyUser.class)==null){
                                startActivity(new Intent(getContext(),LoginActivity.class));
                                getActivity().finish();
                                Toast.makeText(getActivity(),"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Intent intent = new Intent(getActivity(), UserCenterActivity.class);
                            intent.putExtra("USER",user);
                            startActivity(intent);

                        }
                    });
                    Glide.with(getContext()).load(user.getAvatar())
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(RequestOptions.bitmapTransform(new CropCircleTransformation())).into(avatar[count]);

                    if(count==list.size()-1){
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
                    //Log.i(TAG, "Scroll DOWN");
                }
                if (scrollY < oldScrollY) {
                    //Log.i(TAG, "Scroll UP");
                }

                if (scrollY == 0) {
                    // Log.i(TAG, "TOP SCROLL");
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

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
        query.addWhereEqualTo("ActivityID", activityId);
        query.order("-createdAt");
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
       if (bmobUser==null){
           joinButton.setText("未登录");
           return;
       }
        if (!Arrays.toString(activity.getJoinUser()).contains(bmobUser.getObjectId())) {
            ifJoin = false;
            joinButton.setText("加 入!");
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
         galleryAdapter = new GalleryAdapter(picMap,uriList);

         recyclerView.setAdapter(galleryAdapter);
      BmobQuery<Dynamics> query = new BmobQuery<>();

      BmobQuery<Dynamics> q1 = new BmobQuery<>();
      q1.addWhereEqualTo("ifAdd2Gallery",true);
      query.and(Arrays.asList(q1));
      query.setLimit(500);
      query.addWhereEqualTo("activityId",activity.getObjectId());
      query.order("-likeCount");

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

        if(!bmobUser.getObjectId().equals(info.getUserId())){

        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        ActivityMessage msg = new ActivityMessage();
        msg.setContent(content);//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("currentuser", info.getUserId());
        map.put("userid", bmobUser.getObjectId());
        map.put("username",bmobUser.getUsername());
        map.put("useravatar",bmobUser.getAvatar());
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
