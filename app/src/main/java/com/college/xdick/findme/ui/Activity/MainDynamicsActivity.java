package com.college.xdick.findme.ui.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.CommentScrollView;
import com.college.xdick.findme.MyClass.DownLoadImageService;
import com.college.xdick.findme.MyClass.ImageDownLoadCallBack;
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.CommentAdapter;
import com.college.xdick.findme.adapter.DynamicsCommentAdapter;
import com.college.xdick.findme.bean.Comment;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.DynamicsComment;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Fragment.DynamicsFragment;
import com.college.xdick.findme.ui.Fragment.UserCenterDynamicsFragment;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pl.tajchert.waitingdots.DotsTextView;

import static android.view.View.GONE;
import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/5/16.
 */

public class MainDynamicsActivity extends AppCompatActivity {

    private TextView content1,time1,user1,commentcount,likecount;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    public DynamicsCommentAdapter adapter;
    private List<DynamicsComment> commentList=new ArrayList<>();
    public boolean ifReply=false;
    private ImageView sendComment,like;
    private EditText editComment;
    private DynamicsComment replyComment,fromComment;
    private ImageView avatar,avatar1,avatar2,avatar3,avatar4,avatar5,avatar6;
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private String dynamicsId;
    private InputMethodManager imm ;
    private DotsTextView dots;
    private LinearLayout loadlayout;
    private NineGridImageViewAdapter mAdapter;
    private NineGridImageView gridImageView;
    private Dialog mDialog;
    private ViewPagerFixed pager;
    private MyUser  bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private Dynamics dynamics;
    private  ImageView[] avatarView =new ImageView[6];

    private  int size =0;
    public static boolean ifEmpty=false;
    public static int ADD=2,REFRESH=1,REPLY=3;
    private int commentCount;


