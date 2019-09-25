package com.ioter.medical.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ioter.medical.AppApplication;
import com.ioter.medical.common.CustomProgressDialog;
import com.ioter.medical.common.http.BaseUrlInterceptor;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.presenter.BasePresenter;
import com.ioter.medical.ui.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public abstract class BaseFragment<T extends BasePresenter> extends BackPressedFragment implements BaseView
{

    private Unbinder mUnbinder;

    private AppApplication mApplication;

    private View mRootView;

    protected AppCompatActivity mActivity;

    protected CustomProgressDialog progressDialog;


    @Inject
    public T mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        mRootView = inflater.inflate(setLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        mActivity = (AppCompatActivity) this.getActivity();
        this.mApplication = (AppApplication) getActivity().getApplication();
        setupAcitivtyComponent(mApplication.getAppComponent());
        init(mRootView);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }

    public static Retrofit toretrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //添加拦截器，自动追加参数
        builder.addInterceptor(new BaseUrlInterceptor());
        String BASE_URL = ACache.get(AppApplication.getApplication()).getAsString("BASE_URL");
        if (BASE_URL == null){
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mUnbinder != Unbinder.EMPTY)
        {
            mUnbinder.unbind();
        }
    }

    public void myOnKeyDwon()
    {

    }

    public void myOnKeyUp()
    {

    }

    @Override
    public void showLoading()
    {
    }

    @Override
    public void showError(String msg)
    {
    }

    @Override
    public void dismissLoading()
    {
    }

    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init(View view);

    public abstract void showBarCode(String barCode);


    @Override
    public void onBackPressed()
    {
        mActivity.onBackPressed();
    }

}
