package top.dzou.my_toutiao.event;

import android.view.MenuItem;

public class TabRefreshEvent {
    private MenuItem menuItem;
    private String channelCode;
    private int position;

    public TabRefreshEvent(MenuItem menuItem, String channelCode, int position) {
        this.menuItem = menuItem;
        this.channelCode = channelCode;
        this.position = position;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
