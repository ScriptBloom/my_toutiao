package top.dzou.my_toutiao.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.model.NewsDetail;
import top.dzou.my_toutiao.utils.TimeUtils;

public class NewsDetailHeaderView extends FrameLayout {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.ll_info)
    public LinearLayout mLlInfo;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_author)
    TextView mTvAuthor;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.wv_content)
    WebView mWvContent;

    private Context mContext;
    private OnDataLoadedListener mListener;

    public NewsDetailHeaderView(Context context) {
        this(context, null);
        mContext = context;
    }

    public NewsDetailHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsDetailHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.item_newss_detail_header, this);
        ButterKnife.bind(this, this);
    }

    public void setData(NewsDetail newsDetail, OnDataLoadedListener loadedListener) {
        mListener = loadedListener;
        mTvTitle.setText(newsDetail.title);
        if (newsDetail.media_user == null) {
            mLlInfo.setVisibility(INVISIBLE);
        } else {
            if (!TextUtils.isEmpty(newsDetail.media_user.avatar_url)) {
                Glide.with(mContext)
                        .load(newsDetail.media_user.avatar_url)
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.ic_circle_default)
                                .centerCrop()
                                .circleCrop())
                        .into(mIvAvatar);
            }
            mTvAuthor.setText(newsDetail.media_user.screen_name);
            mTvTime.setText(TimeUtils.getShortTime(newsDetail.publish_time * 1000L));
        }
        if (TextUtils.isEmpty(newsDetail.content)) {
            mWvContent.setVisibility(GONE);
            return;
        }
        mWvContent.getSettings().setJavaScriptEnabled(true);
        String htmlPart1 = "<!DOCTYPE HTML html>\n" +
                "<head><meta charset=\"utf-8\"/>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, user-scalable=no\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<style> \n" +
                "img{width:100%!important;height:auto!important}\n" +
                " </style>";
        String htmlPart2 = "</body></html>";
        String html = htmlPart1 + newsDetail.content + htmlPart2;
        mWvContent.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        mWvContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (mListener != null) {
                    mListener.onLoadFinish();
                }
            }
        });
    }


    public interface OnDataLoadedListener {
        void onLoadFinish();
    }
}
