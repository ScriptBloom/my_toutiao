package top.dzou.my_toutiao.ui.activity;

import android.text.TextUtils;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.Jzvd;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.event.VideoProgressAndCommentEvent;
import top.dzou.my_toutiao.model.NewsDetail;
import top.dzou.my_toutiao.ui.activity.base.NewsDetailBaseActivity;
import top.dzou.my_toutiao.ui.widget.MyJzvdStd;
import top.dzou.my_toutiao.ui.widget.NewsDetailHeaderView;
import top.dzou.my_toutiao.utils.UIUtils;
import top.dzou.my_toutiao.utils.VideoParser;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VideoDetailActivity extends NewsDetailBaseActivity {

    @BindView(R.id.iv_back) ImageView mBack;
    @BindView(R.id.jzvd) MyJzvdStd mJzvd;

    private String mVideoUrl;
    private long mProgress;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_video_detail;
    }


    @Override
    protected void initView() {
        super.initView();
        UIUtils.setStatusBarColor(this,UIUtils.getResource().getColor(R.color.black,null));
    }

    @Override
    protected void initData() {
        super.initData();
        mProgress = getIntent().getLongExtra(PROGRESS,0);
        mVideoUrl = getIntent().getStringExtra(VIDEO_URL);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mJzvd.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, VISIBLE, GONE);
        mJzvd.titleTextView.setVisibility(GONE);
    }

    @Override
    public void onGetDetailSuccess(NewsDetail newsDetail) {
        mHeaderView.setData(newsDetail, () -> mStateView.showContent());
        if(TextUtils.isEmpty(mVideoUrl)){
            //解析视频
            VideoParser parser = new VideoParser() {
                @Override
                public void onSuccess(String url) {
                    UIUtils.postTaskSafely(()->{
                        playVideo(url,newsDetail);
                    });
                }

                @Override
                public void onDecodeError(String errorMsg) {
                    UIUtils.showToast(errorMsg);
                }
            };
            parser.decodeVideo(newsDetail.url);
        }else {
            playVideo(mVideoUrl,newsDetail);
        }
    }

    private void playVideo(String url, NewsDetail newsDetail) {
        mJzvd.setUp(url,newsDetail.title, Jzvd.SCREEN_WINDOW_LIST);
        mJzvd.seekToInAdvance = mProgress;
        mJzvd.startVideo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(Jzvd.backPress()){
            return;
        }
        postRefreshVideoProgressAndCommentCountEvent(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @OnClick(R.id.iv_back)void onBackClick(){
        postRefreshVideoProgressAndCommentCountEvent(true);
    }

}
