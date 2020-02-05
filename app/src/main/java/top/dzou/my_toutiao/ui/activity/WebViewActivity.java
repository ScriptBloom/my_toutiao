package top.dzou.my_toutiao.ui.activity;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.OnClick;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.utils.UIUtils;

public class WebViewActivity extends BaseActivity {
    public static final String URL = "url";

    @BindView(R.id.wv_content)
    WebView mWvContent;
    @BindView(R.id.pb_loading)
    ProgressBar mProgress;

    private String mUrl;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        super.initData();
        mUrl = getIntent().getStringExtra(URL);
        if(!TextUtils.isEmpty(mUrl)) {
            mWvContent.loadUrl(mUrl);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        mWvContent.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && mWvContent.canGoBack()) {  //表示按返回键
                    mWvContent.goBack();   //后退
                    return true;    //已处理
                }
            }
            return false;
        });

        mWvContent.getSettings().setJavaScriptEnabled(true);
        mWvContent.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
        mWvContent.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgress.setProgress(newProgress);
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        UIUtils.setStatusBarColor(this,UIUtils.getResource().getColor(R.color.color_BDBDBD,null));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_web_view;
    }

    @OnClick(R.id.iv_back)void onBackClick(){
        finish();
    }
}
