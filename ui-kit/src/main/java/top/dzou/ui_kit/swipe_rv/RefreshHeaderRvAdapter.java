package top.dzou.ui_kit.swipe_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.dzou.ui_kit.R;

public class RefreshHeaderRvAdapter extends RecyclerView.Adapter<RefreshHeaderRvAdapter.RefreshViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;

    private IRefresh mRefresh;
    private List<String> mDatas;
    private LayoutInflater mInflater;

    public RefreshHeaderRvAdapter(List<String> mDatas, Context context) {
        this.mDatas = mDatas;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setmRefresh(IRefresh mRefresh) {
        this.mRefresh = mRefresh;
    }

    @NonNull
    @Override
    public RefreshViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            return new RefreshViewHolder(mRefresh.getHeaderView());
        }else{
            return new RefreshViewHolder(mInflater.inflate(R.layout.item_normal,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RefreshViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    class RefreshViewHolder extends RecyclerView.ViewHolder{

        public RefreshViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
