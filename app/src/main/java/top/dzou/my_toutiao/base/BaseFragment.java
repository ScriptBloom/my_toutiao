package top.dzou.my_toutiao.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.nukc.stateview.StateView;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import top.dzou.my_toutiao.R;

public abstract class BaseFragment<T extends BasePresenter> extends LazyLoadFragment {
    protected Activity mActivity;
    protected View rootView;
    protected T mPresenter;
    protected StateView mStateView;//用于显示加载中、网络异常，空布局、内容布局

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getContentLayoutId(),container,false);
            ButterKnife.bind(this, rootView);
            mStateView = StateView.inject(getStateViewRoot());
            if (mStateView != null){
                mStateView.setLoadingResource(R.layout.page_loading);
                mStateView.setRetryResource(R.layout.page_net_error);
            }
            initData();
            initView(rootView);
            initListener();
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    /**StateView的根布局，默认是整个界面，如果需要变换可以重写此方法*/
    public View getStateViewRoot() {
        return rootView;
    }

    protected void initListener(){

    }

    protected void initData(){

    }

    protected void initView(View rootView){

    }

    protected abstract int getContentLayoutId();

    protected abstract T createPresenter();


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter = null;
        }
        rootView = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    public boolean isEventBusRegisted(Object subscribe) {
        return EventBus.getDefault().isRegistered(subscribe);
    }

    public void registerEventBus(Object subscribe) {
        if (!isEventBusRegisted(subscribe)) {
            EventBus.getDefault().register(subscribe);
        }
    }

    public void unregisterEventBus(Object subscribe) {
        if (isEventBusRegisted(subscribe)) {
            EventBus.getDefault().unregister(subscribe);
        }
    }
}
