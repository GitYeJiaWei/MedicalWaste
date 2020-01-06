package com.ioter.medical.common.http;

import android.text.TextUtils;
import android.util.Log;


import com.ioter.medical.AppApplication;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.data.http.ApiService;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/17.
 */

public class BaseUrlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        /*String ip = ACache.get(AppApplication.getApplication()).getAsString("ip");
        String host = ACache.get(AppApplication.getApplication()).getAsString("host");*/
        String token = ACache.get(AppApplication.getApplication()).getAsString("token");
        String BASE_URL = ACache.get(AppApplication.getApplication()).getAsString("BASE_URL");

        /*if (ip == null) {
            ip = ApiService.ip;
            ACache.get(AppApplication.getApplication()).put("ip", ip);
        }
        if (host == null){
            host = ApiService.host;
            ACache.get(AppApplication.getApplication()).put("host", host);
        }*/
        if (BASE_URL == null){
            BASE_URL = ApiService.BASE_URL;
            ACache.get(AppApplication.getApplication()).put("BASE_URL", BASE_URL);
        }

        //获取原始的originalRequest
        Request originalRequest = chain.request();
        //获取老的url
        HttpUrl oldUrl = originalRequest.url();

        Request.Builder builder =null;
        if (TextUtils.isEmpty(token)) {
            builder = originalRequest.newBuilder()
                    .addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        }else {
            //获取originalRequest的创建者builder,并添加请求头
            builder = originalRequest.newBuilder()
                    .addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8")
                    .addHeader("Authorization", "Bearer " + token);
        }


        //根据头信息中配置的value,来匹配新的base_url地址
        String base_url = BASE_URL;
        HttpUrl baseURL = HttpUrl.parse(base_url);
        //重建新的HttpUrl，需要重新设置的url部分
        HttpUrl newHttpUrl = oldUrl.newBuilder()
                .scheme(baseURL.scheme())//http协议如：http或者https
                .host(baseURL.host())//主机地址
                .port(baseURL.port())//端口
                .build();
        //获取处理后的新newRequest
        Request newRequest = builder.url(newHttpUrl).build();
        Log.d("ReToken","url："+newHttpUrl);
        Log.d("ReToken","Content-Type："+"application/x-www-form-urlencoded; charset=utf-8");
        Log.d("ReToken","Authorization："+"Bearer " + token);
        return chain.proceed(newRequest);
    }
}
