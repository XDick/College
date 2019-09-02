package com.college.xdick.findme.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.college.xdick.findme.MyClass.MyGlideEngine;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.GridViewAddImagesAdapter;
import com.college.xdick.findme.bean.Dynamics;
import com.college.xdick.findme.bean.Goods;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.util.ClassFileHelper;
import com.college.xdick.findme.util.FileUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class SetGoodsActivity extends BaseActivity {
    private GridViewAddImagesAdapter adapter;
    private List<Uri> mSelected = new ArrayList<>();
    private int REQUEST_CODE_CHOOSE = 1;
    private LinearLayout layout;
    private  EditText contentEdit,titleEdit,priceEdit,originalPriceEdit
            ,tagEdit,countEdit;
    private Button sendButton;
    private List<String> picPath = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setgoods);
        initView();

    }

    private void initView(){
        sendButton = findViewById(R.id.button);
        contentEdit= findViewById(R.id.content_edittext);
        titleEdit =findViewById(R.id.title_edittext);
        priceEdit = findViewById(R.id.price_edittext);
        originalPriceEdit = findViewById(R.id.original_price_edittext);
        tagEdit = findViewById(R.id.tag_edittext);
        countEdit = findViewById(R.id.count_edittext);

        adapter = new GridViewAddImagesAdapter(mSelected, this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        final float scale = getResources().getDisplayMetrics().density;
        GridView gridView = findViewById(R.id.choosegrid);
        adapter.setMaxImages(10);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Matisse.from(SetGoodsActivity.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(10-adapter.getCount())
                        .gridExpectedSize((int) (120 * scale + 0.5f))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.6f)
                        .theme( R.style.Matisse_FindMe)
                        .imageEngine(new MyGlideEngine())
                        .showSingleMediaType(true)
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });

        layout = findViewById(R.id.content_linearlayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentEdit.setFocusable(true);
                contentEdit.setFocusableInTouchMode(true);
                contentEdit.requestFocus();
                contentEdit.findFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(contentEdit, 0);
            }
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=0;
                for (Uri u : mSelected) {
                    String path= FileUtil.uriToFile(u, SetGoodsActivity.this);

                    File file=new File(path);
                    final String fileType = path
                            .substring(path.lastIndexOf("."));
                    final String renamePath;
                   if(num+1!=mSelected.size()){
                       renamePath="data/user/0/com.college.xdick.findme/cache/"
                               +"goods_image_"+"_"
                               + titleEdit.getText().toString()+"_"+num
                               +fileType;
                   }else {
                     renamePath="data/user/0/com.college.xdick.findme/cache/"
                               +"goods_image_"+"_"
                               + titleEdit.getText().toString()+"_"+"pay"
                               +fileType;
                   }

                    final File newFile = new File(renamePath);
                    try{
                        ClassFileHelper.copyFileTo(file,newFile);}
                    catch (IOException e){
                        e.printStackTrace(); }
                    picPath.add(renamePath);
                    ++num;
                }
                finish();
                BmobFile.uploadBatch(picPath.toArray(new String[picPath.size()]), new UploadBatchListener() {


                    @Override
                    public void onSuccess(List<BmobFile> list, List<String> list1) {

                        if (list1.size() == picPath.size()) {

                            for (String path :picPath){

                                ClassFileHelper.deleteFile(new File(path));
                            }
                            final Goods goods = new Goods();
                            goods.setTitle(titleEdit.getText().toString());
                            goods.setContent(contentEdit.getText().toString());
                            goods.setCover(list1.get(0));
                            goods.setPayPicture(list1.get(list1.size()-1));
                            goods.setOriginalPrice(Double.valueOf(originalPriceEdit.getText().toString()));
                            goods.setPrice(Double.valueOf(priceEdit.getText().toString()));
                            goods.setSaleNumber(Integer.valueOf(countEdit.getText().toString()));
                            String tag = tagEdit.getText().toString();
                            goods.addAll("tag", Arrays.asList( tag.split(" ")));
                            goods.addAll("picture", list1.subList(1,list1.size()-1));
                            goods.save(new SaveListener<String>() {

                                @Override
                                public void done(final String objectId, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(SetGoodsActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SetGoodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {
                        Toast.makeText(SetGoodsActivity.this, i1+"%", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                });
            }


        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            mSelected.addAll(Matisse.obtainResult(data));
            adapter.notifyDataSetChanged();


        }

    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
    finish();
                break;
    default:
            break;
}

        return true;
                }
}
