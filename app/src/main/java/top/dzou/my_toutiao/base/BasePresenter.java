package top.dzou.my_toutiao.base;

import top.dzou.my_toutiao.api.net.ApiRetrofit;
import top.dzou.my_toutiao.api.net.ApiTTService;

public abstract class BasePresenter<V> {
    protected ApiTTService mApiService = ApiRetrofit.getServiceInstance();
    protected V mView;

    public BasePresenter(V view) {
        attachView(view);
    }

    public void attachView(V view) {
        mView = view;
    }

    public void detachView() {
        mView = null;
    }
}
