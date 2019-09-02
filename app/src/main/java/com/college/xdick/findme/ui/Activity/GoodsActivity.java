package com.college.xdick.findme.ui.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.college.xdick.findme.MyClass.DownLoadImageService;
import com.college.xdick.findme.MyClass.ImageDownLoadCallBack;
import com.college.xdick.findme.MyClass.MyBannerImageLoader;
import com.college.xdick.findme.MyClass.PicturePageAdapter;
import com.college.xdick.findme.MyClass.ViewPagerFixed;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.DynamicsComment;
import com.college.xdick.findme.bean.Goods;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import cn.bmob.v3.listener.UpdateListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class GoodsActivity extends BaseActivity {
    private TextView title,price,originalPrice,content,sale;
    private Banner banner;
    private Goods goods=new Goods();
    private Toolbar toolbar;
    private Dialog mDialog;
    private ViewPagerFixed pager;
    private Button buy;
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        goods = ((Goods)getIntent().getSerializableExtra("GOODS"));
        initView();
    }

    private void initView(){

        toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);
        toolbar.setTitle(goods.getTitle());

        title= findViewById(R.id.title);
        price = findViewById(R.id.price);
        originalPrice = findViewById(R.id.original_price);
        content = findViewById(R.id.content);
        banner = findViewById(R.id.banner);
        sale= findViewById(R.id.sale);

        sale.setText("限量:"+goods.getSaleNumber());
        title.setText(goods.getTitle());
        price.setText("￥"+goods.getPrice());
        originalPrice.setText("￥"+goods.getOriginalPrice());
        content.setText(goods.getContent());
        final List<String> image = new ArrayList<>();
        image.add(goods.getCover());
        image.addAll(Arrays.asList(goods.getPicture()));

        banner.setImageLoader(new MyBannerImageLoader());
        banner.setImages(image)
                .setDelayTime(3000)
                .setBannerAnimation(Transformer.DepthPage)
                .setBannerStyle(BannerConfig.NUM_INDICATOR)
                .setIndicatorGravity(BannerConfig.RIGHT);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
             showPictureDialog(position,image);
            }
        });
        banner.start();


        buy= findViewById(R.id.goods_buy_button);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GoodsActivity.this,"添加官方人员微信购买",Toast.LENGTH_LONG).show();
                showPictureDialog(0,Arrays.asList(goods.getPayPicture()));
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){


        if (myUser.isGod()){
            getMenuInflater().inflate(R.menu.toolbar_goods,menu);
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

            case  R.id.delete_goods:
            {
                Goods goods1 = new Goods();
                goods1.setObjectId(goods.getObjectId());
                goods1.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null) {
                            Toast.makeText(GoodsActivity.this,"成功删除",Toast.LENGTH_SHORT).show();
                            finish();
                            BmobQuery<DynamicsComment> query = new BmobQuery<>();

                            String[] pic1 = goods.getPicture();
                            String pic2 = goods.getCover();
                            String pic3 =goods.getPayPicture();
                            String[]pic = new String[pic1.length+2];
                            pic[0]=pic2;
                            pic[1]=pic3;
                            for (int i=0;i<pic1.length;i++){
                                pic[i+2]=pic1[i];
                            }
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

                break;
            }


            default:
                break;
        }

        return true;
    }

    public void showPictureDialog(final int mPosition, final List<String> uri) {


        //创建dialog
        mDialog = new Dialog(GoodsActivity.this, R.style.PictureDialog);
        final Window window1 = mDialog.getWindow() ;
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window1.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 1.0); // 改变的是dialog框在屏幕中的位置而不是大小
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕
        window1.setAttributes(p);
        View inflate = View.inflate(GoodsActivity.this, R.layout.chat_picture_dialog, null);//该layout在后面po出
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.setContentView(inflate, layoutParams);



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
        final List<String> mListPicPath = new ArrayList<>();
        mListPicPath.addAll(uri);
        PicturePageAdapter adapter = new PicturePageAdapter((ArrayList<String>) mListPicPath, GoodsActivity.this);
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
                        .setContentView(GoodsActivity.this, R.layout.popup_savepic)
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
                                downloadFile(uri.get(mPosition));
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GoodsActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GoodsActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
        //启动图片下载线程
        new Thread(service).start();}
}
