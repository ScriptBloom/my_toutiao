package top.dzou.my_toutiao.mvp.presenter;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.dzou.my_toutiao.api.net.ApiRetrofit;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.model.News;
import top.dzou.my_toutiao.model.NewsData;
import top.dzou.my_toutiao.model.NewsResponse;
import top.dzou.my_toutiao.mvp.view.INewsView;
import top.dzou.my_toutiao.utils.PreUtils;

public class NewsListPresenter extends BasePresenter<INewsView> {

    private static final String TAG = NewsListPresenter.class.getSimpleName();

    private long lastTime;

    public NewsListPresenter(INewsView view) {
        super(view);
    }

    public void getNewsList(String channelCode) {
        Log.d(TAG,"获取新闻列表"+channelCode);
        lastTime = PreUtils.getLong(channelCode, 0);
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis() / 1000;
        }

        Call<NewsResponse> newsList = ApiRetrofit.getServiceInstance().getNewsList(channelCode, lastTime, System.currentTimeMillis() / 1000);
        newsList.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                Log.d(TAG,"response");
                lastTime = System.currentTimeMillis() / 1000;
                PreUtils.putLong(channelCode, lastTime);//保存刷新的时间戳

                List<NewsData> data = response.body().data;
                List<News> newsList = new ArrayList<>();
                if (data != null && !data.isEmpty()) {
                    for (NewsData newsData : data) {
                        News news = new Gson().fromJson(newsData.content, News.class);
                        newsList.add(news);
                    }
                }
                mView.onSuccess(newsList,response.body().tips.display_info);
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.d(TAG,"error:"+t.getMessage());
                mView.onError();
            }
        });
    }
}
