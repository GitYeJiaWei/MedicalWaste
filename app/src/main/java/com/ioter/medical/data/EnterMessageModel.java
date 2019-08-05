package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Detail;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.EnterMessageContract;

import java.util.Map;

import io.reactivex.Observable;

public class EnterMessageModel implements EnterMessageContract.IEnterMessageModel{
    private ApiService mApiService;

    public EnterMessageModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Detail>> EnterMessage(Map<String,Object> map) {
        return mApiService.stockindetail(map);
    }
}