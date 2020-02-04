package top.dzou.my_toutiao.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;

import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.utils.UIUtils;

public class FlashActivity extends BaseActivity {

    @Override
    protected void initView() {
        super.initView();
        UIUtils.hideStatusBar(FlashActivity.this);
//        UIUtils.hideActionBar(FlashActivity.this);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(FlashActivity.this, MainActivity.class));
            finish();
        },2000);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_splash;
    }
}
