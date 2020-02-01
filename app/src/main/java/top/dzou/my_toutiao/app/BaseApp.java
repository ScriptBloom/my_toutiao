package top.dzou.my_toutiao.app;

import android.app.Application;
import android.os.Handler;

public class BaseApp extends Application {

    private static Thread mainThread;
    private static long mainThreadId;
    private static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mainThread = Thread.currentThread();
        mainThreadId = Thread.currentThread().getId();
        mainHandler = new Handler();
//        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static Thread getMainThread() {
        return mainThread;
    }

    public static long getMainThreadId() {
        return mainThreadId;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }
}
