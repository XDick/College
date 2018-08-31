package com.college.xdick.findme.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
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
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.bean.School;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class MorePicAdapter extends RecyclerView.Adapter<MorePicAdapter.ViewHolder> {

    private Map<String,Dynamics> picMap;
    private List<String> picList;
    private Context mContext;
    private Dialog mDialog;
    private ViewPagerFixed pager;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.more_pic_imageview);
        }



    }


   public MorePicAdapter(List<String>list1,Map<String,Dynamics> list2){
       picMap= list2;
        picList =list1;
   }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mContext=parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_morepic,parent,false);

        ViewHolder holder = new ViewHolder(view);




        return holder;
    }





    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {



            String uri =picList.get(position);
            Glide.with(mContext).load(uri).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(position,picList,picMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return picList.size();
    }

    public void showPictureDialog(final int mPosition, final List<String> mListPicPath,
                                  final Map<String,Dynamics>map) {




        final LinearLayout clickToDetail;
        final TextView content,likeCount,commentCount;


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




        if (mPosition==0){
            content.setText("");
            commentCount.setText(0+"");
            likeCount.setText(0+"");
            clickToDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {}});


        }
        else {
            content.setText(map.get(mListPicPath.get(mPosition)).getContent());
            commentCount.setText(map.get(mListPicPath.get(mPosition)).getReplycount()+"");
            likeCount.setText(map.get(mListPicPath.get(mPosition)).getLikeCount()+"");
            clickToDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyUser.getCurrentUser(MyUser.class)==null){
                        mContext.startActivity(new Intent(mContext,LoginActivity.class));
                        ((Activity)mContext).finish();
                        Toast.makeText(mContext,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                    query.addWhereEqualTo("username", map.get(mListPicPath.get(mPosition)).getUser());
                    query.getObject(map.get(mListPicPath.get(mPosition)).getUserId(), new QueryListener<MyUser>() {

                        @Override
                        public void done(final MyUser object, BmobException e) {
                            if (e == null) {

                                Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                intent.putExtra("DYNAMICS", map.get(mListPicPath.get(mPosition)));
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
                        content.setText(map.get(mListPicPath.get(position)).getContent());
                        commentCount.setText(map.get(mListPicPath.get(position)).getReplycount()+"");
                        likeCount.setText(map.get(mListPicPath.get(position)).getLikeCount()+"");
                        clickToDetail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (MyUser.getCurrentUser(MyUser.class)==null){
                                    mContext.startActivity(new Intent(mContext,LoginActivity.class));
                                    ((Activity)mContext).finish();
                                    Toast.makeText(mContext,"请先登录（*＾-＾*）",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                                query.addWhereEqualTo("username", map.get(mListPicPath.get(position)).getUser());
                                query.getObject(map.get(mListPicPath.get(position)).getUserId(), new QueryListener<MyUser>() {

                                    @Override
                                    public void done(final MyUser object, BmobException e) {
                                        if (e == null) {

                                            Intent intent = new Intent(mContext, MainDynamicsActivity.class);
                                            intent.putExtra("DYNAMICS", map.get(mListPicPath.get(position)));
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
                //showPicDialog();
            }
        });
    }
}
