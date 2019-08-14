package com.ioter.medical;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.ioter.medical.common.AppCaughtException;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerAppComponent;
import com.ioter.medical.di.module.AppModule;
import com.rscja.deviceapi.RFIDWithUHF;
import com.zebra.adc.decoder.Barcode2DWithSoft;

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

    public static RFIDWithUHF mReader; //RFID扫描

    public static Barcode2DWithSoft barcode2DWithSoft = null;//二维扫码

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

    private static int cycleCount = 3;//循环3次初始化
    //初始化RFID扫描
    public static void initUHF()
    {
        cycleCount = 3;
        try
        {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex)
        {
            //ToastUtil.toast(ex.getMessage());
            return;
        }

        if (mReader != null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!mReader.init())
                    {
                        Log.d(TAG, "init uhf fail,reset ...");
                        if(cycleCount > 0)
                        {
                            cycleCount--;
                            if (mReader != null)
                            {
                                mReader.free();
                            }
                            initUHF();
                        }
                    }else
                    {
                        Log.d(TAG, "init uhf success");
                    }
                }
            }).start();
        }
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        Thread.setDefaultUncaughtExceptionHandler(new AppCaughtException());// 注册全局异常捕获
    }

    public static class InitBarCodeTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params)
        {

            if (barcode2DWithSoft == null)
            {
                barcode2DWithSoft = Barcode2DWithSoft.getInstance();
            }
            boolean reuslt = false;
            if (barcode2DWithSoft != null)
            {
                reuslt = barcode2DWithSoft.open(mApplication);
            }
            return reuslt;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if (result)
            {
                barcode2DWithSoft.setParameter(324, 1);
                barcode2DWithSoft.setParameter(300, 0); // Snapshot Aiming
                barcode2DWithSoft.setParameter(361, 0); // Image Capture Illumination

                // interleaved 2 of 5
                barcode2DWithSoft.setParameter(6, 1);
                barcode2DWithSoft.setParameter(22, 0);
                barcode2DWithSoft.setParameter(23, 55);

            } else
            {
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
    }


}
