package top.dzou.my_toutiao.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import top.dzou.my_toutiao.R;
import top.dzou.my_toutiao.base.BaseActivity;
import top.dzou.my_toutiao.base.BasePresenter;
import top.dzou.my_toutiao.listener.PermissionListener;
import top.dzou.my_toutiao.model.js.ImgOpJs;
import top.dzou.my_toutiao.ui.fragment.BigImgFragment;
import top.dzou.my_toutiao.utils.FileUtils;
import top.dzou.my_toutiao.utils.UIUtils;

public class ImagePagerActivity extends BaseActivity {

    private static final String TAG = ImagePagerActivity.class.getSimpleName();

    private static final int DOWNLOADING = 1;
    private static final int DOWNLOADED = 2;
    private static final int NOT_DOWNLOAD = 3;

    @BindView(R.id.vp_pics)
    ViewPager mViewPager;
    @BindView(R.id.tv_indicator)
    TextView mTvIndicator;
    @BindView(R.id.tv_save)
    TextView mTvSave;

    private List<String> mUrlList = new LinkedList<>();
    private int mPos;
    private String mCurrentImgUrl;
    private List<BigImgFragment> mFragments = new ArrayList<>();
    private Map<String,Integer> mDownLoadState = new HashMap<>();
    private MediaScannerConnection connection = null;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_img_page;
    }

    @Override
    protected void initData() {
        super.initData();
        ArrayList<String> urlList = getIntent().getStringArrayListExtra(ImgOpJs.IMG_ARRAY);
        if(urlList!=null&&!urlList.isEmpty()){
            mUrlList.addAll(urlList);
        }
        mCurrentImgUrl = getIntent().getStringExtra(ImgOpJs.IMG);
        mPos = mUrlList.indexOf(mCurrentImgUrl);
        for (String s : mUrlList) {
            BigImgFragment fragment = new BigImgFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ImgOpJs.IMG,s);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
            mDownLoadState.put(s,NOT_DOWNLOAD);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        UIUtils.hideStatusBar(this);
        mViewPager.setOffscreenPageLimit(mUrlList.size());
        mTvIndicator.setText(mPos+1+"/"+mUrlList.size());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                mCurrentImgUrl = mUrlList.get(position);
//                mPos = position;
//                mTvIndicator.setText(mPos+1+"/"+mUrlList.size());
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentImgUrl = mUrlList.get(position);
                mPos = position;
                mTvIndicator.setText(mPos+1+"/"+mUrlList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });
        mViewPager.setCurrentItem(mPos);// 设置当前所在的位置
    }

    @OnClick(R.id.tv_save)void onSaveClick(){
        requestPermissionsRuntime(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                downLoadImg(mCurrentImgUrl);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
                UIUtils.showToast(getString(R.string.write_storage_permission_deny));
            }
        });
    }


    private void downLoadImg(String mCurrentImgUrl) {
        String msg = null;
        if(mDownLoadState.get(mCurrentImgUrl)==NOT_DOWNLOAD) {
            new DownLoadImgTask().execute(mCurrentImgUrl);
        }else if(mDownLoadState.get(mCurrentImgUrl)==DOWNLOADING){
            UIUtils.showToast("正在下载");
        }else{
            UIUtils.showToast("该图片已经保存");
        }

    }

    class DownLoadImgTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            String imgUrl = strings[0];
            mDownLoadState.put(imgUrl,DOWNLOADING);
            File file = null;
            try {
                FutureTarget<File> future = Glide
                        .with(ImagePagerActivity.this)
                        .load(imgUrl)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                file = future.get();

                String filePath = file.getAbsolutePath();

                String destFileName = System.currentTimeMillis() + FileUtils.getImageFileExt(filePath);
                File destFile = new File(FileUtils.getDir(""), destFileName);

                FileUtils.copy(file, destFile);// 保存图片

                // 最后通知图库更新
                /**
                 * 三种方式
                 * 1.广播
                 * 2.使用MediaStore中insertImage
                 * 3.在API29中上面两种方式都已废弃，使用MediaScannerConnection.scanFile支持回调
                 */
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                        Uri.fromFile(new File(destFile.getPath()))));

//                MediaStore.Images.Media.insertImage(ImagePagerActivity.this.getContentResolver(),
//                        file.getAbsolutePath(), destFileName, null);

                connection = new MediaScannerConnection(ImagePagerActivity.this, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        connection.scanFile(destFile.getPath(), "image/jpeg");
                    }
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        connection.disconnect();
                    }
                });
                connection.connect();
            } catch (Exception e) {
                Log.e(TAG,e.getMessage());
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mDownLoadState.put(mCurrentImgUrl,NOT_DOWNLOAD);
            UIUtils.showToast("下载失败，网络错误");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            UIUtils.showToast("保存成功");
            mDownLoadState.put(mCurrentImgUrl,DOWNLOADED);
        }
    }
}
