package com.ioter.medical.data.http;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Detail;
import com.ioter.medical.bean.FeeRule;
import com.ioter.medical.bean.OutDetail;
import com.ioter.medical.bean.StockIn;
import com.ioter.medical.bean.StockOut;
import com.ioter.medical.bean.WasteViewsBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiService
{


    String BASE_URL = "http://192.168.66.3:8118/";

    //token为方法名，基类中不能加入方法名
    @FormUrlEncoded
    @POST("token")//登录
    Observable<Object> login(@FieldMap Map<String ,String> params);

    //更改密码
    @FormUrlEncoded
    @POST("/api/User/ChangePassword")
    Observable<BaseBean<String>> setting(@FieldMap Map<String,String> params);

    //主页统计数据
    @GET("/api/Home/Index")
    Observable<BaseBean<FeeRule>> rulelist();

    //收集的医废查询
    @FormUrlEncoded
    @POST("/api/Waste/List")
    Observable<BaseBean<Object>> wastelist(@FieldMap Map<String,Object> params);

    //收集医废
    @FormUrlEncoded
    @POST("api/Waste/Save")
    Observable<BaseBean<WasteViewsBean>> wastesave(@FieldMap Map<String,Object> params);

    //获取用户信息（二维码扫描）
    @GET("api/User/GetUser")
    Observable<BaseBean<Object>> getuser(@QueryMap Map<String,String> params);

    //医废类型查询
    @GET("api/Waste/WasteTypes")
    Observable<BaseBean<Object>> wastetypes();

    //医废入库单查询
    @FormUrlEncoded
    @POST("api/StockIn/List")
    Observable<BaseBean<List<StockIn>>> stockin(@FieldMap Map<String,Object> params);

    //医废入库登记
    @FormUrlEncoded
    @POST("api/StockIn/Save")
    Observable<BaseBean<Object>> stocksave(@FieldMap Map<String,Object> params);

    //医废详情（扫废物二维码）
    @GET("api/Waste/Detail")
    Observable<BaseBean<WasteViewsBean>> wastedetail(@QueryMap Map<String,String> params);

    //医废入库详情
    @GET("api/StockIn/Detail")
    Observable<BaseBean<Detail>> stockindetail(@QueryMap Map<String,Object> params);

    //入库确认
    @GET("api/StockIn/Confirm")
    Observable<BaseBean<Object>> stockinconfirm(@QueryMap Map<String,String> params);

    //医废出库单查询
    @FormUrlEncoded
    @POST("api/StockOut/List")
    Observable<BaseBean<List<StockOut>>> stockoutlist(@FieldMap Map<String,Integer> params);

    //医废出库登记
    @FormUrlEncoded
    @POST("api/StockOut/Save")
    Observable<BaseBean<Object>> stockout(@FieldMap Map<String,Object> params);

    //获取周转桶信息
    @GET("api/StockOut/GetDustbinInfo")
    Observable<BaseBean<Object>> getdustbininfo(@QueryMap Map<String,String> params);

    //医废出库详情
    @GET("api/StockOut/Detail")
    Observable<BaseBean<OutDetail>> stockoutdetail(@QueryMap Map<String,String> params);
}
