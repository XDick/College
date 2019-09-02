package com.college.xdick.findme.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.college.xdick.findme.MyClass.BackHandlerHelper;
import com.college.xdick.findme.MyClass.FragmentBackHandler;
import com.college.xdick.findme.MyClass.GpsEvent;
import com.college.xdick.findme.MyClass.MyBannerImageLoader;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.adapter.ActivityAdapter2;
import com.college.xdick.findme.adapter.ActivityAdapter3;
import com.college.xdick.findme.adapter.ActivityAdapter4;
import com.college.xdick.findme.adapter.ActivityAdapter5;
import com.college.xdick.findme.adapter.CommentAdapter;
import com.college.xdick.findme.adapter.FindNewsAdapter;
import com.college.xdick.findme.bean.BannerManagement;
import com.college.xdick.findme.bean.FindNews;
import com.college.xdick.findme.bean.MainTagBean;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.DetailActivityActivity;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Activity.SearchActivity;
import com.college.xdick.findme.ui.Activity.SplashActivity;
import com.college.xdick.findme.ui.Activity.WebActivity;
import com.college.xdick.findme.ui.Base.BaseActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;
import com.college.xdick.findme.ui.Base.MyBaseActivity;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import jp.wasabeef.glide.transformations.BlurTransformation;
import km.lmy.searchview.SearchView;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class MainFragment extends BaseFragment implements FragmentBackHandler {
    private List<MyActivity> activityList_join = new ArrayList<>();
    private List<MyActivity> activityList_recommend = new ArrayList<>();
    private Banner banner;
    private SearchView searchView;
    private ActivityAdapter5 adapter;
    private ActivityAdapter4 activityAdapter2;
    private RecyclerView recyclerView_join,recyclerView_recommend;
    private CityPickerView mPicker;
    private TextView gpsTextView,title1,title2,title3,title4;
    private ImageView img_music,img_study,img_sport,img_party;
    private MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private CardView cardView1,cardView2,cardView3,cardView4;
    private List<MainTagBean> tagList = new ArrayList<>();
    private List<TextView> textViewList = new ArrayList<>();
    private List<CardView> cardViewList = new ArrayList<>();
    private List<Integer>  integerList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private int refreshCount=0;
    private boolean ifEmpty=false;
    private   int size =0;
    private long bmobTime=0;
    private final int ADD =1;
    private final int REFRESH=2;
    private List<MyActivity> allRecommentActivities=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initViews();
        setSearch();
        initRecyclerView();
        initPicker();
        mPicker.init(getActivity());
        EventBus.getDefault().register(this);

        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);

                refresh();
            }
        });

        return rootView;
    }

    private void initViews(){

        LinearLayout recommentLayout = rootView.findViewById(R.id.recommend_activity_layout);
        recommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityList_recommend.clear();
               Collections.shuffle(allRecommentActivities);
                List<MyActivity> myActivityList = allRecommentActivities.subList(0,(allRecommentActivities.size()<6?allRecommentActivities.size():6));
                Collections.sort(myActivityList);
                activityList_recommend.addAll(myActivityList);
                activityAdapter2.notifyDataSetChanged();

            }
        });

        ImageView background = rootView.findViewById(R.id.background);
        Glide.with(this).load(R.drawable.findme_background)
                .apply(bitmapTransform(new BlurTransformation(4, 3)))
                .into(background);

        banner = rootView.findViewById(R.id.banner);
        banner.setImageLoader(new MyBannerImageLoader());


        swipeRefresh =rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


        cardView1= rootView.findViewById(R.id.cardview1);
        cardView2= rootView.findViewById(R.id.cardview2);
        cardView3= rootView.findViewById(R.id.cardview3);
        cardView4= rootView.findViewById(R.id.cardview4);
        cardViewList.add(cardView1);
        cardViewList.add(cardView2);
        cardViewList.add(cardView3);
        cardViewList.add(cardView4);

        integerList.add(R.drawable.music);
        integerList.add(R.drawable.study);
        integerList.add(R.drawable.sports);
        integerList.add(R.drawable.party);


        title1 = rootView.findViewById(R.id.title1);
        title2 = rootView.findViewById(R.id.title2);
        title3 = rootView.findViewById(R.id.title3);
        title4 = rootView.findViewById(R.id.title4);
        textViewList.add(title1);
        textViewList.add(title2);
        textViewList.add(title3);
        textViewList.add(title4);
        NestedScrollView scrollView = rootView.findViewById(R.id.scrollView_main);
       scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
           @Override
           public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
               if (scrollY > oldScrollY) {
                   ((MainActivity) getActivity()).ifHideBar(true);
               } else {
                   ((MainActivity) getActivity()).ifHideBar(false);
               }

               if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                   if (ifEmpty) {
                       adapter.changeMoreStatus(CommentAdapter.NO_MORE);

                   } else {
                       adapter.changeMoreStatus(CommentAdapter.LOADING_MORE);
                   }

                   if (ifEmpty) {
                       //null
                   } else {
                       initJoinActivity(ADD);
                   }
               }
           }
       });
        LinearLayout allActivityLayout = rootView.findViewById(R.id.all_activity_layout);
        allActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), DetailActivityActivity.class);
                startActivity(intent);
            }
        });

        Button searchButton = rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.open();
            }
        });
        Toolbar toolbar = rootView.findViewById(R.id.toolbar_ac);
        toolbar.setTitle("");
        gpsTextView = rootView.findViewById(R.id.gps_main_ac_toolbar);
        if (myUser != null) {
            String[] gps = BmobUser.getCurrentUser(MyUser.class).getGps();
            gpsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPicker.showCityPicker();
                }
            });
            if (gps != null) {

                gpsTextView.setText(gps[2]);
                if (gpsTextView.getText().equals("")){
                    gpsTextView.setText("请手动定位");
                }

            }
        } else {
            gpsTextView.setText("未登录");
            gpsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            });
        }
        ((AppCompatActivity)getContext()).setSupportActionBar(toolbar);
        img_music = rootView.findViewById(R.id.img_music);
        img_study = rootView.findViewById(R.id.img_study);
        img_sport = rootView.findViewById(R.id.img_sports);
        img_party = rootView.findViewById(R.id.img_party);


    }
    private void initPicker(){
        mPicker= new CityPickerView();
        CityConfig cityConfig = new CityConfig.Builder()
                .confirTextColor("#cf0606")
                .setCityWheelType(CityConfig.WheelType.PRO_CITY)
                .build();

        if (BmobUser.getCurrentUser(MyUser.class)!=null){
            String[] gps =BmobUser.getCurrentUser(MyUser.class).getGps();
            if (gps!=null){
                cityConfig = new CityConfig.Builder()
                        .confirTextColor("#cf0606")
                        .setCityWheelType(CityConfig.WheelType.PRO_CITY)
                        .province(gps[1])
                        .city(gps[2])
                        .build();
            }
        }


        mPicker.setConfig(cityConfig);

//监听选择点击事件及返回结果
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                String p=null,c=null,d=null;
                //省份
                if (province != null) {
                    p=province.getName();
                }

                //城市
                if (city != null) {
                    c=city.getName();
                }

                //地区
                if (district != null) {
                    d=district.getName();
                }

                final String[] gps = {"", p, c, d, ""};
                EventBus.getDefault().post(new GpsEvent(gps));

            }

            @Override
            public void onCancel() {
                ToastUtils.showLongToast(getActivity(), "已取消");
            }
        });


    }
    private void initRecyclerView() {

        recyclerView_join = rootView.findViewById(R.id.recyclerview_join_ac);
        recyclerView_recommend = rootView.findViewById(R.id.recyclerview_recommend);

        activityAdapter2 = new ActivityAdapter4(activityList_recommend);
        adapter = new ActivityAdapter5(activityList_join);

       final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        GridLayoutManager layoutManager2 =
                new GridLayoutManager(getContext(),3);

        recyclerView_join.setLayoutManager(layoutManager);
        recyclerView_recommend.setLayoutManager(layoutManager2);




        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, recyclerView_join, false);
        View empty1 = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_join, recyclerView_join, false);
        View empty2 = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_activity, recyclerView_join, false);
        //View empty3 = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_recommend, recyclerView_recommend, false);


        if (myUser==null){
            adapter.setEmptyView(empty1);
            Button button  = empty1.findViewById(R.id.login_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                    startActivity(new Intent(getContext(),LoginActivity.class));
                }
            });
        }
        else {
            adapter.setEmptyView(empty2);
           // activityAdapter2.setEmptyView(empty3);
        }

        adapter.addFooterView(footer);




        recyclerView_recommend.setAdapter(activityAdapter2);
        recyclerView_join.setAdapter(adapter);

    }


    private void initRecommendActivity(){
        activityList_recommend.clear();
        BmobQuery<MyActivity> query = new BmobQuery<>();
        query.order("-createdAt");
        query.include("host[username|avatar|Exp]");

        if(BmobUser.getCurrentUser(MyUser.class)!=null&& BmobUser.getCurrentUser(MyUser.class).getTag()!=null) {
            List<BmobQuery<MyActivity>> queries = new ArrayList<>();
            String tag[] =  BmobUser.getCurrentUser(MyUser.class).getTag();
            if (tag.length==0){
                if (++refreshCount==4){
                    swipeRefresh.setRefreshing(false);
                }
                return ;
            }
            for (int i = 0; i < tag.length; i++) {
                BmobQuery<MyActivity> q = new BmobQuery<MyActivity>();
                q.addWhereContainsAll("tag", Arrays.asList(tag[i]));
                queries.add(q);
            }
            query.or(queries);
        }

        query.setLimit(30);

        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                if (e==null){
                    allRecommentActivities.clear();
                    allRecommentActivities.addAll(list);
                    Collections.shuffle(list);
                    List<MyActivity> myActivityList = list.subList(0,(list.size()<6?list.size():6));
                    Collections.sort(myActivityList);
                    activityList_recommend.addAll(myActivityList);
                    activityAdapter2.notifyDataSetChanged();
                    if (++refreshCount==4){
                        swipeRefresh.setRefreshing(false);
                    }

                }
            }
        });

    }

    private void initTag(){
        tagList.clear();
        BmobQuery<MainTagBean> query = new BmobQuery<MainTagBean>();
        query.order("order");
        query.findObjects(new FindListener<MainTagBean>() {
            @Override
            public void done(List<MainTagBean> list, BmobException e) {
                if (e==null){
                   tagList.addAll(list);
                   for (int i = 0; i<4;i++){
                       final MainTagBean tagBean =tagList.get(i);
                       final int path = integerList.get(i);
                       textViewList.get(i).setText(tagList.get(i).getMainTag());
                       cardViewList.get(i).setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent intent = new Intent(getContext(),DetailActivityActivity.class);
                               intent.putExtra("TAG",tagBean);
                               intent.putExtra("IMG",path);

                               startActivity(intent);
                           }
                       });



                   }

                    Glide.with(getContext()).load(R.drawable.music).into(img_music);
                    Glide.with(getContext()).load(R.drawable.study).into(img_study);
                    Glide.with(getContext()).load(R.drawable.sports).into(img_sport);
                    Glide.with(getContext()).load(R.drawable.party).into(img_party);


                    if (++refreshCount==4){
                        swipeRefresh.setRefreshing(false);
                    }

                }
            }
        });

    }

    private void initBanner(){


        final List<String> image = new ArrayList<>();
        final List<String> title = new ArrayList<>();
        final List<BannerManagement> bannerElement = new ArrayList<>();

        BmobQuery<BannerManagement> query = new BmobQuery<>();
        query.include("activity[title|cover|place|gps]");
        query.addWhereNotEqualTo("order",0);
        query.order("order");
        query.findObjects(new FindListener<BannerManagement>() {
            @Override
            public void done(final List<BannerManagement> list, BmobException e) {
                if (e==null){

                   for (final BannerManagement mBanner:list){

                       bannerElement.add(mBanner);

                       if (mBanner.getType().equals("web")){
                           image.add(mBanner.getCoverUri());
                           title.add(mBanner.getWebTitle());

                       }
                         else if (mBanner.getType().equals("activity")){
                           if (mBanner.getCoverUri()==null||mBanner.getCoverUri().equals("")){
                               image.add(mBanner.getActivity().getCover());
                           }
                           else {
                               image.add(mBanner.getCoverUri());
                           }


                           if (mBanner.getWebTitle()==null||mBanner.getWebTitle().equals("")){
                               title.add(mBanner.getActivity().getTitle());
                           }
                           else {
                               title.add(mBanner.getWebTitle());
                           }

                       }

                   }


                if (list.size()<5){
                                BmobQuery<MyActivity> query = new BmobQuery<>();
                                query.addWhereGreaterThan("endDate",bmobTime);//60*60*24*1000
                                query.include("host[username|avatar|Exp]");
                                query.order("-joinUser");
                                query.setLimit(5-list.size());
                                query.findObjects(new FindListener<MyActivity>() {
                                    @Override
                                    public void done(final List<MyActivity> list, BmobException e) {
                                        if (e==null){
                                            for (MyActivity activity:list){
                                              //  Log.d("haha",activity.getTitle());
                                                image.add(activity.getCover());
                                                title.add(activity.getTitle());
                                                bannerElement.add(new BannerManagement("activity",
                                                        null,null,null,activity));
                                            }



                                            banner.setImages(image)
                                                    .setBannerTitles(title)
                                                    .setDelayTime(3000)
                                                    .setBannerAnimation(Transformer.DepthPage)
                                                    .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                                                    .setIndicatorGravity(BannerConfig.RIGHT);



                                            banner.setOnBannerListener(new OnBannerListener() {
                                                @Override
                                                public void OnBannerClick(int position) {

                                                    if (bannerElement.get(position).getType().equals("web")){Intent intent = new Intent(getContext(), WebActivity.class);
                                                        intent.putExtra("URL", bannerElement.get(position).getWebUri());
                                                        startActivity(intent);}

                                                    else if (bannerElement.get(position).getType().equals("activity")){
                                                        BmobQuery<MyActivity>query1 = new BmobQuery<>();
                                                        query1.include("host[username|avatar|Exp]");
                                                        query1.getObject(bannerElement.get(position).getActivity().getObjectId(), new QueryListener<MyActivity>() {
                                                            @Override
                                                            public void done(MyActivity myActivity, BmobException e) {
                                                                if (e==null){
                                                                    Intent intent = new Intent(getContext(), ActivityActivity.class);
                                                                    intent.putExtra("ACTIVITY", myActivity);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                                    }


                                                }
                                            });




                                            banner.start();

                                            if (++refreshCount==4){
                                                swipeRefresh.setRefreshing(false);
                                            }

                                        }

                                    }
                                });

                }
                else {
                    banner.setImages(image)
                            .setBannerTitles(title)
                            .setDelayTime(3000)
                            .setBannerAnimation(Transformer.DepthPage)
                            .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                            .setIndicatorGravity(BannerConfig.RIGHT)

                    ;



                    banner.setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {

                            if (bannerElement.get(position).getType().equals("web")){Intent intent = new Intent(getContext(), WebActivity.class);
                                intent.putExtra("URL", bannerElement.get(position).getWebUri());
                                startActivity(intent);}

                            else if (bannerElement.get(position).getType().equals("activity")){
                                BmobQuery<MyActivity>query1 = new BmobQuery<>();
                                query1.include("host[username|avatar|Exp]");
                                query1.getObject(bannerElement.get(position).getActivity().getObjectId(), new QueryListener<MyActivity>() {
                                    @Override
                                    public void done(MyActivity myActivity, BmobException e) {
                                        if (e==null){
                                            Intent intent = new Intent(getContext(), ActivityActivity.class);
                                            intent.putExtra("ACTIVITY", myActivity);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }


                        }
                    });




                    banner.start();

                    if (++refreshCount==4){
                        swipeRefresh.setRefreshing(false);
                    }
                }
                }
             }
        });





    }

    private  void initJoinActivity(final int state){

        if (myUser==null){
            if (++refreshCount==4){
                swipeRefresh.setRefreshing(false);
            }
            return;
        }
        BmobQuery<MyActivity> query = new BmobQuery<>();
        List<BmobQuery<MyActivity>> queries = new ArrayList<>();

        BmobQuery<MyActivity> q1 = new BmobQuery<>();
        BmobQuery<MyActivity> q2 = new BmobQuery<>();
        q1.addWhereContainsAll("joinUser",Arrays.asList(myUser.getObjectId()));
        q2.addWhereEqualTo("host",myUser.getObjectId());
        queries.add(q1);
        queries.add(q2);
        query.or(queries);
        query.order("-date");
        query.setLimit(10);
        query.setSkip(size);
        query.include("host[username|avatar|Exp]");
        final int listsize = activityList_join.size();
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> list, BmobException e) {
                if (e == null) {
                    if (state == ADD) {
                        activityList_join.addAll(list);
                        if (listsize == activityList_join.size()) {
                            ifEmpty = true;
                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                            adapter.notifyDataSetChanged();


                        } else if (listsize + 10 > activityList_join.size()) {
                            ifEmpty = true;
                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                            adapter.notifyItemInserted(adapter.getItemCount() - 1);

                        } else {

                            adapter.changeMoreStatus(ActivityAdapter.PULLUP_LOAD_MORE);
                            adapter.notifyItemInserted(adapter.getItemCount() - 1);
                            size = size + 10;
                        }
                    } else if (state == REFRESH) {
                        activityList_join.clear();
                        if (list.size() < 10) {
                            ifEmpty = true;
                            activityList_join.addAll(list);
                            adapter.changeMoreStatus(ActivityAdapter.NO_MORE);
                            adapter.notifyDataSetChanged();
                        } else {
                            ifEmpty = false;
                            activityList_join.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                        size = 10;
                        swipeRefresh.setRefreshing(false);
                    }

                }

                else{
                    if (++refreshCount==4){
                        swipeRefresh.setRefreshing(false);
                    }
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    public void refresh(){
        refreshCount=0;
        size =0;
        swipeRefresh.setRefreshing(true);

        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(final Long aLong, BmobException e) {

                if (e == null) {
                    bmobTime=aLong*1000L;
                    ((BaseActivity)getActivity()).setBmobTime(bmobTime);
                    initJoinActivity(REFRESH);
                    initRecommendActivity();
                    initBanner();
                    initTag();

                }
            else {
                getActivity().finish();
                startActivity(new Intent(getContext(), SplashActivity.class));
                }
            }
        }

        );




        if (getActivity() == null){
            return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(final GpsEvent gpsEvent) {
        if (myUser != null) {
            String[] gps = gpsEvent.getMessage();
            if (gps != null) {
                gpsTextView.setText(gps[2]);
                if (gpsTextView.getText().equals("")){
                    gpsTextView.setText("请手动定位");
                }
            }
        }

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
