package top.dzou.my_toutiao.event;

public class VideoProgressAndCommentEvent {
    private long progress;
    private long commmentCount;
    private String channelCode;
    private int position;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getCommmentCount() {
        return commmentCount;
    }

    public void setCommmentCount(long commmentCount) {
        this.commmentCount = commmentCount;
    }

}
