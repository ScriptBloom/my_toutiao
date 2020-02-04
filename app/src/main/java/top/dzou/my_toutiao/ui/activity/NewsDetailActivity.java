package top.dzou.my_toutiao.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.OnClick;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.model.NewsDetail;
import top.dzou.my_toutiao.ui.activity.base.NewsDetailBaseActivity;
import top.dzou.my_toutiao.ui.widget.NewsDetailHeaderView;
import top.dzou.my_toutiao.utils.UIUtils;

public class NewsDetailActivity extends NewsDetailBaseActivity {

    @BindView(R.id.iv_back)
    ImageView mBack;
    @BindView(R.id.ll_user)
    LinearLayout mLlUser;
    @BindView(R.id.iv_avatar)
    ImageView mAvatar;
    @BindView(R.id.tv_author)
    TextView mTvAuthor;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_news_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        UIUtils.setStatusBarColor(this, UIUtils.getResource().getColor(R.color.color_BDBDBD, null));
    }

    @Override
    protected void initListener() {
        super.initListener();
        int llInfoBottom = mHeaderView.mLlInfo.getBottom();
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRvComment.getLayoutManager();
        mRvComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = layoutManager.findFirstVisibleItemPosition();
                View first = layoutManager.findViewByPosition(position);
                int height = layoutManager.getHeight();
                int scrollHeight = position * height - first.getTop();
                if(scrollHeight>=llInfoBottom){
                    mLlUser.setVisibility(View.VISIBLE);
                }else {
                    mLlUser.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onGetDetailSuccess(NewsDetail body) {
        mHeaderView.setData(body, new NewsDetailHeaderView.OnDataLoadedListener() {
            @Override
            public void onLoadFinish() {
                mStateView.showContent();
            }
        });
        mLlUser.setVisibility(View.GONE);
        if (body.media_user != null){
            Glide.with(this)
                    .load(body.media_user.avatar_url)
                    .apply(new RequestOptions()
                    .centerCrop().circleCrop().placeholder(R.mipmap.ic_circle_default))
                    .into(mAvatar);
            mTvAuthor.setText(body.media_user.screen_name);
        }
    }

    @Override
    public void onBackPressed() {
        postRefreshVideoProgressAndCommentCountEvent(false);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        postRefreshVideoProgressAndCommentCountEvent(false);
    }
}
