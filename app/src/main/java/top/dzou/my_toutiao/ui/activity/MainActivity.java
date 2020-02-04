package top.dzou.my_toutiao.ui.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.jzvd.Jzvd;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.event.TabRefreshCompletedEvent;
import top.dzou.my_toutiao.event.TabRefreshEvent;
import top.dzou.my_toutiao.ui.adapter.MainTabAdapter;
import top.dzou.my_toutiao.ui.fragment.HomeFragment;
import top.dzou.my_toutiao.ui.fragment.MicroToutiaoFragment;
import top.dzou.my_toutiao.ui.fragment.MineFragment;
import top.dzou.my_toutiao.ui.fragment.VideoFragment;
import top.dzou.my_toutiao.utils.UIUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bnv)
    BottomNavigationView mBnv;
//    @BindView(R.id.viewpager) ViewPager mMainTab;

    private MainTabAdapter mMinTabAdapter;
    private List<BaseFragment> mFragments = new ArrayList<>();
    private int lastShowFragment = 0;
    private BaseFragment mHomeFrg, mVideoFrg, mMicroFrg, mMineFrg;
    private static final String HOME_FRAGMENT_TAG = "mHome";
    private static final String VIDEO_FRAGMENT_TAG = "mVideo";
    private static final String MICRO_FRAGMENT_TAG = "mMicro";
    private static final String MINE_FRAGMENT_TAG = "mMine";

    private Map<Integer, Integer> mStatusColors = new HashMap<Integer, Integer>() {
        {
            put(R.id.homeFragment, R.color.color_D33D3C);
            put(R.id.videoFragment, R.color.color_BDBDBD);
            put(R.id.microToutiaoFragment, R.color.color_BDBDBD);
        }
    };
    /*private Map<Integer, Integer> mPos = new HashMap<Integer, Integer>() {
        {
            put(0, R.id.homeFragment);
            put(1, R.id.videoFragment);
            put(2, R.id.microToutiaoFragment);
            put(3, R.id.mineFragment);
        }
    };*/


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        FragmentManager fManager = getSupportFragmentManager();
//        if (savedInstanceState != null) {
//            mHomeFrg = (BaseFragment) fManager.findFragmentByTag(HOME_FRAGMENT_TAG);
//            mVideoFrg = (BaseFragment) fManager.findFragmentByTag(VIDEO_FRAGMENT_TAG);
//            mMicroFrg = (BaseFragment) fManager.findFragmentByTag(MICRO_FRAGMENT_TAG);
//            mMineFrg = (BaseFragment) fManager.findFragmentByTag(MINE_FRAGMENT_TAG);
//        }
//        super.onCreate(savedInstanceState);
//    }


    @Override
    protected void initData() {
        super.initData();
//        mFragments.add(new HomeFragment());
//        mFragments.add(new VideoFragment());
//        mFragments.add(new MicroToutiaoFragment());
//        mFragments.add(new MineFragment());

        mMineFrg = new MineFragment();
        mHomeFrg = new HomeFragment();
        mVideoFrg = new VideoFragment();
        mMicroFrg = new MicroToutiaoFragment();
    }

    @Override
    protected void initView() {
        super.initView();
        //设置statusBar
        UIUtils.setStatusBarColor(this, UIUtils.getColor(mStatusColors.get(R.id.homeFragment)));
//        UIUtils.hideActionBar(MainActivity.this);
        /*NavController navController = Navigation.findNavController(MainActivity.this, R.id.fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(mBnv.getMenu()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(mBnv, navController);*/

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, mHomeFrg, HOME_FRAGMENT_TAG)
                .show(mHomeFrg)
                .commit();
        mMinTabAdapter = new MainTabAdapter(mFragments, getSupportFragmentManager());
