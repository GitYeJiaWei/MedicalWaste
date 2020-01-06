package com.ioter.medical.ui.activity;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ioter.medical.AppApplication;
import com.ioter.medical.bean.BaseEpc;
import com.ioter.medical.common.ActivityCollecter;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.http.BaseUrlInterceptor;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.SoundManage;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.presenter.BasePresenter;
import com.ioter.medical.ui.BaseView;
import com.ioter.medical.ui.receiver.NetworkChangeEvent;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ioter.medical.ui.activity.MainActivity.barcode2DWithSoft;
import static com.ioter.medical.ui.activity.MainActivity.mReader;


public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView {

    private Unbinder mUnbinder;

    protected AppApplication mApplication;

    private Toast mToast = null;
    private ProgressDialog waitDialog = null;
    protected boolean mCheckNetWork = true; //默认检查网络状态

    protected Boolean IsFlushList = true; // 是否刷列表
    protected Object beep_Lock = new Object();
    //蜂鸣器
    protected ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);

    @Inject
    public T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //当该window对用户可见时，让设备屏幕处于高亮（bright）状态
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/

        setContentView(setLayout());

        mUnbinder = ButterKnife.bind(this);
        this.mApplication = (AppApplication) getApplication();
        setupAcitivtyComponent(mApplication.getAppComponent());
        ActivityCollecter.addActivity(this);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IsFlushList = true;
        initSound();
        //在无网络情况下打开APP时，系统不会发送网络状况变更的Intent，需要自己手动检查
        hasNetWork(NetUtils.isConnected(this));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();

        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            mUnbinder.unbind();
        }
        if (waitDialog != null) {
            waitDialog = null;
        }

        EventBus.getDefault().unregister(this);
        ActivityCollecter.removeActivity(this);
    }

    //扫条码
    public void ScanBarcode() {
        if (barcode2DWithSoft != null) {
            barcode2DWithSoft.scan();
            barcode2DWithSoft.setScanCallback(ScanBack);
        }
    }

    public Barcode2DWithSoft.ScanCallback
            ScanBack = new Barcode2DWithSoft.ScanCallback() {
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            if (length < 1) {
            } else {
                final String barCode = new String(bytes, 0, length);
                if (barCode != null && barCode.length() > 0) {
                    SoundManage.PlaySound(BaseActivity.this, SoundManage.SoundType.SUCCESS);
                    if (barCode.toUpperCase().startsWith("AA") || barCode.toUpperCase().startsWith("BB")) {
                        showBarCode(barCode.toUpperCase());
                    } else {
                        ToastUtil.toast("二维码不符合");
                    }
                }
            }
        }
    };

    public void showBarCode(String barcode) {

    }

    public static Retrofit toretrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //添加拦截器，自动追加参数
        builder.addInterceptor(new BaseUrlInterceptor());
        String BASE_URL = ACache.get(AppApplication.getApplication()).getAsString("BASE_URL");
        if (BASE_URL == null) {
            BASE_URL = ApiService.BASE_URL;
        }

        Retrofit retrofit = new Retrofit.Builder()
                //设置基础的URL
                .baseUrl(BASE_URL)
                //设置内容格式,这种对应的数据返回值是Gson类型，需要导包
                .addConverterFactory(GsonConverterFactory.create())
                //设置支持RxJava，应用observable观察者，需要导包
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();
        return retrofit;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BaseEpc baseEpc = (BaseEpc) msg.obj;
            if (baseEpc != null) {
                handleUi(baseEpc);
            }
        }
    };

    //处理ui
    public void handleUi(BaseEpc baseEpc) {
        synchronized (beep_Lock) {
            beep_Lock.notify();
        }
    }

    //配置读写器参数
    protected void initSound() {
        // 蜂鸣器发声
        AppApplication.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                while (IsFlushList) {
                    synchronized (beep_Lock) {
                        try {
                            beep_Lock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    if (IsFlushList) {
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        IsFlushList = false;
        synchronized (beep_Lock) {
            beep_Lock.notifyAll();
        }
        super.onPause();
    }

    //隐藏软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                ScreenUtils.hideKeyboard(ev, view, BaseActivity.this);//调用方法判断是否需要隐藏键盘
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    protected boolean loopFlag = false;

    class TagThread extends Thread {

        private int mBetween = 80;

        public TagThread(int iBetween) {
            mBetween = iBetween;
        }

        public void run() {
            String strTid;
            String strResult;

            String[] res = null;
            while (loopFlag) {

                res = mReader.readTagFromBuffer();//.readTagFormBuffer();

                if (res != null) {

                    strTid = res[0];
                    if (!strTid.equals("0000000000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Message msg = handler.obtainMessage();
                    BaseEpc baseEpc = new BaseEpc();
                    baseEpc._EPC = mReader.convertUiiToEPC(res[1]);
                    baseEpc._TID = strResult;
                    try {
                        baseEpc.rssi = (new Double(Double.valueOf(res[2]))).intValue();
                    } catch (Exception e) {

                    }
                    msg.obj = baseEpc;
                    handler.sendMessage(msg);
                }
                try {
                    sleep(mBetween);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init();


    @Override
    public void showLoading() {
        if (waitDialog == null) {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage("加载中...");
        waitDialog.show();
    }

    @Override
    public void showError(String msg) {
        if (waitDialog != null) {
            waitDialog.setMessage(msg);
            waitDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    //检查网络
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        hasNetWork(event.isConnected);
    }

    private void hasNetWork(boolean has) {
        if (isCheckNetWork()) {
            handleNetWorkTips(has);
        }
    }

    protected void handleNetWorkTips(boolean has) {
    }

    public void setCheckNetWork(boolean checkNetWork) {
        mCheckNetWork = checkNetWork;
    }

    public boolean isCheckNetWork() {
        return mCheckNetWork;
    }

}
