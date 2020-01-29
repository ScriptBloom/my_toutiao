package top.dzou.my_toutiao.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.utils.UIUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bnv)
    BottomNavigationView mBnv;


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
    protected void initView() {
        super.initView();
        //设置statusBar
        UIUtils.setStatusBarColor(this, UIUtils.getColor(mStatusColors.get(R.id.homeFragment)));
        UIUtils.hideActionBar(MainActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置导航
        //使用Navigation监听器设置statusBar颜色
        mBnv.setOnNavigationItemSelectedListener(menuItem -> {
            setStatusBarColor(menuItem.getItemId());
            Log.d("menu", menuItem.getTitle().toString() + "更换statusBar颜色");
            return true;
        });
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(mBnv.getMenu()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(mBnv, navController);
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

    }
}
