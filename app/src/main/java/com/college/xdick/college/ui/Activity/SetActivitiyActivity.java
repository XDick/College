package com.college.xdick.college.ui.Activity;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.college.xdick.college.R;


import com.college.xdick.college.bean.MyActivity;

import com.college.xdick.college.bean.MyUser;
import com.college.xdick.college.util.FileUtil;

import java.io.File;
import java.util.Set;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static cn.bmob.newim.core.BmobIMClient.getContext;

/**
 * Created by Administrator on 2018/4/11.
 */

public class SetActivitiyActivity extends AppCompatActivity {
    EditText titleE,timeE,placeE,contentE;
    Button uploadButton;
    String picturePath = " ";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setactivity);
         initView();
    }


    private void  initView()
    {


        titleE= findViewById(R.id.setac_title_edittext);
        timeE = findViewById(R.id.setac_time_edittext);
        placeE= findViewById(R.id.setac_place_edittext);
        contentE = findViewById(R.id.setac_content_edittext);
        uploadButton = findViewById(R.id.ac_upload_pic_button);

        Toolbar toolbar =findViewById(R.id.toolbar_setac);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){ actionBar.setDisplayHomeAsUpEnabled(true);}


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否6.0以上的手机   不是就不用
                if(!isGrantExternalRW(SetActivitiyActivity.this)){
                    return;
                }
                else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, 0);

                }
                /**
                 *以带结果的方式启动Intent，这样就可以拿到图片地址
                 */


            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.toolbar_activity_menu, menu);
        return true;
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.start_activity:

                final MyActivity activity = new MyActivity();
                final String content = contentE.getText().toString();
                final String title = titleE.getText().toString();
                final String time = timeE.getText().toString();
                final String place = placeE.getText().toString();


                if(picturePath!=null){
                    final BmobFile bmobFile = new BmobFile(new File(picturePath));
                    bmobFile.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //Toast.makeText(getContext(), "上传文件成功:" + bmobFile.getFileUrl(), Toast.LENGTH_LONG).show();
                                String coverURL = bmobFile.getFileUrl();
                                activity.setCover(coverURL);
                                activity.setTitle(title);
                                activity.setTime(time);
                                activity.setContent(content);
                                activity.setPlace(place);
                                activity.setHostBy(BmobUser.getCurrentUser(MyUser.class).getUsername());
                                activity.save(new SaveListener<String>() {

                                    @Override
                                    public void done(String objectId, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(SetActivitiyActivity.this, "发起成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });
                            } else

                            {
                                Toast.makeText(getContext(), picturePath + e.getMessage(), Toast.LENGTH_LONG).show();
                            }




                        }
                    });}

                finish();

                break;

            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
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
               picturePath= fileUtil.getFilePathByUri(this,originalUri);
            } catch (Exception e) {
                Log.e("TAG","错误："+ e.toString());
            }
        }
    }



    public static boolean isGrantExternalRW(android.app.Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }



    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
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
