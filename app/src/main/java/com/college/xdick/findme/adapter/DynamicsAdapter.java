package com.college.xdick.findme.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.DownLoadImageService;
import com.college.xdick.findme.MyClass.ImageDownLoadCallBack;
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.DynamicsComment;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;
import com.college.xdick.findme.ui.Activity.UserCenterActivity;
import com.college.xdick.findme.ui.Fragment.AllDynamicsFragment;
import com.college.xdick.findme.ui.Fragment.MyDynamicsFragment;
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
import java.util.Date;
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
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.view.View.GONE;
import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DynamicsAdapter extends RecyclerView.Adapter<DynamicsAdapter.ViewHolder> {
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;

    private int load_more_status;
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;

    public static final int  NO_MORE=2;

    private int fragmentNum;



    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;
       private List<Dynamics> mDynamicsList;
       private  NineGridImageViewAdapter<String> mAdapter;
       private Context mContext;
        private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
         private Dialog mDialog;

           private ViewPagerFixed pager;
       private int flag=0;
       private MyDynamicsFragment fragment1;
       private UserCenterDynamicsFragment fragment2;
      private AllDynamicsFragment fragment3;



    static class ViewHolder extends RecyclerView.ViewHolder{

           TextView username,time,reply,content,likecount;
           LinearLayout layout;
           ImageView avatar,like,more;
           NineGridImageView gridImageView;

        TextView title,time2,host,join;
        ImageView cover;
        CardView cardView;

           public ViewHolder(View view){
               super(view);

               more = view.findViewById(R.id.dynamics_more);
              username = view.findViewById(R.id.username_main);
              time = view.findViewById(R.id.time_main);
              layout = view.findViewById(R.id.layout_main);
              reply = view.findViewById(R.id.reply_main);
              content = view.findViewById(R.id.content_main);
              avatar = view.findViewById(R.id.avatar_main);
              gridImageView= view.findViewById(R.id.nineGrid);
              like = view.findViewById(R.id.like);
              likecount=view.findViewById(R.id.likecount);


               title= view.findViewById(R.id.title_ac);
               cardView =view.findViewById(R.id.cardview_ac);
               cover = view.findViewById(R.id.cover_ac);
               time2= view.findViewById(R.id.time_ac);

               host = view.findViewById(R.id.host_ac);
           }

        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            }else{
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
       }


       public DynamicsAdapter(List<Dynamics> dynamics){
           mDynamicsList = dynamics;
       }


       public void SetFlag(int flag){
           this.flag=flag;
       }

       public void setFragment(MyDynamicsFragment fragment){
        fragment1=fragment;
        fragmentNum=1;
       }
    public void setFragment(UserCenterDynamicsFragment fragment){
        fragment2=fragment;
        fragmentNum=2;
    }

    public void setFragment(AllDynamicsFragment fragment){
        fragment3=fragment;
        fragmentNum=3;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (mContext == null) {
            mContext = parent.getContext();
        }


        if (viewType == ITEM_TYPE_HEADER) {
            return new DynamicsAdapter.ViewHolder(mHeaderView);
        } else if (viewType == ITEM_TYPE_EMPTY) {
            return new DynamicsAdapter.ViewHolder(mEmptyView);
        } else if (viewType == ITEM_TYPE_FOOTER) {

            return new DynamicsAdapter.ViewHolder(mFooterView);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dynamics, parent, false);
            final ViewHolder holder = new ViewHolder(view);










            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (fragmentNum==2){
                        return;
                    }
                    int position = holder.getAdapterPosition();
                    final Dynamics dynamics = mDynamicsList.get(position);

                    BmobQuery<MyUser> query = new BmobQuery<>();
                    query.getObject(dynamics.getMyUser().getObjectId(), new QueryListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            if (e==null){
                               if (flag==0){
                                   Intent intent = new Intent(mContext, UserCenterActivity.class);
                                   intent.putExtra("USER", myUser);
                                   mContext.startActivity(intent);
                               }
                               else {
                                   startChatting(myUser.getObjectId(),myUser.getUsername(),myUser.getAvatar());
                               }

                            }
                        }
                    });

        }});


            return holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mHeaderView && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (null != mFooterView
                && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        }
        if (null != mEmptyView && mDynamicsList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        if (type == ITEM_TYPE_FOOTER) {
            TextView textView= mFooterView.findViewById(R.id.footer_text);
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    textView.setText("上拉加载更多");
                    break;
                case LOADING_MORE:
                    textView.setText("正在加载数据...");
                    break;

                case NO_MORE:
                    textView.setText("没有更多了");
                    break;

        }
            return;
        }

        final int realPos = getRealItemPosition(position);


        final EasyPopup easyPopup = EasyPopup.create()
                .setContentView(mContext, R.layout.popup_dynamics)
                .setWidth(400)
                .setBackgroundDimEnable(true)
                //变暗的透明度(0-1)，0为完全透明
                .setDimValue(0.4f)
                //变暗的背景颜色
                .apply();

        final LinearLayout delete = easyPopup.findViewById(R.id.dynamics_delete);
        final LinearLayout report = easyPopup.findViewById(R.id.dynamics_report);

      final Dynamics dynamics = mDynamicsList.get(realPos);


                    Glide.with(mContext).load(new mGlideUrl(dynamics.getMyUser().getAvatar() +"!/fp/3000"))
                            .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(bitmapTransform(new CropCircleTransformation())).into(holder.avatar);


                    holder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                            intent.putExtra("DYNAMICS", dynamics);
                            intent.putExtra("USER",dynamics.getMyUser());

                            mContext.startActivity(intent);
                        }
                    });







        mAdapter = new NineGridImageViewAdapter<String>() {

            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String photo) {
                Glide.with(mContext).load(new mGlideUrl(photo) +"!/min/150")
                        .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .into(imageView);
            }

            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

            @Override
            protected void onItemImageClick(Context context, ImageView imageView, final int index, final List<String> photoList) {
                List<String> picList = new  ArrayList<>(photoList);
                showPictureDialog(index,picList,dynamics,dynamics.getMyUser());


            }
        };

        holder.gridImageView.setAdapter(mAdapter);

      if (dynamics.getActivity()==null){
          holder.cardView.setVisibility(GONE);
      }
      else {


          holder.cardView.setVisibility(View.VISIBLE);

          if (dynamics.getActivity().getHost() == null) {
              holder.title.setText("该活动已被删除");

              holder.time2.setText("");

              Glide.with(mContext).load(mContext.getDrawable(R.drawable.icon)).apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(holder.cover);

              holder.host.setText("");
              holder.cardView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      //

                  }
              });
          }  else {

              holder.title.setText(dynamics.getActivity().getTitle());

              holder.time2.setText(dynamics.getActivity().getTime());

              Glide.with(mContext).load(new mGlideUrl(dynamics.getActivity().getCover() + "!/fp/5000")).apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(holder.cover);

              holder.host.setText("由" + dynamics.getActivity().getHost().getUsername() + "发起");

              holder.cardView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      BmobQuery<MyActivity> query = new BmobQuery<>();
                      query.include("host[avatar|username|Exp]");
                      query.getObject(dynamics.getActivity().getObjectId(), new QueryListener<MyActivity>() {
                          @Override
                          public void done(MyActivity activity, BmobException e) {
                              if (e == null) {

                                  Intent intent = new Intent(mContext, ActivityActivity.class);
                                  intent.putExtra("ACTIVITY", activity);
                                  mContext.startActivity(intent);
                              }
                          }
                      });


                  }
              });
          }
      }




                    final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String  bombtime = sdf.format(new Date( ));
                    String rawdate =dynamics.getCreatedAt();
                    String date=rawdate.substring(0,rawdate.indexOf(" "));
                    String time = rawdate.substring(rawdate.indexOf(" ")+1,rawdate.length()-3);
                   if (bombtime.equals(date)){
                       holder.time.setText(time);
                   }
                   else{

                       holder.time.setText(rawdate.substring(0,rawdate.length()-3));
                   }

                   report.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Toast.makeText(mContext,"举报成功，感谢反馈",Toast.LENGTH_SHORT).show();
                           easyPopup.dismiss();
                           sendMessage(myUser.getUsername()+"举报此动态违规"
                                   ,new BmobIMUserInfo("90b5bdad85","叉地克",""),
                                   dynamics.getObjectId(), dynamics.getContent());
                       }
                   });


                   String username="";
                  try{
                      username=dynamics.getMyUser().getUsername();
                  }
                  catch (NullPointerException e){
                      e.printStackTrace();
                  }


           if (username.equals(myUser.getUsername())|| myUser.isGod()){

               delete.setVisibility(View.VISIBLE);
               delete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       easyPopup.dismiss();
                       MyUser myUser1= new MyUser();
                       myUser1.setObjectId(myUser.getObjectId());
                       myUser1.increment("dynamicsCount",-1);
                       myUser1.update(new UpdateListener() {
                           @Override
                           public void done(BmobException e) {
                               if (e==null){

                                   Dynamics dynamics1 = new Dynamics();
                                   dynamics1.setObjectId(dynamics.getObjectId());
                                   dynamics1.delete(new UpdateListener() {
                                       @Override
                                       public void done(BmobException e) {
                                           if (e==null) {
                                              switch (fragmentNum){
                                                  case 1:
                                                  {
                                                      fragment1.setSize(0);
                                                      fragment1.initData(MyDynamicsFragment.REFRESH);
                                                      break;
                                                  }
                                                  case 2:
                                                      fragment2.setSize(0);
                                                      fragment2.initData(UserCenterDynamicsFragment.REFRESH);
                                                      break;

                                                  case 3:
                                                  {
                                                      fragment3.setSize(0);
                                                      fragment3.initData(MyDynamicsFragment.REFRESH);
                                                      break;
                                                  }

                                                      default:break;


                                              }
                                               Toast.makeText(mContext,"成功删除",Toast.LENGTH_SHORT).show();

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
                                                          // Toast.makeText(mContext,"全部删除成功",Toast.LENGTH_SHORT).show();
                                                       } else {
                                                          // Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                           if (failUrls != null) {
                                                               //Toast.makeText(mContext,"删除失败个数："+failUrls.length,Toast.LENGTH_SHORT).show();
                                                           } else {
                                                               //Toast.makeText(mContext,"全部删除失败：",Toast.LENGTH_SHORT).show();
                                                           }
                                                       }
                                                   }
                                               });



                                           }}
                                   });


                               }
                               else {
                                   Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                           }
                       });








                   }
               });
           }
           else {
               delete.setVisibility(GONE);
           }

                   holder.more.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {


                           easyPopup.showAtAnchorView(holder.more, YGravity.ABOVE, XGravity.LEFT, 0, -50);
                       }
                   });





                    holder.gridImageView.setVisibility(View.VISIBLE);
                    holder.content.setVisibility(View.VISIBLE);
                    holder.username.setText(dynamics.getMyUser().getUsername());

                    holder.reply.setText(dynamics.getReplycount() + "");

                    if (dynamics.getContent().equals("")) {
                        holder.content.setVisibility(GONE);
                    } else {

                        holder.content.setText(dynamics.getContent());
                    }





                    if (dynamics.getPicture() == null) {
                        holder.gridImageView.setVisibility(GONE);
                    } else {


                        holder.gridImageView.setImagesData(Arrays.asList(dynamics.getPicture()));
                    }

                    final List<String> likelist;
                    if (dynamics.getLike() != null) {
                        List<String> like1 = Arrays.asList(dynamics.getLike());
                        likelist = new ArrayList<>(like1);
                        holder.likecount.setText(dynamics.getLike().length + "");
                    } else {
                        likelist = new ArrayList<>();
                        holder.likecount.setText("0");

                    }


                    if (Arrays.toString(dynamics.getLike()).contains(BmobUser.getCurrentUser().getObjectId())) {
                        holder.like.setBackground(mContext.getResources().getDrawable(R.drawable.thumb_up_t));
                    }
                    else {
                        holder.like.setBackground(mContext.getResources().getDrawable(R.drawable.thumb_up));}


                    holder.like.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (Arrays.toString(likelist.toArray(new String[  likelist.size()])).contains(
                                    BmobUser.getCurrentUser().getObjectId())) {

                                Dynamics dynamics1 = new Dynamics();
                                dynamics1.setObjectId(dynamics.getObjectId());
                                dynamics1.setIfAdd2Gallery(dynamics.isIfAdd2Gallery());
                                dynamics1.increment("likeCount" ,-1);
                                dynamics1.removeAll("like", Arrays.asList(BmobUser.getCurrentUser().
                                        getObjectId()));
                                dynamics1.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            likelist.removeAll(Arrays.asList(BmobUser.getCurrentUser().
                                                    getObjectId()));
                                            dynamics.setLike(  likelist.toArray(new String[  likelist.size()]));
                                            holder.likecount.setText(  likelist.size() + "");
                                            holder.like.setBackground(mContext.getResources().getDrawable(R.drawable.thumb_up));
                                        }

                                    }
                                });

                            }

                            else {

                                Dynamics dynamics1 = new Dynamics();
                                dynamics1.setObjectId(dynamics.getObjectId());
                                dynamics1.setIfAdd2Gallery(dynamics.isIfAdd2Gallery());
                                dynamics1.increment("likeCount" ,1);
                                dynamics1.addUnique("like",BmobUser.getCurrentUser().
                                        getObjectId());

                                dynamics1.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            sendMessage(myUser.getUsername() + "点赞了你的动态",
                                                    new BmobIMUserInfo(dynamics.getMyUser().getObjectId(), dynamics.getMyUser().getUsername(), null),
                                                    dynamics.getObjectId(), dynamics.getContent());


                                            likelist.add(BmobUser.getCurrentUser().
                                                    getObjectId());

                                            dynamics.setLike(  likelist.toArray(new String[likelist.size()]));
                                            holder.likecount.setText(  likelist.size() + "");
                                            holder.like.setBackground(mContext.getResources().getDrawable(R.drawable.thumb_up_t));

                                        }
                                    }
                                });
                            }
                        }
                    });






            }

    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }


    @Override
    public int getItemCount() {
        if(mDynamicsList!=null){
            int itemCount = mDynamicsList.size();
            if (null != mEmptyView && itemCount == 0) {
                itemCount++;
            }
            if (null != mHeaderView) {
                itemCount++;
            }
            if (null != mFooterView) {
                itemCount++;
            }
            return itemCount;
        }


           return 0;






    }
    public void addHeaderView(View view) {
        mHeaderView = view;
        notifyItemInserted(0);
    }

    public void addFooterView(View view) {
        mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setEmptyView(View view) {
        mEmptyView = view;
        notifyDataSetChanged();
    }










    public void showPictureDialog(final int mPosition, final List<String> mListPicPath,
                                  final Dynamics dynamics,final MyUser user) {
       TextView content,likeCount,commentCount;
       final LinearLayout clickToDetail;



        //创建dialog
        mDialog = new Dialog(mContext, R.style.PictureDialog);
        final Window window1 = mDialog.getWindow() ;
        WindowManager m = ((Activity)mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window1.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 1.0); // 改变的是dialog框在屏幕中的位置而不是大小
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕
        window1.setAttributes(p);
        View inflate = View.inflate(mContext, R.layout.picture_dialog, null);//该layout在后面po出
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.setContentView(inflate, layoutParams);


        commentCount = inflate.findViewById(R.id.comment);
        likeCount = inflate.findViewById(R.id.like);
        content = inflate.findViewById(R.id.content);
        clickToDetail=inflate.findViewById(R.id.layout_to_dynamics);
        content.setText(dynamics.getContent());
        commentCount.setText(dynamics.getReplycount()+"");
        likeCount.setText(dynamics.getLikeCount()+"");

        clickToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                            clickToDetail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                    intent.putExtra("DYNAMICS", dynamics);
                                    intent.putExtra("USER",user);

                                    mContext.startActivity(intent);
                                }
                            });



                        }



        });
        pager = inflate.findViewById(R.id.gallery01);
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
        PicturePageAdapter adapter = new PicturePageAdapter((ArrayList<String>) mListPicPath, mContext);
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
                        .setContentView(mContext, R.layout.popup_savepic)
                        .setWidth(400)
                        .setBackgroundDimEnable(true)
                        //变暗的透明度(0-1)，0为完全透明
                        .setDimValue(0.4f)
                        //变暗的背景颜色
                        .apply();


                 savePop.showAtAnchorView(pager,YGravity.CENTER, XGravity.CENTER, 0, 0);

                 LinearLayout savepic = savePop.findViewById(R.id.save_pic);
                LinearLayout downloadpic = savePop.findViewById(R.id.download_pic);
                downloadpic.setOnClickListener(new View.OnClickListener() {
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
                 savepic.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         new Thread(new Runnable() {
                             @Override
                             public void run() {
                                 downloadFile(mListPicPath.get(mPosition)+"!/scale/80");
                             }
                         }).start();

                         savePop.dismiss();
                     }
                 });
            }
        });
    }



    public void sendMessage(String content ,BmobIMUserInfo info,String objectId,String objectTitle) {

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
            map.put("activityid",objectId);
            map.put("activityname",objectTitle);
            map.put("type","dynamics");
            msg.setExtraMap(map);
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {//发送成功

                    } else {//发送失败
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,"保存成功",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onDownLoadFailed() {
                            // 图片保存失败
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
            //启动图片下载线程
            new Thread(service).start();


    }








    private void startChatting(String id,String name,String avatar){

        BmobIMUserInfo info = new BmobIMUserInfo(id, name, avatar);
        BmobIMConversation conversationEntrance=  BmobIM.getInstance().startPrivateConversation( info,null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("c",conversationEntrance);
        Intent intent = new Intent(mContext,
                ChatActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);


    }

    public void changeMoreStatus(int status){
        load_more_status=status;
    }



}
