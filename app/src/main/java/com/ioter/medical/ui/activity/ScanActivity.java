package com.ioter.medical.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.common.BitmapUtil;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.di.component.AppComponent;

import butterknife.BindView;

import static com.ioter.medical.ui.activity.LoginActivity.USER_ID;

public class ScanActivity extends BaseActivity {

    @BindView(R.id.img_scan)
    ImageView imgScan;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public int setLayout() {
        return R.layout.activity_scan;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init() {
        title.setText("我的二维码");

        final String id = ACache.get(AppApplication.getApplication()).getAsString(USER_ID);
        AppApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                Create2QR2("{iotId:" + id + "}", imgScan);
            }
        });
    }

    //生成二维码的方法
    public void Create2QR2(String urls, final ImageView imageView) {
        String uri = urls;
        int mScreenWidth = 0;
        final Bitmap bitmap;
        try {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;

            bitmap = BitmapUtil.createQRImage(uri, mScreenWidth,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.me));//自己写的方法

            if (bitmap != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
