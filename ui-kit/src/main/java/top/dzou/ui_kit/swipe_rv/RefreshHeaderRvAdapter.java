package top.dzou.ui_kit.swipe_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import top.dzou.ui_kit.R;

public abstract class RefreshHeaderRvAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;

    protected IRefresh mRefresh;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    public RefreshHeaderRvAdapter(int layoutResId,List<T> mDatas, Context context) {
        super(layoutResId,mDatas);
        this.mDatas = mDatas;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setmRefresh(IRefresh mRefresh) {
        this.mRefresh = mRefresh;
    }

//    @NonNull
//    @Override
//    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if(viewType == TYPE_HEADER){
//            return new BaseViewHolder(mRefresh.getHeaderView());
//        }else{
//            return new BaseViewHolder(mInflater.inflate(R.layout.item_normal,parent,false));
//        }
//    }

    @Override
    protected abstract void convert(BaseViewHolder baseViewHolder, T t);


//    @Override
//    public int getItemCount() {
//        return mDatas.size();
//    }

//    @Override
//    public int getItemViewType(int position) {
//        if(position == 0){
//            return BaseQuickAdapter.HEADER_VIEW;
//        }
//        return BaseQuickAdapter.EMPTY_VIEW;
//    }
}
