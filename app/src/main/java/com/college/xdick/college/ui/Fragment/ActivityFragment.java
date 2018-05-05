package com.college.xdick.college.ui.Fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.college.R;
import com.college.xdick.college.adapter.ActivityAdapter;

import com.college.xdick.college.bean.MyActivity;
import com.college.xdick.college.bean.MyCircleBanner;
import com.college.xdick.college.bean.MyUser;

import com.college.xdick.college.ui.Activity.DynamicsActivity;
import com.college.xdick.college.ui.Activity.LoginActivity;
import com.college.xdick.college.ui.Activity.MainActivity;
import com.college.xdick.college.ui.Activity.SetActivitiyActivity;
import com.college.xdick.college.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;
import static cn.bmob.newim.core.BmobIMClient.getContext;

/**
 * Created by Administrator on 2018/4/11.
 */

public class ActivityFragment extends Fragment {
  static int flag =0;
    String picturePath="";
    View rootview;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private View headview;
    private TextView username;
    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    static private List<MyActivity> activityList= new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private ActivityAdapter adapter;
    private ImageView head;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =inflater.inflate(R.layout.fragment_activity,container,false);

        if(flag==0){
            FirstInitFromActivity();
        }

        initBaseView();
        initRecyclerView();
        setHasOptionsMenu(true);
        BmobCheckIfLogin();

        return rootview;
    }










    private void initBaseView(){
        Toolbar toolbar =rootview.findViewById(R.id.toolbar_ac);
        toolbar.setTitle("自己找活动");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        swipeRefresh =rootview.findViewById(R.id.swipe_refresh_ac);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mDrawerLayout = rootview.findViewById(R.id.drawer_layout_ac);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.drawer);
        navigationView =rootview.findViewById(R.id.nav_view_ac);
        headview = navigationView.inflateHeaderView(R.layout.nav_header);
        head = headview.findViewById(R.id.header);
        if(bmobUser!=null){
        Glide.with(getContext()).load(bmobUser.getAvatar()).into(head);
        head.setBackground(null);}
        navigationView.inflateMenu(R.menu.nav_menu);
        headview = navigationView.getHeaderView(0);
        username =headview.findViewById(R.id.username);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.me:

                        break;

                    default:
                        break;

                }

                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }


    private void showUserInfo(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                username.setText(bmobUser.getUsername());
            }
        });
    }
    private void exitUserInfo(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                username.setText("点击头像以登录");
            }
        });
    }





    private void initRecyclerView(){

        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_ac);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
       adapter = new ActivityAdapter(activityList);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.item_banner, recyclerView, false);
        MyCircleBanner mBanner = header.findViewById(R.id.banner1);

// 设置数据源
        List<String> mInfos = new ArrayList<>();
        mInfos.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525524410260&di=11f8a52e59eb0e9f69712638c84378eb&imgtype=0&src=http%3A%2F%2Fwww.psjia.com%2Fuploads%2Fallimg%2F141018%2F20103513U-1.jpg");
        mInfos.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525524435385&di=c06d5f3f9c461f083fac9869fc63211e&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0128255966e88fa8012193a3081d0c.jpg");
        mInfos.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525524455341&di=83b5ae0736791cffde21b34e76d4962e&imgtype=0&src=http%3A%2F%2Fpic26.photophoto.cn%2F20130218%2F0017030048084638_b.jpg");
       mInfos.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525524493057&di=2051a5df3d3443dbc6634cfbf8605751&imgtype=0&src=http%3A%2F%2Fpic32.nipic.com%2F20130814%2F3961389_185504091311_2.jpg");
       mInfos.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1815436007,1600791990&fm=27&gp=0.jpg");
        // 使用mBanner的接口，直接自动播放
        mBanner.play(mInfos);


        adapter.addHeaderView(header);
        recyclerView.setAdapter(adapter);

    }

    private void initData(){

       if(activityList!=null) {activityList.clear();}

        BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(99);
//执行查询方法
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> object, BmobException e) {
                if(e==null){
                    for (MyActivity activity : object) {
                        activityList.add(activity);}
                    Collections.reverse(activityList); // 倒序排列
                    // Toast.makeText(getContext(),"成功接收内容",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getContext(),"网络不佳",Toast.LENGTH_SHORT).show();
                }
            }
        });





    }






    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.toolbar_set_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.set_activity:
                if(bmobUser!=null){
                    Intent intent = new Intent(getContext(), SetActivitiyActivity.class);
                    startActivity(intent);}
                else
                {Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }

        return true;
    }


    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    initData();
                    Thread.sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void BmobCheckIfLogin(){


        if(bmobUser != null){
            showUserInfo();

            headview.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                /**
                                                 *以带结果的方式启动Intent，这样就可以拿到图片地址
                                                 */
                                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                intent.setType("image/*");
                                                startActivityForResult(intent, 0);

                                            }

                                        }

            );

        }else{
            headview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                }

            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            try {
                Uri originalUri = data.getData();//获得图片的uri
                if(originalUri ==null){
                    geturi(data);
                }
                FileUtil fileUtil=new FileUtil();
                Log.d("TAG","uri是："+originalUri.toString());
                picturePath= fileUtil.getFilePathByUri(getContext(),originalUri);


            } catch (Exception e) {
                Log.e("TAG","错误："+ e.toString());
            }


            finally {
                final BmobFile bmobFile = new BmobFile(new File(picturePath));
                bmobFile.uploadblock(new UploadFileListener() {

                    @Override
                    public void done(BmobException e) {

                              bmobUser.setAvatar(bmobFile.getFileUrl());
                        bmobUser.update(bmobUser.getObjectId(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(getContext(),"修改头像成功",Toast.LENGTH_SHORT).show();
                                    Glide.with(getContext()).load(bmobUser.getAvatar()).into(head);
                                }else{

                                }
                            }
                        });


                                }






                    }
                );}
            }
        }







    public void uploadPic(){
        final BmobFile bmobFile = new BmobFile(new File(picturePath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null) {
                    bmobUser.setAvatar(bmobFile.getFileUrl());
                    Toast.makeText(getContext(), "上传文件成功:" + bmobFile.getFileUrl(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                }



            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });

    }

    private  void FirstInitFromActivity(){
        MainActivity activity = (MainActivity) getActivity();

        if(activityList.isEmpty()){
            activityList = activity.getListData();
        }
        flag++;
    }


    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = getContext().getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }
}
