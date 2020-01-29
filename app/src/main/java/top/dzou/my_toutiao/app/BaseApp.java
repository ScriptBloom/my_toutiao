package top.dzou.my_toutiao.app;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Logger.addLogAdapter(new AndroidLogAdapter());
    }

}
