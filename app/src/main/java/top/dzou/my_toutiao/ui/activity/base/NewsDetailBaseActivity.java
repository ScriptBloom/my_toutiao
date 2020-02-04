package top.dzou.my_toutiao.ui.activity.base;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.nukc.stateview.StateView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JzvdMgr;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.event.VideoProgressAndCommentEvent;
import top.dzou.my_toutiao.model.CommentResponse;
import top.dzou.my_toutiao.model.CommentVo;
import top.dzou.my_toutiao.mvp.presenter.NewsDetailPresenter;
import top.dzou.my_toutiao.mvp.view.INewsDetailView;
import top.dzou.my_toutiao.ui.activity.NewsDetailActivity;
import top.dzou.my_toutiao.ui.adapter.CommentAdapter;
import top.dzou.my_toutiao.ui.widget.NewsDetailHeaderView;
import top.dzou.ui_kit.swipe_rv.SwipeFreshRecyclerView;

public abstract class NewsDetailBaseActivity extends BaseActivity<NewsDetailPresenter> implements INewsDetailView, BaseQuickAdapter.RequestLoadMoreListener {

    private static final String TAG = NewsDetailActivity.class.getSimpleName();

    public static final String CHANNEL_CODE = "channelCode";
    public static final String VIDEO_URL = "videoUrl";
    public static final String PROGRESS = "progress";
    public static final String POSITION = "position";
    public static final String DETAIL_URL = "detailUrl";
    public static final String GROUP_ID = "groupId";
    public static final String ITEM_ID = "itemId";


    @BindView(R.id.fl_content)
    FrameLayout mFlContent;
    @BindView(R.id.rv_comment)
    public RecyclerView mRvComment;
    @BindView(R.id.tv_comment_count)
    TextView mTvCommentCount;

    protected StateView mStateView;
    private String mChannelCode;
    private String mDetalUrl;
    private String mGroupId;
    private String mItemId;
    private int mPosition;
    private CommentAdapter mCommentAdapter;
    protected NewsDetailHeaderView mHeaderView;
    private List<CommentVo> mCommentList = new ArrayList<>();
    private CommentResponse mCommentResponse;

    @Override
    protected NewsDetailPresenter createPresenter() {
        return new NewsDetailPresenter(this);
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        mChannelCode = intent.getStringExtra(CHANNEL_CODE);
        mPosition = intent.getIntExtra(POSITION, 0);
        mDetalUrl = intent.getStringExtra(DETAIL_URL);
        mGroupId = intent.getStringExtra(GROUP_ID);
        mItemId = intent.getStringExtra(ITEM_ID);
        mItemId = mItemId.replace("i", "");
        mPresenter.getDetails(mDetalUrl);
        mStateView = StateView.inject(mFlContent);
        if (mStateView != null) {
            mStateView.setLoadingResource(R.layout.page_loading);
            mStateView.setRetryResource(R.layout.page_net_error);
        }
        loadComments();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mCommentAdapter = new CommentAdapter(this, mCommentList);
        mRvComment.setLayoutManager(new LinearLayoutManager(this));
        mRvComment.setAdapter(mCommentAdapter);
        //header view
        mHeaderView = new NewsDetailHeaderView(this);
        mCommentAdapter.addHeaderView(mHeaderView);
        //下拉刷新
        mCommentAdapter.setEnableLoadMore(true);
        mCommentAdapter.setOnLoadMoreListener(this, mRvComment);
        mCommentAdapter.setEmptyView(R.layout.pager_no_comment);
        mCommentAdapter.setHeaderAndEmpty(true);
        mStateView.setOnRetryClickListener(() -> {
            Log.d(TAG,"重试loadComments");
            loadComments();
        });
    }

    protected void loadComments() {
        mStateView.showLoading();
        mPresenter.getComments(mGroupId, mItemId, 1);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    public void onError() {
        mStateView.showRetry();
    }

    @Override
    public void onGetCommentSuccess(CommentResponse body) {
        mStateView.showContent();
        if (body.data == null || body.data.isEmpty()) {
            mCommentAdapter.loadMoreEnd();
        }

        if (body.total_number > 0) {
            mTvCommentCount.setVisibility(View.VISIBLE);
            mTvCommentCount.setText(String.valueOf(body.total_number));
        } else {
            mTvCommentCount.setVisibility(View.INVISIBLE);
        }

        mCommentList.addAll(body.data);
        mCommentAdapter.notifyDataSetChanged();
        if (!body.has_more) {
            mCommentAdapter.loadMoreEnd();
        } else {
            mCommentAdapter.loadMoreComplete();
        }
        mCommentResponse = body;
    }

    //下拉刷新
    @Override
    public void onLoadMoreRequested() {
        mPresenter.getComments(mGroupId, mItemId, mCommentList.size() / Constant.COMMENT_PAGE_SIZE + 1);
    }


    @OnClick(R.id.fl_comment_icon)
    void onCommentsClick() {
        RecyclerView.LayoutManager layoutManager = mRvComment.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            int firstPosition = linearManager.findFirstVisibleItemPosition();
            int last = linearManager.findLastVisibleItemPosition();
            if (firstPosition == 0 && last == 0) {
                //处于头部，滚动到第一个条目
                mRvComment.scrollToPosition(1);
            } else {
                //不是头部，滚动到头部
                mRvComment.scrollToPosition(0);
            }
        }
    }

    protected void postRefreshVideoProgressAndCommentCountEvent(boolean isVideo) {
        VideoProgressAndCommentEvent event = new VideoProgressAndCommentEvent();
        event.setChannelCode(mChannelCode);
        event.setPosition(mPosition);
        if (mCommentResponse != null) {
            event.setCommmentCount(mCommentResponse.total_number);
        }
        if (isVideo && JZMediaManager.instance() != null && JzvdMgr.getCurrentJzvd() != null) {
            //如果是视频详情
            long progress = JZMediaManager.instance().getCurrentPosition();
            event.setProgress(progress);
        }
        EventBus.getDefault().postSticky(event);
        finish();
    }
}
