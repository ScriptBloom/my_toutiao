package top.dzou.my_toutiao.ui.adapter;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.model.News;
import top.dzou.ui_kit.swipe_rv.RefreshHeaderRvAdapter;

public class NewsListAdapter extends RefreshHeaderRvAdapter<News> {

    /**
     * 纯文字布局(文章、广告)
     */
    public static final int TEXT_NEWS = 100;
    /**
     * 居中大图布局(1.单图文章；2.单图广告；3.视频，中间显示播放图标，右侧显示时长)
     */
    public static final int CENTER_SINGLE_PIC_NEWS = 200;
    /**
     * 右侧小图布局(1.小图新闻；2.视频类型，右下角显示视频时长)
     */
    public static final int RIGHT_PIC_VIDEO_NEWS = 300;
    /**
     * 三张图片布局(文章、广告)
     */
    public static final int THREE_PICS_NEWS = 400;
    private String mChannelCode;

//    public NewsListAdapter(int layoutResId, @Nullable List<News> data) {
//        super(layoutResId, data);
//    }

    public NewsListAdapter(@Nullable List<News> data, @Nullable String channelCode, Context context) {
        super(R.layout.fragment_news_list, data, context);
        this.mChannelCode = channelCode;
    }

//    public NewsListAdapter(int layoutResId) {
//        super(layoutResId);
//    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, News item) {

    }


    @Override
    protected int getViewType(News news) {
        if (news.has_video) {
            //如果有视频
            if (news.video_style == 0) {
                //右侧视频
                if (news.middle_image == null || TextUtils.isEmpty(news.middle_image.url)) {
                    return TEXT_NEWS;
                }
                return RIGHT_PIC_VIDEO_NEWS;
            } else if (news.video_style == 2) {
                //居中视频
                return CENTER_SINGLE_PIC_NEWS;
            }
        } else {
            //非视频新闻
            if (!news.has_image) {
                //纯文字新闻
                return TEXT_NEWS;
            } else {
                if (news.image_list == null || news.image_list.isEmpty()) {
                    //图片列表为空，则是右侧图片
                    return RIGHT_PIC_VIDEO_NEWS;
                }

                if (news.gallary_image_count == 3) {
                    //图片数为3，则为三图
                    return THREE_PICS_NEWS;
                }

                //中间大图，右下角显示图数
                return CENTER_SINGLE_PIC_NEWS;
            }
        }

        return TEXT_NEWS;
    }
}
