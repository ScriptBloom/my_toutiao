package top.dzou.my_toutiao.mvp.view;

import java.util.List;

import top.dzou.my_toutiao.model.News;

public interface INewsView {
    void onSuccess(List<News> newList, String tipInfo);
    void onError();
}
