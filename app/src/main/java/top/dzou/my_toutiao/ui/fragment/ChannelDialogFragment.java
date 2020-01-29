package top.dzou.my_toutiao.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.api.Constant;
import top.dzou.my_toutiao.listener.ItemDragHelperCallBack;
import top.dzou.my_toutiao.listener.OnChannelDragListener;
import top.dzou.my_toutiao.listener.OnChannelListener;
import top.dzou.my_toutiao.model.Channel;
import top.dzou.my_toutiao.ui.adapter.ChannelDialogRvAdapter;

import static top.dzou.my_toutiao.model.Channel.TYPE_MY_CHANNEL;

public class ChannelDialogFragment extends DialogFragment implements OnChannelDragListener {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private ChannelDialogRvAdapter mAdapter;
    private List<Channel> mData = new ArrayList<>();
    private DialogInterface.OnDismissListener mOnDismissListener;//关闭回调接口
    private ItemTouchHelper mHelper;
    private OnChannelListener mOnChannelListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            //添加动画
            dialog.getWindow().setWindowAnimations(R.style.dialogSlideAnim);
        }
        return inflater.inflate(R.layout.fragment_channel, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    public static ChannelDialogFragment getInstance(List<Channel> selectedChannels, List<Channel> unSelectedChannels) {
        //把tab channel传递给dialog
        ChannelDialogFragment dialogFragment = new ChannelDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.DATA_SELECTED, (Serializable) selectedChannels);
        bundle.putSerializable(Constant.DATA_UNSELECTED, (Serializable) unSelectedChannels);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        processLogic();
    }

    private void processLogic() {
        mData.add(new Channel(Channel.TYPE_MY, "我的频道", ""));
        Bundle bundle = getArguments();
        List<Channel> selectedDatas = (List<Channel>) bundle.getSerializable(Constant.DATA_SELECTED);
        List<Channel> unselectedDatas = (List<Channel>) bundle.getSerializable(Constant.DATA_UNSELECTED);
        setDataType(selectedDatas, TYPE_MY_CHANNEL);
        setDataType(unselectedDatas, Channel.TYPE_OTHER_CHANNEL);
        mData.addAll(selectedDatas);
        mData.add(new Channel(Channel.TYPE_OTHER, "推荐频道", ""));
        if (unselectedDatas!=null) {
            mData.addAll(unselectedDatas);
        }

        mAdapter = new ChannelDialogRvAdapter(mData);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(manager);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                return itemViewType == TYPE_MY_CHANNEL || itemViewType == Channel.TYPE_OTHER_CHANNEL ? 1 : 4;
            }
        });
        //设置ItemTouchHelper 实现拖动并且交换位置
        ItemDragHelperCallBack callBack = new ItemDragHelperCallBack(this);
        mHelper = new ItemTouchHelper(callBack);
        mAdapter.setOnChannelDragListener(this);
        mHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setDataType(List<Channel> datas, int type) {
        if (datas == null) return;
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).setItemType(type);
        }
    }

    @OnClick(R.id.close_img_view) void onClickCloseChannelSelected(View view) {
        dismiss();
    }

    public void setmOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onStarDrag(BaseViewHolder baseViewHolder) {
        mHelper.startDrag(baseViewHolder);
    }

    @Override
    public void onItemMove(int starPos, int endPos) {
        //我的频道之间移动
        if (mOnChannelListener != null)
            mOnChannelListener.onItemMove(starPos - 1, endPos - 1);//去除标题所占的一个index
        onMove(starPos, endPos);
    }

    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {
        //移动到我的频道
        onMove(starPos, endPos);
        if (mOnChannelListener != null)
            mOnChannelListener.onMoveToMyChannel(starPos - 1 - mAdapter.getMyChannelSize(), endPos - 1);
    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
        onMove(starPos, endPos);
        if (mOnChannelListener != null)
            mOnChannelListener.onMoveToOtherChannel(starPos - 1, endPos - 2 - mAdapter.getMyChannelSize());
    }

    private void onMove(int starPos, int endPos) {
        Channel startChannel = mData.get(starPos);
        //先删除之前的位置
        mData.remove(starPos);
        //添加到现在的位置
        mData.add(endPos, startChannel);
        mAdapter.notifyItemMoved(starPos, endPos);
    }

    public void setOnChannelListener(OnChannelListener onChannelListener) {
        mOnChannelListener = onChannelListener;
    }
}
