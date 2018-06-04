package com.college.xdick.findme.ui.Fragment;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.college.xdick.findme.MyClass.BackHandlerHelper;
import com.college.xdick.findme.MyClass.FragmentBackHandler;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.FindNewsAdapter;
import com.college.xdick.findme.bean.FindNews;
import com.college.xdick.findme.bean.MyCircleBanner;
import com.college.xdick.findme.ui.Activity.SearchActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.b.V;
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
    private   List<String> mInfos = new ArrayList<>();
    private SearchView searchView;





    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       rootView =inflater.inflate(R.layout.fragment_search,container,false);
        initBaseView();
        initRecyclerView();
        if(flag_search==0){
            loadlayout.setVisibility(View.VISIBLE);
            dots.start();
            initNews();
            flag_search=1;}


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
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new FindNewsAdapter(newsList);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.item_banner, recyclerView, false);
        MyCircleBanner mBanner =  header.findViewById(R.id.banner1);
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView, false);
// 设置数据源
        mInfos.add( "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525619386672&di=7f1cb1528333b60f87a50a1016f6d6f4&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3Dc20bee098082b90129a0cb701be4c302%2F4b90f603738da97734e54305ba51f8198718e3d2.jpg");
        mInfos.add( "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525619368239&di=4ada85418df0fe74ab3196663a4f9e25&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fa6efce1b9d16fdfae6becffcbe8f8c5495ee7bd5.jpg");
        mInfos.add( "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525619350732&di=27952991e7142a797a600fbb39e0e72f&imgtype=0&src=http%3A%2F%2Fb.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F342ac65c103853430a74d9149513b07eca808848.jpg");
        //adapter.addHeaderView(header);
        View empty = LayoutInflater.from(getContext()).inflate(R.layout.item_empty, recyclerView, false);
         adapter.setEmptyView(empty);
        adapter.addFooterView(footer);
        recyclerView.setAdapter(adapter);
        mBanner.play(mInfos);

    }


    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    newsList.clear();
                    initNews();
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
