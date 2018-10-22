package com.college.xdick.findme.ui.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.SearchActivityAdapter;
import com.college.xdick.findme.adapter.SearchUserAdapter;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/6/3.
 */

public class SearchUserActivity extends BaseActivity {
     Toolbar toolbar;
    RecyclerView recyclerViewUser;
    SearchUserAdapter adapter;
    List<MyUser> userList = new ArrayList<>();
    private int size =10;
    private boolean ifEmpty=false;

    static public int JOIN=0;
    static public int SEARCH=1;
    static public int FOLLOWING=2;
    static public int FOLLOWED=3;




    private int signal;
    private int followingFlag=0;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        try {
            userList.addAll((List<MyUser>)getIntent().getSerializableExtra("USERLIST"));
        }catch (Exception e){
            e.printStackTrace();
        }

        signal=getIntent().getIntExtra("SIGNAL",10);

        if (signal==FOLLOWING||signal==FOLLOWED){
            size=0;
            initData(signal);
        }

        toolbar =findViewById(R.id.toolbar_search);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(toolbar);
        initRecyclerView();






    }


    private void initRecyclerView(){
        recyclerViewUser = findViewById(R.id.recyclerview_user);
       final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewUser.setLayoutManager(layoutManager);

        adapter = new SearchUserAdapter(userList);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer,recyclerViewUser, false);
        adapter.addFooterView(footer);
        recyclerViewUser.setNestedScrollingEnabled(false);
        recyclerViewUser.setAdapter(adapter);
        if (userList.size()<10){
            adapter.changeMoreStatus(SearchActivityAdapter.NO_MORE);
            adapter.notifyDataSetChanged();
        }
        recyclerViewUser.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(ifEmpty){
                    adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                }else
                {
                    adapter.changeMoreStatus(ActivityAdapter.LOADING_MORE);}
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&  layoutManager.findLastVisibleItemPosition()+1  == adapter.getItemCount()) {
                    if (ifEmpty){
                        //null
                    }
                    else {initData(signal);}
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initData(int signal){



        if (signal==JOIN){
            initJoinData();
        }
        else if (signal==SEARCH){
            initSearchData();
        }
        else if (signal==FOLLOWING){
            initFollowingData();
        }
        else if (signal==FOLLOWED){
            initFollowedDate();
        }
    }


    private  void initJoinData(){
        if (getIntent().getStringArrayExtra("EXTRA")==null){
            return;
        }
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereContainedIn("objectId", Arrays.asList(getIntent().getStringArrayExtra("EXTRA")));
        query.setLimit(10);
        query.setSkip(size);
        final int listsize = userList.size();
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(final List<MyUser> list, BmobException e) {
                if(e==null){
                    userList.addAll( list);
                        if (listsize == userList.size()) {
                            ifEmpty = true;
                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                            adapter.notifyDataSetChanged();
                        } else if (listsize + 10 > userList.size()) {
                            ifEmpty = true;
                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                            adapter.notifyItemInserted(adapter.getItemCount() - 1);

                        }


                        else {
                            adapter.notifyItemInserted(adapter.getItemCount()-1);
                            size = size + 10;
                        }
                    }



                else
                {
                    Toast.makeText(SearchUserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        });


    }


   private void initSearchData(){
       if (getIntent().getStringExtra("EXTRA")==null){
           return;
       }
       BmobQuery<MyUser> q1 = new BmobQuery<>();
       q1.addWhereEqualTo("username",getIntent().getStringExtra("EXTRA"));

       BmobQuery<MyUser> q2 = new BmobQuery<>();
       q2.addWhereEqualTo("school",getIntent().getStringExtra("EXTRA"));

       List<BmobQuery<MyUser>> queries = new ArrayList<>();
       queries.add(q1);
       queries.add(q2);


       BmobQuery<MyUser> query = new BmobQuery<>();
       query.or(queries);
       query.setLimit(10);
       query.setSkip(size);
       final int listsize = userList.size();
       query.findObjects(new FindListener<MyUser>() {
           @Override
           public void done(final List<MyUser> list, BmobException e) {
               if(e==null){
                   userList.addAll( list);
                   if (listsize == userList.size()) {
                       ifEmpty = true;
                       adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                       adapter.notifyDataSetChanged();
                   } else if (listsize + 10 > userList.size()) {
                       ifEmpty = true;
                       adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                       adapter.notifyItemInserted(adapter.getItemCount() - 1);

                   }


                   else {
                       adapter.notifyItemInserted(adapter.getItemCount()-1);
                       size = size + 10;
                   }
               }



               else
               {
                   Toast.makeText(SearchUserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
               }

           }

       });

    }



    private void initFollowingData(){
        if (getIntent().getStringArrayExtra("EXTRA")==null){
            return;
        }
        BmobQuery<MyUser> query = new BmobQuery<>();
        String[] following= getIntent().getStringArrayExtra("EXTRA");
        query.addWhereContainedIn("objectId", Arrays.asList(following));
        query.setLimit(10);
        query.setSkip(size);
        final int listsize = userList.size();
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(final List<MyUser> list, BmobException e) {
                if(e==null){
                    userList.addAll( list);

                      if (followingFlag==0){

                        if(list.size()<10){
                            ifEmpty=true;
                            adapter.changeMoreStatus(SearchUserAdapter.NO_MORE);
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            ifEmpty=false;
                            adapter.notifyDataSetChanged();}
                        size=10;
                        followingFlag=1;
                        return;
                    }

                    if (listsize == userList.size()) {
                        ifEmpty = true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyDataSetChanged();
                    } else if (listsize + 10 > userList.size()) {
                        ifEmpty = true;
                        adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                        adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    }


                    else {
                        adapter.notifyItemInserted(adapter.getItemCount()-1);
                        size = size + 10;
                    }
                }



                else
                {
                    Toast.makeText(SearchUserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

   private void initFollowedDate(){
       if (getIntent().getStringExtra("EXTRA")==null){
           return;
       }
       BmobQuery<MyUser> query = new BmobQuery<>();
       query.addWhereContainsAll("following",Arrays.asList(getIntent().getStringExtra("EXTRA")));
       query.setLimit(10);
       query.setSkip(size);
       final int listsize = userList.size();
       query.findObjects(new FindListener<MyUser>() {
           @Override
           public void done(final List<MyUser> list, BmobException e) {
               if(e==null){
                   userList.addAll( list);

                   if (followingFlag==0){

                       if(list.size()<10){
                           ifEmpty=true;
                           adapter.changeMoreStatus(SearchUserAdapter.NO_MORE);
                           adapter.notifyDataSetChanged();
                       }
                       else {
                           ifEmpty=false;
                           adapter.notifyDataSetChanged();}
                       size=10;
                       followingFlag=1;
                       return;
                   }

                   if (listsize == userList.size()) {
                       ifEmpty = true;
                       adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                       adapter.notifyDataSetChanged();
                   } else if (listsize + 10 > userList.size()) {
                       ifEmpty = true;
                       adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                       adapter.notifyItemInserted(adapter.getItemCount() - 1);

                   }


                   else {
                       adapter.notifyItemInserted(adapter.getItemCount()-1);
                       size = size + 10;
                   }
               }



               else
               {
                   Toast.makeText(SearchUserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
               }

           }

       });
   }




    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case  android.R.id.home:
            {
                finish();
                break;
            }


            default:
                break;
        }

        return true;
    }
}
