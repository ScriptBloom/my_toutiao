package top.dzou.my_toutiao.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;

import java.util.List;

import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.model.News;
import top.dzou.my_toutiao.utils.TimeUtils;
import top.dzou.my_toutiao.utils.UIUtils;
import top.dzou.ui_kit.swipe_rv.RefreshHeaderRvAdapter;

public class MultiNewsListAdapter extends RefreshHeaderRvAdapter<News> {

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
    private RequestOptions sOptions = new RequestOptions().placeholder(R.mipmap.ic_default);

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //更新Rv View需要在主线程执行
        UIUtils.postTaskSafely(()->{
            setHeaderView(mRefresh.getHeaderView());
        });
        return super.onCreateViewHolder(parent,viewType);
    }

    public MultiNewsListAdapter(@Nullable List<News> data, @Nullable String channelCode, Context context) {
        super(R.layout.fragment_news_list, data, context);
        this.mChannelCode = channelCode;
        //Step.1
        this.setMultiTypeDelegate(new MultiTypeDelegate<News>() {
            @Override
            protected int getItemType(News news) {
                //根据你的实体类来判断布局类型
                return getViewType(news);
            }
        });
        //Step.2
        this.getMultiTypeDelegate()
                .registerItemType(MultiNewsListAdapter.TEXT_NEWS, R.layout.item_text_news)
                .registerItemType(MultiNewsListAdapter.CENTER_SINGLE_PIC_NEWS, R.layout.item_center_video_news)
                .registerItemType(MultiNewsListAdapter.THREE_PICS_NEWS, R.layout.item_three_pics_news)
                .registerItemType(MultiNewsListAdapter.RIGHT_PIC_VIDEO_NEWS, R.layout.item_right_pic_video_news);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, News news) {
        doProcessCommon(baseViewHolder, news);
        switch (baseViewHolder.getItemViewType()) {
            case TEXT_NEWS:
                doProcessConvertTextNews(baseViewHolder, news);
                break;
            case CENTER_SINGLE_PIC_NEWS:
                doProcessConvertCenterPic(baseViewHolder, news);
                break;
            case THREE_PICS_NEWS:
                doProcessConvertThreePics(baseViewHolder, news);
                break;
            case RIGHT_PIC_VIDEO_NEWS:
                doProcessConvertRightPic(baseViewHolder, news);
                break;
            default:
                break;
        }
    }

    private void doProcessCommon(BaseViewHolder helper, News news) {
        if (TextUtils.isEmpty(news.title)) {
            //如果没有标题，则直接跳过
            return;
        }
        //设置标题、底部作者、评论数、发表时间
        helper.setText(R.id.tv_title, news.title)
                .setText(R.id.tv_author, news.source)
                .setText(R.id.tv_comment_num, news.comment_count + UIUtils.getResource().getString(R.string.comment))
                .setText(R.id.tv_time, TimeUtils.getShortTime(news.behot_time * 1000));

        //根据情况显示置顶、广告和热点的标签
        int position = helper.getAdapterPosition();
        String[] channelCodes = UIUtils.getResource().getStringArray(R.array.channel_code);
        boolean isTop = position == 0 && mChannelCode.equals(channelCodes[0]); //属于置顶
        boolean isHot = news.hot == 1;//属于热点新闻
        boolean isAD = !TextUtils.isEmpty(news.tag) ? news.tag.equals(Constant.ARTICLE_GENRE_AD) : false;//属于广告新闻
        boolean isMovie = !TextUtils.isEmpty(news.tag) ? news.tag.equals(Constant.TAG_MOVIE) : false;//如果是影视
        helper.setVisible(R.id.tv_tag, isTop || isHot || isAD);//如果是上面任意一个，显示标签
        helper.setVisible(R.id.tv_comment_num, !isAD);//如果是广告，则隐藏评论数

        String tag = "";
        if (isTop) {
            tag = UIUtils.getResource().getString(R.string.to_top);
            helper.setTextColor(R.id.tv_tag, UIUtils.getColor(R.color.color_F96B6B));
        } else if (isHot) {
            tag = UIUtils.getResource().getString(R.string.hot);
            helper.setTextColor(R.id.tv_tag, UIUtils.getColor(R.color.color_F96B6B));
        } else if (isAD) {
            tag = UIUtils.getResource().getString(R.string.ad);
            helper.setTextColor(R.id.tv_tag, UIUtils.getColor(R.color.color_3091D8));
        } else if (isMovie) {
            //如果是影视
            tag = UIUtils.getResource().getString(R.string.tag_movie);
            helper.setTextColor(R.id.tv_tag, UIUtils.getColor(R.color.color_F96B6B));
        }
        helper.setText(R.id.tv_tag, tag);
    }

    private void doProcessConvertRightPic(BaseViewHolder helper, News news) {
        //右侧小图布局，判断是否有视频
        if (news.has_video) {
            helper.setVisible(R.id.ll_duration, true);//显示时长
            helper.setText(R.id.tv_duration, TimeUtils.secToTime(news.video_duration));//设置时长
        } else {
            helper.setVisible(R.id.ll_duration, false);//隐藏时长
        }
        Glide.with(mContext)
                .load(news.middle_image.url)
                .apply(sOptions)
                .into((ImageView) helper.getView(R.id.iv_img));
    }

    private void doProcessConvertThreePics(BaseViewHolder helper, News news) {
        Glide.with(mContext)
                .load(news.image_list.get(0).url)
                .apply(sOptions)
                .into((ImageView) helper.getView(R.id.iv_img1));
        Glide.with(mContext)
                .load(news.image_list.get(1).url)
                .apply(sOptions)
                .into((ImageView) helper.getView(R.id.iv_img2));
        Glide.with(mContext)
                .load(news.image_list.get(2).url)
                .apply(sOptions)
                .into((ImageView) helper.getView(R.id.iv_img3));
    }

    private void doProcessConvertCenterPic(BaseViewHolder helper, News news) {
        //中间大图布局，判断是否有视频
        TextView tvBottomRight = helper.getView(R.id.tv_bottom_right);
        if (news.has_video) {
            helper.setVisible(R.id.iv_play, true);//显示播放按钮
            tvBottomRight.setCompoundDrawables(null, null, null, null);//去除TextView左侧图标
            helper.setText(R.id.tv_bottom_right, TimeUtils.secToTime(news.video_duration));//设置时长
            Glide.with(mContext)
                    .load(news.video_detail_info.detail_video_large_image.url)
                    .apply(sOptions)
                    .into((ImageView) helper.getView(R.id.iv_img));
        } else {
            helper.setVisible(R.id.iv_play, false);//隐藏播放按钮
            if (news.gallary_image_count == 1) {
                tvBottomRight.setCompoundDrawables(null, null, null, null);//去除TextView左侧图标
            } else {
                tvBottomRight.setCompoundDrawables(mContext.getResources().getDrawable(R.mipmap.icon_picture_group), null, null, null);//TextView增加左侧图标
                helper.setText(R.id.tv_bottom_right, news.gallary_image_count + UIUtils.getResource().getString(R.string.img_unit));//设置图片数
            }
            Glide.with(mContext)
                    .load(news.image_list.get(0).url.replace("list/300x196", "large"))
                    .apply(sOptions)
                    .into((ImageView) helper.getView(R.id.iv_img));
        }
    }

    private void doProcessConvertTextNews(BaseViewHolder baseViewHolder, News news) {
    }


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
