package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.OutRegisterContract;

import java.util.Map;

import io.reactivex.Observable;

public class OutRegisterModel implements OutRegisterContract.IOutRegisterModel{
    private ApiService mApiService;

    public OutRegisterModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Object>> OutRegister(Map<String,Object> map) {
        return mApiService.stockout(map);
    }
}