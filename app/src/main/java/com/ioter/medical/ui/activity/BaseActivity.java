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
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.ioter.medical.AppApplication;
import com.ioter.medical.bean.BaseEpc;
import com.ioter.medical.common.ActivityCollecter;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.http.BaseUrlInterceptor;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.presenter.BasePresenter;
import com.ioter.medical.ui.BaseView;
import com.ioter.medical.ui.receiver.NetworkChangeEvent;
import okhttp3.OkHttpClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Inject;


public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView {

    private Unbinder mUnbinder;

    protected AppApplication mApplication;

    private ProgressDialog waitDialog = null;
    protected boolean mCheckNetWork = true; //默认检查网络状态

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
        //在无网络情况下打开APP时，系统不会发送网络状况变更的Intent，需要自己手动检查
        hasNetWork(NetUtils.isConnected(this));
    }

    @Override
    protected void onDestroy() {
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
