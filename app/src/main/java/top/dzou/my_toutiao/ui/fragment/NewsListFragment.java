package top.dzou.my_toutiao.ui.fragment;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZMediaManager;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.event.TabRefreshCompletedEvent;
import top.dzou.my_toutiao.event.TabRefreshEvent;
import top.dzou.my_toutiao.event.VideoProgressAndCommentEvent;
import top.dzou.my_toutiao.model.News;
import top.dzou.my_toutiao.model.NewsRecord;
import top.dzou.my_toutiao.model.VideoInfo;
import top.dzou.my_toutiao.mvp.presenter.NewsListPresenter;
import top.dzou.my_toutiao.mvp.view.INewsView;
import top.dzou.my_toutiao.ui.activity.NewsDetailActivity;
import top.dzou.my_toutiao.ui.activity.VideoDetailActivity;
import top.dzou.my_toutiao.ui.activity.WebViewActivity;
import top.dzou.my_toutiao.ui.activity.base.NewsDetailBaseActivity;
import top.dzou.my_toutiao.ui.adapter.MultiNewsListAdapter;
import top.dzou.my_toutiao.ui.adapter.VideoRvAdapter;
import top.dzou.my_toutiao.ui.widget.MyJzvdStd;
import top.dzou.my_toutiao.utils.NetUtils;
import top.dzou.my_toutiao.utils.NewsRecordHelper;
import top.dzou.my_toutiao.utils.UIUtils;
import top.dzou.ui_kit.swipe_rv.OnRefresh;
import top.dzou.ui_kit.swipe_rv.SwipeFreshRecyclerView;
import top.dzou.ui_kit.tip_notice.TipView;

public class NewsListFragment extends BaseFragment<NewsListPresenter> implements INewsView {

    private static final String TAG = "NewsListFragment";
    @BindView(R.id.rv)
    SwipeFreshRecyclerView mRv;
    @BindView(R.id.tip_view)
    TipView mTip;

    private OnRefresh mRefreshListener;
    private List<News> mNewsList = new ArrayList<>();
    private boolean isVideoFg;
    private String mChannelCode;
    private boolean isRecommendChannel;
    private BaseQuickAdapter mAdapter;
    private Gson mGson = new GsonBuilder().create();
    private NewsRecord mNewsRecord;
    private boolean isClickTabRefreshing;
    private boolean isHomeTabRefresh;
    private MenuItem mAnimMenuItem;


    public NewsListFragment() {
    }

    @Override
    protected void initData() {
        super.initData();
        mChannelCode = getArguments().getString(Constant.CHANNEL_CODE, "");
        isVideoFg = getArguments().getBoolean(Constant.IS_VIDEO_LIST, false);
        String[] channelCodes = getResources().getStringArray(R.array.channel_code);
        isRecommendChannel = mChannelCode.equals(channelCodes[0]);//是否是推荐频道
    }

    @Override
    protected void initListener() {
        super.initListener();
        mRefreshListener = () -> UIUtils.postTaskOtherThread(() -> {
            mPresenter.getNewsList(mChannelCode);
        });
        mRv.setOnRefresh(mRefreshListener);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            News news = mNewsList.get(position);
            String itemId = news.item_id;
            StringBuffer urlSb = new StringBuffer("http://m.toutiao.com/i");
            urlSb.append(itemId).append("/info/");
            String url = urlSb.toString();//http://m.toutiao.com/i6412427713050575361/info/
            Intent intent = null;
            if (news.has_video) {
                intent = new Intent(mActivity, VideoDetailActivity.class);
                if (JZMediaManager.instance() != null && JzvdMgr.getCurrentJzvd() != null) {
                    long progress = JZMediaManager.instance().getCurrentPosition();
                    if (progress != 0) {
                        intent.putExtra(VideoDetailActivity.PROGRESS, progress);
                    }
                    VideoInfo videoInfo = news.video_detail_info;
                    String videoUrl = "";
                    if (videoInfo != null && !TextUtils.isEmpty(videoInfo.parse_video_url)) {
                        videoUrl = videoInfo.parse_video_url;
                    }
                    intent.putExtra(VideoDetailActivity.VIDEO_URL, videoUrl);
                }
            } else {
                //如果是新闻消息
                if (news.article_type == 1) {
                    intent = new Intent(mActivity, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.URL, news.article_url);
                    startActivity(intent);
                    return;
                }
                intent = new Intent(mActivity, NewsDetailActivity.class);
            }
            intent.putExtra(NewsDetailBaseActivity.CHANNEL_CODE, mChannelCode);
            intent.putExtra(NewsDetailBaseActivity.POSITION, position);
            intent.putExtra(NewsDetailBaseActivity.DETAIL_URL, url);
            intent.putExtra(NewsDetailBaseActivity.GROUP_ID, news.group_id);
            intent.putExtra(NewsDetailBaseActivity.ITEM_ID, news.item_id);
            startActivity(intent);
        });
