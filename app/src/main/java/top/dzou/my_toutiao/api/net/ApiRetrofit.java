package top.dzou.my_toutiao.api.net;

import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import top.dzou.my_toutiao.app.MyApp;
import top.dzou.my_toutiao.utils.NetUtils;

public class ApiRetrofit {
    private static ApiRetrofit mApiRetrofit;
    private static Retrofit RETROFIT;
    private OkHttpClient mClient;
    private static ApiTTService SERVICE;
    private static final String TAG = "toutiao request";

    private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(new File(MyApp.getContext().getCacheDir(), "responses"), cacheSize);
    /**接口根地址*/
    public static final String BASE_SERVER_URL = "http://is.snssdk.com/";
    public static final String HOST_VIDEO = "http://i.snssdk.com";
    public static final String URL_VIDEO="/video/urls/v/1/toutiao/mp4/%s?r=%s";

    private static Interceptor mHeaderIntercepter = chain -> {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.108 Safari/537.36 2345Explorer/8.0.0.13547");
        builder.addHeader("Cache-Control", "max-age=0");
        builder.addHeader("Upgrade-Insecure-Requests", "1");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");
        builder.addHeader("Cookie", "uuid=\"w:f2e0e469165542f8a3960f67cb354026\"; __tasessionId=4p6q77g6q1479458262778; csrftoken=7de2dd812d513441f85cf8272f015ce5; tt_webid=36385357187");
        return chain.proceed(builder.build());
    };

    private static Interceptor mLogIntercepter = chain -> {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        Log.i(TAG,"----------Request Start----------------");
        Log.i(TAG,"| " + request.toString());
        Log.i(TAG,"| Response:" + content);
        Log.i(TAG,"----------Request End:" + duration + "毫秒----------");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    };

    //缓存配置
    private static Interceptor mCacheInterceptor = chain -> {

        CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        cacheBuilder.maxAge(0, TimeUnit.SECONDS);
        cacheBuilder.maxStale(365, TimeUnit.DAYS);
        CacheControl cacheControl = cacheBuilder.build();

        Request request = chain.request();
        if (!NetUtils.isNetworkAvailable(MyApp.getContext())) {
            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        if (NetUtils.isNetworkAvailable(MyApp.getContext())) {
            int maxAge = 0; // read from cache
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public ,max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    };



    private static Retrofit getRetrofitInstance(){
        if(RETROFIT==null){
            synchronized (ApiRetrofit.class){
                if(RETROFIT==null){
                    RETROFIT = new Retrofit.Builder()
                            .baseUrl(BASE_SERVER_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
//                                    .setLenient()
                                    .create()))
//                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//支持RxJava
                            .client(new OkHttpClient.Builder()
                                    .addInterceptor(mCacheInterceptor)
                                    .addInterceptor(mHeaderIntercepter)
                                    .addInterceptor(mLogIntercepter)
                                    .cache(cache)
                                    .connectTimeout(20, TimeUnit.SECONDS)
                                    .readTimeout(20, TimeUnit.SECONDS)
                                    .build())
                            .build();
                }
            }
        }
        return RETROFIT;
    }

    public static ApiTTService getServiceInstance(){
        if(SERVICE==null){
            synchronized (ApiRetrofit.class){
                if(SERVICE==null){
                    if(RETROFIT==null){
                        RETROFIT = getRetrofitInstance();
                    }
                    SERVICE = RETROFIT.create(ApiTTService.class);
                }
            }
        }
        return SERVICE;
    }
}
