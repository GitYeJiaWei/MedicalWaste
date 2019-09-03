package com.ioter.medical;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.ioter.medical.common.AppCaughtException;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerAppComponent;
import com.ioter.medical.di.module.AppModule;

import java.util.concurrent.ExecutorService;


public class AppApplication extends Application
{

    private AppComponent mAppComponent;

    private static ExecutorService mThreadPool;

    private static AppApplication mApplication;

    public static AppApplication getApplication()
    {
        return mApplication;
    }

    public AppComponent getAppComponent()
    {
        return mAppComponent;
    }

    public static ExecutorService getExecutorService()
    {
        return mThreadPool;
    }

    public static Gson mGson;

    public static Gson getGson()
    {
        return mGson;
    }

    public static String TAG ="MEWaste";


    @Override
    public void onCreate()
    {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .build();
        mApplication = (AppApplication) mAppComponent.getApplication();
        mGson = mAppComponent.getGson();
        mThreadPool = mAppComponent.getExecutorService();
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        //设置AppCaughtException为系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(new AppCaughtException());// 注册全局异常捕获
    }




}
