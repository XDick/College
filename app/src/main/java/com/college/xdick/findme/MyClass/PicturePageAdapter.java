package com.college.xdick.findme.MyClass;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.util.ArrayList;


public class PicturePageAdapter extends PagerAdapter {
    private ArrayList<String> mListUrls;
    private SparseArray<View> mViews = new SparseArray<>();
    private Context mContext;
    private int size;
    private OnPictureClickListener mOnPictureClickListener;
    private OnPictureLongClickListener mOnPictureLongClickListener;

    public PicturePageAdapter(ArrayList<String> listUrls, Context context) {
        mContext = context;
        mListUrls = listUrls;
        size = mListUrls == null ? 0 : mListUrls.size();
    }

    public SparseArray<View> getData(){
        return this.mViews;
    }

    public int getCount() {
        return size;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        arg0.removeView(mViews.get(arg1));
    }

    public void finishUpdate(View arg0) {
    }

    public Object instantiateItem(ViewGroup arg0, int arg1) {
        View view = mViews.get(arg1);
        if(view == null){
            //引入photoView
            PhotoView img = new PhotoView(mContext);
            try {
                img.setBackgroundColor(0xff000000);
                //Glide需要自己去引入了
                Glide
                        .with(mContext)
                        .load(mListUrls.get(arg1))
                        .into(img);

                img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                img.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(ImageView view, float x, float y) {
                        if (mOnPictureClickListener != null) {
                            mOnPictureClickListener.OnClick();
                        }
                    }

                });
                //设置长按图片监听
                img.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnPictureLongClickListener != null) {
                            mOnPictureLongClickListener.OnLongClick();
                        }
                        return false;
                    }
                });
                mViews.put(arg1,img);
                view = img;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ((ViewPagerFixed) arg0).addView(view);

        return view;
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * 图片监听接口
     */
    public interface OnPictureClickListener{
        void OnClick();
    }

    /**
     * 设置图片监听
     * @param onPictureClickListener
     */
    public void setOnPictureClickListener(OnPictureClickListener onPictureClickListener){
        mOnPictureClickListener = onPictureClickListener;
    }

    /**
     * 长按图片监听接口
     */
    public interface OnPictureLongClickListener{
        void OnLongClick();
    }

    /**
     * 设置长按图片监听
     * @param onPictureLongClickListener
     */
    public void setOnPictureLongClickListener(OnPictureLongClickListener onPictureLongClickListener){
        mOnPictureLongClickListener = onPictureLongClickListener;
    }
}