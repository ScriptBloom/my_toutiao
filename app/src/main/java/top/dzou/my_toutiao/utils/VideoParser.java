package top.dzou.my_toutiao.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.dzou.my_toutiao.api.net.ApiRetrofit;
import top.dzou.my_toutiao.model.Video;
import top.dzou.my_toutiao.model.VideoModel;

public abstract class VideoParser {

    public void decodeVideo(String srcUrl){
        Call<String> videoParseHtml = ApiRetrofit.getServiceInstance().getVideoHtml("https://pv.vlogdownloader.com");
        videoParseHtml.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Pattern pattern = Pattern.compile("var hash = \"(.+)\"");
                Matcher matcher = pattern.matcher(response.body());
                if (matcher.find()) {
                    String hash = matcher.group(1);
                    String url = String.format("http://pv.vlogdownloader.com/api.php?url=%s&hash=%s", srcUrl, hash);
                    Call<VideoModel> videoData = ApiRetrofit.getServiceInstance().getVideoData(url);
                    videoData.enqueue(new Callback<VideoModel>() {
                        @Override
                        public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                            List<Video> video = response.body().video;
                            if (video!=null&&video.size()>0){
                                //取数组中最后一个
                                Video videoResult = video.get(video.size() - 1);
                                onSuccess(videoResult.url);
                            }
                        }

                        @Override
                        public void onFailure(Call<VideoModel> call, Throwable t) {
                            onDecodeError(t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public abstract void onSuccess(String url);
    public abstract void onDecodeError(String errorMsg);
}
