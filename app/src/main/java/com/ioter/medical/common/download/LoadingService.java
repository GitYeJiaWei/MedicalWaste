package com.ioter.medical.common.download;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ioter.medical.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * IntentService是继承并处理异步请求的一个类，
 * 在IntentService内有一个工作线程来处理耗时操作，
 * 启动IntentService的方式和启动传统的Service一样，
 * 同时，当任务执行完后，IntentService会自动停止，而不需要我们手动去控制或stopSelf()。
 * 另外，可以启动IntentService多次，
 * 而每一个耗时操作会以工作队列的方式在IntentService的onHandleIntent回调方法中执行，
 * 并且，每次只会执行一个工作线程，执行完第一个再执行第二个，以此类推
 */

public class LoadingService extends IntentService {
    private HttpUtils httpUtils;
    NotificationManager nm;
    private String url,path;
    private SharedPreferences sharedPreferences;
    public LoadingService(String name) {
        super(name);
    }
    public LoadingService() {
        super("MyService");

    }


    public static void startUploadImg(Context context)
    {
        Intent intent = new Intent(context, LoadingService.class);
        context.startService(intent);
    }



    public void onCreate() {
        super.onCreate();
        httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        updateApk();
    }




    private void updateApk(){
        url = sharedPreferences.getString("url","").replaceAll("\\\\","/");
        path = sharedPreferences.getString("path","");

        httpUtils.download(url,
                path , new RequestCallBack<File>() {
                    @Override
                    public void onLoading(final long total, final long current,
                                          boolean isUploading) {
                        createNotification(total,current);
                        sendBroadcast(new Intent().setAction("android.intent.action.loading"));//发送正在下载的广播
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        nm.cancel(R.layout.notification_item);
                        Toast.makeText(LoadingService.this,"下载成功...",Toast.LENGTH_SHORT).show();
                        installApk();//下载成功 打开安装界面
                        stopSelf();//结束服务
                        sendBroadcast(new Intent().setAction("android.intent.action.loading_over"));//发送下载结束的广播
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Toast.makeText(LoadingService.this,"下载失败...",Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent().setAction("android.intent.action.loading_over"));//发送下载结束的广播
                        nm.cancel(R.layout.notification_item);
                        stopSelf();
                    }
                });
    }
    /**
     * 安装下载的新版本
     */
    protected void installApk() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        this.startActivity(intent);
    }

    private void createNotification(final long total, final long current){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//必须要设置这个属性，否则不显示
        RemoteViews contentView = new RemoteViews(this.getPackageName(),R.layout.notification_item);
        contentView.setProgressBar(R.id.progress, (int)total, (int)current, false);
        builder.setOngoing(true);//设置左右滑动不能删除
        Notification notification  = builder.build();
        notification.contentView = contentView;
        nm.notify(R.layout.notification_item,notification);//发送通知
    }


}
