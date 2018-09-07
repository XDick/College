package com.college.xdick.findme.ui.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.LinearLayout;

import com.college.xdick.findme.MyClass.BackHandlerHelper;
import com.college.xdick.findme.MyClass.FragmentBackHandler;
import com.college.xdick.findme.MyClass.MyBannerImageLoader;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.FindNewsAdapter;
import com.college.xdick.findme.bean.FindNews;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.SearchActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import km.lmy.searchview.SearchView;
import pl.tajchert.waitingdots.DotsTextView;

/**
 * Created by Administrator on 2018/4/10.
 */

public class SearchFragment extends Fragment implements FragmentBackHandler {
    @Nullable
   private static List<FindNews> newsList = new ArrayList<>();
    private FindNewsAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private View rootView;
    static int flag_search=0;
    private DotsTextView dots;
    private LinearLayout loadlayout;
    private RecyclerView recyclerView;
    private Banner banner;
    private SearchView searchView;
    private  static List<String> imageList = new ArrayList<>();
    private   static List<String> titleList = new ArrayList<>();
    private  static List<MyActivity> activityList = new ArrayList<>();





    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       rootView =inflater.inflate(R.layout.fragment_search,container,false);
        initBaseView();
        if(flag_search==0){
            loadlayout.setVisibility(View.VISIBLE);
            dots.start();
            initNews();
            initBanner();


        }
        initRecyclerView();
        setHasOptionsMenu(true);

        return  rootView;
    }


    private void initBaseView() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar_search);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        swipeRefresh =rootView.findViewById(R.id.swipe_refresh_search);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        dots =  rootView.findViewById(R.id.dots);
        loadlayout=  rootView.findViewById(R.id.loading_layout);


        setSearch();


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.toolbar_search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

             case  R.id.home:
             {
                 getActivity().finish();
             }

            case R.id.menu_search:

                searchView.open();


            default:
                break;
        }

        return true;
    }


    private void initRecyclerView(){

        recyclerView = rootView.findViewById(R.id.recyclerview_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FindNewsAdapter(newsList);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.item_header_banner, recyclerView, false);
        banner =  header.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new MyBannerImageLoader());
        //设置图片集合

        banner.setImages(imageList)
                .setDelayTime(3000)
                .setBannerTitles(titleList)
                .setBannerAnimation(Transformer.Default)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setIndicatorGravity(BannerConfig.RIGHT);

        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(getContext(), ActivityActivity.class);
                intent.putExtra("ACTIVITY",activityList.get(position));
                startActivity(intent);
            }
        });
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
        adapter.addHeaderView(header);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty, recyclerView, false);
        adapter.setEmptyView(empty);
        adapter.addFooterView(footer);
        recyclerView.setAdapter(adapter);


    }


    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    newsList.clear();
                    initNews();
                    initBanner();
                    Thread.sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                if (getActivity() == null)
                    return;
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

    private void initBanner(){
        activityList.clear();
        imageList.clear();
        titleList.clear();
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
                if (e==null){
                    BmobQuery<MyActivity> query = new BmobQuery();
                    query.addWhereGreaterThan("date", aLong*1000L-60*60*24*2.5*1000);//60*60*24*1000

                    query.order("-joinCount");
                    query.setLimit(5);
                    query.findObjects(new FindListener<MyActivity>() {
                        @Override
                        public void done(final List<MyActivity> list, BmobException e) {
                            if (e==null){
                                activityList.addAll(list);
                                for (MyActivity activity:list){
                                    imageList.add(activity.getCover());
                                    String[] gps=activity.getGps();
                                    titleList.add("("+gps[1]+")"+activity.getTitle());
                                }
                               if (flag_search==0){
                                   initRecyclerView();
                                   flag_search=1;
                               }

                            }

                        }
                    });


                }
            }
        });
    }

    private void initNews(){

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

        Document document = Jsoup.
                connect("http://www.mtime.com/")
                .timeout(50000).get();

        Elements titleElements = document.getElementsByClass("newsitem");
        Elements bodyElements = document.getElementsByClass("newsitem");
        Elements imgElements = document.getElementsByClass("newsitem");
        Elements sizeElements = document.getElementsByClass("newsitem");
        Elements contentElements = document.getElementsByClass("newsitem");
        Log.d("", "title:" + titleElements
                .select("a").select("title").text());
        Log.d("", bodyElements.text());
        Log.d("", "pic:" + imgElements.select("img").attr("src"));

        for(int i=0;i<sizeElements.size();i++){

            FindNews news =
                    new FindNews(bodyElements.get(i).select("h3").text(),
                            bodyElements.get(i).getElementsByClass("describe").text(),
                            imgElements.get(i).select("img").attr("data-src")
                    ,  contentElements.get(i).select("a").attr("href"));

            newsList.add(news);
        }


    } catch (Exception e)
    {
        e.printStackTrace();
        Log.d("", "访问网络失败了！");

    }
    finally {

                    if (getActivity()==null){
                        return;
                    }
                  getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          adapter.notifyDataSetChanged();
                          loadlayout.setVisibility(View.GONE);

                      }
                  });

                }

} }).start();
    }




    private void setSearch() {

        searchView = rootView.findViewById(R.id.searchView);
        searchView.setHintText("输入用户名、学校名、地名或标签...");
        searchView.setOnSearchActionListener(new SearchView.OnSearchActionListener() {
            @Override
            public void onSearchAction(String s) {
              searchView.addOneHistory(searchView.getEditTextView().getText().toString());
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("SEARCH",searchView.getEditTextView().getText().toString());
                startActivity(intent);
                searchView.close();
            }
        });

        searchView.setHistoryItemClickListener(new SearchView.OnHistoryItemClickListener() {
            @Override
            public void onClick(String s, int i) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("SEARCH",s);
                startActivity(intent);
                searchView.close();
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if(searchView.isOpen()){
            searchView.close();
            return true;
        }


        return BackHandlerHelper.handleBackPress(this);
    }



}
