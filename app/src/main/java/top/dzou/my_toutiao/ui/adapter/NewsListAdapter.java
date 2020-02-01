package top.dzou.my_toutiao.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.model.News;

public class NewsListAdapter extends BaseQuickAdapter<News, BaseViewHolder> {

    private String mChannelCode;

    public NewsListAdapter(int layoutResId, @Nullable List<News> data) {
        super(layoutResId, data);
    }

    public NewsListAdapter(@Nullable List<News> data,@Nullable String channelCode) {
        this(R.layout.fragment_news_list,data);
        this.mChannelCode = channelCode;
    }

    public NewsListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, News item) {

    }
}
