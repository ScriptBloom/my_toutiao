package top.dzou.my_toutiao.ui.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.jzvd.JzvdStd;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.listener.VideoStateListener;
import top.dzou.my_toutiao.model.News;
import top.dzou.my_toutiao.ui.widget.MyJzvdStd;
import top.dzou.my_toutiao.utils.TimeUtils;
import top.dzou.my_toutiao.utils.UIUtils;
import top.dzou.my_toutiao.utils.VideoParser;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VideoRvAdapter extends BaseQuickAdapter<News, BaseViewHolder> {

    public VideoRvAdapter(int layoutResId, @Nullable List<News> data) {
        super(layoutResId, data);
    }

    public VideoRvAdapter(@Nullable List<News> data) {
        this(R.layout.item_video_list, data);
    }

    public VideoRvAdapter(int layoutResId) {
        this(layoutResId, null);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, News news) {
        MyJzvdStd jzvdStd = helper.getView(R.id.video_player);
        helper.setVisible(R.id.ll_title, true);//显示标题栏
        helper.setText(R.id.tv_title, news.title);//设置标题
        if (news.video_detail_info != null) {
            String format = UIUtils.getResource().getString(R.string.video_watch_count);
            int watchCount = news.video_detail_info.video_watch_count;
            String countUnit = "";
            if (watchCount > 10000) {
                watchCount = watchCount / 10000;
                countUnit = "万";
            }
            helper.setText(R.id.tv_watch_count, String.format(format, watchCount + countUnit));//播放次数
            //缩略图
            Glide.with(mContext)
                    .load(news.video_detail_info.detail_video_large_image.url)
                    .apply(new RequestOptions().placeholder(R.color.color_d8d8d8))
                    .into(jzvdStd.thumbImageView);
        }
        //作者头像
        Glide.with(mContext)
                .load(news.user_info.avatar_url)
                .apply(new RequestOptions().placeholder(R.mipmap.ic_circle_default).centerCrop().circleCrop())
                .into((ImageView) helper.getView(R.id.iv_avatar));
        helper.setVisible(R.id.ll_duration, true)//显示时长
                .setText(R.id.tv_duration, TimeUtils.secToTime(news.video_duration));//设置时长
        helper.setText(R.id.tv_author, news.user_info.name)//昵称
                .setText(R.id.tv_comment_count, String.valueOf(news.comment_count));//评论数
        jzvdStd.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
        jzvdStd.tinyBackImageView.setVisibility(GONE);
        jzvdStd.titleTextView.setText("");//清除标题,防止复用的时候出现
        jzvdStd.setVideoStateListener(new VideoStateListener() {

            boolean isVideoParsing = false; //视频是否在解析的标识

            @Override
            public void onStart() {
                String videoUrl = "";
                if (news.video_detail_info != null) {
                    //取出解析后的网址
                    videoUrl = news.video_detail_info.parse_video_url;
                }

                if (!TextUtils.isEmpty(videoUrl)) {
                    //如果已经解析过
                    jzvdStd.setUp(videoUrl, news.title, JzvdStd.SCREEN_WINDOW_LIST);
                    jzvdStd.startVideo();
                    return;
                }
                //没有解析过
                parseVideo();
            }

            private void parseVideo() {
                if (isVideoParsing) {
                    return;
                }
                //隐藏开始按钮 显示加载中
                jzvdStd.setAllControlsVisiblity(GONE, GONE, GONE, VISIBLE, VISIBLE, GONE, GONE);
                helper.setVisible(R.id.ll_duration, false);//隐藏时长
                helper.setVisible(R.id.ll_title, false);//隐藏标题栏
                VideoParser videoParser = new VideoParser() {
                    @Override
                    public void onSuccess(String url) {
                        UIUtils.postTaskSafely(new Runnable() {
                            @Override
                            public void run() {
                                //更改视频是否在解析的标识
                                isVideoParsing = false;

                                //准备播放
                                jzvdStd.setUp(url, news.title, JzvdStd.SCREEN_WINDOW_LIST);

                                if (news.video_detail_info != null) {
                                    news.video_detail_info.parse_video_url = url; //赋值解析后的视频地址
                                    jzvdStd.seekToInAdvance = news.video_detail_info.progress; //设置播放进度
                                }

                                //开始播放
                                jzvdStd.startVideo();
                            }
                        });
                    }

                    @Override
                    public void onDecodeError(String errorMsg) {
                        isVideoParsing = false;//更改视频是否在解析的标识
                        //隐藏加载中 显示开始按钮
                        jzvdStd.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                        UIUtils.showToast(errorMsg);
                    }
                };
                videoParser.decodeVideo(news.url);
            }

            //todo
            @Override
            public void onChangeScreen() {

            }
        });
    }
}
