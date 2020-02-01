package top.dzou.my_toutiao.ui.fragment;


import android.content.Intent;
import android.os.Bundle;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZMediaManager;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.model.News;
import top.dzou.my_toutiao.model.NewsRecord;
import top.dzou.my_toutiao.model.VideoInfo;
import top.dzou.my_toutiao.mvp.presenter.NewsListPresenter;
import top.dzou.my_toutiao.mvp.view.INewsView;
import top.dzou.my_toutiao.ui.activity.VideoDetailActivity;
import top.dzou.my_toutiao.ui.adapter.NewsListAdapter;
import top.dzou.my_toutiao.ui.adapter.VideoRvAdapter;
import top.dzou.my_toutiao.ui.widget.MyJzvdStd;
import top.dzou.my_toutiao.utils.NewsRecordHelper;
import top.dzou.my_toutiao.utils.UIUtils;
import top.dzou.ui_kit.swipe_rv.OnRefresh;
import top.dzou.ui_kit.swipe_rv.SwipeFreshRecyclerView;
import top.dzou.ui_kit.tip_notice.TipView;

public class NewsListFragment extends BaseFragment<NewsListPresenter> implements INewsView {

    @BindView(R.id.rv) SwipeFreshRecyclerView mRv;
    @BindView(R.id.tip_view) TipView mTip;

    private OnRefresh mRefreshListener;
    private List<News> mNewsList = new ArrayList<>();
    private boolean isVideoFg;
    private String mChannelCode;
    private boolean isRecommendChannel;
    private BaseQuickAdapter mAdapter;
    private Gson mGson = new GsonBuilder().create();
    private NewsRecord mNewsRecord;


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
        mRefreshListener = () -> UIUtils.postTaskSafely(()->{
            mPresenter.getNewsList(mChannelCode);
        });
        mRv.setOnRefresh(mRefreshListener);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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
                    //todo
                }
                intent.putExtra(VideoDetailActivity.CHANNEL_CODE, mChannelCode);
                intent.putExtra(VideoDetailActivity.POSITION, position);
                intent.putExtra(VideoDetailActivity.DETAIL_URL, url);
                intent.putExtra(VideoDetailActivity.GROUP_ID, news.group_id);
                intent.putExtra(VideoDetailActivity.ITEM_ID, news.item_id);
                startActivity(intent);
            }
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
                        if(current!=null&&current.currentScreen!=Jzvd.SCREEN_WINDOW_FULLSCREEN){
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
        mRv.setLayoutManager(new GridLayoutManager(mActivity, 1));
        if (isVideoFg) {
            mAdapter = new VideoRvAdapter(mNewsList);
            mRv.setAdapter(mAdapter);
        } else {
            //todo
            //其他新闻列表
            mAdapter = new NewsListAdapter(mNewsList,mChannelCode);
        }
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
        //查找该频道的最后一组记录
        mNewsRecord = NewsRecordHelper.getLastNewsRecord(mChannelCode);
        if (mNewsRecord == null) {
            //找不到记录，拉取网络数据
            mNewsRecord = new NewsRecord();//创建一个没有数据的对象
            mPresenter.getNewsList(mChannelCode);
            return;
        }
        //找到最后一组记录，转换成新闻集合并展示
        List<News> newsList = NewsRecordHelper.convertToNewsList(mNewsRecord.getJson());
        mNewsList.addAll(newsList);//添加到集合中
        mAdapter.notifyDataSetChanged();//刷新adapter
    }

    @Override
    public void onSuccess(List<News> newList, String tipInfo) {
        if(mNewsList==null||mNewsList.isEmpty()){
            //todo
        }
        if(newList==null||newList.isEmpty()){
            UIUtils.showToast(UIUtils.getResource().getString(R.string.no_news_now));
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
        NewsRecordHelper.save(mChannelCode, mGson.toJson(newList));
    }

    @Override
    public void onError() {
        mTip.show();
        if(mNewsList==null||mNewsList.isEmpty()){
            //todo
        }
    }

    /**
     * 处理置顶新闻和广告重复
     */
    private void dealRepeat(List<News> newList) {
        if (isRecommendChannel && mNewsList!=null||!mNewsList.isEmpty()) {
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
}
