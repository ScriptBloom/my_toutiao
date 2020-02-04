package top.dzou.my_toutiao.ui.activity;

import android.webkit.WebView;

import butterknife.BindView;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.base.BasePresenter;

public class WebViewActivity extends BaseActivity {
    public static final String URL = "url";

    @BindView(R.id.wv_content)
    WebView mWvContent;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_web_view;
    }
}
