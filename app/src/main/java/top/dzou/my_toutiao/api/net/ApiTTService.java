package top.dzou.my_toutiao.api.net;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;
import top.dzou.my_toutiao.model.CommentResponse;
import top.dzou.my_toutiao.model.NewsDetail;
import top.dzou.my_toutiao.model.NewsResponse;
import top.dzou.my_toutiao.model.ResultResponse;
import top.dzou.my_toutiao.model.VideoModel;

public interface ApiTTService {
    String GET_ARTICLE_LIST = "api/news/feed/v62/?refer=1&count=20&loc_mode=4&device_id=34960436458&iid=13136511752";
    String GET_COMMENT_LIST = "article/v2/tab_comments/";

    /**
     * 获取视频页的html代码
     */
    @GET
    Call<String> getVideoHtml(@Url String url);

    /**
     * 获取视频数据json
     * @param url
     * @return
     */
    @GET
    Call<VideoModel> getVideoData(@Url String url);

    /**
     * 获取新闻列表
     *
     * @param category 频道
     * @return
     */
    @GET(GET_ARTICLE_LIST)
    Call<NewsResponse> getNewsList(@Query("category") String category, @Query("min_behot_time") long lastTime, @Query("last_refresh_sub_entrance_interval") long currentTime);


    /**
     * 获取新闻详情
     */
    @GET
    Call<ResultResponse<NewsDetail>> getNewsDetail(@Url String url);

    /**
     * 获取评论列表数据
     *
     * @param groupId
     * @param itemId
     * @param offset
     * @param count
     * @return
     */
    @GET(GET_COMMENT_LIST)
    Call<CommentResponse> getComment(@Query("group_id") String groupId, @Query("item_id") String itemId, @Query("offset") String offset, @Query("count") String count);
}
