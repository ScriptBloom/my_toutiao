package top.dzou.my_toutiao.ui.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.jzvd.Jzvd;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.ui.adapter.MainTabAdapter;
import top.dzou.my_toutiao.ui.fragment.HomeFragment;
import top.dzou.my_toutiao.ui.fragment.MicroToutiaoFragment;
import top.dzou.my_toutiao.ui.fragment.MineFragment;
import top.dzou.my_toutiao.ui.fragment.VideoFragment;
import top.dzou.my_toutiao.utils.UIUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bnv) BottomNavigationView mBnv;
    @BindView(R.id.viewpager) ViewPager mMainTab;

    private MainTabAdapter mMinTabAdapter;
    private List<BaseFragment> mFragments = new ArrayList<>();

    private Map<Integer, Integer> mStatusColors = new HashMap<Integer, Integer>() {
        {
            put(R.id.homeFragment, R.color.color_D33D3C);
            put(R.id.videoFragment, R.color.color_BDBDBD);
            put(R.id.microToutiaoFragment, R.color.color_BDBDBD);
        }
    };

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        mFragments.add(new HomeFragment());
        mFragments.add(new VideoFragment());
        mFragments.add(new MicroToutiaoFragment());
        mFragments.add(new MineFragment());
    }

    @Override
    protected void initView() {
        super.initView();
        //设置statusBar
        UIUtils.setStatusBarColor(this, UIUtils.getColor(mStatusColors.get(R.id.homeFragment)));
        UIUtils.hideActionBar(MainActivity.this);
        /*NavController navController = Navigation.findNavController(MainActivity.this, R.id.fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(mBnv.getMenu()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(mBnv, navController);*/
        mMinTabAdapter = new MainTabAdapter(mFragments,getSupportFragmentManager());
        mMainTab.setAdapter(mMinTabAdapter);
        //设置图标颜色
        mBnv.setItemIconTintList(null);
        //使用创建ColorStateList的方式来设置text颜色
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };

        int[] colors = new int[]{getResources().getColor(R.color.black,null),
                getResources().getColor(R.color.color_D33D3C,null)
        };
        mBnv.setItemTextColor(new ColorStateList(states,colors));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    protected void initListener() {
        super.initListener();
        //设置导航
        //使用Navigation监听器设置statusBar颜色
        mBnv.setOnNavigationItemSelectedListener(menuItem -> {
            setStatusBarColor(menuItem.getItemId());
            Jzvd.releaseAllVideos();//底部页签切换或者是下拉刷新，释放资源
            Log.d("menu", menuItem.getTitle().toString() + "更换statusBar颜色");
            switch (menuItem.getItemId()){
                case R.id.homeFragment:
                    mMainTab.setCurrentItem(0);
                    break;
                case R.id.videoFragment:
                    mMainTab.setCurrentItem(1);
                    break;
                case R.id.microToutiaoFragment:
                    mMainTab.setCurrentItem(2);
                    break;
                case R.id.mineFragment:
                    mMainTab.setCurrentItem(3);
                    break;
            }
            return true;
        });
        mMainTab.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBnv.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
