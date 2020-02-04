package top.dzou.my_toutiao.mvp.view;

import top.dzou.my_toutiao.model.CommentResponse;
import top.dzou.my_toutiao.model.NewsDetail;

public interface INewsDetailView {

    void onGetDetailSuccess(NewsDetail body);

    void onGetCommentSuccess(CommentResponse body);

    void onError();
}
