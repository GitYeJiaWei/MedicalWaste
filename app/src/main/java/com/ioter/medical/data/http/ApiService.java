package com.ioter.medical.data.http;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.LoginBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService
{


    String BASE_URL = "http://192.168.66.3:8118/";

    //token为方法名，基类中不能加入方法名
    @FormUrlEncoded
    @POST("token")//登录
    Observable<LoginBean> login(@FieldMap Map<String ,String> params);

    //更改密码
    @FormUrlEncoded
    @POST("/api/User/ChangePassword")
    Observable<BaseBean<String>> setting(@FieldMap Map<String,String> params);
}
