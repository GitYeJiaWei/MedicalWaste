package com.ioter.medical.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;

import com.ioter.medical.AppApplication;
import com.ioter.medical.common.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 全局异常捕获
 */
public class AppCaughtException implements UncaughtExceptionHandler
{
    private UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;// 系统默认的异常捕获对象

    public AppCaughtException()
    {
        // 获取系统默认的UncaughtException处理器
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     *
     * ComponentName，顾名思义，就是组件名称，通过调用Intent中的setComponent方法，
     * 我们可以打开另外一个应用中的Activity或者服务
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        //把错误日志写到本地
        savaInfoToSD(AppApplication.getApplication(), ex);
        if (!handleException(thread, ex) && mDefaultUncaughtExceptionHandler != null)
        {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
        } else
        {
            /*Intent intent = new Intent();
            //第一个参数是要启动应用的包名称，第二个参数是你要启动的Activity或者Service的全称（包名+类名）
            intent.setComponent(
                    new ComponentName(VariableConstant.APP_PACKAGE_MAIN, "com.ioter.medical.ui.activity.LoginActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppApplication.getApplication().startActivity(intent);*/

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtil.toast("喵,很抱歉,程序出现异常,即将退出!");
                Looper.loop();
            }
        }.start();
        // 把异常信息和设备信息上传到服务器
        //subMitThreadAndDeviceInfo(AppApplication.getApplication(), thread, ex);
        return true;
    }

    /**
     * 保存错误日志到本地
     * @param context
     * @param ex
     * @return
     */
    private String savaInfoToSD(Context context, Throwable ex)
    {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : obtainSimpleInfo(context)
                .entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }

        sb.append(obtainExceptionInfo(ex));

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            String dirPath = Environment.getExternalStorageDirectory()
                    .getPath().toString()
                    + "/";
            File dir = new File(dirPath + "crashForDeveloper" + File.separator);
            if (!dir.exists())
            {
                dir.mkdir();
            }

            File[] files = dir.listFiles();
            if (files.length > 20)
            {
                // delete when record over 20
                for (int i = 0; i < files.length; i++)
                {
                    files[i].delete();
                }
            }
            try
            {
                fileName = dir.toString() + File.separator
                        + paserTime(System.currentTimeMillis()) + ".txt";
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        return fileName;
    }

    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     *
     * @param context
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context context)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try
        {
            mPackageInfo = mPackageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);

        map.put("MODEL", "" + Build.MODEL);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("PRODUCT", "" + Build.PRODUCT);

        return map;
    }

    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     *
     * @param milliseconds
     * @return
     */
    private String paserTime(long milliseconds)
    {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String times = format.format(new Date(milliseconds));

        return times;
    }

    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable)
    {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        mPrintWriter.close();
        // Log.e(TAG, mStringWriter.toString());
        return mStringWriter.toString();
    }


}