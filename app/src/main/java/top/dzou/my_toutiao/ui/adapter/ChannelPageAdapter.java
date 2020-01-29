package top.dzou.my_toutiao.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import top.dzou.my_toutiao.model.Channel;

public class ChannelPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> listFragments;
    private List<Channel> channels;

    public ChannelPageAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> listFragments,
                              List<Channel> channels) {
        super(fm, behavior);
        this.listFragments = listFragments;
        this.channels = channels;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).getChannelTitle();
    }
}
