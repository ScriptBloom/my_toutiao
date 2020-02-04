package top.dzou.my_toutiao.mvp.presenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.api.net.ApiRetrofit;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.model.CommentResponse;
import top.dzou.my_toutiao.model.NewsDetail;
import top.dzou.my_toutiao.model.ResultResponse;
import top.dzou.my_toutiao.mvp.view.INewsDetailView;

public class NewsDetailPresenter extends BasePresenter<INewsDetailView> {
    public NewsDetailPresenter(INewsDetailView view) {
        super(view);
    }


    public void getComments(String groupId, String itemId, int pageNow){
        int offset = (pageNow - 1) * Constant.COMMENT_PAGE_SIZE;
        Call<CommentResponse> commentCall = ApiRetrofit.getServiceInstance().getComment(groupId, itemId, offset + "", String.valueOf(Constant.COMMENT_PAGE_SIZE));
        commentCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                mView.onGetCommentSuccess(response.body());
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                mView.onError();
            }
        });
    }


    public void getDetails(String url){
        Call<ResultResponse<NewsDetail>> newsDetailCall = ApiRetrofit.getServiceInstance().getNewsDetail(url);
        newsDetailCall.enqueue(new Callback<ResultResponse<NewsDetail>>() {
            @Override
            public void onResponse(Call<ResultResponse<NewsDetail>> call, Response<ResultResponse<NewsDetail>> response) {
                mView.onGetDetailSuccess(response.body().data);
            }

            @Override
            public void onFailure(Call<ResultResponse<NewsDetail>> call, Throwable t) {
                mView.onError();
            }
        });
    }
}
