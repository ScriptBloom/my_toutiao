package top.dzou.my_toutiao.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import top.dzou.my_toutiao.model.Channel;
import top.dzou.my_toutiao.ui.fragment.NewsListFragment;

public class ChannelPageAdapter extends FragmentStatePagerAdapter {
    private List<NewsListFragment> mFragments;
    private List<Channel> mChannels;

    public ChannelPageAdapter(@NonNull FragmentManager fm, int behavior, List<NewsListFragment> fragmentList,
                              List<Channel> channelList) {
        super(fm, behavior);
        mFragments = fragmentList != null ? fragmentList : new ArrayList<>();
        mChannels = channelList != null ? channelList : new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).getChannelTitle();
    }
}
