package com.college.xdick.findme.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.MyClass.DownLoadImageService;
import com.college.xdick.findme.MyClass.ImageDownLoadCallBack;
import com.college.xdick.findme.MyClass.PicDynamicsMap;
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
import com.college.xdick.findme.MyClass.mGlideUrl;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.GalleryActivity;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;
import com.college.xdick.findme.util.AppManager;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.w3c.dom.Text;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/5/19.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>

{
    private int ITEM_TYPE_NORMAL = 0;
    private int ITEM_TYPE_HEADER = 1;
    private int ITEM_TYPE_FOOTER = 2;
    private int ITEM_TYPE_EMPTY = 3;




    private Map<String,Dynamics> mPicMap;
    private List<String> uriList;
    private String activityId;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;

    private Dialog mDialog;
    private ViewPagerFixed pager;




    static class ViewHolder extends RecyclerView.ViewHolder{


        ImageView pic;
        TextView more;




        public ViewHolder(View view){
            super(view);
        pic = view.findViewById(R.id.gallery_pic);
        more = view.findViewById(R.id.gallery_more);

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



    public GalleryAdapter(Map<String,Dynamics> map,List<String> pic,String activityId){
        mPicMap=map;
        uriList=pic;
        this.activityId=activityId;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        if(mContext == null){
            mContext = parent.getContext();
        }


        if (viewType == ITEM_TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else if (viewType == ITEM_TYPE_EMPTY) {
            return new ViewHolder(mEmptyView);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new ViewHolder(mFooterView);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_gallery, parent, false);
            final ViewHolder holder = new GalleryAdapter.ViewHolder(view);

            WindowManager wm = (WindowManager)mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();


            FrameLayout frameLayout = view.findViewById(R.id.gallery_layout);
            ViewGroup.LayoutParams para = frameLayout.getLayoutParams();
            para.width=width-dp2px(120);
            frameLayout.setLayoutParams(para);






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
        if (null != mEmptyView && uriList.size() == 0){
            return ITEM_TYPE_EMPTY;
        }
        return ITEM_TYPE_NORMAL;

    }


    @Override
    public void onBindViewHolder(final GalleryAdapter.ViewHolder holder, int position) {

        int type = getItemViewType(position);

        if (type == ITEM_TYPE_HEADER
                || type == ITEM_TYPE_FOOTER
                || type == ITEM_TYPE_EMPTY) {
            return;
        }


        final int realPos = getRealItemPosition(position);
        String picUri = uriList.get(realPos);






            holder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (realPos==0){
                        Intent intent = new Intent(mContext, GalleryActivity.class);
                        PicDynamicsMap map=new PicDynamicsMap();
                        map.setMap(mPicMap);
                        intent.putExtra("MAP",map);
                        intent.putStringArrayListExtra("PIC",(ArrayList<String>) uriList);
                        intent.putExtra("ACTIVITYID",activityId);
                        mContext.startActivity(intent);

                    }
                    else
                    {

                        showPictureDialog(realPos,uriList,mPicMap);
                    }

                }
            });


        if (realPos==0){
            Glide.with(mContext).load(new mGlideUrl(picUri +"!/fp/1000"))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).apply(bitmapTransform(new BlurTransformation(10, 8))).into(holder.pic);

            holder.more.setVisibility(View.VISIBLE);
        }
        else {
            holder.more.setVisibility(View.INVISIBLE);
            Glide.with(mContext).load(new mGlideUrl(picUri +"!/scale/40"))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).into(holder.pic);
        }




    }
    private int getRealItemPosition(int position) {
        if (null != mHeaderView) {
            return position - 1;
        }
        return position;
    }



    @Override
    public int getItemCount() {

        if (uriList!=null) {

            int itemCount = uriList.size();
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

    private int dp2px(int dpValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,
                mContext.getResources().getDisplayMetrics());
    }


    public void showPictureDialog(final int mPosition,final List<String> mListPicPath,
                                 final Map<String,Dynamics> map) {






        final LinearLayout clickToDetail;
        final TextView content,likeCount,commentCount;


        //创建dialog
        mDialog = new Dialog(mContext, R.style.PictureDialog);
        final Window window1 = mDialog.getWindow() ;
        final WindowManager m = ((Activity)mContext).getWindowManager();
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


        content.setText(map.get(uriList.get(mPosition)).getContent());


       if (mPosition==0){
           commentCount.setText(0+"");
           likeCount.setText(0+"");
           clickToDetail.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {}});


       }
       else {
           commentCount.setText(map.get(uriList.get(mPosition)).getReplycount()+"");
           likeCount.setText(map.get(uriList.get(mPosition)).getLikeCount()+"");
           clickToDetail.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   if (MyUser.getCurrentUser(MyUser.class)==null){
                      AppManager.getAppManager().finishAllActivity();
                       mContext.startActivity(new Intent(mContext,LoginActivity.class));
                      // Toast.makeText(mContext,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                       return;
                   }
                   BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                   query.addWhereEqualTo("username", map.get(uriList.get(mPosition)).getMyUser().getUsername());
                   query.getObject(map.get(uriList.get(mPosition)).getMyUser().getObjectId(), new QueryListener<MyUser>() {

                       @Override
                       public void done(final MyUser object, BmobException e) {
                           if (e == null) {

                               Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                               intent.putExtra("DYNAMICS", map.get(uriList.get(mPosition)));
                               intent.putExtra("USER", object);
                               mContext.startActivity(intent);
                           }
                       }


                   });


               }


           });

       }





        pager = inflate.findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

                if (position==0){
                    content.setText("");
                    commentCount.setText(0+"");
                    likeCount.setText(0+"");
                    clickToDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    } );
                }
                else {
                    content.setText(map.get(uriList.get(position)).getContent());
                    commentCount.setText(map.get(uriList.get(position)).getReplycount()+"");
                    likeCount.setText(map.get(uriList.get(position)).getLikeCount()+"");
                    clickToDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (MyUser.getCurrentUser(MyUser.class)==null){
                                mContext.startActivity(new Intent(mContext,LoginActivity.class));
                                ((Activity)mContext).finish();
                               // Toast.makeText(mContext,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                            query.addWhereEqualTo("username", map.get(uriList.get(position)).getMyUser().getUsername());
                            query.getObject(map.get(uriList.get(position)).getMyUser().getObjectId(), new QueryListener<MyUser>() {

                                @Override
                                public void done(final MyUser object, BmobException e) {
                                    if (e == null) {

                                        Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                        intent.putExtra("DYNAMICS", map.get(uriList.get(position)));
                                        intent.putExtra("USER", object);
                                        mContext.startActivity(intent);
                                    }
                                }

                            });


                        }

                    });


                }



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


                savePop.showAtAnchorView(pager, YGravity.CENTER, XGravity.CENTER, 0, 0);

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
}




