package top.dzou.my_toutiao.ui.fragment;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.Jzvd;
import me.weyye.library.colortrackview.ColorTrackTabLayout;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.listener.OnChannelListener;
import top.dzou.my_toutiao.model.Channel;
import top.dzou.my_toutiao.ui.activity.FlashActivity;
import top.dzou.my_toutiao.ui.adapter.ChannelPageAdapter;
import top.dzou.my_toutiao.utils.PreUtils;
import top.dzou.my_toutiao.utils.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements OnChannelListener {

    @BindView(R.id.tab_channel) TabLayout mTabLayout;
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.iv_operation) ImageView mImgView;

    private FragmentStatePagerAdapter mPagerAdapter;
    private List<NewsListFragment> mFragments ;
    private List<Channel> mSelectedChannels ;
    private List<Channel> mUnSelectedChannels ;
    private static Gson sGson = new GsonBuilder().create();
    private String[] mChannelCodes;

    public HomeFragment() {
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        super.initData();
        mSelectedChannels = new ArrayList<>();
        mUnSelectedChannels = new ArrayList<>();
        mFragments = new ArrayList<>();
        //整理数据 如果选择过就用选择的，没有就用全部
        String selected = PreUtils.getString(Constant.SELECTED_CHANNEL_JSON, "");
        String unSelected = PreUtils.getString(Constant.UNSELECTED_CHANNEL_JSON, "");
        if (TextUtils.isEmpty(selected) && TextUtils.isEmpty(unSelected)) {
            String[] channels = getResources().getStringArray(R.array.channel);
            String[] channelCodes = getResources().getStringArray(R.array.channel_code);
            for (int i = 0; i < channelCodes.length; i++) {
                mSelectedChannels.add(new Channel(channels[i], channelCodes[i]));
            }
            String channelJson = sGson.toJson(mSelectedChannels);
            PreUtils.putString(Constant.SELECTED_CHANNEL_JSON, channelJson);
        } else {
            mSelectedChannels = sGson.fromJson(selected, new TypeToken<List<Channel>>() {
            }.getType());
            mUnSelectedChannels = sGson.fromJson(unSelected, new TypeToken<List<Channel>>() {
            }.getType());
            if(mUnSelectedChannels==null) mUnSelectedChannels = new ArrayList<>();
        }
        //把tab添加到fragment的arguments中
        mChannelCodes = getResources().getStringArray(R.array.channel_code);
        for (Channel channel : mSelectedChannels) {
            NewsListFragment fragment = new NewsListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.CHANNEL_CODE, channel.getChannelCode());
            bundle.putBoolean(Constant.IS_VIDEO_LIST, channel.getChannelCode().equals(mChannelCodes[1]));
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mPagerAdapter = new ChannelPageAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT, mFragments, mSelectedChannels);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSelectedChannels.size());
        //设置每个Tab的内边距
//        mTabLayout.setTabPaddingLeftAndRight(UIUtils.dip2Px(10), UIUtils.dip2Px(10));
        mTabLayout.setupWithViewPager(mViewPager, true);
        mTabLayout.post(() -> {
            //设置最小宽度，使其可以在滑动一部分距离
            ViewGroup slidingTabStrip = (ViewGroup) mTabLayout.getChildAt(0);
            slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + mImgView.getMeasuredWidth());
        });
//        mTabLayout.setSelectedTabIndicatorHeight(0);
    }


    @OnClick(R.id.iv_operation) void onClick(View view){
        switch (view.getId()){
            case R.id.iv_operation:
                Log.d("dzou","点击添加channel");
                ChannelDialogFragment dialogFragment = ChannelDialogFragment.getInstance(mSelectedChannels,mUnSelectedChannels);
                dialogFragment.setOnChannelListener(this);
                dialogFragment.show(getChildFragmentManager(), "CHANNEL");
                dialogFragment.setmOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mPagerAdapter.notifyDataSetChanged();
                        mViewPager.setOffscreenPageLimit(mSelectedChannels.size());
                        mViewPager.setCurrentItem(mTabLayout.getSelectedTabPosition());
//                        mTabLayout.setCurrentItem(mTabLayout.getSelectedTabPosition());
                        ViewGroup slidingTabStrip = (ViewGroup) mTabLayout.getChildAt(0);
                        //注意：因为最开始设置了最小宽度，所以重新测量宽度的时候一定要先将最小宽度设置为0
                        slidingTabStrip.setMinimumWidth(0);
                        slidingTabStrip.measure(0, 0);
                        slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + mImgView.getMeasuredWidth());

                        //保存选中和未选中的channel
                        PreUtils.putString(Constant.SELECTED_CHANNEL_JSON, sGson.toJson(mSelectedChannels));
                        PreUtils.putString(Constant.UNSELECTED_CHANNEL_JSON, sGson.toJson(mUnSelectedChannels));
                    }
                });
                break;
        }
    }

    //todo
    @Override
    protected void initListener() {
        super.initListener();
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
        /*mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
    }

    @Override
    public void onItemMove(int starPos, int endPos) {
        listMove(mSelectedChannels, starPos, endPos);
        listMove(mFragments, starPos, endPos);
    }


    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {
        //移动到我的频道
        Channel channel = mUnSelectedChannels.remove(starPos);
        mSelectedChannels.add(endPos, channel);
        NewsListFragment newsFragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CHANNEL_CODE, channel.channelCode);
        bundle.putBoolean(Constant.IS_VIDEO_LIST, channel.channelCode.equals(mChannelCodes[1]));
        newsFragment.setArguments(bundle);
        mFragments.add(newsFragment);
    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
        //移动到推荐频道
        mUnSelectedChannels.add(endPos, mSelectedChannels.remove(starPos));
        mFragments.remove(starPos);
    }

    private void listMove(List datas, int starPos, int endPos) {
        Object o = datas.get(starPos);
        //先删除之前的位置
        datas.remove(starPos);
        //添加到现在的位置
        datas.add(endPos, o);
    }

    @Override
    protected void loadData() {

    }

    public String getCurrentChannelCode(){
        int currentItem = mViewPager.getCurrentItem();
        return mSelectedChannels.get(currentItem).channelCode;
    }
}