    private TextView title,time2,host,join;
    private ImageView cover;
    private CardView cardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dynamics);
        initRecycler();
        initView();
        initComment(REFRESH);
        initLike();
    }

    private void initView(){
        CommentScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.registerOnScrollViewScrollToBottom(new CommentScrollView.OnScrollBottomListener() {
            @Override
            public void srollToBottom() {
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
            }
        });

        Intent intent = getIntent();
        dynamics = (Dynamics)intent.getSerializableExtra("DYNAMICS");
        final MyUser nowUser = (MyUser) intent.getSerializableExtra("USER");
         commentCount= dynamics.getReplycount();
        final  String user = dynamics.getUser();
        String content = dynamics.getContent();
        dynamicsId = dynamics.getObjectId();

        like =findViewById(R.id.maindynamics_send_like_imageview);
        final List<String> likelist;
        if (dynamics.getLike() != null) {
            List<String> like1 = Arrays.asList(dynamics.getLike());
            likelist = new ArrayList<>(like1);
        } else {
            likelist = new ArrayList<>();

        }


        if (Arrays.toString(dynamics.getLike()).contains(BmobUser.getCurrentUser().getObjectId())) {
           like.setBackground(getResources().getDrawable(R.drawable.thumb_up_t));
        }
        else {
            like.setBackground(getResources().getDrawable(R.drawable.thumb_up));}


        like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Arrays.toString(likelist.toArray(new String[  likelist.size()])).contains(
                        BmobUser.getCurrentUser().getObjectId())) {

                    Dynamics dynamics1 = new Dynamics();
                    dynamics1.setObjectId(dynamics.getObjectId());
                    dynamics1.increment("likeCount" ,-1);
                    dynamics1.setIfAdd2Gallery(dynamics.isIfAdd2Gallery());
                    dynamics1.removeAll("like", Arrays.asList(BmobUser.getCurrentUser().
                            getObjectId()));
                    dynamics1.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                likelist.removeAll(Arrays.asList(BmobUser.getCurrentUser().
                                        getObjectId()));
                                dynamics.setLike(  likelist.toArray(new String[  likelist.size()]));
                               like.setBackground(getResources().getDrawable(R.drawable.thumb_up));
                            }

                        }
                    });

                }

                else {

                    Dynamics dynamics1 = new Dynamics();
                    dynamics1.setObjectId(dynamics.getObjectId());
                    dynamics1.increment("likeCount" ,1);
                    dynamics1.setIfAdd2Gallery(dynamics.isIfAdd2Gallery());
                    dynamics1.addUnique("like",BmobUser.getCurrentUser().
                            getObjectId());

                    dynamics1.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sendMessage(bmobUser.getUsername() + "点赞了你的动态",
                                        new BmobIMUserInfo(dynamics.getUserId(), dynamics.getUser(), null),
                                        dynamics.getObjectId(), dynamics.getContent());


                                likelist.add(BmobUser.getCurrentUser().
                                        getObjectId());
                                dynamics.setLike(  likelist.toArray(new String[likelist.size()]));
                             like.setBackground(getResources().getDrawable(R.drawable.thumb_up_t));

                            }
                        }
                    });
                }
            }
        });

                    avatar=findViewById(R.id.dynamics_main_avatar);
                    Glide.with(this).load(nowUser.getAvatar())
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(bitmapTransform(new CropCircleTransformation())).into(avatar);
                    avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(MainDynamicsActivity.this, UserCenterActivity.class);
                            intent.putExtra("USER",nowUser);
                            startActivity(intent);
                        }});

         avatar1=findViewById(R.id.avatar1);
        avatar2=findViewById(R.id.avatar2);
        avatar3=findViewById(R.id.avatar3);
        avatar4=findViewById(R.id.avatar4);
        avatar5=findViewById(R.id.avatar5);
        avatar6=findViewById(R.id.avatar6);
        avatarView[0]=avatar1;
        avatarView[1]=avatar2;
        avatarView[2]=avatar3;
        avatarView[3]=avatar4;
        avatarView[4]=avatar5;
        avatarView[5]=avatar6;
          likecount = findViewById(R.id.likecount_text);
        title= findViewById(R.id.title_ac);
        cardView =findViewById(R.id.cardview_ac);
        cover = findViewById(R.id.cover_ac);
        time2= findViewById(R.id.time_ac);

        host = findViewById(R.id.host_ac);
        join = findViewById(R.id.join_ac);

        if (dynamics.getActivityTitle()==null){
            cardView.setVisibility(GONE);
        }
        else {
           cardView.setVisibility(View.VISIBLE);

            title.setText(dynamics.getActivityTitle());

            time2.setText(dynamics.getActivityTime());

            Glide.with(this).load(dynamics.getActivityCover())
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(cover);

            host.setText("由"+dynamics.getActivityHost()+"发起");

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BmobQuery<MyActivity> query = new BmobQuery<>();
                    query.getObject(dynamics.getActivityId(), new QueryListener<MyActivity>() {
                        @Override
                        public void done(MyActivity activity, BmobException e) {
                            if (e==null){

                                Intent intent = new Intent(MainDynamicsActivity.this, ActivityActivity.class);
                                intent.putExtra("ACTIVITY",activity);
                                startActivity(intent);
                            }
                        }
                    });




                }
            });
        }


        mAdapter = new NineGridImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String photo) {
                Glide.with(MainDynamicsActivity.this).load(photo)
                        .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(imageView);
            }

            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

            @Override
            protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> photoList) {
                List<String> picList = new  ArrayList<>(photoList);
                showPictureDialog(index,picList);
            }
        };
        gridImageView= findViewById(R.id.nineGrid);
        gridImageView.setAdapter(mAdapter);
        if (dynamics.getPicture()==null){
            gridImageView.setVisibility(View.GONE);
        }
        else {
            gridImageView.setImagesData(Arrays.asList(dynamics.getPicture()));
        }


        dots = findViewById(R.id.dots);
        loadlayout= findViewById(R.id.loading_layout);
        loadlayout.setVisibility(View.VISIBLE);
        dots.start();
        imm = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);

        content1 = findViewById(R.id.content_main_dynamics);
        time1 = findViewById(R.id.time_main_dynamics);
        user1 = findViewById(R.id.username_main_dynamics);
        toolbar =findViewById(R.id.toolbar_maindynamics);
        commentcount = findViewById(R.id.dynamics_main_comment_count);
        avatar = findViewById(R.id.dynamics_main_avatar);

        editComment = findViewById(R.id.maindynamics_edit_comment_edittext);
        sendComment = findViewById(R.id.maindynamics_send_comment_imageview);

        content1.setText(content);
        user1.setText(user);



        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String  bombtime = sdf.format(new Date());
        String rawdate =dynamics.getCreatedAt();
        String date=rawdate.substring(0,rawdate.indexOf(" "));
        String time = rawdate.substring(rawdate.indexOf(" ")+1,rawdate.length()-3);
        if (bombtime.equals(date)){
            time1.setText(time);
        }
        else{

            time1.setText(rawdate.substring(0,rawdate.length()-3));
        }



        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);



        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editComment.getText().toString().isEmpty()){
                    return;
                }

                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if(ifReply){
                    replyComment.setContent(editComment.getText().toString());
                    replyComment.setUserName(myUser.getUsername());
                    replyComment.setUserID(myUser.getObjectId());
                    fromComment.addReply();
                    fromComment.update();
                    final String content=editComment.getText().toString();
                    editComment.setText("");
                    replyComment.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Toast.makeText(MainDynamicsActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                                sendMessage(bmobUser.getUsername()+"回复了你:"+content,
                                        new BmobIMUserInfo(replyComment.getReplyuserId(),
                                                replyComment.getUserName(),null),
                                        dynamics.getObjectId(),dynamics.getContent());


                                commentList.clear();
                                size=0;
                                initComment(REPLY);
                                ifReply=false;
                                editComment.setHint("发表你的评论");
                                Dynamics dynamics1 = new Dynamics();
                                dynamics1.setObjectId(dynamicsId);
                                dynamics1.increment("replycount",1);
                                dynamics1.setIfAdd2Gallery(dynamics.isIfAdd2Gallery());
                                dynamics1.update();



                            }else{
                                Toast.makeText(MainDynamicsActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
                else {


                    DynamicsComment comment = new DynamicsComment ();

                    comment.setUserName(myUser.getUsername());
                    comment.setUserID(myUser.getObjectId());
                    comment.setContent(editComment.getText().toString());
                    comment.setDynamicsID(dynamicsId);
                    final String content=editComment.getText().toString();
                    editComment.setText("");
                    comment.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {

                                Dynamics dynamics1 = new Dynamics();
                                dynamics1.setObjectId(dynamicsId);
                                dynamics1.increment("replycount",1);
                                dynamics1.setIfAdd2Gallery(dynamics.isIfAdd2Gallery());
                                dynamics1.update();

                                Toast.makeText(MainDynamicsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();

                                sendMessage(bmobUser.getUsername()+"评论了你的动态:"+content,new BmobIMUserInfo(dynamics.getUserId(),dynamics.getUser(),null),
                                        dynamics.getObjectId(),dynamics.getContent());

                                commentList.clear();
                                size=0;
                                initComment(REPLY);
                            } else {
                                Toast.makeText(MainDynamicsActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                            }

                        }

                });
                }



    }
        });


    }

    private void initRecycler(){

        recyclerView = findViewById(R.id.recyclerview_main_dynamics);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.recycler_decoration));
        recyclerView.addItemDecoration(decoration);
        adapter = new DynamicsCommentAdapter(commentList);
        View empty = LayoutInflater.from(this).inflate(R.layout.item_empty_comment, recyclerView, false);
        adapter.setEmptyView(empty);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addFooterView(footer);
        recyclerView.setAdapter(adapter);


    }




    public void reply(DynamicsComment fromcomment, DynamicsComment  tocomment){

        editComment.setFocusable(true);
        editComment.setFocusableInTouchMode(true);
        editComment.requestFocus();
        editComment.findFocus();

        imm.showSoftInput( editComment, 0);
        replyComment=tocomment;
        fromComment=fromcomment;
        editComment.setHint("@"+ fromComment.getUserName());

        ifReply=true;
    }


    public void initComment(final int state){

        BmobQuery<DynamicsComment> query = new BmobQuery<DynamicsComment>();
        query.addWhereEqualTo("dynamicsID", dynamicsId);
        query.order("-createdAt");
        query.setLimit(10);
        query.setSkip(size);
        final int listsize = commentList.size();
        query.findObjects(new FindListener<DynamicsComment>() {
            @Override
            public void done(List<DynamicsComment> list, BmobException e) {
                if (e == null) {

                    commentList.addAll(list);

                    if (state==REFRESH) {
                         commentList.clear();
                        if (list.size()<10){
                            commentList.addAll(list);
                            adapter.changeMoreStatus(DynamicsCommentAdapter.DONTSHOW);
                            ifEmpty=true;

                        }else {
                            commentList.addAll(list);
                            ifEmpty=false;
                        }

                        adapter.notifyDataSetChanged();
                        commentcount.setText("("+commentCount+")");
                        loadlayout.setVisibility(View.GONE);

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

                        if (list.size()<10){
                            initRecycler();
                            adapter.changeMoreStatus(DynamicsCommentAdapter.DONTSHOW);
                            adapter.notifyDataSetChanged();
                            ifEmpty=true;


                        }else {
                            initRecycler();
                            ifEmpty=false;
                        }

                        size=10;

                        commentcount.setText("("+ ++commentCount+")");
                    }




                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }

            }

        });}







    public void sendMessage(String content ,BmobIMUserInfo info,String objectId,String objectTitle) {

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
            map.put("activityid",objectId);
            map.put("activityname",objectTitle);
            map.put("type","dynamics");
            msg.setExtraMap(map);
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {//发送成功

                    } else {//发送失败
                        Toast.makeText(MainDynamicsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    public boolean onCreateOptionsMenu(Menu menu){


        if (dynamics.getUser().equals(bmobUser.getUsername())){
            getMenuInflater().inflate(R.menu.toolbar_delete_dynamics,menu);
        }

        return true;
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case  android.R.id.home:
            {
                finish();
                break;
            }

            case R.id.menu_delete_dynamics:
            {
                bmobUser.removeAll("dynamics", Arrays.asList(dynamics.getObjectId()));
                bmobUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){

                            Dynamics dynamics1 = new Dynamics();
                            dynamics1.setObjectId(dynamics.getObjectId());
                            dynamics1.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e==null) {
                                        Toast.makeText(MainDynamicsActivity.this,"成功删除",Toast.LENGTH_SHORT).show();
                                        finish();
                                        BmobQuery<DynamicsComment> query = new BmobQuery<>();
                                        query.addWhereEqualTo("dynamicsID", dynamics.getObjectId());
                                        query.findObjects(new FindListener<DynamicsComment>() {
                                            @Override
                                            public void done(List<DynamicsComment> list, BmobException e) {
                                                List<BmobObject> commentList = new ArrayList<BmobObject>();
                                                commentList.addAll(list);
                                                new BmobBatch().deleteBatch(commentList).doBatch(new QueryListListener<BatchResult>() {
                                                    @Override
                                                    public void done(List<BatchResult> list, BmobException e) {
                                                        if (e == null) {

                                                        }
                                                    }
                                                });

                                            }
                                        });


                                        String[] pic = dynamics.getPicture();
                                        BmobFile.deleteBatch(pic, new DeleteBatchListener() {

                                            @Override
                                            public void done(String[] failUrls, BmobException e) {
                                                if (e == null) {

                                                } else {
                                                    if (failUrls != null) {

                                                    } else {

                                                    }
                                                }
                                            }
                                        });



                                    }}
                            });


                        }
                        else {
                            Toast.makeText(MainDynamicsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });}



            default:
                break;
        }

        return true;
    }


    private void initLike(){

       final String[] like = dynamics.getLike();
          try {
              likecount.setText(like.length+"赞");
              BmobQuery<MyUser> query =new BmobQuery<>();
              query.addWhereContainedIn("objectId",Arrays.asList(like));
              query.findObjects(new FindListener<MyUser>() {
                  @Override
                  public void done(List<MyUser> list, BmobException e) {
                      if (e==null){
                          if(!list.isEmpty()){  for (int i =0; i<list.size();i++) {
                              Glide.with(MainDynamicsActivity.this).load(list.get(i).getAvatar())
                                      .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(bitmapTransform(new CropCircleTransformation())).into(avatarView[i]);
                              Log.d("","头像"+list.get(i).getAvatar());
                              if (i==5){
                                  break;
                              }
                          }
                          }
                      }
                  }
              });

          }
          catch (Exception e){
              e.printStackTrace();
          }



          }

    public void showPictureDialog(final int mPosition,final List<String> mListPicPath) {

        TextView content,likeCount,commentCount;

        //创建dialog
        mDialog = new Dialog(this, R.style.PictureDialog);
        final Window window1 = mDialog.getWindow() ;
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window1.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 1.0); // 改变的是dialog框在屏幕中的位置而不是大小
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕
        window1.setAttributes(p);
        View inflate = View.inflate(this, R.layout.picture_dialog, null);//该layout在后面po出
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.setContentView(inflate, layoutParams);


        commentCount = inflate.findViewById(R.id.comment);
        likeCount = inflate.findViewById(R.id.like);
        content = inflate.findViewById(R.id.content);
        content.setText(dynamics.getContent());
        commentCount.setText(dynamics.getReplycount()+"");
        likeCount.setText(dynamics.getLikeCount()+"");
        pager = (ViewPagerFixed) inflate.findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        PicturePageAdapter adapter = new PicturePageAdapter((ArrayList<String>) mListPicPath, this);
        pager.setAdapter(adapter);
        pager.setPageMargin(0);
        pager.setCurrentItem(mPosition);
        window1.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });
        mDialog.show();
        adapter.setOnPictureClickListener(new PicturePageAdapter.OnPictureClickListener() {
            @Override
            public void OnClick() {
                window1.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mDialog.dismiss();
            }
        });
        //长按图片保存
        adapter.setOnPictureLongClickListener(new PicturePageAdapter.OnPictureLongClickListener() {
            @Override
            public void OnLongClick() {
                //展示保存取消dialog
                final EasyPopup savePop = EasyPopup.create()
                        .setContentView(MainDynamicsActivity.this, R.layout.popup_savepic)
                        .setWidth(400)
                        .setBackgroundDimEnable(true)
                        //变暗的透明度(0-1)，0为完全透明
                        .setDimValue(0.4f)
                        //变暗的背景颜色
                        .apply();


                savePop.showAtAnchorView(pager, YGravity.CENTER, XGravity.CENTER, 0, 0);

                LinearLayout savepic = savePop.findViewById(R.id.save_pic);
                savepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadFile(mListPicPath.get(mPosition));
                            }
                        }).start();

                        savePop.dismiss();
                    }
                });
            }
        });
    }



    @Override
    public void onBackPressed() {
        if(ifReply){

            ifReply=false;
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            editComment.setHint("发表你的评论");
        }

        else {
            finish();
        }

    }
public void setSize(int size1){
        size=size1;
}

public void setCount(int state){
    if (state==0){
       commentCount=--commentCount;
    }
    else if (state==1){
        commentCount=++commentCount;
    }

}
    public void downloadFile(final String url) {
        DownLoadImageService service = new DownLoadImageService(getApplicationContext(),
                url,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {
                    }
                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap) {
                        // 在这里执行图片保存方法
                         runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainDynamicsActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                       runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainDynamicsActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
        //启动图片下载线程
        new Thread(service).start();


    }
}
