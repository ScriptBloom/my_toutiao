package top.dzou.my_toutiao.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class Channel implements Serializable, MultiItemEntity {

    public static final int TYPE_MY = 1;
    public static final int TYPE_OTHER = 2;
    public static final int TYPE_MY_CHANNEL = 3;
    public static final int TYPE_OTHER_CHANNEL = 4;

    public String channelTitle;
    public String channelCode;
    public int itemType;

    public Channel(String title, String channelCode) {
        this(TYPE_MY_CHANNEL, title, channelCode);
    }

    public Channel(int type, String title, String channelCode) {
        this.channelTitle = title;
        this.channelCode = channelCode;
        itemType = type;
    }
    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getChannelCode() {
        return channelCode;
    }
}
