package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedCollectContract;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class MedCollectModel implements MedCollectContract.IMedCollectModel{
    private ApiService mApiService;

    public MedCollectModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Object>> medCollect(Map<String,Object> map) {
        return mApiService.wastelist(map);
    }
}