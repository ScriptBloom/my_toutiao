package top.dzou.my_toutiao.model.js;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;
import java.util.List;

import top.dzou.my_toutiao.ui.activity.ImagePagerActivity;

public class ImgOpJs {

    private static final String TAG = ImgOpJs.class.getSimpleName();
    public static final String IMG = "img";
    public static final String IMG_ARRAY = "img_array";
    public static final String BYTEDANCE = "bytedance";
    private Context mContext;
    private List<String> urlList = new ArrayList<>();

    public ImgOpJs(Context context){
        this.mContext = context;
    }

    @JavascriptInterface
    public void openImg(String url){
        Log.d(TAG,"openimg");
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        intent.putExtra(IMG,url);
        intent.putStringArrayListExtra(IMG_ARRAY, (ArrayList<String>) urlList);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void getImgArray(String imgListString){
        Log.d(TAG,"getImgArray");
        String[] urls = imgListString.split(";");
        for (String url : urls) {
            urlList.add(url);
        }
    }
}
