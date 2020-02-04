package top.dzou.my_toutiao.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.model.Comment;
import top.dzou.my_toutiao.model.CommentResponse;
import top.dzou.my_toutiao.model.CommentVo;
import top.dzou.my_toutiao.utils.TimeUtils;

public class CommentAdapter extends BaseQuickAdapter<CommentVo, BaseViewHolder> {

    private Context mContext;

    public CommentAdapter(Context context, List<CommentVo> data) {
        super(R.layout.item_comment, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentVo commentVo) {
        Glide.with(mContext)
                .load(commentVo.comment.user_profile_image_url)
                .apply(new RequestOptions().placeholder(R.mipmap.ic_circle_default)
                        .centerCrop()
                        .circleCrop())
                .into((ImageView) helper.getView(R.id.iv_avatar));
        helper.setText(R.id.tv_name, commentVo.comment.user_name)
                .setText(R.id.tv_like_count, String.valueOf(commentVo.comment.digg_count))
                .setText(R.id.tv_content, commentVo.comment.text)
                .setText(R.id.tv_time, TimeUtils.getShortTime(commentVo.comment.create_time * 1000));
    }
}
