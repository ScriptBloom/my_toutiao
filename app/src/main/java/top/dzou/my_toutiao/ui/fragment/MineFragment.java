package top.dzou.my_toutiao.ui.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.base.BasePresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends BaseFragment {


    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

}
