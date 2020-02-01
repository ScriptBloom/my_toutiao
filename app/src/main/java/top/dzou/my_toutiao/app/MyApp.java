package top.dzou.my_toutiao.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

public class MyApp extends BaseApp {

    @SuppressLint("CI_StaticFieldLeak")
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        LitePal.initialize(getApplicationContext());//初始化litePal
    }
}
