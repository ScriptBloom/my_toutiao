package top.dzou.my_toutiao.ui.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.Jzvd;
import me.weyye.library.colortrackview.ColorTrackTabLayout;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.model.Channel;
import top.dzou.my_toutiao.ui.adapter.ChannelPageAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends BaseFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tab_channel)
    TabLayout mTabLayout;

    private List<Channel> mChannels = new ArrayList<>();
    private List<NewsListFragment> mFragments = new ArrayList<>();
    private ChannelPageAdapter mAdapter;


    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }


    @Override
    protected void initData() {
        super.initData();
        mFragments = new ArrayList<>();
        mChannels = new ArrayList<>();
        String[] videoChannels = getResources().getStringArray(R.array.channel_video);
        String[] viedeoChannelCodes = getResources().getStringArray(R.array.channel_code_video);
        for (int i = 0; i < videoChannels.length; i++) {
            mChannels.add(new Channel(videoChannels[i], viedeoChannelCodes[i]));
        }
        for (Channel channel : mChannels) {
            NewsListFragment fragment = new NewsListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.CHANNEL_CODE, channel.getChannelCode());
            bundle.putBoolean(Constant.IS_VIDEO_LIST, true);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }

    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mAdapter = new ChannelPageAdapter(getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mFragments, mChannels);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mFragments.size());
        //设置每个Tab的内边距
//        mTabLayout.setTabPaddingLeftAndRight(UIUtils.dip2Px(10), UIUtils.dip2Px(10));
        mTabLayout.setupWithViewPager(mViewPager, true);
        mTabLayout.post(() -> {
            //设置最小宽度，使其可以在滑动一部分距离
            ViewGroup slidingTabStrip = (ViewGroup) mTabLayout.getChildAt(0);
            slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth());
        });
//        mTabLayout.setSelectedTabIndicatorHeight(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当页签切换的时候，如果有播放视频，则释放资源
                Jzvd.releaseAllVideos();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void loadData() {

    }

    public String getCurrentChannelCode(){
        int currentItem = mViewPager.getCurrentItem();
        return mChannels.get(currentItem).channelCode;
    }
}
