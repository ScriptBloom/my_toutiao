package top.dzou.my_toutiao.ui.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseFragment;
import top.dzou.my_toutiao.mvp.presenter.NewsListPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsListFragment extends BaseFragment<NewsListPresenter> {


    public NewsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    protected int getContentLayoutId() {
        return 0;
    }

    @Override
    protected NewsListPresenter createPresenter() {
        return null;
    }

}
