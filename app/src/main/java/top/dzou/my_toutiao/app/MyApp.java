package top.dzou.my_toutiao.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

public class MyApp extends BaseApp {

    @SuppressLint("CI_StaticFieldLeak")
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static Handler getMainHandler() {
        return getMainHandler();
    }

    public static long getMainThreadId() {
        return getMainThreadId();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