//        mAdapter.setEnableLoadMore(true);
        if (isVideoFg) {
            mRv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(@NonNull View view) {

                }

                @Override
                public void onChildViewDetachedFromWindow(@NonNull View view) {
                    MyJzvdStd jzvdStd = view.findViewById(R.id.video_player);
                    if (jzvdStd != null && jzvdStd.jzDataSource != null && jzvdStd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
                        Jzvd current = JzvdMgr.getCurrentJzvd();
                        if (current != null && current.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
                            Jzvd.releaseAllVideos();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
//        Log.d(TAG, String.valueOf(mRv==null)+String.valueOf(mActivity.getLocalClassName()));
        mRv.setLayoutManager(new GridLayoutManager(mActivity, 1));
        if (isVideoFg) {
            mAdapter = new VideoRvAdapter(mNewsList, mActivity);
        } else {
            //其他新闻列表
            mAdapter = new MultiNewsListAdapter(mNewsList, mChannelCode, mActivity);
        }
        mRv.setAdapter(mAdapter);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_news_list;
    }

    @Override
    protected NewsListPresenter createPresenter() {
        return new NewsListPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void loadData() {
        mStateView.showLoading();
        //查找该频道的最后一组记录
//        UIUtils.postTaskOtherThread(()->{
        mNewsRecord = NewsRecordHelper.getLastNewsRecord(mChannelCode);
        Log.d(TAG, "loadData newslist");
        if (mNewsRecord == null) {
            Log.d(TAG, "数据库为空");
            //找不到记录，拉取网络数据
            mNewsRecord = new NewsRecord();//创建一个没有数据的对象
            mPresenter.getNewsList(mChannelCode);
            return;
        }
        //找到最后一组记录，转换成新闻集合并展示
        List<News> newsList = NewsRecordHelper.convertToNewsList(mNewsRecord.getJson());
        mNewsList.addAll(newsList);//添加到集合中
//        });
        mAdapter.notifyDataSetChanged();//刷新adapter
        mStateView.showContent();//显示内容
    }

    @Override
    public void onSuccess(List<News> newList, String tipInfo) {
        if (isHomeTabRefresh) {
            postRefreshCompletedEvent();//发送加载完成的事件
        }
        mRv.refreshComplete();
        if (mNewsList == null || mNewsList.isEmpty()) {
            if (newList == null || newList.isEmpty()) {
                //获取不到数据,显示空布局
                mStateView.showEmpty();
            }
        }
        if (newList == null || newList.isEmpty()) {
            UIUtils.showToast(UIUtils.getResource().getString(R.string.no_news_now));
            return;
        }

        if (TextUtils.isEmpty(newList.get(0).title)) {
            //由于汽车、体育等频道第一条属于导航的内容，所以如果第一条没有标题，则移除
            newList.remove(0);
        }

        dealRepeat(newList);//处理新闻重复问题

        mNewsList.addAll(0, newList);
        mAdapter.notifyDataSetChanged();

        mTip.show(tipInfo);
        //保存到数据库
        UIUtils.postTaskOtherThread(() -> {
            NewsRecordHelper.save(mChannelCode, mGson.toJson(newList));
        });
    }

    @Override
    public void onError() {
        mTip.show();
        if (mNewsList == null || mNewsList.isEmpty()) {
            mStateView.showRetry();
        }
    }

    /**
     * 处理置顶新闻和广告重复
     */
    private void dealRepeat(List<News> newList) {
        if (isRecommendChannel && mNewsList != null && !mNewsList.isEmpty()) {
            //如果是推荐频道并且数据列表已经有数据,处理置顶新闻或广告重复的问题
            mNewsList.remove(0);//由于第一条新闻是重复的，移除原有的第一条
            //新闻列表通常第4个是广告,除了第一次有广告，再次获取的都移除广告
            if (newList.size() >= 4) {
                News fourthNews = newList.get(3);
                //如果列表第4个和原有列表第4个新闻都是广告，并且id一致，移除
                if (fourthNews.tag.equals(Constant.ARTICLE_GENRE_AD)) {
                    newList.remove(fourthNews);
                }
            }
        }
    }

    /**
     * 接收到点击底部首页页签下拉刷新的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(TabRefreshEvent event) {
        if (event.getChannelCode().equals(mChannelCode)) {
            //如果和当前的频道码一致并且不是刷新中,进行下拉刷新
            if (!NetUtils.isNetworkAvailable(mActivity)) {
                //网络不可用弹出提示
                mTip.show();
                return;
            }
            isClickTabRefreshing = true;
            if (event.getPosition() == 0) {
                //如果页签是首页，则换成就加载的图标并执行动画
//                MenuItem menuItem = event.getMenuItem();
//                menuItem.setIcon(R.mipmap.tab_loading);
//                menuItem.setChecked(true);
                //设置动画
//                ImageView qrView = (ImageView) getLayoutInflater().inflate(R.layout.action_view, null);
//                qrView.setImageResource(R.mipmap.tab_loading);
//                menuItem.setActionView(qrView);
//                showAnimate(menuItem);
            }

            mRv.scrollToPosition(0);//滚动到顶部
            mRefreshListener.onRefresh();
            isHomeTabRefresh = event.getPosition() == 0;
        }
    }

    //无法使用BottomNavigationView设置动画 todo
    private void showAnimate(MenuItem item) {
        hideAnimate();
        mAnimMenuItem = item;
        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        ImageView qrView = (ImageView) getLayoutInflater().inflate(R.layout.action_view, null);
//        qrView.setImageResource(R.mipmap.tab_loading);
        mAnimMenuItem.setActionView(qrView);
        //显示动画
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.anim_rotate_imgview);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        animation.setInterpolator(interpolator);
        animation.setRepeatCount(-1);
        qrView.startAnimation(animation);

    }

    private void hideAnimate() {
        if (mAnimMenuItem != null) {
            View view = mAnimMenuItem.getActionView();
            if (view != null) {
                view.clearAnimation();
                mAnimMenuItem.setActionView(null);
            }
        }
    }

    private void postRefreshCompletedEvent() {
        if (isClickTabRefreshing) {
            //如果是点击底部刷新获取到数据的,发送加载完成的事件
            EventBus.getDefault().post(new TabRefreshCompletedEvent());
            isClickTabRefreshing = false;
        }
    }

    /**
     * 接收到刷新进度和评论数的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    private void onVideoProgressAndCommentCountEvent(VideoProgressAndCommentEvent event) {
        if (!event.getChannelCode().equals(mChannelCode)) {
            //如果频道不一致，不用处理
            return;
        }
        News news = mNewsList.get(event.getPosition());
        news.comment_count = (int) event.getCommmentCount();
        if (news.video_detail_info != null) {
            //如果有视频
            long progress = event.getProgress();
            news.video_detail_info.progress = progress;
        }
        //刷新adapter
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEventBus(NewsListFragment.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterEventBus(NewsListFragment.this);
    }

}
