package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedEnterContract;

import java.util.Map;

import io.reactivex.Observable;

public class MedEnterModel implements MedEnterContract.IMedEnterModel{
    private ApiService mApiService;

    public MedEnterModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Object>> medEnter(Map<String,Object> map) {
        return mApiService.stockin(map);
    }
}