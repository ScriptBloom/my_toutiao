package top.dzou.ui_kit.swipe_rv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeFreshRecyclerView extends RecyclerView {

    private RefreshHeaderRvAdapter mAdapter;
    private OnRefresh mOnRefreshListener;
    private IRefresh mRefreshHeader;
    private float mLastY = -1;//保存结束的y坐标
    private float sumOffSet = 0;
    private boolean mRefreshing = false;

    public SwipeFreshRecyclerView(@NonNull Context context) {
        this(context,null);
    }

    public SwipeFreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeFreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnRefresh(OnRefresh mOnRefresh) {
        this.mOnRefreshListener = mOnRefresh;
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        this.mAdapter = (RefreshHeaderRvAdapter) adapter;
        mRefreshHeader = new ArrowRefresh(getContext().getApplicationContext());
        mAdapter.setmRefresh(mRefreshHeader);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(mLastY == -1){
            mLastY = e.getRawY();
        }
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getRawY();
                sumOffSet = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = (e.getRawY() - mLastY) / 3;//为了防止滑动幅度过大，将实际手指滑动的距离除以2
                sumOffSet += deltaY;
                mLastY = e.getRawY();
                if(isHeaderOnTop()&&!mRefreshing){
                    mRefreshHeader.onMove(deltaY, sumOffSet);//移动刷新的头部View
                    if (mRefreshHeader.getVisibleHeight() > 0) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isHeaderOnTop()&& !mRefreshing) {
                    if (mRefreshHeader.onRelease()) {
                        //手指松开
                        if (mOnRefreshListener != null) {
                            mRefreshing = true;
                            mOnRefreshListener.onRefresh();//回调刷新完成的监听
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    private boolean isHeaderOnTop() {
        return mRefreshHeader.getHeaderView().getParent() != null;
    }

    public void refreshComplete() {
        if (mRefreshing) {
            mRefreshing = false;
            mRefreshHeader.refreshComplete();

        }
    }
}
