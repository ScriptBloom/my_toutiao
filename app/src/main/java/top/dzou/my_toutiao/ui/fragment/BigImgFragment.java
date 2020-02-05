package top.dzou.my_toutiao.ui.fragment;

import android.view.MotionEvent;
import android.view.View;

import com.sunfusheng.GlideImageView;
import com.sunfusheng.progress.CircleProgressView;
import com.sunfusheng.progress.OnProgressListener;

import butterknife.BindView;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.model.js.ImgOpJs;

public class BigImgFragment extends BaseFragment {

    @BindView(R.id.pv_pic)
    GlideImageView mPv;
    @BindView(R.id.progressView)
    CircleProgressView mCPv;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_big_img;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        super.initData();
        String imgUrl = getArguments().getString(ImgOpJs.IMG);
        mPv.centerCrop().load(imgUrl, R.color.placeholder_color, new OnProgressListener() {
            @Override
            public void onProgress(boolean isComplete, int percentage, long bytesRead, long totalBytes) {
                mCPv.setProgress(percentage);
                mCPv.setVisibility(isComplete ? View.GONE : View.VISIBLE);
            }
        });
//        GlideImageLoader imageLoader = new GlideImageLoader(mPv);
//        imageLoader.setOnGlideImageViewListener(imgUrl, new OnGlideImageViewListener() {
//            @Override
//            public void onProgress(int percent, boolean isDone, GlideException exception) {
//                if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
//                    UIUtils.showToast(getString(R.string.net_error));
//                }
//                mCPv.setProgress(percent);
//                mCPv.setVisibility(isDone ? View.GONE : View.VISIBLE);
//            }
//        });
//        RequestOptions requestOptions = imageLoader.requestOptions(R.color.placeholder_color)
//                .centerCrop()
//                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//
//        RequestBuilder<Drawable> requestBuilder = imageLoader.requestBuilder(imgUrl, requestOptions);
//        requestBuilder.transition(DrawableTransitionOptions.withCrossFade())
//                .into(new SimpleTarget<Drawable>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
//                    @Override
//                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                        if (resource.getIntrinsicHeight() > DisplayUtil.getScreenHeight(mActivity)) {
//                            mPv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        }
//                        requestBuilder.into(mPv);
//                    }
//                });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initListener() {
        super.initListener();
        mPv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
//        mPv.setOnPhotoTapListener(new OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(ImageView view, float x, float y) {
//                mActivity.finish();
//            }
//        });
    }
}