//        mMainTab.setAdapter(mMinTabAdapter);
        //设置图标颜色
        mBnv.setItemIconTintList(null);
        //使用创建ColorStateList的方式来设置text颜色
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };

        int[] colors = new int[]{getResources().getColor(R.color.black, null),
                getResources().getColor(R.color.color_D33D3C, null)
        };
        mBnv.setItemTextColor(new ColorStateList(states, colors));
    }

    private void setStatusBarColor(int menuItemId) {
        if (menuItemId == R.id.mineFragment) {
            //如果是我的页面，状态栏设置为透明状态栏
            UIUtils.translucentStatusBar(MainActivity.this, true);
        } else {
            UIUtils.setStatusBarColor(MainActivity.this, UIUtils.getColor(mStatusColors.get(menuItemId)));
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initListener() {
        super.initListener();
        //设置导航
        //使用Navigation监听器设置statusBar颜色
//        mBnv.setOnNavigationItemSelectedListener(menuItem -> {
//            setStatusBarColor(menuItem.getItemId());
//            Jzvd.releaseAllVideos();//底部页签切换或者是下拉刷新，释放资源
//            switch (menuItem.getItemId()) {
//                case R.id.homeFragment:
//                    mMainTab.setCurrentItem(0);
//                    break;
//                case R.id.videoFragment:
//                    mMainTab.setCurrentItem(1);
//                    break;
//                case R.id.microToutiaoFragment:
//                    mMainTab.setCurrentItem(2);
//                    break;
//                case R.id.mineFragment:
//                    mMainTab.setCurrentItem(3);
//                    break;
//            }
//            //当前页和点击页同一页时触发刷新，使用event bus
//            int position = mPos.get(mMainTab.getCurrentItem());
//            if (position == menuItem.getItemId() && mMainTab.getCurrentItem() == 0 || mMainTab.getCurrentItem() == 1) {
//                //如果当前页码和点击的页码一致,进行下拉刷新
//                String channelCode = "";
//                if (position == 0) {
//                    channelCode = ((HomeFragment) mFragments.get(0)).getCurrentChannelCode();//获取到首页当前显示的fragment的频道
//                } else {
//                    channelCode = ((VideoFragment) mFragments.get(1)).getCurrentChannelCode();//获取到视频当前显示的fragment的频道
//                }
//                //本来用于设置动画，但是BottomNavigation无法设置MenuItem动画，遂摒弃
////                postTabRefreshEvent(menuItem, mMainTab.getCurrentItem(), channelCode);//发送下拉刷新的事件
//            }
//            return true;
//        });

        mBnv.setOnNavigationItemSelectedListener(menuItem -> {
            setStatusBarColor(menuItem.getItemId());
            Jzvd.releaseAllVideos();
            switch (menuItem.getItemId()) {
                case R.id.homeFragment:
                    if (lastShowFragment != 0) {
                        switchFrament(mHomeFrg, HOME_FRAGMENT_TAG);
                        lastShowFragment = 0;
                    }
                    break;
                case R.id.videoFragment:
                    if (lastShowFragment != 1) {
                        switchFrament(mVideoFrg, VIDEO_FRAGMENT_TAG);
                        lastShowFragment = 1;
                    }
                    break;
                case R.id.microToutiaoFragment:
                    if (lastShowFragment != 2) {
                        switchFrament(mMicroFrg, MICRO_FRAGMENT_TAG);
                        lastShowFragment = 2;
                    }
                    break;
                case R.id.mineFragment:
                    if (lastShowFragment != 3) {
                        switchFrament(mMineFrg, MINE_FRAGMENT_TAG);
                        lastShowFragment = 3;
                    }
                    break;
            }
            return true;
        });
//        mMainTab.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                mBnv.getMenu().getItem(position).setChecked(true);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    public void switchFrament(Fragment frg, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAll(transaction);
        if (!frg.isAdded()) {
            transaction.add(R.id.fragment, frg, tag);
        }
        transaction.show(frg).commitAllowingStateLoss();
    }

    private void hideAll(FragmentTransaction transaction) {
        if (mMicroFrg != null) {
            transaction.hide(mMicroFrg);
        }
        if (mHomeFrg != null) {
            transaction.hide(mHomeFrg);
        }
        if (mVideoFrg != null) {
            transaction.hide(mVideoFrg);
        }
        if (mMineFrg != null) {
            transaction.hide(mMineFrg);
        }
    }

    private void postTabRefreshEvent(MenuItem menuItem, int position, String channelCode) {
        TabRefreshEvent event = new TabRefreshEvent(menuItem, channelCode, position);
        EventBus.getDefault().post(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshCompletedEvent(TabRefreshCompletedEvent event) {
        //接收到刷新完成的事件，取消旋转动画，更换底部首页页签图标
        MenuItem bottomItem = mBnv.getMenu().getItem(0);

        cancelTabLoading(bottomItem);//停止旋转动画

        bottomItem.setIcon(R.mipmap.tab_home_selected);//更换成首页原来图标
        bottomItem.setChecked(true);//刷新图标
    }

    /**
     * 停止首页页签的旋转动画
     */
    private void cancelTabLoading(MenuItem bottomItem) {
        Animation animation = bottomItem.getActionView().getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEventBus(MainActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterEventBus(MainActivity.this);
    }

}
